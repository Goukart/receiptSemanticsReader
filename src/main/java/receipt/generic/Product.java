package receipt.generic;

import java.io.Serializable;

public class Product implements Serializable {
    //------------- Class Head
    protected final String productName;
    protected final Integer price;
    protected final char taxRate;
    protected final int quantity;
    protected final String currency;

    public Product(String name, Integer price, char taxRate, int quantity, String currency){
        productName = name;
        this.price = price;
        this.taxRate = taxRate;
        this.quantity = quantity;
        this.currency = currency;
    }
    public Product(String name, Integer price, char taxRate, String currency){
        this(name, price, taxRate, 1, currency);
    }


    //------------- Getter
    public String getProductName() {
        return productName;
    }

    private Integer getPrice() {
        return price;
    }

    public char getTaxRate() {
        return taxRate;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCurrency() {
        return currency;
    }

    //------------- Advanced Getter
    public Integer getTotalPrice(){
        return getPrice() * quantity;
    }

    public Integer getUnitPrice(){
        return getPrice();
    }


    //------------- Override Methods
    @Override
    public String toString(){
        return getProductName() +
                "    x" + getQuantity() +
                "    " + getPrice() +
                " " + getCurrency() +
                " " + getTaxRate() + System.lineSeparator();
    }
}
