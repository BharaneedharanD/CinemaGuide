package com.bharanee.android.cinemaguide;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity implements MovieAdapter.poster_click,LoaderManager.LoaderCallbacks<Cursor> {
private RecyclerView rv_movies;
private MovieAdapter adapter;
private String sort_type;
private static final int FAVOURITES_LOADER_ID=101;
    NetworkUtils networkobj=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
         networkobj=NetworkUtils.getNetworkObject();
         sort_type=getResources().getString(R.string.top_rated);
        rv_movies=findViewById(R.id.rv_movie_posters);
        GridLayoutManager layoutManager=new GridLayoutManager(HomeActivity.this,2);
        new getData().execute(networkobj);
        rv_movies.setLayoutManager(layoutManager);
        rv_movies.setHasFixedSize(true);
        adapter=new MovieAdapter(this,HomeActivity.this);
        adapter.reset();
        rv_movies.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sort_type.equals("favourites"))
            getLoaderManager().restartLoader(FAVOURITES_LOADER_ID,null,this);
            rv_movies.addOnScrollListener(scrollListener);
    }

    private RecyclerView.OnScrollListener scrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy<0 || sort_type.equals("favourites"))return;
            NetworkUtils networkObject=NetworkUtils.getNetworkObject();
            if (lastitemdisplaying(rv_movies)&&networkObject.curr_page<=networkObject.final_page)
            {
                networkObject.curr_page++;
            new getData().execute(networkObject);return;
            }

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        rv_movies.removeOnScrollListener(scrollListener);
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

    }*/

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
        adapter.setData(movieArray,true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.reset();
    }
///////////////////////////////////////////////////////////////////////////////
    class getData extends AsyncTask<NetworkUtils,Void,MovieDetails[]>{

        @Override
        protected MovieDetails[] doInBackground(NetworkUtils... networkObj) {
            NetworkUtils networkConnector=networkObj[0];
            MovieDetails[] parsed=networkConnector.getJson(sort_type,HomeActivity.this);
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
                    new getData().execute(networkobj);}
                break;
            case R.id.popular_menu_id:
                if (!sort_type.equals(getString(R.string.popular)))
                {
                    adapter.reset();
                    sort_type=getResources().getString(R.string.popular);
                    new getData().execute(networkobj);}
                break;
            case R.id.favourite_menu_id:
                if (!sort_type.equals("favourites")){
                    sort_type="favourites";
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
