package com.lvr.threerecom.bean;

/**
 * Created by lvr on 2017/5/19.
 */

public class RatingResultBean {
    /**
     * rating : 5
     * is_rating_success : success
     */

    private int rating;
    private String is_rating_success;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getIs_rating_success() {
        return is_rating_success;
    }

    public void setIs_rating_success(String is_rating_success) {
        this.is_rating_success = is_rating_success;
    }
}
