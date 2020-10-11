package semanticInterpreter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssignmentLogic {
    static String readText;
    private static String regexPostalCode = "(\\d{5})";
    private static String regexCountryCode = "([a-zA-Z]{1,3})";
    private static String regexCountryPostSplit = "([- ])";
    private static String regexAnyPostalCode = "(" + regexCountryCode + regexCountryPostSplit + "?)?" + regexPostalCode;


    public AssignmentLogic(String text){
        readText = text;
    }


    private static String findPostalCode(){
        return "";
    }

    //------------- Public Methods
    //--------- Static
    public static String findCompany(List<String> line){
        return join(line);
    }
    public static String findStreetName(List<String> line){
        // Expected form: (<word><space>)* <houseNumber>
        return joinTo(line, line.size()-2);
    }
    public static String findHouseNumber(List<String> line){
        // Expected form: (<word><space>)* <houseNumber>
        if(line.size()<2)
            return null;
        return line.get(line.size()-1);
    }
    public static String findCity(List<String> line){
        // Expected form: <text>(<digit>)*5 <space> (<word><space>)*
        for (String word:line) {
            if(word.matches(regexAnyPostalCode)) {
                return join(line, line.indexOf(word)+1);
            }
        }
        return null;
    }
    public static String findPostalCode(List<String> line){
        //For other lengths it may has to be passed as parameter
        Pattern pattern = Pattern.compile(regexPostalCode);

        for (String word:line) {
            Matcher matcher = pattern.matcher(word);
            if(matcher.find()){
                return matcher.group(0);
            }
        }
        return null;
    }
    public static String findCountryCode(List<String> line){
        // Expected form: <CountryCode>[-|<space>]<postalCode><space>(<word><space>)*
        //final String regex = regexCountryCode + "?" + regexCountryPostSplit + "?" + regexPostalCode;

        final String sLine = join(line);
        if(sLine.matches(".*" + regexAnyPostalCode + ".*")) {
            String[] words = sLine.trim().split("\\s+");
            for (int i=0; i<words.length; i++){
                String word = words[i];
                if(word.matches(regexPostalCode) && i>0 && words[i-1].matches(regexCountryCode)){
                    // If the country code was split apart, because its like: <country><space><postal>
                    return words[i-1];
                }else if(word.matches(regexCountryCode + "?" + regexCountryPostSplit + regexPostalCode)){
                    // If the country code is split by '-' from the postal code
                    return word.split("-")[0];
                }else if (word.matches(regexAnyPostalCode)){
                    // If the country code is not split by anything or no country code at all
                    return word.replaceAll(regexPostalCode, "");
                }
            }

        }else {
            return null;
        }
        /*
        for (String word:line) {
            if(word.matches(regex)){
                if (line.indexOf(word)>0)
                    return line.get(line.indexOf(word)-1);
                return  word.trim().split("\\s+")[0].split("-")[0];
            }
        }*/
        /*for (String word:line) {
            if(word.matches(regex)){
                if (line.indexOf(word)>0)
                    return line.get(line.indexOf(word)-1);
                return  word.trim().split("\\s+")[0].split("-")[0];
            }
        }*/

        return null;
    }
    public static String findCountry(List<String> line){
        return getCountryName(findCountryCode(line));
    }
    public static String findProductName(List<String> line){
        for (int i = 0; i< line.size(); i++){
            String word = line.get(i);
            if(word.matches("\\d*,\\d{2}")){
                return joinTo(line, i-1);
            }
        }
        //System.out.println("Could not find product name in: " + line);
        return null;
    }
    public static String findProductPrice(List<String> line){
        for (String word:line) {
            if(word.matches("\\d*,\\d{2}")){
                return word;
            }
        }
        return null;
    }
    //--------- Getter
    public static String getRegexPostalCode() {
        return regexPostalCode;
    }
    //--------- Setter
    public static void setRegexPostalCode(int length) {
        AssignmentLogic.regexPostalCode = "(\\d{" + length + "})";
    }

    //------------- Private Methods
    private static String join(List<String> list, int from, int to){
        StringJoiner sList = new StringJoiner(" ");

        for (int i = from; i <= to; i++) {
            sList.add(list.get(i));
        }

        return sList.toString();
    }
    private static String join(List<String> list, int from){
        return join(list, from, list.size()-1);
    }
    private static String join(List<String> list){
        return join(list, 0, list.size()-1);
    }
    private static String joinTo(List<String> list, int to){
        return join(list, 0, to);
    }
    private static String getCountryName(String code){
        switch (code){
            case "D":
            case "DE":
                return "Deutschland";
            default:
                return null;
        }
    }
}