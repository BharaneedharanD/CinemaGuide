package com.bharanee.android.cinemaguide;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

public class BackgroundTasks extends AsyncTask<Integer,Void,ArrayList<String>>{

        Context context;
        ArrayList<String> movieVideoKeys=null;
        public VideoAdapter videoAdapter;
        int movieId;
        NetworkUtils networkUtils;
        public BackgroundTasks(Context context,int movieId,VideoAdapter adapter) {
            this.context = context;
            this.movieId=movieId;
            networkUtils=NetworkUtils.getNetworkObject();
            videoAdapter=adapter;
        }

        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            movieVideoKeys=networkUtils.getVideos(movieId,context);
            return movieVideoKeys;
        }

        @Override
        protected void onPostExecute(ArrayList<String> movievideos) {
            //VideoAdapter videoAdapter=new VideoAdapter(context);
            videoAdapter.setData(movievideos);
        }
    }

