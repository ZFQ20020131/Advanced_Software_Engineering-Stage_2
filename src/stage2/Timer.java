package part2;

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

/**
 * Controls the timing for simulation processes, acting as a notifier for changes in simulation time.
 */
@SuppressWarnings("deprecation")
class Timer extends Thread implements Subject {
    private int currentTime = 0;
    private boolean isAdjusting = false;
    private SimTime simulationTime;
    private List<Observer> observers = new LinkedList<>();

    /**
     * Constructs a Timer with a specific simulation time control.
     *
     * @param simulationTime Object controlling simulation speed and pausing.
     */
    public Timer(SimTime simulationTime) {
        this.simulationTime = simulationTime;
    }

    /**
     * Registers an observer for notification of time changes.
     *
     * @param observer Observer to register.
     */
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the notification list.
     *
     * @param observer Observer to remove.
     */
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers of a change.
     */
    @Override
    public void notifyObservers() {
        observers.forEach(observer -> observer.update(null, this));
    }

    /**
     * Main execution method for the Timer, incrementing time and notifying observers at each step.
     */
    @Override
    public void run() {
        while (true) {
            incrementTime();
            try {
                Thread.sleep(simulationTime.getCurrentInterval());
                while (simulationTime.isPaused()) {
                    Thread.sleep(10); // Pause check interval
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Increments the current simulation time and notifies all observers.
     */
    private synchronized void incrementTime() {
        updateTime(currentTime + 1);
    }

    /**
     * Updates the simulation time and notifies waiting threads.
     *
     * @param newTime New simulation time.
     */
    public synchronized void updateTime(int newTime) {
        isAdjusting = true;
        this.currentTime = newTime;
        isAdjusting = false;
        notifyAll();
        notifyObservers();
    }

    /**
     * Returns the current simulation time as an integer.
     *
     * @return Current simulation time.
     */
    public synchronized int getCurrentTime() {
        while (isAdjusting) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return this.currentTime;
    }

    /**
     * Formats and returns the current simulation time as a string in HH:MM format.
     *
     * @return Formatted time string.
     */
    public synchronized String getTimeString() {
        while (isAdjusting) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int minutes = this.currentTime % 60;
        int hours = this.currentTime / 60 % 24;
        return String.format("[%02d:%02d]", hours, minutes);
    }
}

