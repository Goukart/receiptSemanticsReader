package receipt.edeka;

import receipt.generic.Address;

public class EDEKAstore extends receipt.generic.Store {
    //------------- Class Head
    private final String storeName;
    private final String storeID;
    private final String taxNumber;
    
    

    public EDEKAstore(String storeName, Address address, String ID, String taxNumber){
        super("EDEKA", address);

        this.storeName = storeName;
        storeID = ID;
        this.taxNumber = taxNumber;
    }


    //------------- Getter
    public String getStoreName() {
        return storeName;
    }

    public String getStoreID() {
        return storeID;
    }

    public String getTaxNumber() {
        return taxNumber;
    }
    
}
