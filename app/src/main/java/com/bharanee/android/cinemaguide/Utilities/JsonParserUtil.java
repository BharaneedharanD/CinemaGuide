package com.bharanee.android.cinemaguide.Utilities;
import android.content.Context;
import com.bharanee.android.cinemaguide.Modal.MovieDetails;
import com.bharanee.android.cinemaguide.Modal.MovieXtraDetails;
import com.bharanee.android.cinemaguide.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParserUtil {

    public MovieDetails[] returnJson(String str, Context context) throws JSONException {
     MovieDetails[] parsedresult=null;

        JSONObject jsonObject=new JSONObject(str);
        int max_pages=jsonObject.getInt(context.getString(R.string.TotalPages));
        NetworkUtils.final_page=max_pages;
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
        movieObject.setMovieTitle(jsonObject.getString(context.getString(R.string.MovieTitle)));
        movieObject.setPosterImage(jsonObject.getString(context.getResources().getString(R.string.PosterPath)));
        movieObject.setAverageVote(jsonObject.getDouble(context.getString(R.string.AverageScore)));
        movieObject.setSynopsis(jsonObject.getString(context.getString(R.string.Synopsis)));
        movieObject.setBackdropImage(jsonObject.getString(context.getString(R.string.backdrop_path)));
        movieObject.setReleaseData(jsonObject.getString(context.getString(R.string.releaseDate)));

        //set all trailers video paths

        return movieObject;
    }

    public MovieXtraDetails returnReviews(String next, Context context, MovieXtraDetails movieObject) throws JSONException{
        MovieXtraDetails object=movieObject;
        JSONObject jsonObject=new JSONObject(next);
        JSONArray reviewArrays=jsonObject.getJSONArray(context.getString(R.string.results));
        for (int i=0;i<reviewArrays.length();i++)
        {
            JSONObject movieReview=reviewArrays.getJSONObject(i);
            object.addReviews(movieReview.getString("author")+"-"+movieReview.getString("content"));
        }


    return object;
    }

    public ArrayList<String> returnVideos(String next, Context context) throws JSONException{
        ArrayList<String> moviekeys=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(next);
        JSONArray reviewArrays=jsonObject.getJSONArray(context.getString(R.string.results));
        for (int i=0;i<reviewArrays.length();i++)
        {
            JSONObject movieReview=reviewArrays.getJSONObject(i);
            moviekeys.add(movieReview.getString("key"));
        }
    return moviekeys;
    }
}
