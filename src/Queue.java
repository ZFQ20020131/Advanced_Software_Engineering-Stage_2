package stage2;

import java.util.*;

/**
 * Manages a queue of passengers for a simulation.
 * This class is responsible for adding and removing passengers from a queue,
 * and it communicates with observers when changes occur.
 * Extends Observable to allow observation and implements Runnable for thread functionality.
 */
@SuppressWarnings("deprecation")
public class Queue extends Observable implements Subject, Runnable {
    private java.util.Queue<BookingDetails> passengerLine = new LinkedList<>();
    private List<Observer> observerList = new ArrayList<>();
    private List<BookingDetails> unprocessedBookingDetails = new ArrayList<>();
    private Timer simTimer;
    private Random rnd = new Random();
    private Boolean allEnqueued = false;

    // Constructor: Initializes queue with a timer and a list of bookings.
    public Queue(Timer timer, AllBookings bookings) {
        this.simTimer = timer;
        this.unprocessedBookingDetails.addAll(bookings.getAllBookings().values());
    }

    // Logs a message with the current simulation time.
    private synchronized void logActivity(String message) {
        Log.INSTANCE.addMessage(simTimer.getTimeString() + " " + message);
    }

    // Adds a booking directly to the queue and notifies observers.
    public synchronized void enqueue(BookingDetails bookingDetails) {
        passengerLine.offer(bookingDetails);
        logActivity(bookingDetails.getFullName() + " joined the queue.");
        notifyObservers(bookingDetails);
    }

    // Randomly selects a booking to add to the queue until all are enqueued.
    public synchronized void addRandomBooking() {
        if (!allEnqueued && !unprocessedBookingDetails.isEmpty()) {
            BookingDetails bookingDetails = unprocessedBookingDetails.remove(rnd.nextInt(unprocessedBookingDetails.size()));
            passengerLine.add(bookingDetails);
            logActivity(bookingDetails.getFullName() + " joined the queue.");
            notifyObservers(bookingDetails);
        } else if (unprocessedBookingDetails.isEmpty()) {
            allEnqueued = true;
            logActivity("All passengers have joined the queue");
        }
    }

    // Removes the first booking in the queue and notifies observers.
    public synchronized BookingDetails removeFirst() {
        BookingDetails removed = passengerLine.poll();
        if (removed != null) {
            logActivity(removed.getFullName() + " left the queue.");
            notifyObservers();
        }
        return removed;
    }

    // Moves the first booking to the back of the queue and notifies observers.
    public synchronized void recycleBooking() {
        if (!passengerLine.isEmpty()) {
            BookingDetails temp = passengerLine.poll();
            passengerLine.offer(temp);
            logActivity("Recycled to the end: " + temp.getFullName());
            notifyObservers(temp);
        }
    }

    // Main loop for the thread, enqueuing passengers at intervals.
    public void run() {
        for (int i = 0; i < 6; i++) {
            addRandomBooking();
        }
        while (!passengerLine.isEmpty()) {
            synchronized (simTimer) {
                try {
                    simTimer.wait();
                    for (int i = 0; i < 6; i++) {
                        addRandomBooking();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Registers an observer to receive updates.
    @Override
    public void registerObserver(Observer observer) {
        observerList.add(observer);
    }

    // Removes an observer from the notification list.
    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    // Notifies all observers without specific booking information.
    @Override
    public void notifyObservers() {
        observerList.forEach(observer -> observer.update(this, null));
    }

    // Notifies all observers with specific booking information.
    public void notifyObservers(BookingDetails bookingDetails) {
        observerList.forEach(observer -> observer.update(this, bookingDetails));
    }

    // Returns the number of bookings in the queue.
    public int queueSize() {
        return passengerLine.size();
    }

    // Peeks at the first booking in the queue without removing it.
    public BookingDetails firstInLine() {
        return passengerLine.peek();
    }

    // Provides access to the internal queue.
    public synchronized java.util.Queue<BookingDetails> getPassengerQueue() {
        return this.passengerLine;
    }
}




