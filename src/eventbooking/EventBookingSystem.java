/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eventbooking;

import java.util.ArrayList;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
/**
 *
 * @author ASUS
 */
public class EventBookingSystem {
    private static SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    
    private ArrayList<Event> events;
    private ArrayList<User> users;
    private HashMap<Integer,String> creators;
    private ArrayList<Booking> bookings;
    
    private Connection connect = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet=null;
    
    public void loadDatabase() throws Exception{ //load data from mysql database to arraylist and hashmap in the program
        try {
            //Class.forName("com.mysql.jdbc.Driver"); //load the Driver class for the database. May be omitted
            connect = DriverManager.getConnection("jdbc:mysql://localhost/eventbooking?autoReconnect=true&useSSL=false","sqluser","sqluserpw"); //set up connection with DB
            for (int i=0; i< 4;i++) {
                String statement="select * from eventbooking.";
                switch(i) {
                    case 0:
                        statement+="event";
                        break;
                    case 1:
                        statement+="user";
                        break;
                    case 2:
                        statement+="creator";
                        break;
                    case 3:
                        statement+="booking";
                        break;
                }
                preparedStatement=connect.prepareStatement(statement);
                resultSet=preparedStatement.executeQuery();
                writeResultSet(resultSet, i);
            }

        }
        catch (Exception e) {
            throw e;
        }
        close();
    }
    
    
    public boolean createEvent(int creatorID, String name,String description, Date eDate, String place, int capacity, String promoCode, double price) throws SQLException {
        if (creators.containsKey(creatorID)) { //this id is really a creator
            for (Event event : events) {
                if (event.getCreatorID()==creatorID && event.getName().equals(name)) { //the event already exists
                    return false;
                }
            }
            Event newEvent = new Event (creatorID, name, description, eDate, place, capacity, 0, promoCode, price);
            events.add(newEvent);
            Event.increEventCounter();
            
            preparedStatement=connect.prepareStatement("insert into eventbooking.event values (default,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1,creatorID);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,description);
            preparedStatement.setDate(4,(java.sql.Date) eDate);
            preparedStatement.setString(5,place);
            preparedStatement.setInt(6,capacity);
            preparedStatement.setInt(7,0);
            preparedStatement.setString(8,promoCode);
            preparedStatement.setDouble(9,price);
            
            preparedStatement.executeUpdate();
            return true;
        }
        return false; //no such creator exists =>false
    }
    
    public boolean changeEventName(int creatorID, int eventID, String newName) throws SQLException { //change the event name
        Event event = getEvent(eventID);
        if (event!=null) { //the event exists
            if (event.getCreatorID()==creatorID) {
                event.setName(newName);
                preparedStatement=connect.prepareStatement("update eventbooking.event set name=? where eventID=?");
                preparedStatement.setString(0,newName);
                preparedStatement.setInt(1,eventID);
                preparedStatement.executeUpdate();
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }
    
    public boolean setCapacity (int creatorID, int eventID, int newCapacity) throws SQLException { //new Capacity has to be >= currentSize
        Event event = getEvent(eventID);
        if (event != null) {
            if (event.getCreatorID()==creatorID) {
                boolean ok = event.setCapacity(newCapacity);
                if (ok) {
                    preparedStatement =connect.prepareStatement("update event set capacity =? where eventID=?");
                    preparedStatement.setInt(0, newCapacity);
                    preparedStatement.setInt(1,eventID);
                    preparedStatement.executeUpdate();
                }
                return ok;
            }
            return false;
        }
        return false;
    }
    
    public boolean deleteEvent(int creatorID, int eventID) throws SQLException {
        Event event = getEvent(eventID);
        if (event!=null) {
            if (event.getCreatorID()==creatorID) {
                boolean ok = events.remove(event);
                if (ok) {
                    preparedStatement = connect.prepareStatement("delete from eventbooking.event where eventID=?");
                    preparedStatement.setInt(0, eventID);
                    preparedStatement.executeUpdate();
                    Event.decreEventCounter();
                }
                return ok;
            }
            return false;
        }
        return false;
    }
    
    public boolean bookEvent(int eventID, int userID, int numberBooked) throws SQLException { //add new booking if not exceed capacity, update event size
        if (checkBooking(eventID, userID) ==null) { //this eventID and userID pair does not exist
            Event event = getEvent(eventID);
            if (getUser(userID) != null && event !=null) { //ensures user and event exists
                boolean booked = event.increSize(numberBooked);
                if (booked) {
                    updateEventSize(event);
                    Booking newBooking = new Booking (eventID, userID, numberBooked);
                    bookings.add(newBooking);
                    preparedStatement = connect.prepareStatement("insert into eventbooking.booking values (?,?,?)");
                    preparedStatement.setInt(0,userID);
                    preparedStatement.setInt(1,eventID);
                    preparedStatement.setInt(2,numberBooked);
                    preparedStatement.executeUpdate();
                }
                return booked;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    public boolean modifyBooking (int eventID, int userID, int newNumber) throws SQLException { //change numberBooked if able (not exceed capacity), update event size
        Booking booking = checkBooking(eventID,userID);
        if (booking != null) { //this pair exists already
            Event event = getEvent(eventID);
            event.decreSize(booking.getNumberBooked());
            boolean ok = event.increSize(newNumber);
            if (ok) {
                updateEventSize(event);
                booking.changeNumber(newNumber);
                preparedStatement=connect.prepareStatement("update booking set numberOfPlaces=? where bookerID=? and eventID=?");
                preparedStatement.setInt(0,newNumber);
                preparedStatement.setInt(1,userID);
                preparedStatement.setInt(2,eventID);
                preparedStatement.executeUpdate();
            }
            return ok;
        }
        else {
            return false;
        }
    }
    
    public boolean deleteBooking(int eventID,int userID) throws SQLException { //delete a booking and update event size
        Booking booking = checkBooking(eventID,userID);
        if (booking !=null) { //booking exists
            Event event = getEvent(eventID);
            boolean ok = event.decreSize(booking.getNumberBooked());
            if (ok) {
                updateEventSize(event);
                bookings.remove(booking);
                preparedStatement = connect.prepareStatement("delete from booking where bookerID=? and eventID=?");
                preparedStatement.setInt(0, userID);
                preparedStatement.setInt(1,eventID);
                preparedStatement.executeUpdate();
            }
            return ok;
        }
        return false;
    }
    
    public void close() { //close the resultSet and the connection with database
        try {
            if (resultSet!=null) {
                resultSet.close();
            }
            if (connect != null) {
                connect.close();
            }
        }
        catch (Exception e) {
            
        }
    }
    
    public String[] getEventDetails() {
        String[] eventDetails;
        if (!events.isEmpty()) {
            eventDetails = new String[events.size()];
            int i=0;
            for (Event event : events) {
                eventDetails[i]=event.getDetails();
                i++;
            }
        }
        else {
            eventDetails = new String[1];
            eventDetails[0]="";
        }
        return eventDetails;
    }
    
    /*PRIVATE METHODS */
    
    private void updateEventSize(Event event) throws SQLException { //change currentSize of an event
        preparedStatement = connect.prepareStatement("update event set currentSize=? where eventID=?");
        preparedStatement.setInt(0,event.getCurrentSize());
        preparedStatement.setInt(1,event.getEventID());
        preparedStatement.executeUpdate();
    }
    
    private void writeResultSet(ResultSet resultSet, int i) throws SQLException { //called by loadDatabase, switching between cases
        switch(i) {
            case 0:
                loadEvents(resultSet);
                break;
            case 1:
                loadUsers(resultSet);
                break;
            case 2:
                loadCreators(resultSet);
                break;
            case 3:
                loadBookings(resultSet);
                break;
        }
    }
    
    private void loadEvents (ResultSet resultSet) throws SQLException { //load events from mysql database to arraylist of events
        while (resultSet.next()) {
            int creatorID=resultSet.getInt("creatorID");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            Date date = resultSet.getDate("eDate");
            String place = resultSet.getString("place");
            int capacity = resultSet.getInt("capacity");
            int currentSize=resultSet.getInt("currentSize");
            String promoCode = resultSet.getString("promoCode");
            double price = resultSet.getDouble("price");
            Event nextEvent = new Event(creatorID,name,description,date,place,capacity,currentSize,promoCode,price);
            events.add(nextEvent);
        }
    }
    
    private void loadUsers(ResultSet resultSet) throws SQLException { //load Users from mysql database to arraylist of users
        while (resultSet.next()) {
            int userID = resultSet.getInt("userID");
            String name = resultSet.getString("name");
            String email= resultSet.getString("email");
            String phone = resultSet.getString("phone");
            String address = resultSet.getString("address");
            User nextUser = new User(userID,name,email,phone,address);
            users.add(nextUser);
        }
    }
    
    /*
        load creators from mysql database to hashmap of <creatorID, password>
        since creator is actually just a user, so we only need its ID. Other data is stored in users already.
    */
    private void loadCreators(ResultSet resultSet) throws SQLException { 
        while (resultSet.next()) {
            int creatorID = resultSet.getInt("creatorID");
            String password = resultSet.getString("password");
            creators.put(creatorID,password);
        }
    }
    
    private void loadBookings(ResultSet resultSet) throws SQLException { //load all bookings from database to arraylist of bookings
        while (resultSet.next()) {
            int bookerID = resultSet.getInt("bookerID");
            int eventID = resultSet.getInt("eventID");
            int numberOfPlaces = resultSet.getInt("numberOfPlaces");
            Booking nextBooking = new Booking (bookerID,eventID,numberOfPlaces);
            bookings.add(nextBooking);
        }
    }
    
    private Booking checkBooking(int eventID, int bookerID) { //check if a booking with this eventID and booker ID already exists
        for (Booking booking : bookings) {
            if (booking.getEventID()==eventID && booking.getBookerID()==bookerID) { //booking already exists
                return booking;
            }
        }
        return null;
    }
  
    private Event getEvent(int eventID) { //get the event corresponding to eventID
        for (Event event: events) {
            if(event.getEventID()==eventID) {
                return event;
            }
        }
        return null;
    }
    
    private User getUser(int userID) { //get the user corresponding to userID
        for (User user:users) {
            if (user.getID()==userID) {
                return user;
            }
        }
        return null;
    }
}
