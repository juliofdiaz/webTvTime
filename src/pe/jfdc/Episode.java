package pe.jfdc;

import java.util.Calendar;

public class Episode{
    private String name;
    private int number;
    private Season theSeason;
    private String overview;
    private Calendar date;
    private String tvdbImage;
    private String tmdbImage;

    public Episode () {
        this.name = null;
        this.number = -1;
        this.theSeason = null;
        this.overview = null;
        this.date = null;
        this.tvdbImage = null;
        this.tmdbImage = null;
    }

    public void setName ( String newName ) {
        this.name = newName;
    }

    public String getName () {
        return this.name;
    }

    public void setNumber ( int newNumber ) {
        this.number = newNumber;
    }

    public int getNumber () {
        return this.number;
    }

    public void setSeason ( Season newSeason ) {
        this.theSeason = newSeason;
    }

    public Season getSeason () {
        return this.theSeason;
    }

    public void setOverview ( String newOverview ) {
        this.overview = newOverview;
    }

    public String getOverview () {
        return this.overview;
    }

    public void setDate ( Calendar newDate ) {
        this.date = newDate;
    }

    public Calendar getDate () {
        return this.date;
    }

    public void setTvdbImage ( String newTvdbImage ) {
        this.tvdbImage = newTvdbImage;
    }

    public String getTvdbImage () {
        return this.tvdbImage;
    }

    public void setTmdbImage ( String newTmdbImage ) {
        this.tmdbImage = newTmdbImage;
    }

    public String getTmdbImage () {
        return this.tmdbImage;
    }

    /**
     * This method parses a String into a Calendar given the format of the app
     *
     * @param   newDateString       The String input
     */
    public void setDateFromString ( String newDateString ) {
        this.setDate( Utilities.getCalendarFromString( newDateString ) );
    }

    public String toString(){
        return "[name:" + this.name + ", number:" + this.number +
                ", season:" + this.theSeason + ", date:" +
                this.date + ", tvdbImage:" + this.tvdbImage +
                ", tmdbImage:" + tmdbImage + "]";
    }
}