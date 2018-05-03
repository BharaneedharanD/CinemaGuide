package com.bharanee.android.cinemaguide;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParserUtil {

    public MovieDetails[] returnJson(String str, Context context) throws JSONException {
     MovieDetails[] parsedresult=null;

        JSONObject jsonObject=new JSONObject(str);
        int max_pages=jsonObject.getInt(context.getString(R.string.TotalPages));
        NetworkUtils.getNetworkObject().final_page=max_pages;
        JSONArray movieArray = jsonObject.getJSONArray(context.getResources().getString(R.string.results));

        parsedresult = new MovieDetails[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            MovieDetails movieObject=new MovieDetails();
            movieObject.setPoster_Path(movie.getString(context.getResources().getString(R.string.PosterPath)));
            movieObject.setMovie_Id(movie.getInt(context.getResources().getString(R.string.ID)));
            parsedresult[i]=movieObject;
        }


            return parsedresult;
    }

    public MovieXtraDetails returnDetails(String next, Context context) throws JSONException {
        MovieXtraDetails movieObject=new MovieXtraDetails();
        JSONObject jsonObject=new JSONObject(next);
        movieObject.setMovieTitle(jsonObject.getString(context.getResources().getString(R.string.MovieTitle)));
        movieObject.setPosterImage(jsonObject.getString(context.getResources().getString(R.string.PosterPath)));
        movieObject.setAverageVote(jsonObject.getDouble(context.getResources().getString(R.string.AverageScore)));
        movieObject.setSynopsis(jsonObject.getString(context.getResources().getString(R.string.Synopsis)));
        movieObject.setReleaseData(jsonObject.getString(context.getResources().getString(R.string.releaseDate)));
        return movieObject;
    }
}
