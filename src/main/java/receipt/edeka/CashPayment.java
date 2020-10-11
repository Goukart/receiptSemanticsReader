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
public class CashPayment extends Payment {
    final int change;

    public CashPayment() {
        this.change = -1;
    }
    public CashPayment(String currency, String methode, int payValue, int change) {
        super(currency, methode, payValue);
        this.change = change;
    }
    
    
    public int getChange() {
        return change;
    } 
}
