package receipt.edeka;


import receipt.generic.Address;
import semanticInterpreter.AssignmentLogic;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;


public class Template implements Serializable {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");

    //------------- Keys And Locations
    private final HashMap<String, String> keys;
    private final List<List<String>> receipt;

    private final String ADDRESS = "address";
    private final String CURRENCY = "currency";
    private final String ItemList = "itemList";
    private final String ItemCount = "itemCount";
    private final String SUM = "sum";
    private final String TaxRates = "taxRates";
    private final String DATE = "date";
    private final String StoreID = "storeID";
    private final String TransactionNumber = "transactionNumber";
    private final String EveryVAT = "everyVAT";
    private final String TaxNumber = "taxNumber";
    private final String TaxExemption = "taxExemption";


    public Template(final List<List<String>> receipt){
        //--------- Key Words
        final String addressKey = "EDEKA";
        final String beginProductsKey = "EUR";
        final String endProductsKey = "Posten:";


        //--------- Set Keys And Ranges
        keys = new HashMap<>();
        keys.put(TaxExemption, "false");
        for (int i = 0; i < receipt.size(); i++) {
            final List<String> line = receipt.get(i);

            if(line.get(0).equals(beginProductsKey)){
                // Currency and item list beginning
                keys.put(CURRENCY, line.get(0));
                keys.put(ItemList, String.valueOf(i+1));
            } else if(line.get(0).contains(endProductsKey)){
                // Item count and end of item list
                keys.put(ItemCount, line.get(1));
                keys.put(ItemList, keys.get(ItemList) + "-" + (i-1));
            } else if(line.get(0).toUpperCase().equals("SUMME") && line.get(1).toUpperCase().equals("EUR")){
                // Sum
                keys.put(SUM, line.get(line.size()-1));
            } else if(isTaxRateTable(line)){
                // Tax rates and VATs
                i++;
                Range range = new Range(i,i);
                for (int t=i; receipt.get(t).get(0).length() == 1; t++){
                    range = new Range(i,t);
                    keys.put(TaxRates, range.toString());
                    // VATs
                    keys.put(EveryVAT, range.toString());
                }
                i = range.lower;
            } else if(isMetaDataLine(line)){
                // Date, store ID and transaction number
                i++;
                List<String> next = receipt.get(i);

                keys.put(DATE, next.get(0) + " " + next.get(1));
                keys.put(StoreID, next.get(2));
                keys.put(TransactionNumber, next.get(5));
            } else if(line.get(0).toUpperCase().equals("Steuernummer:".toUpperCase())){
                keys.put(TaxNumber, line.get(1));
            } else if(hasTaxExemption(line)){
                keys.put(TaxExemption, "true");
            }


            for (String word : line) {
                if(word.toUpperCase().contains(addressKey)){
                    if(keys.containsKey(ADDRESS)){
                        continue;
                    }
                    keys.put(ADDRESS, "0-" + (i+10));
                }
            }
        }


        this.receipt = receipt;
    }


    //------------- Public Methods
    public Address findAddress(){
        //System.out.println(keys.get(ADDRESS));
        final String keyWord = "Tel.";

        String company = "";
        String street = "";
        String houseNumber = "";
        String postalCode = "";
        String city = "";
        String country = "";

        Range range = new Range(keys.get(ADDRESS));

        for(int i=range.upper-1; i>=range.lower; i--){
            if (receipt.get(range.lower + i).get(0).contains(keyWord)){

                company = AssignmentLogic.findCompany(receipt.get(i-3));
                street = AssignmentLogic.findStreetName(receipt.get(i-2));
                houseNumber = AssignmentLogic.findHouseNumber(receipt.get(i-2));
                postalCode = AssignmentLogic.findPostalCode(receipt.get(i-1));
                city = AssignmentLogic.findCity(receipt.get(i-1));
                country = AssignmentLogic.findCountry(receipt.get(i-1));

                return new Address(company, street, houseNumber, postalCode, city, country);
            }
        }

        System.out.println("Could not find key word [" + keyWord + "] to find address");
        System.out.println("range: " + range);
        return null;
    }
    public EDEKAstore findStore(){
        Address address = findAddress();
        if (address == null)
            return null;

        return new EDEKAstore(address.getCompanyName(), address, findStoreID(), findTaxNumber());
    }
    public List<EDEKAproduct> findProducts(){
        if(keys.get(ItemList) == null){
            System.out.println("Products not found");
            return null;
        }
        Range range = new Range(keys.get(ItemList));

        List<EDEKAproduct> items = new ArrayList<>();
        EDEKAproduct product;
        for (int i = range.lower; i <= range.upper; i++) {
            List<String> line = receipt.get(i);

            Integer quantity = 1;
            Integer unitPrice = 0;
            String name = "";
            if (line.get(0).matches("\\d*")){
                //Multiple Items
                quantity = Integer.valueOf(line.get(0));
                unitPrice = stringToInteger(line.get(2));
                Integer price = quantity * unitPrice;

                i++;
                line = receipt.get(i);

                name = AssignmentLogic.findProductName(line);

                product = new EDEKAproduct(name, price, findTaxRate(line), quantity, findCurrency(), 0 );
                items.add(product);
                continue;
            }

            if(receipt.get(i+1).contains("Preisänderung")){
                //Price Changed
                unitPrice = stringToInteger(line.get(line.size()-2));
                Integer price = quantity * unitPrice;

                name = AssignmentLogic.findProductName(line);


                product = new EDEKAproduct(name, price, findTaxRate(line), quantity, findCurrency(), stringToInteger(receipt.get(i+1).get(1)) );
                items.add(product);

                i = i+2;
                continue;
            }
            //Integer price, char taxRate, int quantity, String currency, Integer discount

            unitPrice = stringToInteger(AssignmentLogic.findProductPrice(line));
            name = AssignmentLogic.findProductName(line);

            product = new EDEKAproduct(name, unitPrice, findTaxRate(line), 1, findCurrency());

            items.add(product);
        }
        return items;
    }
    public Integer findSum(){
        return stringToInteger(keys.get(SUM));
    }
    public Date findTimeStamp(){
        try {
            return dateFormat.parse(keys.get(DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String findCurrency(){
        return keys.get(CURRENCY);
    }
    public Integer findItemCount(){
        return Integer.valueOf(keys.get(ItemCount));
    }
    public HashMap<Character, Integer> findTaxRates(){
        if(keys.get(TaxRates) == null){
            System.out.println("Tax rates not found");
            return null;
        }
        Range range = new Range(keys.get(TaxRates));

        HashMap<Character, Integer> taxRates = new HashMap<>();
        for (int i = range.lower; i <= range.upper; i++) {
            taxRates.put(receipt.get(i).get(0).charAt(0), Integer.valueOf(receipt.get(i).get(1).replaceAll("%","")));
        }
        return taxRates;
    }
    public Integer findSumVAT(){
        Integer sum = 0;
        for (Integer vat:findEveryVAT().values()) {
            sum += vat;
        }
        return sum;
    }
    public HashMap<Character, Integer> findEveryVAT(){
        if(keys.get(EveryVAT) == null){
            System.out.println("VAT not found");
            return null;
        }

        Range range = new Range(keys.get(EveryVAT));
        HashMap<Character, Integer> everyVAT = new HashMap<>();
        for (int i = range.lower; i <= range.upper; i++) {
            everyVAT.put(receipt.get(i).get(0).charAt(0), stringToInteger(receipt.get(i).get(3)));
        }

        return everyVAT;
    }
    public boolean findTaxExemption(){
        // Product does not know if it has this property
        /*for (EDEKAproduct product:findProducts()) {
            if(product.)
        }*/
        return keys.get(TaxExemption).equals("true");
    }
    public String findTransactionNumber(){
        return keys.get(TransactionNumber);
    }


    //------------- Private Methods
    private String findStoreID(){
        return keys.get(StoreID);
    }
    private String findTaxNumber(){
        return keys.get(TaxNumber);
    }
    private Integer stringToInteger(String value){
        if(value == null){
            System.out.println("String to convert to Integer is null");
            return null;
        }else if (!value.contains(",")){
            System.out.println("String to convert to Integer does not match XX,XX [" + value + "]");
            return null;
        }
        return (Integer.valueOf(value.split(",")[0])*100) + Integer.valueOf(value.split(",")[1]);
    }
    private Character findTaxRate(List<String> line){
        for (int i=0; i<line.size();i++) {
            String word = line.get(i);
            if(word.matches("\\d*,\\d{2}") && i+1<line.size()){
                String markings = line.get(i+1);
                if(markings.matches("[*]([A-Z]{1,2})")){
                    return markings.charAt(1);
                }else{
                    return markings.charAt(0);
                }
            }
        }
        return null;
    }
    private boolean isTaxRateTable(final List<String> line){
        List<String> newLine = new ArrayList<>();
        IntStream.range(0, line.size()).forEach(i -> newLine.add(line.get(i).toUpperCase()));

        return newLine.get(0).contains("MWST") && newLine.get(1).contains("NETTO") && newLine.get(2).contains("MWST") && newLine.get(3).contains("UMSATZ");
    }
    private boolean isMetaDataLine(final List<String> line){
        if (line.size()<6)
            return false;

        boolean condition;
        condition = (line.get(0).toUpperCase().equals("Datum".toUpperCase()));
        condition = condition && (line.get(1).toUpperCase().equals("Uhrzeit".toUpperCase()));
        condition = condition && (line.get(2).toUpperCase().equals("Filiale".toUpperCase()));
        condition = condition && (line.get(3).toUpperCase().equals("Pos".toUpperCase()));
        condition = condition && (line.get(4).toUpperCase().equals("Bed".toUpperCase()));
        condition = condition && (line.get(5).toUpperCase().equals("Bon".toUpperCase()));

        return condition;
    }
    private boolean hasTaxExemption(final List<String> line){
        if (line.size()<4)
            return false;

        boolean condition;
        condition = (line.get(0).equals("*"));
        condition = condition && (line.get(1).toUpperCase().equals("Position".toUpperCase()));
        condition = condition && (line.get(2).toUpperCase().equals("nicht".toUpperCase()));
        condition = condition && (line.get(2).toUpperCase().equals("rabattberechtigt".toUpperCase()));

        return condition;
    }
}


class Range {
    final Integer lower;
    final Integer upper;

    Range(Integer low, Integer high){
        lower = low;
        upper = high;
    }
    Range(String range){
        if(range == null)
            throw new IllegalArgumentException("Range is null");
        if(range.indexOf('-') < 0) {
            throw new IllegalArgumentException("Range has not expected format X-Y: " + range);
        }

        Integer[] rangeI = new Integer[2];
        lower = Integer.valueOf(range.substring(0,range.indexOf('-')));
        upper = Integer.valueOf(range.substring(range.indexOf('-')+1));
    }


    public boolean contains(int number){
        return (number >= lower && number <= upper);
    }

    public String toString(){
        return lower.toString() + "-" + upper.toString();
    }
}