package stage2;

//Stores and manages details for an individual booking.
public class BookingDetails {
    // Passenger identification
    private String bookingCode; // Unique booking reference
    private String firstName; // Passenger's first name
    private String lastName; // Passenger's last name

    // Flight and baggage details
    private String flightCode; // Associated flight reference
    private float baggageWeight; // Weight of passenger's baggage
    private float baggageLength; // Length of baggage
    private float baggageHeight; // Height of baggage
    private float baggageWidth; // Width of baggage

    // Status flags
    private boolean checkInStatus; // Indicates if passenger has checked in
    private boolean missedFlight; // Indicates if passenger has missed the flight
    private float excessFeeCharged = 0; // The excess baggage fee charged, if any

    //Initializes booking with passenger and flight details.
    public BookingDetails(String bookingCode, String firstName, String lastName, String flightCode,
                          float baggageWeight, float baggageLength, float baggageHeight, float baggageWidth) {
        this.bookingCode = bookingCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.flightCode = flightCode;
        this.baggageWeight = baggageWeight;
        this.baggageLength = baggageLength;
        this.baggageHeight = baggageHeight;
        this.baggageWidth = baggageWidth;
        this.checkInStatus = false;
        this.missedFlight = false;
    }

    // Getter methods for accessing booking details
    public String getReference() { return bookingCode; }
    public String getFlightCode() { return flightCode; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public float getBaggageWeight() { return baggageWeight; }
    public float getBaggageLength() { return baggageLength; }
    public float getBaggageHeight() { return baggageHeight; }
    public float getBaggageWidth() { return baggageWidth; }
    public boolean getCheckInStatus() { return checkInStatus; }
    public Float getExcessFeeCharged() { return excessFeeCharged; }
    public boolean getMissedFlight() { return missedFlight; }

    // Setter methods for updating booking details
    public void setCheckInStatus(boolean status) { this.checkInStatus = status; }

    //Marks the booking to indicate the flight has been missed.
    public void missFlight() { this.missedFlight = true; }

    //Updates the excess fee charged for baggage, if applicable.
    public void setExcessFeeCharged(Float fee) { this.excessFeeCharged = fee; }

    //@deprecated Use specific baggage setters instead.
    @Deprecated
    public void setBaggageInfo(float w, float v) {
        // Method retained for backward compatibility; prefer individual setters.
    }
}



