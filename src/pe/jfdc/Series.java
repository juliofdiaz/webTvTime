package pe.jfdc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Series {
    private String name;
    private String status;
    private String tvdbBanner;
    private String tvdbBackdrop;
    private String tvdbPoster;
    private String tmdbBackdrop;
    private String tmdbPoster;
    private int tvdbId;
    private int tmdbId;
    private Calendar date;
    private String network;
    private Rating rating;
    private Genre[] genre;
    private ArrayList<Season> seasons;

    public Series () {
        this.name = null;
        this.status = null;
        this.tvdbBanner = null;
        this.tvdbBackdrop = null;
        this.tvdbPoster = null;
        this.tmdbBackdrop = null;
        this.tmdbPoster = null;
        this.tvdbId = -1;
        this.tmdbId = -1;
        this.date = null;
        this.network = null;
        this.rating = Rating.UNDEFINED;
        this.genre = null;
        this.seasons = new ArrayList<Season>();
    }

    public void setName ( String newName ) {
        this.name = newName;
    }

    public String getName () {
        return this.name;
    }

    public void setStatus ( String newStatus ) {
        this.status = newStatus;
    }

    public String getStatus () {
        return this.status;
    }

    public void setTvdbBanner ( String newTvdbBanner ) {
        this.tvdbBanner = newTvdbBanner;
    }

    public String getTvdbBanner () {
        return this.tvdbBanner;
    }

    public void setTvdbBackdrop ( String newTvdbFanart ) {
        this.tvdbBackdrop = newTvdbFanart;
    }

    public String getTvdbBackdrop () {
        return this.tvdbBackdrop;
    }

    public void setTvdbPoster ( String newTvdbPoster ) {
        this.tvdbPoster = newTvdbPoster;
    }

    public String getTvdbPoster () {
        return this.tvdbPoster;
    }

    public void setTmdbBackdrop ( String newTmdbFanart ) {
        this.tmdbBackdrop = newTmdbFanart;
    }

    public String getTmdbBackdrop () {
        return this.tmdbBackdrop;
    }

    public void setTmdbPoster ( String newTmdbPoster ) {
        this.tmdbPoster = newTmdbPoster;
    }

    public String getTmdbPoster () {
        return this.tmdbPoster;
    }

    public void setTvdbId ( int newTvdbId ) {
        this.tvdbId = newTvdbId;
    }

    public int getTvdbId () {
        return this.tvdbId;
    }

    public void setTmdbId ( int newTmdbId ) {
        this.tmdbId = newTmdbId;
    }

    public int getTmdbId () {
        return this.tmdbId;
    }

    public void setDate ( Calendar newDate ) {
        this.date = newDate;
    }

    public Calendar getDate () {
        return this.date;
    }

    public void setNetwork ( String newNetwork ) {
        this.network = newNetwork;
    }

    public String getNetwork () {
        return this.network;
    }

    public void setRating ( Rating newRating ) {
        this.rating = newRating;
    }

    public Rating getRating () {
        return this.rating;
    }

    public void setGenre ( Genre[] newGenre ) {
        this.genre = newGenre;
    }

    public Genre[] getGenre () {
        return this.genre;
    }

    public void setSeasons ( ArrayList<Season> newSeasons ) {
        this.seasons = newSeasons;
    }

    public ArrayList<Season> getSeasons () {
        return this.seasons;
    }

    /**
     * Retrieves a season given a season number
     *
     * @param   seasonNumber    The number of the season to retrieve
     * @return                  The season with the number provided
     */
    public Season getSeasonByNumber ( int seasonNumber ) {
        for ( Season curSeason : this.seasons ) {
            if ( curSeason.getNumber() == seasonNumber ) {
                return curSeason;
            }
        }
        return null;
    }

    /**
     * Retrieves the latest season
     *
     * @return                  The last or latest season
     */
    public Season getLastSeason () {
        int max =-1;
        for ( Season temoSeason : this.seasons ){
            if ( temoSeason.getNumber() > max ) {
                max = temoSeason.getNumber();
            }
        }
        return getSeasonByNumber( max );
    }

    /**
     * Allows parsing a String as input to set the date of a Series
     *
     * @param   newDateString   The string that contains information about
     *                          the date of the Series
     */
    public void setDateFromString ( String newDateString ) {
        this.setDate( Utilities.getCalendarFromString( newDateString ) );
    }

    /**
     * Allows parsing a String as input to set the genre of a Series
     *
     * @param   newGenreString  The string that contains information about
     *                          the genre of the Series
     */
    public void setGenreFromString ( String newGenreString ) {
        String[] tempList = newGenreString.split( "\\|" );
        if ( tempList.length > 0 ) {
            Genre[] genreList = new Genre[tempList.length - 1];
            for (int i = 1; i < tempList.length; i++) {
                genreList[i - 1] = Genre.getEnum(tempList[i]);
            }
            this.setGenre(genreList);
        }
    }

    /**
     * Function retrieved the entire information of a given Series from
     * tmdb with its tmdb id
     *
     * @param   tmdbApiKey      The developer api key for tmdb
     */
    public void retrieveFullTmdbInfo ( String tmdbApiKey ) {
        String url = "https://api.themoviedb.org/3/tv/" + this.tmdbId +
                "?api_key=" + tmdbApiKey;
        String str = Utilities.parseJsonUrl( url );
        try {
            JSONObject theJsonObject = (JSONObject) JSONValue.parseWithException(str);

            String seriesTmdbIdString = Utilities.getJsonObjectText( theJsonObject, "id" );
            this.tmdbId = Integer.parseInt( seriesTmdbIdString );

            this.tmdbBackdrop = Utilities.getJsonObjectText( theJsonObject, "backdrop_path" );

            String seriesDateString = Utilities.getJsonObjectText( theJsonObject, "first_air_date" );
            this.setDateFromString( seriesDateString );

            this.status = Utilities.getJsonObjectText( theJsonObject, "status" );

            this.name = Utilities.getJsonObjectText( theJsonObject, "name" );

            this.tmdbPoster = Utilities.getJsonObjectText( theJsonObject, "poster_path" );

            JSONArray networkJsonArray = (JSONArray) theJsonObject.get( "networks" );
            if ( networkJsonArray.size() > 0 ) {
                JSONObject networkJsonObject = (JSONObject) networkJsonArray.get(0);
                this.network = Utilities.getJsonObjectText(networkJsonObject, "name");
            }

            JSONArray genreJsonArray = (JSONArray) theJsonObject.get( "genres" );
            String seriesGenre = "|";
            for ( Object o : genreJsonArray ) {
                JSONObject tempGenreJsonObject = (JSONObject) o;
                String tempSeriesGenre = Utilities.getJsonObjectText( tempGenreJsonObject, "name" );
                tempSeriesGenre = tempSeriesGenre.replace( " & ", "|" );//WARNINNG THERE MAY BE GENRE IN TMDB THAT DO NOT CONFORM
                seriesGenre += tempSeriesGenre+"|";
            }
            this.setGenreFromString(seriesGenre);

            JSONArray seasonJsonArray = (JSONArray) theJsonObject.get( "seasons" );

            this.seasons = getSeasonsFromJsonArray( seasonJsonArray, this, tmdbApiKey );

        } catch ( ParseException e ) {
            System.exit( 0 );
            //email julio because json is not formatted correctly
        }
    }

    /**
     * Retrieves the full info of a Series with a tvdb id
     *
     * @param   tvdbApiKey      The tvdb api key of the developer
     */
    public void retrieveFullTvdbInfo ( String tvdbApiKey ) {

        String url = "http://thetvdb.com/api/" + tvdbApiKey + "/series/" +
                this.tvdbId + "/all/en.xml";

        Document doc = parseXmlUrl( url );
        NodeList data = doc.getElementsByTagName( "Data" ).item( 0 ).getChildNodes();

        Node seriesItem = data.item( 0 );
        Element seriesElement = (Element) seriesItem;

        this.name = getElementTextContent(seriesElement, "SeriesName");

        this.tvdbBanner = getElementTextContent( seriesElement,"banner" );

        String seriesDate = getElementTextContent( seriesElement,"FirstAired" );
        this.setDateFromString( seriesDate );

        this.network = getElementTextContent( seriesElement,"Network" );

        this.status = getElementTextContent( seriesElement,"Status" );

        this.tvdbBackdrop = getElementTextContent( seriesElement,"fanart" );

        this.tvdbPoster = getElementTextContent( seriesElement,"poster" );

        String seriesRating = getElementTextContent( seriesElement,"ContentRating" );
        this.setRating(Rating.getEnum(seriesRating));

        String seriesGenre = getElementTextContent( seriesElement,"Genre" );
        this.setGenreFromString( seriesGenre );

        Hashtable<Integer,String> posterList = getTvdbSeasonPosters(tvdbApiKey,this.tvdbId);
        for ( int i=1; i<data.getLength(); i++ ) {
            if ( data.item( i ).getNodeType() == Node.ELEMENT_NODE ) {  //Ensure current not is not a text node

                Node tempItem = data.item( i );
                Element eElement = (Element) tempItem;
                Episode curEpisode = new Episode();

                Season tempSeason = new Season();
                String episodeSeasonNumberString = getElementTextContent( eElement,"SeasonNumber" );
                int tempEpisodeSeasonNumber = Integer.parseInt( episodeSeasonNumberString );
                tempSeason.setNumber( tempEpisodeSeasonNumber );
                tempSeason.setTvdbPoster( posterList.get( tempEpisodeSeasonNumber ) );
                tempSeason.setSeries( this );
                curEpisode.setSeason( tempSeason );

                String episodeNumberString = getElementTextContent( eElement, "EpisodeNumber" );
                int tempEpisodeNumber = Integer.parseInt( episodeNumberString );
                curEpisode.setNumber( tempEpisodeNumber );

                String tempEpisodeName = getElementTextContent( eElement,"EpisodeName" );
                curEpisode.setName( tempEpisodeName );

                String tempEpisodeOverview = getElementTextContent( eElement,"Overview" );
                curEpisode.setOverview( tempEpisodeOverview );

                String episodeDateString = getElementTextContent( eElement,"FirstAired" );
                curEpisode.setDateFromString( episodeDateString );

                String tempEpisodeImage = getElementTextContent( eElement,"filename" );
                curEpisode.setTvdbImage( tempEpisodeImage );

                if ( this.seasons.contains(tempSeason) ) {
                    this.getSeasonByNumber(tempSeason.getNumber()).getEpisodes().add( curEpisode );
                }else {
                    tempSeason.getEpisodes().add( curEpisode );
                    this.seasons.add(tempSeason);
                }
            }
        }
    }

    /**
     * The json response from tmdb including all the info of a Series includes a list
     * of Seasons. The Seasons are given as a JsonArray and this methods parses that
     * array and returns them as a list of Seasons.
     *
     * @param   jsonArray       JsonArray obtained from the tmdb JsonObject
     * @param   theSeries       The list of seasons belong to this Series
     * @param   tmdbApiKey      The developers api key
     * @return                  A List of Seasons from a same series
     */
    private static ArrayList<Season> getSeasonsFromJsonArray ( JSONArray jsonArray,
                                                               Series theSeries,
                                                               String tmdbApiKey ) {
        ArrayList<Season> result = new ArrayList<Season>();
        for ( Object o : jsonArray ) {
            JSONObject tempSeasonJsonObject = (JSONObject) o;
            Season tempSeason = new Season();

            String tempSeasonNumberString = Utilities.getJsonObjectText( tempSeasonJsonObject,
                    "season_number" );
            int tempSeasonNumber = Integer.parseInt( tempSeasonNumberString );
            tempSeason.setNumber( tempSeasonNumber );

            String tempSeasonTmdbPoster = Utilities.getJsonObjectText( tempSeasonJsonObject,
                    "poster_path" );
            tempSeason.setTmdbPoster( tempSeasonTmdbPoster );

            tempSeason.setSeries( theSeries );

            tempSeason.setTmdbEpisodes( tmdbApiKey );

            result.add( tempSeason );
        }
        return result;
    }

    /**
     * Retrieves the text content of an Element object
     *
     * @param   theElement      The element that will get its content retrieved
     * @param   tagName         The name of the tag to get the text
     * @return                  The text content of a tag of an element
     */
    private static String getElementTextContent ( Element theElement,
                                                  String tagName ) {
        String theTextContent;
        try{
            NodeList theNodeList =  theElement.getElementsByTagName( tagName );
            Node theNode = theNodeList.item( 0 );
            theTextContent = theNode.getTextContent();
        }catch ( Exception e ) {
            theTextContent = "";
        }

        return theTextContent;
    }

    /**
     * Encapsulate XML url stream into a parseable Document
     *
     * @param   url             The url of the XML
     * @return                  A Document with the parsed XML
     */
    private static Document parseXmlUrl ( String url ) {
        Document doc = null;
        DocumentBuilderFactory dbf;
        DocumentBuilder db = null;

        try{
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse(new URL(url).openStream());
        }catch ( ParserConfigurationException pce ) {
            //System.exit(0);
            //email julio beacause there is something major
        }catch ( SAXException se ) {
            //System.exit(0);
            //email julio because xml of tvdb is not well formatted
        }catch ( IOException ioe ) {
            doc = db.newDocument();
            doc.appendChild( doc.createElement( "Data" ) );
        }

        return doc;
    }

    /**
     * Function retrieved the entire information of a given Series as long
     * as that Series hold a tmdb id
     *
     * @param   tmdbApiKey      The developer api key for tmdb
     * @param   inputSeries     The Series to be queried
     * @return                  The full information of the Series
     */
    public static Series retrieveFullTmdbSeries (String tmdbApiKey,
                                                 Series inputSeries){
        if ( inputSeries.getTmdbId() == -1 ) {
            return new Series();
        }
        inputSeries.retrieveFullTmdbInfo( tmdbApiKey );
        return inputSeries;
    }

    /**
     * Retrieves the full info of a Series with a Series object as long as it
     * contains a tvdb id
     *
     * @param   tvdbApiKey      The tvdb api key of the developer
     * @param   inputSeries     Series MUST have a tvdb id
     * @return                  The full info on the series
     */
    public static Series retrieveFullTvdbSeries ( String tvdbApiKey, Series inputSeries ) {
        if ( inputSeries.getTvdbId() == -1 ) {
            return new Series();
        }
        inputSeries.retrieveFullTvdbInfo(tvdbApiKey);
        return inputSeries;
    }

    /**
     * Gets the highest rated poster for each season of a series with its tvdb id
     *
     * @param   tvdbApiKey      The tvdb api key of the developer
     * @param   tvdbSeriesId    The tvdb id of the series of interest
     * @return                  A hash table where the key is the season number
     *                          and the target is the path poster image
     */
    public static Hashtable<Integer, String> getTvdbSeasonPosters (String tvdbApiKey,
                                                                   int tvdbSeriesId) {
        String bannerUrl = "http://thetvdb.com/api/" + tvdbApiKey + "/series/" +
                tvdbSeriesId + "/banners.xml";

        Document doc = parseXmlUrl( bannerUrl );
        NodeList data = doc.getElementsByTagName( "Banners" ).item(0).getChildNodes();

        Hashtable<Integer,String> list = new Hashtable<Integer,String>();
        Hashtable<Integer,Double> rating = new Hashtable<Integer,Double>();
        for (int i = 0; i < data.getLength(); i++) {
            if (data.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Node tempItem = data.item(i);
                Element eElement = (Element) tempItem;

                boolean isPoster = getElementTextContent( eElement, "BannerType" ).
                        equals( "season" );
                boolean isSeasonWide = getElementTextContent( eElement, "BannerType2" ).
                        equals( "seasonwide" );

                if ( isPoster && !isSeasonWide ) {

                    String tempBannerPath = getElementTextContent( eElement,
                            "BannerPath" );
                    Integer tempSeason = Integer.parseInt( tempBannerPath.
                            split( ".jpg" )[0].split( "-" )[1] );
                    String curRatingString = getElementTextContent( eElement, "Rating");
                    Double curRating = curRatingString.equals("") ? 0 :
                            Double.parseDouble( curRatingString );

                    if ( list.keySet().contains( tempSeason ) ) {
                        if ( curRating >= rating.get(tempSeason) ) {
                            list.put( tempSeason,tempBannerPath );
                            rating.put(tempSeason, curRating);
                        }
                    }else{
                        list.put(tempSeason,tempBannerPath);
                        rating.put(tempSeason,curRating);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Parses the tvdb basic tv series XML stream response into a list
     * of the tv Series that match the input
     *
     * @param   inputSeriesName The String to search the tvdb id
     * @return                  The list of Series based on the search
     *                          string
     */
    public static ArrayList<Series> getTvdbSeries ( String inputSeriesName ) {
        inputSeriesName = inputSeriesName.replace( " ","%20" );
        String url = "http://thetvdb.com/api/GetSeries.php?seriesname="+inputSeriesName;
        Document doc = parseXmlUrl(url);
        NodeList data = doc.getElementsByTagName( "Data" ).item( 0 ).getChildNodes();  //Root of all data

        ArrayList<Series> result = new ArrayList<Series>();

        for ( int i=1; i<data.getLength(); i++ ) {
            if ( data.item( i ).getNodeType() == Node.ELEMENT_NODE ) {  //Ensure current node is not a text node
                Series tempSeries = new Series();

                Node tempItem = data.item( i );
                Element eElement = (Element) tempItem;

                String tempSeriesName = getElementTextContent( eElement,"SeriesName" );
                tempSeries.setName( tempSeriesName );

                String tempSeriesBanner = getElementTextContent( eElement,"banner" );
                tempSeries.setTvdbBanner( tempSeriesBanner );

                String tvdbIdString = getElementTextContent( eElement,"seriesid" );
                int tempTvdbId = Integer.parseInt( tvdbIdString );
                tempSeries.setTvdbId( tempTvdbId );

                String seriesDateString = getElementTextContent( eElement,"FirstAired" );
                tempSeries.setDateFromString( seriesDateString );

                String tempSeriesNetwork = getElementTextContent( eElement,"Network" );
                tempSeries.setNetwork( tempSeriesNetwork );

                result.add( tempSeries );
            }
        }

        return result;
    }

    /**
     * Parses the tmdb basic tv series JSON response into a list of the tv
     * Series that match the input
     *
     * @param   inputSeriesName The String to search the tmdb id
     * @param   tmdbApiKey      The developer's api key for tmdb
     * @return                  The list of Series based on the search string
     *
     */
    public static ArrayList<Series> getTmdbSeries ( String inputSeriesName,
                                                    String tmdbApiKey ) {
        inputSeriesName = inputSeriesName.replace( " ","%20" );

        String url = "https://api.themoviedb.org/3/search/tv?api_key=" +
                tmdbApiKey+"&query="+inputSeriesName;

        String str = Utilities.parseJsonUrl(url);
        ArrayList<Series> result = new ArrayList<Series>();

        try {
            JSONObject theJsonObject = (JSONObject) JSONValue.parseWithException( str );
            JSONArray jsonObjectArray = (JSONArray) theJsonObject.get( "results" );

            for ( Object o:jsonObjectArray ) {
                Series tempSeries = new Series();
                JSONObject tempJsonObject = (JSONObject) o;

                String tempSeriesName = Utilities.getJsonObjectText( tempJsonObject, "original_name" );
                tempSeries.setName( tempSeriesName );

                String tempSeriesBackdrop = Utilities.getJsonObjectText( tempJsonObject, "backdrop_path" );
                tempSeries.setTmdbBackdrop( tempSeriesBackdrop );

                String tempSeriesPoster = Utilities.getJsonObjectText( tempJsonObject, "poster_path" );
                tempSeries.setTmdbPoster( tempSeriesPoster );

                String seriesDateString = Utilities.getJsonObjectText( tempJsonObject, "first_air_date" );
                tempSeries.setDateFromString( seriesDateString );

                String seriesTmdbIdString = tempJsonObject.get( "id" ).toString();
                int tempSeriesTmdbId = Integer.parseInt( seriesTmdbIdString );
                tempSeries.setTmdbId( tempSeriesTmdbId );

                int tempSeriesTvdbId = getTvdbIdFromTmdbId( tempSeriesTmdbId,tmdbApiKey );
                tempSeries.setTvdbId( tempSeriesTvdbId );

                result.add( tempSeries );
            }
        } catch ( ParseException e ) {
            System.exit( 0 );
            //email julio because json is not formatted correctly
        }
        return result;
    }

    /**
     * Retrieves the tvdb id from themoviedb using tmdb id
     *
     * @param   tmdbId          Tmdb id of the query series
     * @param   tmdbApiKey      The developer's api key for tmdb
     * @return                  The respective tvdb id
     */
    public static int getTvdbIdFromTmdbId( int tmdbId, String tmdbApiKey ){
        String url = "https://api.themoviedb.org/3/tv/" + tmdbId +
                "/external_ids?api_key=" + tmdbApiKey;

        String str = Utilities.parseJsonUrl( url );
        int result = -1;

        try {
            JSONObject theJsonObject = (JSONObject) JSONValue.parseWithException( str );
            String tvdbIdString = theJsonObject.get( "tvdb_id" ).toString();
            result = Integer.parseInt( tvdbIdString );
        } catch ( NullPointerException e ) {
            return -1;
        } catch ( ParseException e ) {
            System.exit( 0 );
            //email julio because json is not formatted correctly
        }

        return result;
    }

    /**
     * Returns a string summarizing everything about this Series
     *
     * @return      a String describing this Series
     */
    public String toString () {
        return "[name:" + this.name+", status:" + this.status + ", tvdbBanner:" +
                this.tvdbBanner + ", tvdbBackdrop:" + this.tvdbBackdrop + ", tvdbPoster:" +
                this.tvdbPoster + ", tmdbBackdrop:" + this.tmdbBackdrop + ", tmdbPoster:" +
                this.tmdbPoster + ", tvdbId:" + this.tvdbId + ", tmdbId:" + this.tmdbId +
                ", date:" + this.date + ", network:" + this.network + ", rating:" +
                this.rating + ", genre:" + this.genreToString() + "]";
    }

    /**
     * Returns a string summarizing the array of Genre of this series
     *
     * @return      a String to tell the contents of the genre of this series
     */
    public String genreToString(){
        if ( this.genre == null || genre.length == 0 ) {
            return "(UNDEFINED)";
        }
        String result = "(";
        for ( int i=0; i<this.genre.length-1; i++ ) {
            result = result+this.genre[i] + ",";
        }
        result = result+this.genre[this.genre.length-1]+")";

        return result;
    }

    public enum Genre {
        ACTION, ADVENTURE, ANIMATION, CHILDREN, COMEDY, CRIME,
        DOCUMENTARY, DRAMA, FAMILY, FANTASY, FOOD, GAME_SHOW,
        HOME_AND_GARDEN, HORROR, MINI__SERIES, MYSTERY, NEWS,
        REALITY, ROMANCE, SCIENCE__FICTION, SOAP, SPECIAL_INTEREST,
        SPORT, SUSPENSE, TALK_SHOW, THRILLER, TRAVEL, UNDEFINED,
        WESTERN;

        public static Genre getEnum ( String input ) {
            if(input.equals("") || input.equals( null ) ){
                return Genre.valueOf( "UNDEFINED" );
            }
            input = input.toUpperCase();
            input = input.replace( "-", "__" );
            input = input.replace( " ", "_" );
            return Genre.valueOf( input );
        }

        @Override
        public String toString() {
            if ( !super.toString().equals( "UNDEFINED" ) || super.toString().equals( null ) ) {
                return super.toString().replace("__", "-").replace("_", " ");
            }
            return "UNDEFINED";
        }
    }

    public enum Rating {

        TV__Y, TV__Y7, TV__G, TV__PG, TV__14, TV__MA, UNDEFINED;

        public static Rating getEnum(String input) {
            if(input.equals("") || input.equals( null ) ){
                return Rating.valueOf( "UNDEFINED" );
            }
            input = input.toUpperCase();
            input = input.replace( "-", "__" );
            input = input.replace( " ", "_" );
            return Rating.valueOf( input );
        }

        @Override
        public String toString() {
            if ( !super.toString().equals( "UNDEFINED" ) || super.toString().equals( null ) ) {
                return super.toString().replace("__", "-").replace("_", " ");
            }
            return "UNDEFINED";
        }
    }
}