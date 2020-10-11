package receipt.edeka;

public class EDEKAproduct extends receipt.generic.Product {
    //------------- Class Head
    private final Integer priceChange;

    public EDEKAproduct(String name, Integer price, char taxRate, int quantity, String currency, Integer discount){
        super(name, price, taxRate, quantity, currency);

        this.priceChange = discount;
    }
    public EDEKAproduct(String name, Integer price, char taxRate, String currency, Integer discount){
        super(name, price, taxRate, 1, currency);

        this.priceChange = discount;
    }
    public EDEKAproduct(String name, Integer price, char taxRate, String currency){
        super(name, price, taxRate, 1, currency);

        this.priceChange = 0;
    }
    public EDEKAproduct(String name, Integer price, char taxRate, int quantity, String currency){
        super(name, price, taxRate, quantity, currency);
        priceChange = 0;
    }


    //------------- Getter
    public String getProductName() {
        return productName;
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

    public Integer getPriceChange() {
        return priceChange;
    }

    public Integer getOroginalPrice(){
        return price;
    }

    public Integer getDiscount(){
        return getPriceChange();
    }


    //------------- Advanced Getter
    public Integer getTotalPrice(){
        return price * quantity;
    }

    public Integer getUnitPrice(){
        return price;
    }

    public Integer getNewPrice() {
        return price + priceChange;
    }

    public boolean priceChanged(){
        return getDiscount().equals(0);
    }


    //------------- Override
    /*@Override
    public String toString(){
        for (:
             ) {
            
        }
        return "";
    }*/
}
