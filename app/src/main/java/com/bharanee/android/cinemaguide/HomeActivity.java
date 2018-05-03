package com.bharanee.android.cinemaguide;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity implements MovieAdapter.poster_click {
private RecyclerView rv_movies;
private MovieAdapter adapter;
private String sort_type;
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
        rv_movies.addOnScrollListener(scrollListener);
    }
    private RecyclerView.OnScrollListener scrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy<0)return;
            NetworkUtils networkObject=NetworkUtils.getNetworkObject();
            if (lastitemdisplaying(rv_movies)&&networkObject.curr_page<=networkObject.final_page)
            {
                networkObject.curr_page++;
            new getData().execute(networkObject);return;
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

    class getData extends AsyncTask<NetworkUtils,Void,MovieDetails[]>{

        @Override
        protected MovieDetails[] doInBackground(NetworkUtils... networkObj) {
            NetworkUtils networkConnector=networkObj[0];
            MovieDetails[] parsed=networkConnector.getJson(sort_type,HomeActivity.this);
            return parsed;
        }

        @Override
        protected void onPostExecute(MovieDetails[] strings) {

            adapter.setData(strings);
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
                if (!sort_type.equals(id))
                {   networkobj.curr_page=1;
                    adapter.reset();
                    sort_type=getResources().getString(R.string.popular); new getData().execute(networkobj);}
                break;
            case R.id.popular_menu_id:
                if (!sort_type.equals(id))
                {   networkobj.curr_page=1;
                adapter.reset();
                    sort_type=getResources().getString(R.string.top_rated); new getData().execute(networkobj);}
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
