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
public class Creator extends User {
    
    private String password;
    
    public Creator (int id, String name, String email, String phone, String address, String password) {
        super (id, name, email, phone, address);
        this.id=id;
        this.password=password;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String newPassword) {
        password=newPassword;
    }
    
}
