package stage2;

import java.util.LinkedList;
import java.util.List;

/**
 * Entry point for the simulation program. Initializes and starts simulation components including timers,
 * passenger queues, check-in counters, and the GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize the components needed for the simulation
        Read_CSV csvReader = new Read_CSV();
        AllBookings allBookings = csvReader.getBookings();
        AllFlights allFlights = csvReader.getFlights();
        SimulationTime simulationTime = new SimulationTime();
        Timer simulationTimer = new Timer(simulationTime);
        Queue passengerQueue = new Queue(simulationTimer, allBookings);

        // Create and start check-in counters
        List<Counter> checkinCounters = setupCheckinCounters(allFlights, simulationTimer, passengerQueue);

        // Initialize and register the GUI as an observer to various subjects
        GUI simulationGUI = new GUI(simulationTime, checkinCounters, allFlights.getAllFlights());
        registerObservers(simulationTimer, passengerQueue, allFlights, checkinCounters, simulationGUI);

        // Start simulation threads
        startSimulation(passengerQueue, simulationTimer, checkinCounters);

        // Setup shutdown hook to ensure logs are saved when the program exits
        setupShutdownHook();
    }

    private static List<Counter> setupCheckinCounters(AllFlights flights, Timer timer, Queue pq) {
        List<Counter> counters = new LinkedList<>();
        for (int i = 1; i <= 6; i++) {
            Counter counter = new Counter(i, flights, timer, pq);
            counters.add(counter);
        }
        return counters;
    }

    private static void registerObservers(Timer timer, Queue pq, AllFlights flights, List<Counter> counters, GUI gui) {
        Thread pqThread = new Thread(pq);
        pqThread.start();

        timer.registerObserver(gui);
        pq.registerObserver(gui);
        flights.getAllFlights().forEach((key, value) -> value.registerObserver(gui));
        counters.forEach(counter -> counter.registerObserver(gui));
    }

    private static void startSimulation(Queue pq, Timer timer, List<Counter> counters) {
        try {
            Thread.sleep(1000); // Delay for GUI readiness
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
        timer.start();
        counters.forEach(Thread::start);
    }

    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Log.INSTANCE.flushToDisk(), "Shutdown-thread"));
    }
}

