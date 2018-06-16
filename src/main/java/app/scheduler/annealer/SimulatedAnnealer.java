package app.scheduler.annealer;

import java.util.concurrent.ThreadLocalRandom;

public class SimulatedAnnealer {
    public static Annealable solve(Annealable t, int seconds) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long maxTime = System.currentTimeMillis() + seconds * 1000;
        Annealable best = t;
        // continue until time threshold has been reached
        while (System.currentTimeMillis() < maxTime) {
            double temperature = 10000;
            double coolingRate = 0.007; //it was chosen by magic
            while (temperature > 1) {
                temperature *= 1 - coolingRate;
                // choose a neighbour, switch if probability threshold is met
                Annealable neighbour = t.randomNeighbour();
                if (prob(t.getScore(), neighbour.getScore(), temperature) > random.nextFloat()) {
                    t = neighbour;
                }
                //update the best score
                if (t.getScore() > best.getScore()) {
                    best = t;
                }
            }
            t = best;
        }
        return best;
    }

    private static float prob(float e, float eNew, double temperature) {
        if (eNew < e) {
            return 1;
        }
        return (float) Math.exp((eNew - e) / temperature);
    }
}
