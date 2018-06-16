package app.pdf;

import app.scheduler.MovieTime;
import app.scheduler.Schedule;
import app.scheduler.ScheduleRepository;
import app.user.User;
import app.user.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PDFService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @PreAuthorize("hasAuthority('USER')")
    PDDocument generateUserPDF() throws IOException {
        User user = userRepository.getLoggedInUser();
        Optional<Schedule> userSchedule = scheduleRepository.findFirstByUser(user);
        if (!userSchedule.isPresent()) {
            throw new BadRequestException("No schedule found");
        }
        PDDocument document = new PDDocument();
        // set document information
        document.getDocumentInformation().setCreator("Aaron Malpas");
        document.getDocumentInformation().setTitle("Watch It Right Schedule");

        // create the first page
        PDPage mainPage = new PDPage();
        document.addPage(mainPage);
        PDPageContentStream contentStream = new PDPageContentStream(document, mainPage);

        // set up page
        drawBackground(contentStream);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 700);
        drawTitle(contentStream);

        // find the distinct days that have a scheduled movie (so movies can be grouped together)
        List<MovieTime> movieTimes = userSchedule.get().getMovieTimes();
        List<LocalDate> daysWithMovies = movieTimes.stream()
                .map(movieTime -> movieTime.getStart().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // get maximum title length (for table spacing)
        int maxTitleLength = movieTimes.stream()
                .map(MovieTime::getTitle)
                .mapToInt(String::length)
                .max().orElse(0);

        // height counter to know when the next page should be created
        AtomicInteger heightCounter = new AtomicInteger(0);

        // for each day, draw the header and scheduled movies
        int dayNum = 1;
        for (LocalDate day : daysWithMovies) {
            try {
                // draw header
                contentStream.newLineAtOffset(0, -25);
                drawDayHeader(day, dayNum, contentStream, heightCounter);
                dayNum += 1;

                // draw scheduled movies
                List<MovieTime> movieTimesOnDay = movieTimes.stream()
                        .filter(movieTime -> movieTime.getStart().toLocalDate().isEqual(day))
                        .collect(Collectors.toList());
                drawMovieTimes(movieTimesOnDay, maxTitleLength, contentStream, heightCounter);

                // create new page if necessary
                if (heightCounter.get() > 500) {
                    heightCounter.set(0);
                    PDPage newPage = new PDPage();
                    document.addPage(newPage);

                    // clean up
                    contentStream.endText();
                    contentStream.close();

                    // set up page
                    contentStream = new PDPageContentStream(document, newPage);
                    drawBackground(contentStream);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 700);
                    drawTitle(contentStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        contentStream.endText();
        contentStream.close();
        return  document;
    }

    private void drawBackground(PDPageContentStream contentStream) {
        try {
            contentStream.setNonStrokingColor(Color.decode("#f9f9f9"));
            contentStream.addRect(0, 0, 800, 900);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);
        } catch (IOException e) {
            throw new BadRequestException("Could not create PDF (adding background)");
        }
    }

    private void drawTitle(PDPageContentStream contentStream) {
        try {
            contentStream.setNonStrokingColor(Color.decode("#aa0000"));
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 25);
            contentStream.showText("Watch It Right");
            contentStream.newLineAtOffset(0, -30);
            contentStream.setFont(PDType1Font.HELVETICA, 18);
            contentStream.setNonStrokingColor(Color.BLACK);
        } catch (IOException e) {
            throw new BadRequestException("Could not create PDF (creating title)");
        }

    }

    private void drawDayHeader(LocalDate day, int dayNum, PDPageContentStream contentStream, AtomicInteger heightCounter) {
        try {
            contentStream.setFont(PDType1Font.HELVETICA, 20);

            // draw header
            contentStream.newLineAtOffset(0, -25);
            heightCounter.addAndGet(25);
            contentStream.showText(String.format("Day %d (%s)", dayNum,
                    day.format(DateTimeFormatter.ofPattern("dd/MM/yy"))));

            heightCounter.addAndGet(25);
        } catch (IOException e) {
            throw new BadRequestException("Could not create PDF (day header)");
        }
    }

    private void drawMovieTimes(List<MovieTime> movies, int maxTitleLength,
                                PDPageContentStream contentStream, AtomicInteger heightCounter) {
        try {
            // shift cursor right and down
            contentStream.newLineAtOffset(30, -40);
            heightCounter.addAndGet(40);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);

            // draw table column headers
            int timeTextOffset = maxTitleLength * 7 + 15;
            contentStream.setNonStrokingColor(Color.decode("#336699"));
            contentStream.showText("Movie");
            contentStream.newLineAtOffset(timeTextOffset, 0);
            contentStream.showText("Time");
            contentStream.newLineAtOffset(-timeTextOffset, 0);
            contentStream.setNonStrokingColor(Color.BLACK);

            contentStream.setFont(PDType1Font.HELVETICA, 14);

            // draw each movie
            movies.forEach(movieTime -> {
                try {
                    contentStream.newLineAtOffset(0, -15);
                    // format the start and end times of movies as hours
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
                    String startTimeText = timeFormatter.format(movieTime.getStart());
                    String endTimeText = timeFormatter.format(movieTime.getEnd());

                    // draw schedule title
                    contentStream.showText(movieTime.getTitle());

                    // draw schedule time
                    contentStream.newLineAtOffset(timeTextOffset, 0);
                    contentStream.showText(startTimeText + "-" + endTimeText);
                    contentStream.newLineAtOffset(-timeTextOffset, 0);

                    // update height counter
                    heightCounter.addAndGet(15);
                } catch (IOException e) {
                    throw new BadRequestException("Could not create PDF (drawing movie)");
                }
            });
            // shift cursor back left
            contentStream.newLineAtOffset(-30, 0);
        } catch (IOException e) {
            throw new BadRequestException("Could not create PDF (drawing movie)");
        }
    }
}
