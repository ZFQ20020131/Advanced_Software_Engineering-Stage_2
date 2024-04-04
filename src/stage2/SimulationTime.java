package stage2;

/**
 * Manages simulation time, allowing for adjustments in speed and pausing.
 */
public class SimulationTime {
    private long simulationInterval;
    private boolean isPaused;

    /**
     * Initializes simulation time with a default speed.
     */
    public SimulationTime(){
        simulationInterval = 1000; // Default speed set to 1000ms intervals.
    }

    /**
     * Adjusts the simulation's speed based on a multiplier.
     * The speed adjustment is inversely proportional to the input multiplier.
     * @param speedFactor the factor by which to adjust simulation speed.
     */
    public void adjustSpeed(int speedFactor){
        simulationInterval = 1000 / speedFactor;
    }

    /**
     * Retrieves the current simulation interval.
     * @return Current speed of the simulation as a time interval in milliseconds.
     */
    public long getCurrentInterval(){
        return simulationInterval;
    }

    /**
     * Checks if the simulation is currently paused.
     * @return True if the simulation is paused, false otherwise.
     */
    public boolean isPaused(){
        return isPaused;
    }

    /**
     * Pauses the simulation.
     */
    public synchronized void pause(){
        isPaused = true;
    }

    /**
     * Resumes the simulation from pause.
     */
    public synchronized void resume(){
        isPaused = false;
    }
}

