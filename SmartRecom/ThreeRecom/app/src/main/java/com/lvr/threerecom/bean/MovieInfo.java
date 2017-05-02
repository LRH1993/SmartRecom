package com.lvr.threerecom.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lvr on 2017/4/22.
 */

public class MovieInfo implements Parcelable {

    /**
     * movie_average_score : 3.0
     * movie_download_url : http://us.imdb.com/M/title-exact?Toy%20Story%20(1995)
     * movie_genres : animation children comedy
     * movie_id : 1
     * movie_is_hot : false
     * movie_name : Toy Story (1995)
     * movie_picture_url : http://121.42.174.147:8080/movieImages/1.jpg
     * movie_playcount : 0
     * movie_ratings_times : 0
     * movie_release_date : 1995-01-01
     */

    private double movie_average_score;
    private String movie_download_url;
    private String movie_genres;
    private int movie_id;
    private boolean movie_is_hot;
    private String movie_name;
    private String movie_picture_url;
    private int movie_playcount;
    private int movie_ratings_times;
    private String movie_release_date;

    public double getMovie_average_score() {
        return movie_average_score;
    }

    public void setMovie_average_score(double movie_average_score) {
        this.movie_average_score = movie_average_score;
    }

    public String getMovie_download_url() {
        return movie_download_url;
    }

    public void setMovie_download_url(String movie_download_url) {
        this.movie_download_url = movie_download_url;
    }

    public String getMovie_genres() {
        return movie_genres;
    }

    public void setMovie_genres(String movie_genres) {
        this.movie_genres = movie_genres;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public boolean isMovie_is_hot() {
        return movie_is_hot;
    }

    public void setMovie_is_hot(boolean movie_is_hot) {
        this.movie_is_hot = movie_is_hot;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getMovie_picture_url() {
        return movie_picture_url;
    }

    public void setMovie_picture_url(String movie_picture_url) {
        this.movie_picture_url = movie_picture_url;
    }

    public int getMovie_playcount() {
        return movie_playcount;
    }

    public void setMovie_playcount(int movie_playcount) {
        this.movie_playcount = movie_playcount;
    }

    public int getMovie_ratings_times() {
        return movie_ratings_times;
    }

    public void setMovie_ratings_times(int movie_ratings_times) {
        this.movie_ratings_times = movie_ratings_times;
    }

    public String getMovie_release_date() {
        return movie_release_date;
    }

    public void setMovie_release_date(String movie_release_date) {
        this.movie_release_date = movie_release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.movie_average_score);
        dest.writeString(this.movie_download_url);
        dest.writeString(this.movie_genres);
        dest.writeInt(this.movie_id);
        dest.writeByte(this.movie_is_hot ? (byte) 1 : (byte) 0);
        dest.writeString(this.movie_name);
        dest.writeString(this.movie_picture_url);
        dest.writeInt(this.movie_playcount);
        dest.writeInt(this.movie_ratings_times);
        dest.writeString(this.movie_release_date);
    }

    public MovieInfo() {
    }

    protected MovieInfo(Parcel in) {
        this.movie_average_score = in.readDouble();
        this.movie_download_url = in.readString();
        this.movie_genres = in.readString();
        this.movie_id = in.readInt();
        this.movie_is_hot = in.readByte() != 0;
        this.movie_name = in.readString();
        this.movie_picture_url = in.readString();
        this.movie_playcount = in.readInt();
        this.movie_ratings_times = in.readInt();
        this.movie_release_date = in.readString();
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
