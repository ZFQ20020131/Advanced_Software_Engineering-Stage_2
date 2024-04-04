# Stage2: Flight Booking Simulation

## Overview

Stage2 is a Java-based simulation for managing flight bookings. The application models the booking process, handling flight data, booking queues, and user interactions through a GUI.

## Directory Structure

- **src/**: Contains all the Java source files organized into packages.
    - **stage2/**: Main package with Java classes for the simulation.
        - `Main.java`: The entry point of the application.
        - `GUI.java`: Manages the graphical user interface for the simulation.
        - `AllFlights.java`, `AllBookings.java`: Handle collections of flights and bookings, respectively.
        - `BookingDetails.java`, `FlightDetails.java`: Represent the details of a booking and a flight.
        - `Counter.java`, `Queue.java`: Model the booking counter and queue.
        - `Read_CSV.java`: Utility for reading CSV files.
        - `Log.java`, `SimulationTime.java`, `Timer.java`: Utilities for logging and timing.
        - `Subject.java`: Implements observer pattern for updates.
- **TestData/**: Contains CSV files used as input for the simulation.
    - `Flight.csv`: Flight data.
    - `Booking.csv`: Booking data.
- **out/**: Directory for output files (e.g., compiled classes, logs).
- **Stage2.jar**: Executable JAR file for the application.

## Getting Started

### Prerequisites

- Java JDK 8 or higher.

### Running the Simulation

1. Compile the Java files in the `src` directory. You can do this from the command line or use an IDE like IntelliJ IDEA or Eclipse.
2. Run the `Main.java` class to start the simulation. This can also be done from the command line or through your IDE:
    ```
    java -classpath out/production/Stage2 stage2.Main
    ```
3. The GUI will launch, allowing you to interact with the simulation.

## Input Data Format

The simulation expects two CSV files in the `TestData` directory:

- **Flight.csv**: Contains flight data. Each record represents a flight and should include details such as flight number, destination, and capacity.
- **Booking.csv**: Contains booking requests. Each record represents a booking and should include details like the booking ID, customer name, and requested flight.

## Contributing

Contributions to the Stage2 project are welcome. Please read `CONTRIBUTING.md` for more information on how to submit pull requests.

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.
