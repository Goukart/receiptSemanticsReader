package receipt.generic;

import java.io.Serializable;

public final class Address implements Serializable {
    //------------- Class Head
    private final String companyName;
    private final String street;
    private final String houseNumber;
    private final String postalCode;
    private final String city;
    private final String country;

    public Address(String companyName, String street, String houseNumber, String postalCode, String city, String country){
        this.companyName = companyName;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }


    //------------- Getter
    public String getCompanyName() {
        return companyName;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }


    //------------- Override Methods
    @Override
    public String toString(){
        return "company:\t" + getCompanyName() + System.lineSeparator() +
                "country:\t" + getCountry() + System.lineSeparator() +
                "postal code:\t" + getPostalCode() + System.lineSeparator() +
                "city:\t\t" + getCity() + System.lineSeparator() +
                "street:\t\t" + getStreet() + " " + getHouseNumber() + System.lineSeparator();
    }
}
