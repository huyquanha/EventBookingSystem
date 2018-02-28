/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ASUS
 */
package eventbooking;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Event {
   
    private static int EVENT_COUNTER=0;
    private int eventID, creatorID;
    private String name, description, place; //,creatorID
    private int capacity, currentSize;
    private double price;
    private String promoCode;
    private Date eDate;
    
    public Event (int creatorID, String name, String description, Date date, String place, int capacity, int currentSize, String promoCode, double price) {
        eventID=++EVENT_COUNTER;
        this.creatorID=creatorID;
        this.name=name;
        this.description=description;
        this.place=place;
        this.capacity=capacity;
        this.currentSize=currentSize;
        this.promoCode=promoCode;
        this.price=price;
        eDate=date;
    }
    
    public int getEventID() {
        return eventID;
    }
    
    public int getCreatorID() {
        return creatorID;
    }
    
    public void setCreatorID(int newCreatorID) {
        creatorID=newCreatorID;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String newName) {
        name=newName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String newDescription) {
        description=newDescription;
    }
    
    public String getPlace() {
        return place;
    }
    
    public void setPlace(String newVenue) {
        place=newVenue;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public boolean setCapacity(int newCapacity) {
        if (newCapacity >= currentSize) {
            return true;
        }
        return false;
    }
    
    public int getCurrentSize() {
        return currentSize;
    }
    
    public boolean increSize(int amount) {
        if (currentSize+amount<=capacity) {
            currentSize+=amount;
            return true;
        }
        return false;
    }
    
    public boolean decreSize(int amount) {
        if (amount <=currentSize) {
            currentSize-=amount;
            return true;
        }
        return false;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double newPrice) {
        price=newPrice;
    }
    
    public Date getEventDate() {
        return eDate;
    }
    
    public void setEventDate (Date newDate) {
        eDate=newDate;
    }
    
    public String getDetails() {
        return eventID+" "+creatorID+" "+name+", "+description+" "+place+" "+eDate.toString()+" "+capacity+" "+currentSize+" "+promoCode+" "+price;
    }
    
    public static void increEventCounter()
    {
        EVENT_COUNTER++;
    }
    
    public static void decreEventCounter()
    {
        EVENT_COUNTER--;
    }
}
