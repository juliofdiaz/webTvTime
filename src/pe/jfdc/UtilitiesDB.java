package pe.jfdc;

import java.sql.*;
import java.util.ArrayList;

import org.sqlite.SQLiteConfig;

public class UtilitiesDB{
    public static void main( String args[] ) {
        connectionProvider( new Series() );
    }

    /*
     *
     */
    public static void connectionProvider ( Series series ) {

        String DB_NAME = "/Users/juliofdiaz/Development/TvdbApp/.tvshows.db";

        Connection conn;
        try {

            //Connection to database
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            conn = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME, config.toProperties());
            conn.setAutoCommit(false);

            //GET SERIES BASED ON TVDB ID
            //System.out.println( getSeriesFromDb( conn, 75682  ) );

            //INSERT SERIES IN DATABASE MADAFOCAS
            //fillSeriesTable( conn, series );
            ArrayList<Integer> shows = getAllTvdbIdFromDb( conn );
            for( Integer eachShow : shows ) {
                Series tempShow = getSeriesFromDb( conn, eachShow );
                System.out.println( tempShow.getName() );
                ArrayList<Season> tempSeason = tempShow.getSeasons();
                for( Season eachSeason : tempSeason ){
                    System.out.print(eachSeason.getNumber());
                    ArrayList<Episode> tempEpisode = eachSeason.getEpisodes();
                    System.out.print( "."+tempEpisode.size()+"\t");
                    for(Episode eachEpisode : tempEpisode){
                        System.out.print(eachEpisode.getName());
                    }
                }
                System.out.print("\n");
            }

            //Close connection
            conn.close();
            System.out.println("Records created successfully");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public static ArrayList<Integer> getAllTvdbIdFromDb(Connection conn)
            throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT series_tvdb_id FROM SERIES";
        ResultSet rs = stmt.executeQuery( sql );

        ArrayList<Integer> result = new ArrayList<Integer>();
        while ( rs.next() ) {
            Integer tempTvdbId = rs.getInt( "series_tvdb_id" );
            result.add( tempTvdbId );
        }
        return result;
    }

    /**
     * Retrieves Series from the database given a tvdbId
     *
     * @param   conn                Connection to the database
     * @param   tvdbId              The tvdb id of the series to be queried
     * @return                      The Series retrieved from the database
     * @throws  SQLException        It happens if there is a problem connecting to the
     *                              database or if the sql is wrong
     */
    public static Series getSeriesFromDb ( Connection conn, int tvdbId ) throws SQLException{
        Statement stmt;
        String sql;
        Series series = new Series();

        stmt = conn.createStatement();
        sql = "SELECT * FROM SERIES WHERE series_tvdb_id='" + tvdbId + "';";

        ResultSet rs = stmt.executeQuery( sql );

        if ( !rs.isBeforeFirst() ) {
            return new Series();
        }

        int seriesId = rs.getInt( "series_id" );

        String seriesName = rs.getString( "series_name" );
        series.setName( seriesName );

        String seriesStatus = rs.getString( "series_status" );
        series.setStatus( seriesStatus );

        String seriesTvdbBanner = rs.getString( "series_tvdb_banner" );
        series.setTvdbBanner( seriesTvdbBanner );

        String seriesTvdbBackdrop = rs.getString( "series_tvdb_backdrop" );
        series.setTvdbBackdrop( seriesTvdbBackdrop );

        String seriesTvdbPoster = rs.getString( "series_tvdb_poster");
        series.setTvdbPoster( seriesTvdbPoster );

        String seriesTmdbBackdrop = rs.getString( "series_tmdb_backdrop" );
        series.setTmdbBackdrop( seriesTmdbBackdrop );

        String seriesTmdbPoster = rs.getString( "series_tmdb_poster" );
        series.setTmdbPoster( seriesTmdbPoster );

        int seriesTvdbId = rs.getInt( "series_tvdb_id" );
        series.setTvdbId( seriesTvdbId );

        int seriesTmdbId = rs.getInt( "series_tmdb_id" );
        series.setTmdbId( seriesTmdbId );

        String seriesDate = rs.getString( "series_date" );
        series.setDateFromString( seriesDate );

        String seriesNetwork = rs.getString( "series_network" );
        series.setNetwork( seriesNetwork );

        int seriesRatingId = rs.getInt( "series_rating_id" );
        series.setRating( Series.Rating.getEnum( getRatingFromDbId( conn, seriesRatingId ) ) );

        ArrayList<String> seriesGenresList = getGenresFromSeriesDbId( conn, seriesId );
        Series.Genre[] seriesGenre = new Series.Genre[seriesGenresList.size()];
        for ( int i=0; i<seriesGenresList.size(); i++ ) {
            seriesGenre[i] = Series.Genre.getEnum( seriesGenresList.get( i ) );
        }
        series.setGenre( seriesGenre );

        rs.close();
        stmt.close();

        series.setSeasons( getSeasonsFromSeriesDbId( conn, series, seriesId ) );

        return series;
    }

    /**
     * Insert Series and respective Seasons and Episodes into database
     *
     * @param   conn                Connection to database
     * @param   series              Series to be inserted in the database
     * @throws  SQLException        It happens if there is a problem connecting to the
     *                              database or if the sql is wrong
     */
    public static void fillSeriesTable ( Connection conn, Series series)
            throws SQLException{
        Statement stmt;
        String sql;

        //THIS NEEDS TO BE UNIVERSAL

        String seriesName = getSqlString( series.getName() );
        String seriesDate = Utilities.DateFormatStandard().format( series.getDate().getTime() );
        int ratingId = getRatingId( conn, series );

        stmt = conn.createStatement();
        sql = "INSERT INTO SERIES(series_id,series_name,series_status," +
                "series_tvdb_banner,series_tvdb_backdrop,series_tvdb_poster," +
                "series_tmdb_backdrop,series_tmdb_poster,series_tvdb_id," +
                "series_tmdb_id,series_date,series_network," +
                "series_rating_id)" +
                "VALUES(NULL,'" + seriesName + "','" + series.getStatus() + "'," +
                "'" + series.getTvdbBanner() + "','" + series.getTvdbBackdrop() +
                "','" + series.getTvdbPoster() + "','" + series.getTmdbBackdrop() +
                "','" + series.getTmdbPoster() + "','" + series.getTvdbId() + "'," +
                "'" + series.getTmdbId() + "','" + seriesDate + "','" +
                series.getNetwork() + "','" + ratingId + "');";
        stmt.executeUpdate( sql );

        int seriesDbId = stmt.getGeneratedKeys().getInt( 1 );

        stmt.close();
        conn.commit();

        //INSERT GENRE
        fillEachGenreTable( conn, series, seriesDbId );

        //INSERT SEASONS WITH RESPECTIVE EPISODES
        fillSeasonTable( conn, series, seriesDbId );
    }

    /**
     * Insert Seasons and respective Episodes into database
     *
     * @param   conn                Connection to database
     * @param   series              Series to which the seasons are related
     * @param   seriesDbId          The Series id in the database
     * @throws  SQLException        It happens if there is a problem connecting to the
     *                              database or if the sql is wrong
     */
    private static void fillSeasonTable ( Connection conn, Series series,
                                          int seriesDbId ) throws SQLException{
        Statement stmt;
        String sql;

        ArrayList<Season> seasonList = series.getSeasons();
        for ( Season season : seasonList ) {
            stmt = conn.createStatement();
            sql = "INSERT INTO SEASON(season_id,season_number,season_tvdb_poster," +
                    "season_tmdb_poster,season_series_id)" +
                    "VALUES(NULL,'" + season.getNumber() + "','" + season.getTvdbPoster() +
                    "','" + season.getTmdbPoster() + "','" + seriesDbId + "');";
            stmt.executeUpdate( sql );

            int seasonDbId = stmt.getGeneratedKeys().getInt( 1 );

            stmt.close();
            conn.commit();

            fillEpisodeTable( conn, season, seasonDbId );
        }
    }

    /**
     * Insert Episodes into database
     *
     * @param   conn                Connection to database
     * @param   season              Season to which the episodes are related
     * @param   seasonDbId          The Season id in the database
     * @throws  SQLException        It happens if there is a problem connecting to the
     *                              database or if the sql is wrong
     */
    private static void fillEpisodeTable ( Connection conn, Season season,
                                           int seasonDbId ) throws SQLException{
        Statement stmt;
        String sql;

        ArrayList<Episode> episodeList = season.getEpisodes();

        for ( Episode episode : episodeList ) {
            String name = getSqlString( episode.getName() );
            String overview = getSqlString( episode.getOverview() );
            String episodeDate = Utilities.getStringFromCalendar( episode.getDate() );//DateFormatStandard().format( episode.getDate().getTime() );

            stmt = conn.createStatement();
            sql = "INSERT INTO EPISODES(episode_id,episode_name,episode_number," +
                    "episode_overview,episode_date,episode_tvdb_image," +
                    "episode_tmdb_image,episode_season_id)" +
                    "VALUES(NULL,'" + name + "','" + episode.getNumber() +
                    "','" + overview + "','" + episodeDate +
                    "','" + episode.getTvdbImage() + "','" + episode.getTmdbImage() +
                    "','" + seasonDbId + "');";
            stmt.executeUpdate( sql );

            stmt.close();
            conn.commit();
        }
    }

    /**
     * NOT SUPPORTED BY SQLITE-JDBC
     *
     * @param   conn                Connection to database
     * @param   season              Season to which the episodes are related
     * @param   seasonDbId          The Season id in the database
     * @throws  SQLException        It happens if there is a problem connecting to the
     *                              database or if the sql is wrong
     */
    private static void fillEpisodeTableUNSUPPORTED ( Connection conn, Season season,
                                                      int seasonDbId ) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO EPISODES(episode_id,episode_name,episode_number," +
                "episode_overview,episode_date,episode_tvdb_image," +
                "episode_tmdb_image,episode_season_id) VALUES";

        ArrayList<Episode> episodeList = season.getEpisodes();

        for ( Episode episode : episodeList ) {
            String name = getSqlString( episode.getName() );
            String overview = getSqlString( episode.getOverview() );

            sql =  sql + "(NULL,'" + name + "','" + episode.getNumber() +
                    "','" + overview + "','" + episode.getDate() +
                    "','" + episode.getTvdbImage() + "','" +
                    episode.getTmdbImage() + "','" + seasonDbId + "'), ";

        }

        sql = sql.substring( 0,sql.length()-2 ) + ";";
        stmt.executeUpdate( sql );

        stmt.close();
        conn.commit();

    }

    /**
     * Get the database id of the Rating of a given Series
     *
     * @param   conn                Connection to database
     * @param   series              Series whose Rating we are querying
     * @return                      The id of the queried Rating
     * @throws  SQLException        It happens if there is a problem connecting to the
     *                              database or if the sql is wrong
     */
    private static int getRatingId ( Connection conn, Series series )
            throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM RATING WHERE rating_name='" +
                series.getRating() + "';";
        int result = stmt.executeQuery( sql ).getInt( "rating_id" );
        stmt.close();
        return result;
    }

    /**
     * Get Rating name from the database given the database id of the Rating
     *
     * @param   conn                Connection to the database
     * @param   seriesRatingDbId    The database id of the Rating
     * @return                      The name of the Rating
     * @throws  SQLException        It happens if there is a problem connecting to
     *                              the database or if the sql is wrong
     */
    private static String getRatingFromDbId ( Connection conn, int seriesRatingDbId )
            throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM RATING WHERE rating_id='" +
                seriesRatingDbId + "';";
        String result = stmt.executeQuery( sql ).getString( "rating_name" );
        stmt.close();
        return result;
    }

    /**
     * Get Genre name from the database given the database id of the Genre
     *
     * @param   conn                Connection to the database
     * @param   seriesGenreDbId     The database id of the genre
     * @return                      The name of the genre
     * @throws  SQLException        It happens if there is a problem connecting to
     *                              the database or if the sql is wrong
     */
    private static String getGenreFromDbId ( Connection conn, int seriesGenreDbId )
            throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM GENRE WHERE genre_id='" + seriesGenreDbId + "';";
        String result = stmt.executeQuery( sql ).getString( "genre_name" );
        stmt.close();
        return result;
    }

    /**
     * Retrieves a list of Seasons related to a series database id
     *
     * @param   conn                Connection to the database
     * @param   series              Series whose seasons are queried
     * @param   seriesId            The database id of the series
     * @return                      The list of episodes related to the series
     * @throws  SQLException        It happens if there is a problem connecting to
     *                              the database or if the sql is wrong
     */
    private static ArrayList<Season> getSeasonsFromSeriesDbId( Connection conn, Series series,
                                                               int seriesId ) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM SEASON WHERE season_series_id='" + seriesId + "';";
        ResultSet rs = stmt.executeQuery( sql );

        ArrayList<Season> result = new ArrayList<Season>();
        while ( rs.next() ) {
            Season tempSeason = new Season();
            int tempSeasonId = rs.getInt( "season_id" );

            int tempSeasonNumber = rs.getInt( "season_number" );
            tempSeason.setNumber( tempSeasonNumber );

            String tempSeasonTvdbPoster = rs.getString( "season_tvdb_poster" );
            tempSeason.setTvdbPoster( tempSeasonTvdbPoster );

            String tempSeasonTmdbPoster = rs.getString( "season_tmdb_poster" );
            tempSeason.setTmdbPoster( tempSeasonTmdbPoster );

            tempSeason.setSeries( series );

            tempSeason.setEpisodes( getEpisodesFromSeasonDbId( conn, tempSeason,  tempSeasonId ) );

            result.add( tempSeason );
        }
        rs.close();
        stmt.close();

        return result;
    }

    /**
     * Retrieves a list of Episodes related to a season database id
     *
     * @param   conn                Connection to the database
     * @param   season              Season whose episodes are queried
     * @param   seasonId            The database id of the season
     * @return                      The list of episodes related to the season
     * @throws  SQLException        It happens if there is a problem connecting to
     *                              the database or if the sql is wrong
     */
    private static ArrayList<Episode> getEpisodesFromSeasonDbId ( Connection conn, Season season,
                                                                  int seasonId ) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM EPISODES WHERE episode_season_id='" + seasonId + "';";
        ResultSet rs = stmt.executeQuery( sql );

        ArrayList<Episode> result = new ArrayList<Episode>();
        while (rs.next()) {
            Episode tempEpisode = new Episode();

            String tempEpisodeName = rs.getString( "episode_name" );
            tempEpisode.setName( tempEpisodeName );

            String tempEpisodeOverview = rs.getString( "episode_overview" );
            tempEpisode.setOverview(tempEpisodeOverview);

            int tempEpisodeNumber = rs.getInt( "episode_number" );
            tempEpisode.setNumber( tempEpisodeNumber );

            String tempEpisodeDateString = rs.getString( "episode_date" );
            tempEpisode.setDateFromString( tempEpisodeDateString );

            String tempEpisodeTvdbImage = rs.getString( "episode_tvdb_image" );
            tempEpisode.setTvdbImage(tempEpisodeTvdbImage);

            String tempSeasonTmdbPoster = rs.getString( "episode_tmdb_image" );
            tempEpisode.setTmdbImage(tempSeasonTmdbPoster);

            tempEpisode.setSeason( season );

            result.add( tempEpisode );
        }
        rs.close();
        stmt.close();

        return result;
    }

    /**
     * Retrieves genres from db given a series database id
     *
     * @param   conn                Connection to the database
     * @param   seriesDbId          The database id of the series
     * @return                      The list of genres related to a series id
     * @throws  SQLException        It happens if there is a problem connecting to
     *                              the database or if the sql is wrong
     */
    private static ArrayList<String> getGenresFromSeriesDbId ( Connection conn,
                                                               int seriesDbId )
            throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM EACHGENRE WHERE eachgenre_series_id='" +
                seriesDbId + "';";
        ResultSet rs = stmt.executeQuery( sql );

        ArrayList<String> result = new ArrayList<String>();
        while ( rs.next() ) {
            int tempGenreId = rs.getInt("eachgenre_genre_id" );
            result.add( getGenreFromDbId( conn, tempGenreId ) );
        }
        rs.close();
        stmt.close();
        return result;
    }

    /**
     * Insert Genres from a Series in database
     *
     * @param   conn                Connection to database
     * @param   series              Series whose Genres we are inserting in the database
     * @param   seriesDbId          The id of the Series in the database
     * @throws  SQLException        It happens if there is a problem connecting to the
     *                              database or if the sql is wrong
     */
    private static void fillEachGenreTable ( Connection conn, Series series,
                                             int seriesDbId ) throws SQLException {
        Statement stmt;
        String sql;
        Series.Genre[] genreList = series.getGenre();
        for (Series.Genre g : genreList) {
            stmt = conn.createStatement();
            sql = "SELECT * FROM GENRE WHERE genre_name='" + g + "';";
            int curGenreId = stmt.executeQuery(sql).getInt("genre_id");
            stmt.close();

            stmt = conn.createStatement();
            sql = "INSERT INTO EACHGENRE(eachgenre_id,eachgenre_series_id," +
                    "eachgenre_genre_id)" +
                    "VALUES(NULL,'" + seriesDbId + "','" + curGenreId + "');";
            stmt.executeUpdate(sql);

        }
    }

    /**
     * Makes sure String is SQL safe
     *
     * @param   input               The String to check
     * @return                      The checked String
     */
    private static String getSqlString ( String input ) {
        return input.replace("'","''").replace("’","''");
    }

}
