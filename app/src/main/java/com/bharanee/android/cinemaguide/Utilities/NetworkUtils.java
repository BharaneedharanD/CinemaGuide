package com.bharanee.android.cinemaguide.Utilities;

import android.content.Context;

import com.bharanee.android.cinemaguide.Modal.MovieDetails;
import com.bharanee.android.cinemaguide.Modal.MovieXtraDetails;
import com.bharanee.android.cinemaguide.R;

import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NetworkUtils {
    public static int curr_page=1,final_page=0;
    private static MovieDetails[] result;
    private static MovieXtraDetails movieXtraDetails=null;
    private static ArrayList<String> movieVideoKeys=null;
    //private static NetworkUtils networkObject=null;
    private NetworkUtils(){

    }
    /*public static NetworkUtils getNetworkObject(){
        if (networkObject==null)
        {networkObject=new NetworkUtils();}
        return networkObject;
    }*/
    /*public static void resetObject(){
        networkObject=null;

    }*/
public static MovieDetails[] getJson(String sort_type,Context context){
     String urlstring=context.getResources().getString(R.string.BASE_URL)+sort_type+context.getResources().getString(R.string.param_api_key)
             +context.getResources().getString(R.string.API_KEY)+context.getResources().getString(R.string.param_page_num)+curr_page;
    result=null;
    try {
        URL url=new URL(urlstring);
        execute(url,context.getString(R.string.resultsTypeLists),context,null);
    } catch (MalformedURLException e) {
        e.printStackTrace();
    }finally {
        return result;
    }

}




    public static MovieXtraDetails getDetails(int movieId,Context context){
    String urlString=context.getResources().getString(R.string.BASE_URL)+
            movieId+context.getResources().getString(R.string.param_api_key)+context.getResources().getString(R.string.API_KEY);
    try{
        URL url=new URL(urlString);
        execute(url,context.getString(R.string.resultsTypeDetails),context,null);

    }catch (MalformedURLException e){e.printStackTrace();}
        finally {
        return movieXtraDetails;
    }

}

public static MovieXtraDetails getReviews(int movieId,Context context,MovieXtraDetails movieObject){
        String urlString=context.getString(R.string.BASE_URL)
                +movieId
                +"/reviews"
                +context.getString(R.string.param_api_key)
                +context.getString(R.string.API_KEY);
        try{
            URL url=new URL(urlString);
            execute(url,"reviews",context,movieObject);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        finally {
            return movieXtraDetails;
        }
}


        protected static void execute(URL url, String type, Context context,MovieXtraDetails movieObject) {
            URL urlstring=url;
            HttpURLConnection urlConnection=null;
            try {
                urlConnection= (HttpURLConnection) urlstring.openConnection();
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    if (type.equals(context.getString(R.string.resultsTypeLists)))
                    getParsedString(scanner.next(),context);
                    else if (type.equals(context.getString(R.string.resultsTypeDetails)))
                     getParsedDetails(scanner.next(),context);
                    else if (type.equals("reviews"))
                        getMovieReviews(scanner.next(),context,movieObject);
                    else if (type.equals("videos"))
                        getMovies(scanner.next(),context);
                }
            }
            catch (IOException e){e.printStackTrace();}
            finally {
                urlConnection.disconnect();
            }


        }

    private static void getMovies(String next, Context context) {
        JsonParserUtil obj=new JsonParserUtil();
        try {
            movieVideoKeys=obj.returnVideos(next,context);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getMovieReviews(String next, Context context, MovieXtraDetails movieObject) {
        JsonParserUtil obj=new JsonParserUtil();
        try {
             movieXtraDetails=obj.returnReviews(next,context,movieObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getParsedDetails(String next, Context context) {
        JsonParserUtil obj=new JsonParserUtil();
        try {
            movieXtraDetails= obj.returnDetails(next,context);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected static void getParsedString(String s, Context context) {
            JsonParserUtil obj=new JsonParserUtil();
            try {
               result= obj.returnJson(s,context);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    public static ArrayList<String> getVideos(int movieId, Context context) {
        String urlString=context.getString(R.string.BASE_URL)
                +movieId
                +"/videos"
                +context.getString(R.string.param_api_key)
                +context.getString(R.string.API_KEY);
        try{
            URL url=new URL(urlString);
            execute(url,"videos",context,null);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        finally {
            return movieVideoKeys;
        }
    }


}
