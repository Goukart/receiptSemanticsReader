package receipt;

import receipt.generic.Product;
import receipt.generic.Receipt;
import receipt.generic.Store;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Generic extends Receipt {

    public Generic(Store store, List<Product> products, Integer sum, Date timeStamp, String currency, Integer itemCount, HashMap<Character, Integer> taxRates, Integer sumVAT, HashMap<Character, Integer> everyVAT, boolean taxExemption, String transactionNumber, SimpleDateFormat dateFormat){
        this.dateFormat = dateFormat;

        this.store = store;
        boughtItems = products;
        this.sum = sum;
        this.timeStamp = timeStamp;
        this.currency = currency;
        this.itemCount = itemCount;
        this.taxRates = taxRates;
        this.sumVAT = sumVAT;
        this.everyVAT = everyVAT;
        this.taxExemption = taxExemption;
        this.transactionNumber = transactionNumber;

        sanityChecks();
    }
    public Generic(Store store, List<Product> products, Integer sum, Date timeStamp, String currency, Integer itemCount, HashMap<Character, Integer> taxRates, Integer sumVAT, HashMap<Character, Integer> everyVAT, boolean taxExemption, String transactionNumber){
        this(store, products, sum, timeStamp, currency, itemCount, taxRates, sumVAT, everyVAT, taxExemption, transactionNumber, new SimpleDateFormat("dd.MM.yy HH:mm"));
    }



    //------------- Sanity Checks
    private void sanityChecks(){
        //-------- Checks
        System.out.println("---------- Checking given values with calculated values ----------");
        check("VAT of the sum", checkSumVAT(boughtItems, taxRates, sumVAT, everyVAT));
        check("number of items", checkItemCount(boughtItems, itemCount));
        check("sum up prices equal to sum", checkSumPrices(boughtItems, sum));
    }

    private boolean checkSumVAT(final List<Product> products, final HashMap<Character, Integer> taxRates, final Integer givenVAT, final HashMap<Character, Integer> everyVAT){
        HashMap<Character, Integer> everyNewVAT = new HashMap<>();

        for (Product item : products){
            Integer sum = 0;
            final Character taxRate = item.getTaxRate();

            if(everyNewVAT.containsKey(taxRate))
                sum = everyNewVAT.get(taxRate);

            everyNewVAT.put(taxRate, sum + item.getTotalPrice());
        }

        Integer calculatedVAT = 0;
        for (Character taxRate: everyNewVAT.keySet()){
            calculatedVAT += calculateVAT(taxRates.get(taxRate), everyNewVAT.get(taxRate));

            if(!checkVAT(everyVAT.get(taxRate), taxRates.get(taxRate), everyNewVAT.get(taxRate))){
                System.out.println("Calculated VAT for tax rate '" + taxRate + "' [" + everyNewVAT.get(taxRate) + "] not equal to given value [" + everyVAT.get(taxRate) + "]");
                return false;
            }
        }

        if (!givenVAT.equals(calculatedVAT)){
            System.out.println("Calculated VAT of sum [" + calculatedVAT + "] not equal to given value [" + givenVAT + "]");
            return false;
        }

        return true;
    }

    private boolean checkItemCount(List<Product> products, Integer count){
        Integer items = 0;
        for (Product item : products){
            items += item.getQuantity();
        }

        if(!count.equals(items)){
            System.out.println("Number if items in 'products' [" + items + "] not equal to given value [" + count + "]");
            return false;
        }
        return true;
    }

    private boolean checkSumPrices(List<Product> itemList, Integer sum){
        return sumPrices(itemList).equals(sum);
    }
    //--------- Helping Methods
    private void check(String name, boolean outcome){
        System.out.println((outcome ? "passed, " : "failed, ") + "\"" + name + "\"");
    }

    private boolean checkVAT(final Integer valueVAT, final Integer taxRate, final Integer grossValue) {
        Integer newVAT = calculateVAT(taxRate, grossValue);

        if (!newVAT.equals(valueVAT)){
            System.out.println("Calculated VAT of sum [" + newVAT + "] not equal to given value [" + valueVAT + "]");
            return false;
        }

        return true;
    }

    private Integer calculateVAT(final Integer taxRate, final Integer grossValue){
        /*
        The Formula:
        (netValue * taxRate) + netValue = grossValue
        or
        netValue * (1 + taxRate) = grossValue

        To get the VAT:
        grossValue / (1 + vatRate) = netValue

        Then netValue can be compared with this calculated netValue
         */
        //short: (((PERCENT_AS_INT*10*grossValue)/(PERCENT_AS_INT+taxRate))+ROUND)/10
        //or:    (((1000*grossValue)/(100+taxRate))+5)/10


        // To round up on last digit being >= 5
        final int ROUND = 5;

        // Decuple until its a whole number
        final Integer PERCENT_AS_INT = 100;

        // The formula requires 100% + tax rate %
        final Integer TAX = taxRate + PERCENT_AS_INT;

        // VAT is a percentage (e.g. 5%), so to calculate 5% of X you'd do 0,05*X
        // Since its an Integer, grossValue needs to be multiplied by 100,
        // so VAT can be calculated using 5 not 0,05 for 5%
        Integer newGrossValue = grossValue * PERCENT_AS_INT;

        // To round the last digit and not to loose its value its also multiplied by 10 again
        newGrossValue *= 10;

        // Calculate the net value and round the penultimate digit (..6666X6)
        final Integer calculated = (newGrossValue / TAX) + ROUND;

        // Decimate to get the value in whole cents again
        final Integer newNetValue = calculated / 10;

        // Calculate the VAT of grossValue
        final Integer VAT = grossValue - newNetValue;

        return VAT;
    }


    //------------- Private Methods
    private Integer sumPrices(List<Product> itemList){
        int sum = 0;
        sum = itemList.stream().map(Product::getTotalPrice).reduce(sum, Integer::sum);
        return sum;
    }


    //------------- Getter
    public  List<Product> getBoughtItems() {
        return boughtItems;
    }

    private Store getStore() {
        return store;
    }
}
