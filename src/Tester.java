import org.sqlite.SQLiteConfig;
import pe.jfdc.Episode;
import pe.jfdc.Season;
import pe.jfdc.Series;
import pe.jfdc.UtilitiesDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by juliofdiaz on 2/14/16.
 *
 * @author juliofdiazc
 */
public class Tester {
    public static void main (String[] args){

        String key = "DAB5662103E2BCF1";
        String DB_NAME = "/Users/juliofdiaz/Development/TvdbApp/.tvshows.db";

/*        Connection conn;
        try {

            //Connection to database
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            conn = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME, config.toProperties());
            conn.setAutoCommit(false);


            Series TvdbShow = new Series();
            TvdbShow.setTvdbId(75682);
            TvdbShow.retrieveFullTvdbInfo(key);


            Series LocaldbShow = UtilitiesDB.getSeriesFromDb( conn, 75682 );

            System.out.println( TvdbShow.getName()  +"\n"+LocaldbShow.getName() );

            conn.close();
        }catch (Exception e){
            System.exit(1);
        }*/


        Integer tvdbId = 75682;

        Connection c;
        try {
            c = createConnection( DB_NAME, true, true );

            //tvdb series
            Series tvdbShow = new Series();
            tvdbShow.setTvdbId( tvdbId );
            tvdbShow.retrieveFullTvdbInfo(key);

            //local series
            Series localdbShow = UtilitiesDB.getSeriesFromDb( c, tvdbId );

            ArrayList<Season> tvdbSeasonList = tvdbShow.getSeasons();
            ArrayList<Season> localSeasonList = localdbShow.getSeasons();
            for ( Season tempTvdbSeason : tvdbSeasonList ) {
                Season tempLocalSeason = localdbShow.getSeasonByNumber(tempTvdbSeason.getNumber());
                // if the season exists in hte  local db
                if ( tempLocalSeason != null ) {
                    System.out.println(tempLocalSeason);
                    ArrayList<Episode> tvdbEpisodeList = tempTvdbSeason.getEpisodes();
                    ArrayList<Episode> localEpisodeList = tempLocalSeason.getEpisodes();
                    //if they are the same size check they are all the same
                    //if (localEpisodeList.size() == tvdbEpisodeList.size()) {
                    if ( tempLocalSeason.hasSameEpisodes(tempTvdbSeason) ) {
                        System.out.println("good");
                        /*for (Episode tempTvdbEpisode : tvdbEpisodeList) {
                            Episode tempLocalEpisode = tempLocalSeason.getEpisodeByNumber( tempTvdbEpisode.getNumber() );
                            if ( tempLocalEpisode.getName().equals( tempTvdbEpisode.getName() ) ) {
                                System.out.println(tempTvdbEpisode.getName());
                            } else {
                                System.out.println( "WTF!" );
                            }
                        }*/
                        //otherwise check which ones are not in the database, then add them to it
                    } else {
                        System.out.println("new episodes in season: " + tempTvdbSeason.getNumber());
                    }
                    //add the season
                } else {
                    System.out.println( "new season: "+tempTvdbSeason.getNumber() );
                }
            }


            System.out.println( tvdbShow.getName()  +"\n"+localdbShow.getName() );



            c.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public static Connection createConnection( String dbName, Boolean isEnforceForeignKeys,
                                               Boolean isSetAutoCommit ) throws ClassNotFoundException,
            SQLException {
        Connection conn;
        Class.forName("org.sqlite.JDBC");
        SQLiteConfig config = new SQLiteConfig();
        if ( isEnforceForeignKeys ) {
            config.enforceForeignKeys(true);
        }
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbName, config.toProperties());
        if ( isSetAutoCommit ) {
            conn.setAutoCommit(false);
        }
        return conn;
    }
}