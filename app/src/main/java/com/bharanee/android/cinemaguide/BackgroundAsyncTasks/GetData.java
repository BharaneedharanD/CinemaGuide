package com.bharanee.android.cinemaguide.BackgroundAsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.bharanee.android.cinemaguide.AdapterClasses.MovieAdapter;
import com.bharanee.android.cinemaguide.Modal.MovieDetails;
import com.bharanee.android.cinemaguide.Utilities.NetworkUtils;

public class GetData extends AsyncTask<Void,Void,MovieDetails[]> {
    private String sort_type;
    private Context currentContext;
    private MovieAdapter adapter;
    public GetData(String sort, Context context, MovieAdapter movieAdapter){
        sort_type=sort;
        currentContext=context;
        adapter=movieAdapter;
    }

    @Override
    protected MovieDetails[] doInBackground(Void... voids) {
        MovieDetails[] parsed= NetworkUtils.getJson(sort_type,currentContext);
        return parsed;
    }

    @Override
    protected void onPostExecute(MovieDetails[] strings) {

        adapter.setData(strings,false);
    }
}

