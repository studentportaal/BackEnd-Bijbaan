package models.parser;

public final class Parser {

    public static boolean stringToInt(String toParse){
        try{
            String.valueOf(toParse);
            return true;
        }catch (NumberFormatException nfe){
            return false;
        }
    }
}
