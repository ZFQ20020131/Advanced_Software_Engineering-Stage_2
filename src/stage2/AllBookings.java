package stage2;

import java.util.HashMap;

//Manages booking details using a HashMap.
public class AllBookings {
    private HashMap<String, BookingDetails> bookings;
    private int numOfBookings;


    //Initializes the collection for booking details.
    public AllBookings() {
        bookings = new HashMap<>();
        numOfBookings = 0;
    }


    //Adds a booking to the collection.
    public boolean addBooking(BookingDetails bookingDetails) {
        // Check for blank reference
        if (bookingDetails.getReference().trim().isEmpty()) {
            throw new IllegalStateException("Blank booking reference not allowed.");
        }
        // Check for duplicate reference
        if (bookings.containsKey(bookingDetails.getReference())) {
            throw new IllegalStateException("Duplicate booking reference.");
        }
        bookings.put(bookingDetails.getReference(), bookingDetails);
        numOfBookings++;
        return true;
    }

    //Retrieves a booking by reference code.
    public BookingDetails getBooking(String reference) {
        if (reference.trim().isEmpty()) {
            throw new IllegalArgumentException("Reference code cannot be empty.");
        }
        return bookings.get(reference);
    }

    //Returns the total number of bookings.
    public int getnumofBookings() {
        return numOfBookings;
    }

    //Provides all booking details.
    public HashMap<String, BookingDetails> getAllBookings() {
        return new HashMap<>(bookings);
    }
}



