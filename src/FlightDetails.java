package stage2;

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

//Represent a flight, tracking details such as passengers, baggage, and flight status. And ensure baggage limits are enforced and manages observer notifications for state changes.
@SuppressWarnings({"serial","deprecation"})
public class FlightDetails implements Subject {
    // Identifier for the flight, typically combining airline code and flight number.
    private String flightCode;
    // IATA or ICAO code of the flight's intended destination airport.
    private String destination;
    // Airline operating the flight, represented by its IATA or ICAO code.
    private String carrier;
    // Maximum number of passengers the flight can accommodate.
    private int maxPassengers;
    // Baggage allowances in terms of dimensions and weight per passenger.
    private float allowedBaggageWeight, allowedBaggageLength, allowedBaggageHeight, allowedBaggageWidth;
    // Calculated from the allowed baggage dimensions, representing the volume limit per passenger.
    private float allowedBaggageVolume;
    // Total capacity limits of the baggage hold, by weight and volume.
    private float maxBaggageWeightCapacity, maxBaggageVolumeCapacity;
    // Accumulated totals of baggage weight and volume checked in so far.
    private float totalBaggageWeight, totalBaggageVolume;
    // Aggregate of excess baggage fees collected.
    private float totalExcessFees;
    // Fee charged for baggage exceeding the allowed limits.
    private float excessFeeCharge;
    // Planned departure time of the flight.
    private float departureTime;
    // Indicates whether boarding is currently allowed.
    private boolean gateOpen;
    // current total number of checked-in passengers.
    private int numberOfPassengers;
    // Observers to be notified of changes in flight status.
    private List<Observer> registeredObservers = new LinkedList<>();

    //Initializes a new Flight instance with specified parameters.
    public FlightDetails(String flightCode, String destination, String carrier, int maxPassengers, float allowedBaggageWeight,
                  float allowedBaggageLength, float allowedBaggageHeight, float allowedBaggageWidth,
                  float excessFeeCharge, int time) {
        this.flightCode = flightCode;
        this.destination = destination;
        this.carrier = carrier;
        this.maxPassengers = maxPassengers;
        // Baggage dimensions and weight are set before calculating volume to ensure accuracy.
        this.allowedBaggageWeight = allowedBaggageWeight;
        this.allowedBaggageLength = allowedBaggageLength;
        this.allowedBaggageHeight = allowedBaggageHeight;
        this.allowedBaggageWidth = allowedBaggageWidth;
        // Volume is derived from the provided dimensions.
        this.allowedBaggageVolume = calculateVolume();
        // Capacity limits are directly related to the number of passengers and individual allowances.
        this.maxBaggageWeightCapacity = maxPassengers * allowedBaggageWeight;
        this.maxBaggageVolumeCapacity = maxPassengers * allowedBaggageVolume;
        // Initializing totals and status indicators.
        this.totalBaggageWeight = 0;
        this.totalBaggageVolume = 0;
        this.totalExcessFees = 0;
        this.excessFeeCharge = excessFeeCharge;
        this.departureTime = time;
        this.gateOpen = true;
    }

    //Calculates the allowed baggage volume based on its dimensions.
    private float calculateVolume() {
        return allowedBaggageLength * allowedBaggageHeight * allowedBaggageWidth;
    }


    //Add passengers and notify observers of the status change.
    public void addPassenger() {
        this.numberOfPassengers += 1;
        notifyObserversOfChange();
    }

    //Check if the boarding gate is open. If the boarding time has passed, close the boarding gate and record it.
    public boolean checkGateOpen(int currentTime, String currentTimeString) {
        if (currentTime >= this.departureTime && this.gateOpen) {
            closeGate();
            logFlightDeparture(currentTimeString);
        }
        return this.gateOpen;
    }

    //Check if the luggage is overweight or oversized. If it exceeds the limit, charge an additional fee.
    public void checkBaggage(float weight, float length, float height, float width) {
        float volume = calculateVolume(length, height, width);
        updateTotalBaggage(weight, volume);
        enforceBaggageLimits(weight, volume);
    }

    //Check the luggage based on its volume, the logic is the same as above.
    public void checkBaggageByVolume(float weight, float volume) {
        updateTotalBaggage(weight, volume);
        enforceBaggageLimits(weight, volume);
    }

    private void notifyObserversOfChange() {
        for (Observer obs : registeredObservers) obs.update(null, this);
    }

    private void closeGate() {
        this.gateOpen = false;
        notifyObserversOfChange();
    }

    private void logFlightDeparture(String timeString) {
        Log.INSTANCE.addMessage(timeString + " Flight " + this.flightCode + " has taken off.");
    }

    private float calculateVolume(float length, float height, float width) {
        return length * height * width;
    }

    private void updateTotalBaggage(float weight, float volume) {
        this.totalBaggageWeight += weight;
        this.totalBaggageVolume += volume;
    }

    private void enforceBaggageLimits(float weight, float volume) {
        if (weight > this.allowedBaggageWeight || volume > this.allowedBaggageVolume) {
            this.totalExcessFees += this.excessFeeCharge;
            throw new OverBaggageLimitException("Baggage limit exceeded.");
        }
    }

    @Override
    public void registerObserver(Observer obs) {
        registeredObservers.add(obs);
    }

    @Override
    public void removeObserver(Observer obs) {
        registeredObservers.remove(obs);
    }

    @Override
    public void notifyObservers() {
        for(Observer obs : registeredObservers) obs.update(null, this);
    }

    // Defines an exception for when baggage exceeds allowed limits, providing a message to indicate the specific issue.
    public class OverBaggageLimitException extends RuntimeException {
        public OverBaggageLimitException(String errorMessage) {
            super(errorMessage);
        }
    }

    // Returns the current status of the gate, indicating whether it is open (true) or closed (false).
    public boolean getGateOpen(){
        return this.gateOpen;
    }

    public String getFlightCode() { return this.flightCode; }
    public String getDestination() { return this.destination; }
    public String getCarrier() { return this.carrier; }
    public int getMaxPassengers() { return this.maxPassengers; }
    public float getTotalBaggageWeight() { return this.totalBaggageWeight; }
    public float getTotalBaggageVolume() { return this.totalBaggageVolume; }
    public float getAllowedBaggageWeight() { return allowedBaggageWeight; }
    public float getAllowedBaggageLength() { return allowedBaggageLength; }
    public float getAllowedBaggageHeight() { return allowedBaggageHeight; }
    public float getAllowedBaggageWidth() { return allowedBaggageWidth; }
    public float getAllowedBaggageVolume() { return allowedBaggageVolume; }
    public float getMaxBaggageWeightCapacity() { return maxBaggageWeightCapacity; }
    public float getMaxBaggageVolumeCapacity() { return maxBaggageVolumeCapacity; }
    public float getTotalExcessFees() { return totalExcessFees; }
    public int getNumberOfPassengers() { return numberOfPassengers; }
    public float getExcessFeeCharge() { return excessFeeCharge; }

    // Calculates and returns the percentage of baggage compartment capacity utilized, considering both weight and volume.
    public float getBaggagePercent() {
        return Math.round(10.0f * Math.max(100f * this.totalBaggageWeight / this.maxBaggageWeightCapacity, 100 * this.totalBaggageVolume / this.maxBaggageVolumeCapacity)) / 10.0f;
    }

    // Calculates and returns the percentage of passenger capacity utilized on the flight.
    public float getPassengerCapacity() {
        return (float) this.numberOfPassengers / this.maxPassengers * 100;
    }

    // Logs a message with the current time. Used for tracking events or changes related to the flight.
    private void logMessage(String message, String currentTimeString){
        Log l = Log.INSTANCE;
        l.addMessage(currentTimeString + " " + message);
    }


}

