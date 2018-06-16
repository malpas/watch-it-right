package app.explore.binbinge;

import app.explore.Movie;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BinBingeData {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    private Movie movie;

    private int likes = 0;
    private int totalRates = 0;

    BinBingeData() {
    }

    /**
     * Generate a movie's Bin It or Binge it data based on the number of ratings
     * users have made and the number of those ratings that were likes (binges).
     * @param total Total number of user ratings of movie
     * @param likes Number of user ratings that were likes (binges)
     */
    public BinBingeData(Movie movie, int total, int likes) {
        this.movie = movie;
        this.likes = likes;
        this.totalRates = total;
    }

    /**
     * Estimate rating of a movie using the rule of succession.
     * Given a test that can succeed or fail, with s successes and
     * n results, the probability of another success is (s+1)/(n+2)
     * @return The estimated movie rating
     */
    public int getRating() {
        return (likes + 1) / (totalRates + 2);
    }

    /**
     * Notify the model of a user dislike.
     */
    public void userDislike() {
        totalRates += 1;
    }

    /**
     * Notify the model of a user like
     */
    public void userLike() {
        likes += 1;
        totalRates += 1;
    }
}
