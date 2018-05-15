package com.bharanee.android.cinemaguide;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends AppCompatActivity implements MovieAdapter.poster_click,LoaderManager.LoaderCallbacks<Cursor> {

@BindView(R.id.rv_movie_posters)  RecyclerView rv_movies;
private MovieAdapter adapter;
private String sort_type;
private static final int FAVOURITES_LOADER_ID=101;
//private NetworkUtils networkobj=null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Integer> ids=new ArrayList<Integer>();
        ArrayList<String> poster_images=new ArrayList<String>();
        for (MovieDetails movies:adapter.movielist){
            ids.add(movies.getMovie_Id());
            poster_images.add(movies.getPoster_Path());
        }
        outState.putIntegerArrayList(getString(R.string.MovieID),ids);
        outState.putStringArrayList(getString(R.string.PosterPath),poster_images);
        outState.putString(getString(R.string.sort_type),sort_type);
        outState.putInt(getString(R.string.currentPage),NetworkUtils.curr_page);
        outState.putInt(getString(R.string.lastPage),NetworkUtils.final_page);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
         sort_type=getResources().getString(R.string.top_rated);
         int spanCount=2;
         if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
             spanCount=3;
        GridLayoutManager layoutManager=new GridLayoutManager(HomeActivity.this,spanCount);
        adapter=new MovieAdapter(this,HomeActivity.this);
        adapter.reset();
        if (savedInstanceState!=null){
            String type=savedInstanceState.getString(getString(R.string.sort_type));
            ArrayList<Integer> mIds=savedInstanceState.getIntegerArrayList(getString(R.string.MovieID));
            ArrayList<String> posterPaths=savedInstanceState.getStringArrayList(getString(R.string.PosterPath));
            MovieDetails[] details=new MovieDetails[mIds.size()];
            for (int i=0;i<mIds.size();i++){
                MovieDetails m=new MovieDetails();
                m.setMovie_Id(mIds.get(i));
                m.setPoster_Path(posterPaths.get(i));
                details[i]=m;
            }
            NetworkUtils.curr_page=savedInstanceState.getInt(getString(R.string.currentPage));
            NetworkUtils.final_page=savedInstanceState.getInt(getString(R.string.lastPage));
            adapter.setData(details,type.equals(getString(R.string.favourites)));
        }else{
        new getData().execute();
        }
        rv_movies.setLayoutManager(layoutManager);
        rv_movies.setHasFixedSize(true);

        rv_movies.setAdapter(adapter);
        rv_movies.addOnScrollListener(scrollListener);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private RecyclerView.OnScrollListener scrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy<0 || sort_type.equals(getString(R.string.favourites)))return;
            if (lastitemdisplaying(rv_movies)&& NetworkUtils.curr_page<=NetworkUtils.final_page)
            {
                NetworkUtils.curr_page++;
            new getData().execute();return;
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rv_movies.removeOnScrollListener(scrollListener);
    }

    private boolean lastitemdisplaying(RecyclerView rv_movies) {
    if (rv_movies.getAdapter().getItemCount()>0){
        int lastVisibleItemPosition=( (GridLayoutManager) rv_movies.getLayoutManager()).findLastVisibleItemPosition();
        if (lastVisibleItemPosition!=RecyclerView.NO_POSITION && lastVisibleItemPosition==rv_movies.getAdapter().getItemCount()-1)
            return true;
    }
    return false;
    }


    @Override
    public void on_movie_poster_clicked(int index) {
        Class detailsPage=DetailsActivity.class;
        Intent details_Page=new Intent(this,detailsPage);
        details_Page.putExtra(getResources().getString(R.string.MovieID),adapter.movielist.get(index).getMovie_Id());
        startActivity(details_Page);

    }
//////////////TODO :load favourite movie////////////////////////////////////////////////////////////

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            ;
        };
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data==null)return;
        MovieDetails[] movieArray=new MovieDetails[data.getCount()];
        int i=0;
        if (data.moveToFirst()){
            do {
                MovieDetails details=new MovieDetails();
                String id=data.getString(data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID));
                details.setMovie_Id(Integer.parseInt(id));
                details.setPoster_Path( data.getString(data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH)) );
                movieArray[i]=details;
                i++;
            }while (data.moveToNext());
        }
        getLoaderManager().destroyLoader(FAVOURITES_LOADER_ID);
        adapter.setData(movieArray,true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.reset();
    }
///////////////////////////////////////////////////////////////////////////////
    class getData extends AsyncTask<Void,Void,MovieDetails[]>{

    @Override
    protected MovieDetails[] doInBackground(Void... voids) {
        MovieDetails[] parsed=NetworkUtils.getJson(sort_type,HomeActivity.this);
        return parsed;
    }

    @Override
        protected void onPostExecute(MovieDetails[] strings) {

            adapter.setData(strings,false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_types,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id) {
            case R.id.top_rated_menu_id:
                if (!sort_type.equals(getString(R.string.top_rated)))
                {
                    adapter.reset();
                    sort_type=getResources().getString(R.string.top_rated);
                    new getData().execute();
                }
                break;
            case R.id.popular_menu_id:
                if (!sort_type.equals(getString(R.string.popular)))
                {
                    adapter.reset();
                    sort_type=getResources().getString(R.string.popular);
                    new getData().execute();
                }
                break;
            case R.id.favourite_menu_id:
                if (!sort_type.equals(getString(R.string.favourites))){
                    sort_type=getString(R.string.favourites);
                    adapter.reset();
                    if (null==getLoaderManager().getLoader(FAVOURITES_LOADER_ID))
                    getLoaderManager().initLoader(FAVOURITES_LOADER_ID,null,this);
                    else
                        getLoaderManager().restartLoader(FAVOURITES_LOADER_ID,null,this);
                    //adapter.setData(null);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
