package app.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class PDFController {
    @Autowired
    PDFService pdfService;

    @GetMapping(value = "/schedule/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    byte[] getPDF() throws IOException {
        PDDocument document = pdfService.generateUserPDF();
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        document.save(byteOutputStream);
        document.close();
        return  byteOutputStream.toByteArray();
    }
}
