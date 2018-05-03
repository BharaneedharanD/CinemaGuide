package com.bharanee.android.cinemaguide;

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

    private String movieTitle;
    private String posterImage;
    private String releaseData;
    private String synopsis;
    private double averageVote;
}
