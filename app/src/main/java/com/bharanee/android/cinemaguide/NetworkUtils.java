package com.bharanee.android.cinemaguide;

import android.content.Context;

import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    public int curr_page=1,final_page=0;
    MovieDetails[] result;
    MovieXtraDetails movieXtraDetails=null;
    private static NetworkUtils networkObject=null;
    private NetworkUtils(){

    }
    public static NetworkUtils getNetworkObject(){
        if (networkObject==null)
        {networkObject=new NetworkUtils();}
        return networkObject;
    }
    public static void resetObject(){
        networkObject=null;

    }
public MovieDetails[] getJson(String sort_type,Context context){
     String urlstring=context.getResources().getString(R.string.BASE_URL)+sort_type+context.getResources().getString(R.string.param_api_key)
             +context.getResources().getString(R.string.API_KEY)+context.getResources().getString(R.string.param_page_num)+curr_page;
    result=null;
    try {
        URL url=new URL(urlstring);
        execute(url,context.getString(R.string.resultsTypeLists),context);
    } catch (MalformedURLException e) {
        e.printStackTrace();
    }finally {
        return result;
    }

}
public MovieXtraDetails getDetails(int movieId,Context context){
    String urlString=context.getResources().getString(R.string.BASE_URL)+
            movieId+context.getResources().getString(R.string.param_api_key)+context.getResources().getString(R.string.API_KEY);
    try{
        URL url=new URL(urlString);
        execute(url,context.getString(R.string.resultsTypeDetails),context);
    }catch (MalformedURLException e){e.printStackTrace();}
        finally {
        return movieXtraDetails;
    }

}

        protected void execute(URL url, String type, Context context) {
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
                }
            }
            catch (IOException e){e.printStackTrace();}
            finally {
                urlConnection.disconnect();
            }


        }

    private void getParsedDetails(String next, Context context) {
        JsonParserUtil obj=new JsonParserUtil();
        try {
            movieXtraDetails= obj.returnDetails(next,context);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void getParsedString(String s, Context context) {
            JsonParserUtil obj=new JsonParserUtil();
            try {
               result= obj.returnJson(s,context);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

}
