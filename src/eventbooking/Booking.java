/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventbooking;
/**
 *
 * @author ASUS
 */
public class Booking {
    private int bookerID, eventID, numberOfPlaces;
    
    public Booking (int bookerID, int eventID, int numberOfPlaces) {
        this.bookerID=bookerID;
        this.eventID=eventID;
        this.numberOfPlaces=numberOfPlaces;
    }
    
    public int getBookerID() {
        return bookerID;
    }
    
    public int getEventID() {
        return eventID;
    }
    
    public int getNumberBooked() {
        return numberOfPlaces;
    }
    
    public void changeNumber (int newNumber) {
        numberOfPlaces=newNumber;
    }
}
