package part2;

import java.util.Observer;

/**
 * Defines the contract for a Subject in the Observer pattern.
 * It outlines methods for managing observers in a thread-safe manner, replacing the deprecated Java Observable.
 */
@SuppressWarnings("deprecation")
public interface Subject {

    /**
     * Attaches an observer to this subject.
     * @param observer The observer to be added.
     */
    void registerObserver(Observer observer);

    /**
     * Detaches an observer from this subject.
     * @param observer The observer to be removed.
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all attached observers of a change.
     */
    void notifyObservers();
}
