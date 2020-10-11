package receipt.generic;

import java.io.Serializable;

public class Store implements Serializable {
    //------------- Class Head
    // Full company name
    protected final String companyName;
    // Full Address (company name, street, house number, postal code, city, country)
    protected final Address address;


    public Store(String companyName, Address address){
        this.companyName = companyName;
        this.address = address;
    }


    //------------- Getter
    public String getcompanyName() {
        return companyName;
    }

    public Address getAddress() {
        return address;
    }
}