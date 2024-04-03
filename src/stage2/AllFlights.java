package stage2;

import java.util.HashMap;

//Manages a collection of flight objects, enabling the addition and retrieval of flights by their codes.
public class AllFlights {
    private int numOfFlights;
    private HashMap<String, FlightDetails> flights;

    //Initializes the storage for flight details.
    public AllFlights() {
        numOfFlights = 0;
        flights = new HashMap<>();
    }

    //Adds a flight to the collection, identified by its flight code.
    public boolean addFlight(FlightDetails flight) {
        String flightCode = flight.getFlightCode().trim();
        if (flightCode.isEmpty()) {
            throw new IllegalStateException("Flight code cannot be empty.");
        }
        if (flights.containsKey(flightCode)) {
            throw new IllegalStateException("Flight with the same code already exists.");
        }
        flights.put(flightCode, flight);
        numOfFlights++;
        return true;
    }

    //Retrieves a flight by its code.
    public FlightDetails getFlight(String code) {
        if (code.trim().isEmpty()) {
            throw new IllegalStateException("Flight code cannot be blank.");
        }
        return flights.get(code);
    }

    //Gets the total number of flights in the collection.
    public int getTotalFlights() {
        return numOfFlights;
    }

    //Provides access to the entire collection of flights.
    public HashMap<String, FlightDetails> getAllFlights() {
        return new HashMap<>(flights);
    }
}

