package models.parser;

public final class Parser {

    private Parser(){

    }

    public static boolean stringToInt(String toParse){
        try{
            Integer.parseInt(toParse);
            return true;
        }catch (NumberFormatException nfe){
            return false;
        }
    }
}
