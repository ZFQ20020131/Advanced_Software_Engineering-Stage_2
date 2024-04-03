package stage2;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GUI implements Observer, ChangeListener {
    class PassengerComponent extends JPanel {
        /**
         * Constructs a panel to display booking details for a passenger.
         * This panel displays the flight code, full name of the passenger,
         * baggage weight, and dimensions of the baggage.
         * If the passenger has missed their flight, the flight code is highlighted in red,
         * and a tooltip is shown indicating the missed flight status.
         *
         * @param currentBookingDetails The booking details for the current passenger.
         */
        public PassengerComponent(BookingDetails currentBookingDetails) {
            // Set an empty border
            this.setBorder(createBorder(""));
            // Use a GridLayout with 4 columns
            this.setLayout(new GridLayout(0, 4));
            // Create and add components for displaying booking details
            JLabel flightText = new JLabel(currentBookingDetails.getFlightCode(), SwingConstants.CENTER);
            this.add(flightText);
            JLabel nameText = new JLabel(currentBookingDetails.getFullName(), SwingConstants.CENTER);
            this.add(nameText);
            JLabel weightText = new JLabel(currentBookingDetails.getBaggageWeight() + "kg", SwingConstants.CENTER);
            this.add(weightText);
            JLabel sizeText = new JLabel("L:" + currentBookingDetails.getBaggageLength() + " W:" + currentBookingDetails.getBaggageWidth() + " H:" + currentBookingDetails.getBaggageHeight(), SwingConstants.CENTER);
            this.add(sizeText);
            // Ensure this component doesn't grow beyond its preferred size
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
            // Set a tooltip with the passenger's name and booking reference
            this.setToolTipText("<html>" + "Name: " + currentBookingDetails.getFullName() + "<br>" + "Booking Reference: " + currentBookingDetails.getReference().toUpperCase() + "</html>");
            if (currentBookingDetails.getMissedFlight()) {
                // Highlight in red and show missed flight status if applicable
                flightText.setForeground(Color.red);
                this.setToolTipText("<html>" + "Name: " + currentBookingDetails.getFullName() + "<br>" + "Booking Reference: " + currentBookingDetails.getReference().toUpperCase() + "<br>" + "MISSED FLIGHT" + "</html>");
                this.setEnabled(false);
            }
        }
    }


    class DeskComponent extends JPanel {
        JLabel bagDetails; // Label to display the details of baggage being processed.
        JLabel feeDetails; // Label to display any extra fees associated with the baggage.
        Counter counter; // Reference to the CheckinCounter object this desk component represents.

        /**
         * Constructor for the DeskComponent class. It sets up the UI for a check-in desk, including
         * labels for baggage details and fees, and a button to open/close the desk.
         *
         * @param deskNumber The unique number assigned to this check-in desk.
         * @param counter    The CheckinCounter object that provides information and control for this desk.
         */
        public DeskComponent(int deskNumber, Counter counter) {
            this.counter = counter;
            // Set a border with a title indicating the desk number.
            this.setBorder(createBorder("Desk " + deskNumber));
            // Use a vertical BoxLayout to stack components.
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            // Initialize labels with placeholder text.
            bagDetails = new JLabel("waiting for details");
            this.add(bagDetails); // Add the baggage details label to the panel.

            feeDetails = new JLabel("waiting for details");
            this.add(feeDetails); // Add the fees details label to the panel.

            // Create a button to toggle the open/close status of the desk.
            JButton counterButton = new JButton("Close counter");
            this.add(counterButton);
            // Add action listener to the button to handle clicks.
            counterButton.addActionListener(new ActionListener() {
                @Override
                public synchronized void actionPerformed(ActionEvent e) {
                    // Toggle the open/close status of the counter.
                    counter.toggleCounter();
                    // Update the button text based on the new status of the counter.
                    if(counter.getIsOpen()) {
                        counterButton.setText("Close Counter");
                    } else {
                        // Call the method to update UI elements when the counter is closed.
                        closeCounter();
                        counterButton.setText("Open Counter");
                    }
                }
            });
        }

        /**
         * Updates the display of the desk component with the current booking's details.
         * This method is called to show the passenger's name, the weight of their baggage, and
         * any excess baggage fees that might be applicable. If there are no current bookings
         * (i.e., currentBookingDetails is null), it updates the display to indicate that no
         * customer is currently being served at this desk.
         *
         * @param currentBookingDetails The details of the current booking to be displayed.
         *                              If null, it means there is no passenger currently being
         *                              served at this desk.
         */
        public void setcontents(BookingDetails currentBookingDetails) {
            // Re-enable the desk in case it was previously disabled. This is necessary to ensure
            // the desk can show current booking details after it has been re-opened.
            this.setEnabled(true);

            // Check if there are booking details to display.
            if(currentBookingDetails != null) {
                // Construct and display a message with the passenger's name and the weight of their baggage.
                // This gives a quick overview of the passenger being served and their baggage details.
                bagDetails.setText(currentBookingDetails.getFullName() + " is dropping off 1 bag of " + currentBookingDetails.getBaggageWeight() + "kg");

                // Check if there's any excess baggage fee charged and construct an appropriate message.
                // The message varies depending on whether an extra fee is due.
                Float bagFee = currentBookingDetails.getExcessFeeCharged();
                String feeText = (bagFee == 0) ? "No baggage fee is due" : "A baggage fee of \u00a3" + bagFee + " is due";
                feeDetails.setText(feeText); // Display the constructed fee message.
            } else {
                // If there are no current booking details, update the labels to indicate the desk is not
                // currently serving any passengers.
                bagDetails.setText("Currently not serving a customer");
                feeDetails.setText(" "); // Clear any previous fee details shown.
            }
        }

        /**
         * This method is invoked to mark the desk as closed. It performs two primary functions:
         * firstly, it disables the entire desk component, visually indicating that the counter is no longer
         * active or available for processing passengers. This is achieved by setting the component's enabled
         * state to false, which may also change its appearance to a "grayed out" look, depending on the UI theme.
         *
         * Secondly, it updates the text displayed on the desk's labels. The bag details label is set to
         * "Counter Closed" to explicitly inform users that the desk is not in operation. Additionally, the fee
         * details label is cleared, removing any previously displayed information about baggage fees. This ensures
         * that the desk's display is appropriately reset, reflecting its closed status and removing any irrelevant
         * or outdated information.
         */
        public void closeCounter() {
            this.setEnabled(false);//close desk and gray gui element
            bagDetails.setText("Counter Closed");
            feeDetails.setText(" ");
        }
    }

    class FlightComponent extends JPanel {
        JLabel checkedIn;
        JLabel holdPercent;
        public FlightComponent(FlightDetails currentFlight) {
            // Sets a border with the flight's code for identification and uses a vertical BoxLayout.
            // Initializes labels for displaying 'checked in passengers' and 'hold capacity percent' with placeholder text.
            // Calls setcontents method to fill in the actual flight details into the component.
            // Sets a tooltip providing a detailed summary of the flight, including total fees collected.
            this.setBorder(createBorder(currentFlight.getFlightCode())); // set border
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS)); // set layout
            checkedIn = new JLabel("waiting for details"); //create placeholder label and add to component
            this.add(checkedIn);
            holdPercent = new JLabel("waiting for details");//create placeholder label and add to component
            this.add(holdPercent);
            this.setcontents(currentFlight);//populate labels
            this.setToolTipText("<html>" + "Flight: " + currentFlight.getFlightCode() +"<br>" + "Carrier: " + currentFlight.getCarrier() +"<br>" + "Destination: " + currentFlight.getDestination() +"<br>"+"Total fees collected: "+currentFlight.getTotalExcessFees()+ "</html>");// set text for cursor hover
        }
        /**
         * Updates the labels within this component with specific details from the provided flight object.
         * Displays the current number of checked-in passengers against the flight's maximum capacity and
         * the current baggage hold utilization. If the flight gate is closed (indicating departure),
         * updates the component's border to reflect the flight's departure and disables the component.
         *
         * @param currentFlight The flight object from which to pull the latest information.
         */
        public void setcontents(FlightDetails currentFlight) {
            checkedIn.setText(currentFlight.getNumberOfPassengers() + " checked in of " + currentFlight.getMaxPassengers());//set text for passenger info
            holdPercent.setText("Hold is " + currentFlight.getBaggagePercent() + "% full");//set text for luggage info
            this.setToolTipText("<html>" + "Flight: " + currentFlight.getFlightCode() +"<br>" + "Carrier: " + currentFlight.getCarrier() +"<br>" + "Destination: " + currentFlight.getDestination() +"<br>"+"Total fees collected: \u00a3"+currentFlight.getTotalExcessFees()+ "</html>");// set text for cursor hover
            if(!currentFlight.getGateOpen()){
                this.setBorder(createBorder(currentFlight.getFlightCode() + " DEPARTED")); // set border text when flight has departed
                this.setEnabled(false);//disable component
            }
        }
    }
    static JFrame frame;
    private JPanel queueContentPanel;
    private JPanel desksContentPanel;
    private JPanel flightsContentPanel;
    private JLabel clock;
    private SimulationTime t;
    private DeskComponent[] allDeskComponents;
    private HashMap<String,FlightComponent> allFlightComponents;

    public GUI(SimulationTime t, List<Counter> allCounters, HashMap<String,FlightDetails> allFlights) {
        ToolTipManager.sharedInstance().setInitialDelay(0); // tooltips show immediately
        this.t = t;
        // create frame
        JFrame checkFrame = new JFrame("Management");
        checkFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        checkFrame.setSize(1400, 800); // Adjusted height for better layout

        // Using JTabbedPane for better organization
        JTabbedPane tabbedPane = new JTabbedPane();

        // Queue Panel
        queueContentPanel = new JPanel();
        queueContentPanel.setLayout(new BoxLayout(queueContentPanel, BoxLayout.Y_AXIS));
        JScrollPane queueScrollPane = new JScrollPane(queueContentPanel);
        queueScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Queue Patrons", queueScrollPane);

        // Desks Panel
        desksContentPanel = new JPanel();
        desksContentPanel.setLayout(new BoxLayout(desksContentPanel, BoxLayout.Y_AXIS));
        JScrollPane desksScrollPane = new JScrollPane(desksContentPanel);
        desksScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Check In Desks", desksScrollPane);
        allDeskComponents = new DeskComponent[allCounters.size()];
        int deskIndex = 0;
        for(Counter counter : allCounters) {
            allDeskComponents[deskIndex] = new DeskComponent(counter.getCounterNumber(), counter);
            desksContentPanel.add(allDeskComponents[deskIndex]);
            deskIndex++;
        }

        // Flights Panel
        flightsContentPanel = new JPanel();
        flightsContentPanel.setLayout(new BoxLayout(flightsContentPanel, BoxLayout.Y_AXIS));
        JScrollPane flightsScrollPane = new JScrollPane(flightsContentPanel);
        flightsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Flights", flightsScrollPane);
        allFlightComponents = new HashMap<>();
        allFlights.forEach((key, value) -> {
            FlightComponent fc = new FlightComponent(value);
            allFlightComponents.put(key, fc);
            flightsContentPanel.add(fc);
        });

        // Controls Panel
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        setupControlButtons(playButton, pauseButton);

        JSlider speedSlider = setupSpeedSlider();

        clock = new JLabel("[00:00]");
        clock.setFont(new Font("Serif", Font.BOLD, 24));

        controlsPanel.add(playButton);
        controlsPanel.add(pauseButton);
        controlsPanel.add(speedSlider);
        controlsPanel.add(clock);

        // Adding tabbedPane and controlsPanel to the main frame
        checkFrame.add(tabbedPane, BorderLayout.CENTER);
        checkFrame.add(controlsPanel, BorderLayout.SOUTH);

        checkFrame.setVisible(true);
        checkFrame.setLocationRelativeTo(null);
    }
    /**
     * Configures the play and pause control buttons for the simulation.
     * This method sets up action listeners for both buttons to control
     * the simulation's running state. The play button resumes the simulation,
     * while the pause button pauses it. The method also ensures that only
     * one button is enabled at any time to reflect the current state of the simulation.
     *
     * @param playButton  The button used to resume the simulation.
     * @param pauseButton The button used to pause the simulation.
     */
    private void setupControlButtons(JButton playButton, JButton pauseButton) {
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        playButton.addActionListener(e -> {
            t.resume();
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
        });

        pauseButton.addActionListener(e -> {
            t.pause();
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });
    }
    /**
     * Initializes and configures a slider component to control the simulation speed.
     * The slider allows the user to adjust the speed of the simulation through predefined
     * settings, ranging from 1x to 8x speed. Each position on the slider corresponds to a
     * specific speed multiplier, with tick marks indicating discrete steps between these
     * multipliers. The method also sets up a label table to visually denote each tick mark
     * with its corresponding speed multiplier value. Additionally, it registers the slider
     * with a change listener to handle changes in slider position, enabling dynamic adjustment
     * of the simulation's speed based on user input.
     *
     * @return A JSlider component configured for controlling simulation speed.
     */
    private JSlider setupSpeedSlider() {
        JSlider speedSlider = new JSlider(0, 3, 0);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("1x"));
        labelTable.put(1, new JLabel("2x"));
        labelTable.put(2, new JLabel("4x"));
        labelTable.put(3, new JLabel("8x"));
        speedSlider.setLabelTable(labelTable);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(this);

        return speedSlider;
    }
    /**
     * Creates a titled border with specified text.
     * This method generates a border that is used throughout the GUI components to visually
     * differentiate various sections or panels. The border includes a title, which is centered
     * at the top of the border. This helps in providing a clear, labeled division for UI elements.
     *
     * @param borderText The text to display on the border title.
     * @return A TitledBorder object with the specified title and a black line border.
     */
    TitledBorder createBorder(String borderText) {
        TitledBorder border;
        border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), borderText);//create the titled border
        border.setTitleJustification(TitledBorder.CENTER);//centre title within label
        border.setTitlePosition(TitledBorder.DEFAULT_POSITION);//default position
        return border;
    }
    /**
     * Responds to updates from observed objects. This method is called whenever an observed
     * object is changed. It checks the type of the observable object and the argument passed
     * to determine the appropriate action, such as updating queue display, check-in counter
     * status, flight information, or the simulation clock.
     *
     * @param o   The observable object.
     * @param arg An argument passed by the notifyObservers method.
     */
    @Override
    public void update(Observable o, Object arg) {//run specific method depending on object type, if unrecognised then do nothing
        if(o instanceof Queue) updateQueue(o, arg);
        else if(arg instanceof Counter) updateCounter(arg);
        else if(arg instanceof FlightDetails) updateFlight(arg);
        else if(arg instanceof Timer) updateClock(arg);
    }
    /**
     * Handles changes in the state of the speed slider component. This method is invoked whenever
     * the slider's value is changed, allowing for dynamic adjustment of the simulation speed.
     * The method first checks to ensure that the slider adjustment has stopped (to prevent
     * continuous adjustments while the slider is being moved). It then reads the current position
     * of the slider and maps it to a predefined simulation speed setting. Finally, it updates the
     * simulation speed by calling the `adjustSpeed` method on the `SimTime` instance with the
     * new speed setting.
     *
     * @param e The event object representing the change event.
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {//wait for slider to stop moving
            int sliderPos = (int)source.getValue();
            int simSpeed = -1;
            if (sliderPos == 0) simSpeed = 1;//set simspeed depending on slider position
            else if (sliderPos == 1) simSpeed = 2;
            else if (sliderPos == 2) simSpeed = 4;
            else if (sliderPos == 3) simSpeed = 8;
            t.adjustSpeed(simSpeed);
        }
    }
    /**
     * Updates the queue display with new or removed bookings.
     * This method is responsible for adding a visual representation of a passenger (BookingDetails) to the queue
     * when a new booking is made, or removing the first passenger in line when they are processed. It ensures
     * that the queue display in the UI accurately reflects the current state of the passenger queue.
     *
     * @param o   The observable object, expected to be an instance of PassengerQueue.
     * @param arg The argument passed by the notifyObservers method, expected to be a BookingDetails object or null.
     */
    private void updateQueue(Observable o, Object arg) {
        if(arg!=null) queueContentPanel.add(new PassengerComponent((BookingDetails) arg));
        else queueContentPanel.remove(0);
        queueContentPanel.revalidate(); //update JPanel contents
    }
    /**
     * Updates the specified check-in counter display with the booking it's currently processing.
     * If the counter is open and has a booking, it displays the booking details. If the counter is closed,
     * it updates the display to indicate that the counter is closed.
     *
     * @param arg Expected to be a CheckinCounter object containing the current state and booking details.
     */
    private void updateCounter(Object arg) {
        Counter checkinCounter = (Counter)arg;//cast to CheckinCounter object
        if (checkinCounter.getIsOpen()){
            allDeskComponents[checkinCounter.getCounterNumber()-1].setcontents(checkinCounter.getBooking());//set countents of component
        }
        else {allDeskComponents[checkinCounter.getCounterNumber()-1].closeCounter();}//close counter
    }

    /**
     * Updates the flight component display with current flight information.
     * This includes updating the number of checked-in passengers and the baggage hold usage for the specified flight.
     *
     * @param arg Expected to be a Flight object containing current flight details to display.
     */
    private void updateFlight(Object arg) {
        FlightDetails flight = (FlightDetails)arg;//cast to Flight object
        allFlightComponents.get(flight.getFlightCode()).setcontents(flight);//set contents of flight component
    }
    /**
     * Updates the simulation clock display.
     * This method adjusts the clock in the UI to reflect the current simulation time, ensuring that users
     * can keep track of the simulation's progress.
     *
     * @param arg Expected to be a Timer object containing the current simulation time.
     */
    public void updateClock(Object arg){
        Timer timer = (Timer) arg;//cast to Timer object
        this.clock.setText(timer.getTimeString());//set clock text
    }
}
