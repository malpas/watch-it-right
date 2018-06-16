package app.scheduler.annealer;

public interface Annealable<T> {
    float getScore();
    Annealable randomNeighbour();
}
