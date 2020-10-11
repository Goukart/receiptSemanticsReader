/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package receipt.edeka;

/**
 *
 * @author Benjamin
 */
public class Payment {
    final String currency;
    final String methode;
    final int payValue;
    
    
    Payment(){
        methode = "default";
        payValue = -1;
        currency = "default";
    }
    public Payment(String currency, String methode, int payValue) {
        this.currency = currency;
        this.methode = methode;
        this.payValue = payValue;
    }
    

    public String getCurrency() {
        return currency;
    }

    public String getMethode() {
        return methode;
    }

    public int getPayValue() {
        return payValue;
    }
    
    
}
