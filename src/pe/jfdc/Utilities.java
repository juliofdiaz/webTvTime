package pe.jfdc;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.sqlite.SQLiteConfig;

public final class Utilities {

    private Utilities(){}

    /**
     * Tries to create a connection to a SQLite database. If for some reason
     * it does not work it throws a connection. handle it in a function that
     * calls this one.
     *
     * @param dbName    The name of the database to which to connect
     * @return          A successful connection, otherwise throws an Exception
     */
    public static Connection getSqliteConnection( String dbName ) {
        try {
            Class.forName("org.sqlite.JDBC");

            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);

            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName,
                    config.toProperties());
            conn.setAutoCommit(false);
            return conn;
        } catch ( ClassNotFoundException e ) {
            System.exit(0);
            return null;
        } catch ( SQLException e ) {
            System.exit(0);
            return null;
        }
    }

    /**
     * This is the date format that will be used throughout the app
     *
     * @return                  The date format used in this app
     */
    public static SimpleDateFormat DateFormatStandard (){
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     *
     *
     * @param       input       The string used to retrieve date
     * @return                  The Calendar obtained by parsing the input
     */
    public static Calendar getCalendarFromString ( String input ) {
        Calendar newDate = Calendar.getInstance();
        try {
            newDate.setTime( Utilities.DateFormatStandard().parse( input ) );
            return newDate;
        } catch ( java.text.ParseException e ) {
            return null;
        }catch ( NullPointerException e ){
            return null;
        }
    }

    /**
     *
     *
     * @param       input               The Calendar to be parsed as a String
     * @return                          The String version of the Calendar
     */
    public static String getStringFromCalendar ( Calendar input ) {
        if ( input == null ) {
            return null;
        } else{
            return Utilities.DateFormatStandard().format( input.getTime() );
        }
    }

    /**
     * Retrieves the text value of a tag in a Json Object
     *
     * @param       theJsonObject       The Json object
     * @param       tagName             The name of the tag to extract the text
     * @return                          The value in the Json object of a specified name
     */
    public static String getJsonObjectText ( JSONObject theJsonObject,
                                             String tagName ) {
        try{
            return theJsonObject.get( tagName ).toString();
        }catch ( Exception e ) {
            return null;
        }
    }

    /**
     * Gets a url containing a Json and returns it as a String
     *
     * @param       url     the url of the Json
     * @return              The json found at the url
     */
    public static String parseJsonUrl ( String url ) {
        String result = "";

        try{
            URL u = new URL( url );
            Scanner in = new Scanner( u.openStream() );
            while ( in.hasNext() ){
                result += in.nextLine();
            }
            in.close();
        }catch( IOException e ){
            result = "{\"page\":1,\"results\":[],"+
                    "\"total_pages\":0,\"total_results\":0}";
        }

        return result;
    }

}