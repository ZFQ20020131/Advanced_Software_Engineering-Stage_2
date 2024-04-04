package stage2;

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import stage2.FlightDetails.OverBaggageLimitException;
/**
 * Represents a check-in counter for passengers at an airport simulation.
 * Manages passenger check-ins, interacts with flight details, and updates on passenger processing.
 */
@SuppressWarnings("deprecation")
public class Counter extends Thread implements Subject {
    private int counterId;
    private Queue queue;
    private BookingDetails passenger;
    private FlightDetails passengerFlight;
    private List<Observer> observers = new LinkedList<>();
    private AllFlights flights;
    private Timer timer;
    private int currentTick;
    private String currentTickAsString;
    private boolean isOpen;

    // Constructor for the check-in counter.
    public Counter(int number, AllFlights flights, Timer timer, Queue queue) {
        this.counterId = number;
        this.flights = flights;
        this.timer = timer;
        this.queue = queue;
        this.isOpen = true;
    }

    // Toggles the open/close state of the counter and logs the activity.
    public synchronized void toggleCounter() {
        this.isOpen = !this.isOpen;
        logCounterActivity("Checkin counter " + this.counterId + (this.isOpen ? " opened." : " closed."));
    }

    // Main method to process passengers in the queue at each tick.
    public void run() {
        synchronized(timer) {
            while (true) {
                try {
                    timer.wait();
                    updateTime();
                    servePassenger();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Updates the current simulation time.
    private void updateTime() {
        this.currentTickAsString = timer.getTimeString();
        this.currentTick = timer.getCurrentTime();
    }

    // Returns the counter number.
    public int getCounterNumber() {
        return this.counterId;
    }

    // Returns the current booking details.
    public BookingDetails getBooking() {
        return this.passenger;
    }

    // Sets the flight details for the current passenger.
    public void setPassengerFlight() {
        this.passengerFlight = flights.getFlight(passenger.getFlightCode());
    }

    // Returns the excess fee charged to the current passenger, if any.
    public Float getPassengerExcessFee() {
        return this.passenger.getExcessFeeCharged();
    }

    // Checks if the counter is open.
    public boolean getIsOpen() {
        return this.isOpen;
    }

    // Logs activity related to this check-in counter.
    private void logCounterActivity(String message) {
        Log.INSTANCE.addMessage(this.currentTickAsString + " " + message);
    }

    // Processes the first passenger in the queue if the counter is open.
    public synchronized void servePassenger() {
        if (queue.queueSize() > 0 && this.isOpen) {
            BookingDetails firstPassenger = queue.firstInLine();
            if (firstPassenger != null && !firstPassenger.getMissedFlight()) {
                this.passenger = queue.removeFirst();
                handlePassengerCheckIn();
            } else if (firstPassenger != null) {
                queue.recycleBooking();
                this.passenger = null;
                this.passengerFlight = null;
            }
            notifyObservers();
        }
    }

    // Handles the check-in process, including baggage checks and setting flight status.
    private void handlePassengerCheckIn() {
        setPassengerFlight();
        if (passengerFlight != null && passengerFlight.checkGateOpen(this.currentTick, this.currentTickAsString)) {
            try {
                passengerFlight.checkBaggage(passenger.getBaggageWeight(), passenger.getBaggageLength(), passenger.getBaggageHeight(), passenger.getBaggageWidth());
            } catch (OverBaggageLimitException e) {
                passenger.setExcessFeeCharged(passengerFlight.getExcessFeeCharge());
            }
            passengerFlight.addPassenger();
            logCounterActivity("[Counter " + this.counterId + "] " + passenger.getFullName() + " checked into flight " + passengerFlight.getFlightCode() + ". Excess fee of £" + passenger.getExcessFeeCharged() + " charged.");
        } else {
            passenger.missFlight();
            queue.enqueue(passenger);
            logCounterActivity("[Counter " + this.counterId + "] " + (passengerFlight != null ? passengerFlight.getFlightCode() : "Unknown Flight") + " has already departed, " + passenger.getFullName() + " has missed their flight and has joined the end of the queue.");
        }
    }

    /**
     * Registers a new observer to be notified of updates related to the check-in counter.
     * This allows for real-time updates when check-in counter state changes occur.
     *
     * @param obs The observer that wants to be registered.
     */
    @Override
    public void registerObserver(Observer obs) {
        observers.add(obs);
    }

    /**
     * Removes an observer from the list of registered observers.
     * This observer will no longer receive updates from this check-in counter.
     *
     * @param obs The observer that should be removed.
     */
    @Override
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    /**
     * Notifies all registered observers of a change in the check-in counter's state.
     * This method is typically called after a state change to update all interested parties.
     */
    @Override
    public void notifyObservers() {
        for (Observer obs : observers) obs.update(null, this);
    }

}
