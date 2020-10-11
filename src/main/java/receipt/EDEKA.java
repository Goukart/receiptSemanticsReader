package receipt;

import receipt.edeka.EDEKAproduct;
import receipt.edeka.EDEKAstore;
import receipt.edeka.Template;
import receipt.generic.Product;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


public class EDEKA extends receipt.generic.Receipt implements Serializable{
    private Template template;

    protected List<EDEKAproduct> boughtItems;
    protected EDEKAstore store;

    private final int LINE_WIDTH = 40;
    private final int RIGHT_BORDER = 4;


    public EDEKA(EDEKAstore store, List<EDEKAproduct> products, Integer sum, Date timeStamp, String currency, Integer itemCount, HashMap<Character, Integer> taxRates, Integer sumVAT, HashMap<Character, Integer> everyVAT, boolean taxExemption, String transactionNumber) {
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
        dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");

        sanityChecks();
    }

    public EDEKA(String receipt){
        template = new Template(makeMatrix(receipt));

        this.store = template.findStore();
        boughtItems = template.findProducts();
        this.sum = template.findSum();
        this.timeStamp = template.findTimeStamp();
        this.currency = template.findCurrency();
        this.itemCount = template.findItemCount();
        this.taxRates = template.findTaxRates();
        this.sumVAT = template.findSumVAT();
        this.everyVAT = template.findEveryVAT();
        this.taxExemption = template.findTaxExemption();
        this.transactionNumber = template.findTransactionNumber();
        dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");

        //sanityChecks();
    }

    //------------- Sanity Checks
    private void sanityChecks(){
        //-------- Checks
        System.out.println("---------- Checking given values with calculated values ----------");
        check("VAT of the sum", checkSumVAT(boughtItems, taxRates, sumVAT, everyVAT));
        check("number of items", checkItemCount(boughtItems, itemCount));
        check("sum up prices equal to sum", checkSumPrices(boughtItems, sum));
        check("number of items", countItems());
    }

    private boolean checkSumVAT(final List<EDEKAproduct> products, final HashMap<Character, Integer> taxRates, final Integer givenVAT, final HashMap<Character, Integer> everyVAT){
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

    private boolean checkItemCount(List<EDEKAproduct> products, Integer count){
        Integer items = 0;
        for (Product item : products){
            items += item.getQuantity();
        }

        if(!count.equals(items)){
            System.out.println("Number if items in 'products' [" + items + "] not equal to given value [" + count + "]");
            /*if(!itemCount.equals(countPosts()))
                throw new IllegalArgumentException("counted items does not mach given itemCount.");
            if(!sum.equals(sumPrices()))
                throw new IllegalArgumentException("calculated sum deos not match given sum.");*/
            return false;
        }
        return true;
    }

    private boolean checkSumPrices(List<EDEKAproduct> itemList, Integer sum){
        return sumPrices(itemList).equals(sum);
    }

    private boolean countItems(){
        Integer counted = 0;

        for (Product item : boughtItems){
            counted += item.getQuantity();
        }

        return itemCount.equals(counted);
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
    private Integer sumPrices(List<EDEKAproduct> itemList){
        int sum = 0;
        sum = itemList.stream().map(Product::getTotalPrice).reduce(sum, Integer::sum);
        return sum;
    }
    private List<List<String>> makeMatrix(String text){
        List<List<String>> matrix = new LinkedList<>();

        // Make the matrix
        final String SEPARATOR = ";"; //must be String because regex wants String
        String[] lines = text.split("\\r?\\n"); //split in lines

        // Cleaning up
        for(int i=0;i<lines.length;i++){
            lines[i] = lines[i].replaceAll("\\s+",SEPARATOR); // remove whitespaces
            List<String> lineArray = new LinkedList<>(Arrays.asList(lines[i].split(SEPARATOR))); // split lines in words

            lineArray.removeIf(word -> word == null || "".equals(word)); // remove empty elements (the ones that where whitespaces)

            if (lineArray.size()>=1)
                matrix.add(lineArray);
        }
        return matrix;
    }



    //------------- Printing Methods (experimental)
    private String printHeader(){
        String header = "";

        //header += centerText("EDEKA");
        header += centerText(store.getStoreName());

        header += centerText(store.getAddress().getStreet() + " " + store.getAddress().getHouseNumber());
        header += centerText(store.getAddress().getPostalCode() + " " + store.getAddress().getCity());
        //header += centerText("Tel. " + store.getPhone().getPrefix() + " " + store.getPhone().getConnection());
        header += "\n";

        return header;
    }
    private String printItemList(){
        if(boughtItems == null){
            return "Item list is null";
        }else if(boughtItems.isEmpty()){
            return "Item list is empty";
        }

        String itemList = "";

        itemList += fill(LINE_WIDTH-currency.length()-RIGHT_BORDER) + currency + "\n";
        for(int i=0; i<boughtItems.size(); i++){
            EDEKAproduct product = boughtItems.get(i);

            if(product.getQuantity() > 1){
                String quantityLine = "";
                quantityLine += product.getQuantity() + " x";
                quantityLine += alignRight(quantityLine,priceToString(product.getOroginalPrice()), 13/* ' ' von rechts*/);

                itemList += quantityLine + "\n";
            }

            itemList += product.getProductName();

            itemList += alignRight(product.getProductName(),
                    priceToString(product.getTotalPrice()), RIGHT_BORDER) +
                    //(product.isQualifiedForDiscount() ? ' ' : '*') +
                    " " + product.getTaxRate() + "\n";
        }

        return itemList;
    }
    public String printSum(){
        return priceToString(sum);
    }
    private String printTaxRates(){
        final int NETTO_MARK = 15;
        final int TAX_MARK = 26;
        final String TAX = "MwSt";
        final String NETTO = "NETTO";
        final String SALES = "UMSATZ";


        String headerLine = "";
        headerLine += TAX +
                fill(NETTO_MARK - TAX.length()) +
                NETTO;
        headerLine += fill(TAX_MARK - headerLine.length()) + TAX;
        headerLine += alignRight(headerLine, SALES, 0) + "\n";

        StringBuilder taxRatesLines = new StringBuilder();
        for(Character taxRate : taxRates.keySet()) {
            int rate = taxRates.get(taxRate);
            String line = "";
            line += taxRate + " " + rate + "%";
            //priceToString(boughtItems.get(0).getPrice()-(boughtItems.get(0).getPrice()*rate/100));
            int priceSum = sumPrices(taxRate);
            String netValue = priceToString(priceSum - everyVAT.get(taxRate));
            String taxes = priceToString(everyVAT.get(taxRate));
            //Double space after Price to offset it one to the left. Actual is not center (EDEKA)
            //  -> need to load layout by EDEKAstore, so new object in EDEKAstore to set it
            //two spaces because it get rounded otherwise and is incorrect at >1 digit values -> but >2digit is wrong again
            line = centerInLine(line, netValue + "  ");
            line += fill(TAX_MARK - line.length()) + taxes;
            line += alignRight(line, priceToString(priceSum), 0);

            taxRatesLines.append(line).append("\n");
        }

        return headerLine + taxRatesLines.append("\n\n\n").toString();
    }
    private String printClosing(){
        String bonData = "Datum Uhrzeit  Filiale Pos Bed Bon\n";
        bonData += dateFormat.format(timeStamp) + " " +
                store.getStoreID() + " " +
                "   " + " " + //Unknown what Pos is
                "   " + " " + //staff id?
                transactionNumber + "\n\n\n" +
                centerText("Steuernummer: " + store.getTaxNumber()) ;

        return bonData + "\n";
    }
    public String print(){
        String receipt = "";

        receipt = printHeader();
        receipt += printItemList();

        String post = "Posten: " + itemCount;
        receipt += post + alignRight(post, "----------", RIGHT_BORDER) + "\n";

        receipt += "\n";     //To compensate for not having bold and big text
        receipt += "SUMME " + currency + alignRight("SUMME " + currency, printSum(), RIGHT_BORDER-2) + "\n";
        receipt += "\n";     //To compensate for not having bold and big text

        //receipt += printPaymentInfo();

        receipt += printTaxRates();

        receipt += printClosing();

        return receipt;
    }
    //--------- Helper Methods For Printing
    private String centerText(String text){
        char fillChar = ' ';
        int fillAmount = (LINE_WIDTH - text.length()) /2;

        char[] fill = new char[fillAmount];
        Arrays.fill(fill, fillChar);

        return fill(fillAmount) + text + "\n";
    }
    private String centerInLine(String line, String text){
        char fillChar = ' ';
        int fillAmount = (LINE_WIDTH - text.length()) /2 - line.length();

        char[] fill = new char[fillAmount];
        Arrays.fill(fill, fillChar);

        return line + fill(fillAmount) + text;
    }
    private String fill(int fillAmount){
        char[] fill = new char[fillAmount];
        Arrays.fill(fill, ' ');

        return new String(fill);
    }
    private String alignRight(String givenLine, String insertText, int leftBorder){
        String line = "";
        int alignLeft = LINE_WIDTH-givenLine.length()-insertText.length()-leftBorder;
        //int alignLeft = lineWidth-product.getProductName().length()-product.printPrice().length()-LEFT_BORDER;
        //String.valueOf(product.getPrice()).length() get the char number of a number with "-" sign
        line = fill(alignLeft)
                + insertText;

        return line;
    }



    //------------- Override Methods
    private String priceToString(int price){
        String sign = "";
        if(price<0){
            sign = "-";
            price = price * (-1);
        }

        String cents = String.valueOf(price%100);
        String wholeNumber = String.valueOf(price/100);
        return sign + wholeNumber + "," + (cents.length()==1 ? ("0" + cents) : cents);
    }
    //--------- Getter
    public List<EDEKAproduct> getBoughtItems() {
        return boughtItems;
    }
    public EDEKAstore getStore() {
        return store;
    }

    //---------- Public Methods
    public void saveToFile(String fileName){
        final String path = System.getProperty("user.dir") + File.separator;

        try (PrintWriter out = new PrintWriter(path + fileName + ".txt", StandardCharsets.UTF_8)) {
            out.print(export());
            System.out.println("File [" + fileName + ".txt] was saved to \"" + path + "\"");
        } catch (IOException e) {
            System.out.println("File [" + fileName + ".txt] could not be saved to \"" + path + "\"");
            e.printStackTrace();
        }
    }
    public String export(){
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
                "format:edeka;%n" +
                        "currency:\"" + currency + "\";%n" +
                        "storeName:\"" + store.getStoreName() + "\";%n" +
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
        return fileContent;
    }



    //------------- Getter And Setter
    public Template getTemplate() throws IllegalArgumentException{
        if(template == null)
            throw new IllegalArgumentException("Template not set");
        return template;
    }
    public void setTemplate(List<List<String>> receipt) {
        this.template = new Template(receipt);
    }
}