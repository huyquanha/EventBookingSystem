/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventbooking;

import java.util.ArrayList;
/**
 *
 * @author ASUS
 */
public class User {
    protected String name, email, phoneNum,address;
    protected int id;
    
    public User (int id, String name, String email, String phoneNum, String address) {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phoneNum=phoneNum;
        this.address=address;
    }
    
    public int getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String newName) {
        name=newName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String newEmail) {
        email=newEmail;
    }
    
    public String getPhoneNumber() {
        return phoneNum;
    }
    
    public void setPhoneNumber(String newPhoneNum) {
        phoneNum=newPhoneNum;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String newAddress) {
        address=newAddress;
    }
    
    public String getDetails() {
        return id+" "+name+" "+email+" "+phoneNum+" "+address;
    }
}
