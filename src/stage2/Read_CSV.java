package part1;

import java.io.*;
import java.util.Random;

import javax.swing.*;

/**
 * This is a class to read two CSV files and stores details to the hashmap
 * This class also generate some random number for the size and weight of the baggage of each passenger
 * This also check if the path and format are correct of these csv files
 * @author Leo Kong
 */
public class Read_CSV {
    private AllFlightsDetails flights;
    private AllBookingsDetails bookings;
    public Read_CSV() {
        try {
            String sep = File.separator;
            String bookingCSVpath = new File("./data"+sep+"bookingDetails.csv").getAbsolutePath(); //the path of booking details csv
            String flightCSVpath = new File("./data"+sep+"flightDetails.csv").getAbsolutePath(); //the path of flight details csv

            File flightCSV = new File(flightCSVpath); //opeb the csv file
            File bookingCSV = new File(bookingCSVpath); //opeb the csv file
            BufferedReader bookingBR = new BufferedReader(new FileReader(bookingCSV)); //prepare to read the file
            BufferedReader flightBR = new BufferedReader(new FileReader(flightCSV)); //prepare to read the file

            bookings = new AllBookingsDetails(); //hashmap for booking details
            flights = new AllFlightsDetails(); //hashmap for flight details

            String st;
            while ((st = bookingBR.readLine()) != null) { //read the csv file until it reaches the end
                String[] temp = st.split(",", 0); //splite the read line when there is a ","
                Random rand1 = new Random(); //random number 1 for baggage
                Random rand2 = new Random(); //random number 2 for baggage
                Random rand3 = new Random(); //random number 3 for baggage
                Random rand4 = new Random(); //random number 4 for baggage
                float min = 1; //this is the minimum random number of size or weight
                float maxWeight = 60; //this is the maximum random number of wieght
                float maxLH = 150; //this is the maximum random number of height and length
                float maxW = 100;//this is the maximum random number of width
                float ranWeight = rand1.nextFloat() * (maxWeight - min) + min; //the random weight is going to be between 1 and 60
                ranWeight = Math.round(ranWeight*10.0f)/10.0f;
                float ranL = rand2.nextFloat() * (maxLH - min) + min; //the random length is going to be between 1 and 150
                ranL = Math.round(ranL*10.0f)/10.0f; //round the number
                float ranH = rand3.nextFloat() * (maxLH - min) + min; //the random height is going to be between 1 and 150
                ranH = Math.round(ranH*10.0f)/10.0f; //round the number
                float ranW = rand4.nextFloat() * (maxW - min) + min; //the random width is going to be between 1 and 100
                ranW = Math.round(ranW*10.0f)/10.0f; //round the number
                //Booking example = new Booking(bookingCode, firstName, lastName, flightCode, baggageWeight, baggageLength, baggageHeight, baggageWidth)
                BookingDetails bookingDetails = new BookingDetails(temp[0], temp[1], temp[2], temp[3], ranWeight, ranL, ranH, ranW);
                bookings.addBooking(bookingDetails); //add to the hashmap
            }

            while ((st = flightBR.readLine()) != null) { //read the csv file until it reaches the end
                String[] temp = st.split(",", 0); //splite the read line when there is a ","
                for(int i = 5; i<=10;i++){
                    if(Integer.parseInt(temp[i])<0){ //check if there is any negative number
                        throw new NegativeValuesInCSVException("Value in column "+(i+1)+" of flightDetails.csv has a nagative value. Please do not tamper with CSV files.");
                    }
                }
                String[] tempTime = temp[11].split(":"); //split the component again, and this is for the departure time
                int h = Integer.parseInt(tempTime[0]); //change the string to int
                int m = Integer.parseInt(tempTime[1]); //change the string to int
                int time = h*60 + m; //change the time to minute format, eg. 02:00 to 120
                //Flight example = new Flight(flightCode, destination, carrier, maxPassengers, allowedBaggageWeight, allowedBaggageLength, allowedBaggageHeight, allowedBaggageWidth, excessFeeCharge, time)
                Flight flight = new Flight(temp[0], temp[3], temp[1], Integer.parseInt(temp[5]), Float.parseFloat(temp[6]), Float.parseFloat(temp[7]), Float.parseFloat(temp[8]), Float.parseFloat(temp[9]), Float.parseFloat(temp[10]), time);
                flights.addFlight(flight); //add to the hashmap
            }

            bookingBR.close(); //close the file
            flightBR.close(); //close the file
        }
        catch(FileNotFoundException e) { //exeption of csv files cannot be found
            generatePopUp(e);
        }
        catch(NegativeValuesInCSVException e) { //exception of negative numbers found from csv files
            generatePopUp(e);
        }
        catch(IOException e) { //exception of  IOExceptions not handled by previous catch blocks
            System.out.println("IO exception experienced.");
        }
    }

    public class NegativeValuesInCSVException extends RuntimeException { //check if there is any negative number from the csv file
        public NegativeValuesInCSVException(String errorMessage) {
            super(errorMessage);
        }
    }
    public AllBookingsDetails getBookings() { //other class can call this function to get back the hashmap of booking details
        return bookings;
    }

    public AllFlightsDetails getFlights() { //other class can call this function to back the hashmap of get flight details
        return flights;
    }

    public void generatePopUp(Exception e){ //pop up warning message when exception case
        JFrame f = new JFrame();
        String popupMessage = "Unknown exception when reading CSV";
        if(e instanceof FileNotFoundException){
            popupMessage = "Data folder not found, ensure it is in its original directory.";
        }
        if(e instanceof NegativeValuesInCSVException){
            popupMessage = e.getMessage();
        }
        JOptionPane.showMessageDialog(f, popupMessage,
                "Alert", JOptionPane.WARNING_MESSAGE);
        System.exit(404);
    }
}