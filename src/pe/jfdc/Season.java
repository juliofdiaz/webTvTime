package pe.jfdc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class Season{
    private Series series;
    private int number;
    private String tvdbPoster;
    private String tmdbPoster;
    private ArrayList<Episode> episodes;

    public Season () {
        this.series = null;
        this.number = -1;
        this.tvdbPoster = null;
        this.tmdbPoster = null;
        this.episodes = new ArrayList<Episode>();
    }

    public void setSeries ( Series newSeries ) {
        this.series = newSeries;
    }

    public Series getSeries () {
        return this.series;
    }

    public void setNumber ( int newNumber ) {
        this.number = newNumber;
    }

    public int getNumber () {
        return this.number;
    }

    public void setTvdbPoster (String newTvdbPoster) {
        this.tvdbPoster = newTvdbPoster;
    }

    public String getTvdbPoster () {
        return this.tvdbPoster;
    }

    public void setTmdbPoster ( String newTmdbPoster ) {
        this.tmdbPoster = newTmdbPoster;
    }

    public String getTmdbPoster () {
        return this.tmdbPoster;
    }

    public void setEpisodes ( ArrayList<Episode> newEpisodes ) {
        this.episodes = newEpisodes;
    }

    public ArrayList<Episode> getEpisodes () {
        return this.episodes;
    }

    /**
     * Retrieves an Episode given that episodes number
     *
     * @param seasonNumber  the number of the episode
     * @return              the episode with the number
     */
    public Episode getEpisodeByNumber ( int seasonNumber ) {
        for ( Episode curEpisode : this.episodes ) {
            if ( curEpisode.getNumber() == seasonNumber ) {
                return curEpisode;
            }
        }
        return null;
    }

    /**
     * This method acquires all the Episodes in a Season from tmdb as long as the season
     * has a number and the season holds a series with a tmdb id
     *
     * @param   tmdbApiKey  the developers api key
     */
    public void setTmdbEpisodes ( String tmdbApiKey ) {
        if ( this.number == -1 || this.series.getTmdbId() == -1 ) {
            this.setEpisodes(new ArrayList<Episode>());
        }

        String url = "https://api.themoviedb.org/3/tv/" + this.series.getTmdbId() +
                "/season/" + this.number + "?api_key=" + tmdbApiKey;
        String str = Utilities.parseJsonUrl( url );

        try {
            JSONObject theJsonObject = (JSONObject) JSONValue.parseWithException(str);

            JSONArray genreJsonArray = (JSONArray) theJsonObject.get( "episodes" );
            for ( Object o : genreJsonArray ) {
                JSONObject tempGenreJsonObject = (JSONObject) o;
                Episode tempEpisode = new Episode();

                String tempEpisodeName = Utilities.getJsonObjectText( tempGenreJsonObject, "name" );
                tempEpisode.setName(tempEpisodeName);

                String tempEpisodeNumberString = Utilities.getJsonObjectText( tempGenreJsonObject,
                        "episode_number" );
                int tempEpisodeNumber = Integer.parseInt( tempEpisodeNumberString );
                tempEpisode.setNumber(tempEpisodeNumber);

                String tempEpisodeOverview = Utilities.getJsonObjectText( tempGenreJsonObject,
                        "overview" );
                tempEpisode.setOverview(tempEpisodeOverview);

                String tempEpisodeDateString = Utilities.getJsonObjectText( tempGenreJsonObject,
                        "air_date" );
                tempEpisode.setDateFromString(tempEpisodeDateString);

                String tempEpisodeTmdbImage = Utilities.getJsonObjectText( tempGenreJsonObject,
                        "still_path" );
                tempEpisode.setTmdbImage( tempEpisodeTmdbImage );

                tempEpisode.setSeason( this );

                this.episodes.add(tempEpisode);
            }

        } catch ( ParseException e ) {
            System.exit( 0 );
            //email julio because json is not formatted correctly
        }
    }

    /**
     * This method summarizes the information about this season
     *
     * @return              The String representation of a Season
     */
    public String toString(){
        return "[number:" + this.number + ", tvdbPoster:" + this.tvdbPoster +
                ", tmdbPoster:" + this.tmdbPoster + "]";
    }

    /**
     * This method compares two Seasons. If they are both are part of the same Series
     * and they have the same number, then they are said to be equal.
     *
     * @param other         the Season to compare to this instance of Season
     * @return              true if Series and number is the same
     */
    public boolean equals(Object other){
        Season anotherSeason = (Season)other;

        if ( !( other instanceof Season ) ) {
            return false;
        }

        boolean isSameNumber = this.number == anotherSeason.getNumber();
        boolean isSameSeries = this.series.getName().equals( anotherSeason.
                getSeries().getName() );

        return isSameNumber && isSameSeries;
    }

    /**
     * This method compares the episodes of another Season to this instance of Season.
     * If the number of episodes is the same and the episodes share the same name, then
     * both Seasons are said to have the same episodes.
     *
     * @param other         the Season to campare this instance of Season to
     * @return              true if episodes are the same
     */
    public boolean hasSameEpisodes ( Season other ){
        boolean isSameEpisodes;

        if( this.episodes.size() == other.getEpisodes().size() ) {
            for (Episode eachThisEpisode : this.episodes) {
                Episode eachOtherEpisode = other.getEpisodeByNumber(eachThisEpisode.getNumber());
                if ( !eachOtherEpisode.getName().equals(eachThisEpisode.getName()) ) {
                    System.out.println( "error: episodes have different names. " );
                    return false;
                }
            }
            isSameEpisodes = true;
        } else {
            System.out.println( this.getSeries().getName()+" "+ this.getNumber() +" error: not the same size." );
            isSameEpisodes = false;
        }
        return isSameEpisodes;
    }
}