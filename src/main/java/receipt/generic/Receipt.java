package receipt.generic;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


public abstract class Receipt implements Serializable{
    //------------- Required by Law
    // UStDV § 33 and KassenSichV § 6
    // Containing full company name and address
    protected  /*final*/ Store store;
    // Date of issue
    protected /*final*/ Date timeStamp;
    // List of all bought items
    protected /*final*/ List<Product> boughtItems;
    // boughtItems.size() gets the number of items (also required)
    protected /*final*/ Integer itemCount = null;
    // Total cost of the order
    protected /*final*/ Integer sum;
    // VAT (Value-added tax) to be applied to the total sum
    protected /*final*/ Integer sumVAT;
    // All the tax rates that are applied and their identifier
    protected /*final*/ HashMap<Character, Integer> taxRates;
    // The individual sums, if multiple tax rates are applied to different items
    protected /*final*/ HashMap<Character, Integer> everyVAT;
    // If there is tax exemption on anything or everything, it should be indicated
    protected /*final*/ boolean taxExemption;
    // Transaction number
    protected /*final*/ String transactionNumber;


    //------------- Additional Information
    protected /*final*/ String currency;
    protected /*final*/ SimpleDateFormat dateFormat;





    //------------- Protected Methods
    protected Integer sumPrices(Character taxRate){
        int sum = 0;
        for(Product item : boughtItems){
            if(item.getTaxRate() != taxRate)
                continue;
            sum += item.getTotalPrice();
        }
        return sum;
    }



    //------------- Public Methods
    //--------- Export
    public void saveToFile(String fileName){
        String fileContent;

        StringJoiner strItemList = new StringJoiner(",");
        for (Product item : boughtItems){
            strItemList.add("{name:\"" + item.getProductName() + "\", price:\"" + item.getUnitPrice() + "\", taxRate:\"" + item.getTaxRate() + "\", quantity:\"" + item.getQuantity() + "\"}");
        }
        StringJoiner strTaxRates = new StringJoiner(",");
        for (Character rateID : taxRates.keySet()){
            strTaxRates.add("{" + rateID + ":\"" + taxRates.get(rateID) + "\"}");
        }
        StringJoiner strEveryVAT = new StringJoiner(",");
        for (Character rateID : everyVAT.keySet()){
            strEveryVAT.add("{" + rateID + ":\"" + taxRates.get(rateID) + "\"}");
        }

        fileContent = String.format(
                "format:generic;%n" +
                "currency:\"" + currency + "\";%n" +
                "storeName:\"" + store.getcompanyName() + "\";%n" +
                "address:{" +
                    "companyName:\"" + store.getAddress().getCompanyName() + "\"," +
                    "street:\"" + store.getAddress().getStreet() + "\"," +
                    "houseNumber:\"" + store.getAddress().getHouseNumber() + "\"," +
                    "postalCode:\"" + store.getAddress().getHouseNumber() + "\"," +
                    "city:\"" + store.getAddress().getCity() + "\"," +
                    "country:\"" + store.getAddress().getCountry() + "\"};%n" +
                "itemList:{" + strItemList + "};%n" +
                "itemCount:\"" + itemCount + "\";%n" +
                "taxRates:{" + strTaxRates + "};%n" +
                "everyVAT:{" + strEveryVAT + "};%n" +
                "sum:\"" + sum + "\";%n" +
                "sumVat:\"" + sumVAT + "\";%n" +
                "taxExemption:\"" + taxExemption + "\";%n" +
                "timeStamp:\"" + dateFormat.format(timeStamp) + "\";%n" +
                "dateFormat:\"" + dateFormat.toPattern() + "\"%n" +
                "transactionNumber:\"" + transactionNumber + "\";%n"
        );



        final String path = System.getProperty("user.dir") + File.separator;
        try (PrintWriter out = new PrintWriter(path + fileName + ".txt", StandardCharsets.UTF_8)) {
            out.print(fileContent);
            System.out.println("File [" + fileName + ".txt] was saved to \"" + path + "\"");
        } catch (IOException e) {
            System.out.println("File [" + fileName + ".txt] could not be saved to \"" + path + "\"");
            e.printStackTrace();
        }
    }
    public void saveObjectAsFile(/*Receipt object, */String fileName){
        // This needs separator at the end else its not working (PrintWriter doesn't), never mind was other method call
        final String path = System.getProperty("user.dir") + File.separator;
        final String suffix = ".ser";

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path + fileName + suffix);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(this/*object*/);

            objectOutputStream.close();
            fileOutputStream.close();

            System.out.println("Receipt-Object [" + fileName + suffix + "] was saved to \"" + path + "\"");
        } catch (IOException e) {
            System.out.println("Receipt-Object [" + fileName + suffix + "] could not be saved to \"" + path + "\"");
            e.printStackTrace();
        }
    }

    //--------- Getter
    public Date getTimeStamp() {
        return timeStamp;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public Integer getSum(){
        return sum;
    }

    public Integer getSumVAT(){
        return sumVAT;
    }

    public HashMap<Character, Integer> getTaxRates() {
        return taxRates;
    }

    public HashMap<Character, Integer> getEveryVAT() {
        return everyVAT;
    }

    public boolean isTaxExemption() {
        return taxExemption;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}