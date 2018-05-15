package com.bharanee.android.cinemaguide;

import java.util.ArrayList;

class MovieXtraDetails {
    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getReleaseData() {
        return releaseData;
    }

    public void setReleaseData(String releaseData) {
        this.releaseData = releaseData;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getAverageVote() {
        return averageVote;
    }

    public void setAverageVote(double averageVote) {
        this.averageVote = averageVote;
    }

    public String getBackdropImage() {
        return backdropImage;
    }

    public void setBackdropImage(String backdropImage) {
        this.backdropImage = backdropImage;
    }

    private String movieTitle;
    private String posterImage;
    private String releaseData;
    private String synopsis;
    private double averageVote;
    private String backdropImage;
    private ArrayList<String> videos=new ArrayList<>();
    private ArrayList<String> reviews=new ArrayList<>();

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void addReviews(String review) {
        this.reviews.add(review);
    }
    public void setReviews(ArrayList<String> reviews){
        this.reviews=reviews;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void addVideos(String values) {
        this.videos.add(values);
    }
}
