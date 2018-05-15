package com.bharanee.android.cinemaguide;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements VideoAdapter.viewVideos {
    @BindView(R.id.tv_movie_name)  TextView movie_Title;
    @BindView(R.id.image_poster) ImageView movie_Poster;
    @BindView(R.id.left_review) ImageButton leftReview;
    @BindView(R.id.right_review) ImageButton rightReview;
    @BindView(R.id.tv_vote_average) TextView average_Score;
    @BindView(R.id.tv_release_date) TextView release_Date;
    @BindView(R.id.tv_synopsis) TextView synopsis;
    @BindView(R.id.img_backdrop) ImageView backdrop_image;
    @BindView(R.id.pb_load_status) ProgressBar showProgress;
    @BindView(R.id.txt_reviews) TextView reviews;
    @BindView(R.id.currentPage) TextView currentPage;
    @BindView(R.id.author_name) TextView author;
    @BindView(R.id.list_videos) RecyclerView rv_videos;
    @BindView(R.id.fab_add_favorites) FloatingActionButton likeMovies;
    private BackgroundTasks videoLoad;
    private VideoAdapter videoAdapter;
    private int currentPos=-1;
    private boolean isLiked=false;
    private int movie_ID;
    MovieXtraDetails movieXtraDetails=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        leftReview.setAlpha(0.4f);
        rightReview.setAlpha(0.4f);
        backdrop_image.setScaleType(ImageView.ScaleType.FIT_XY);
        movie_Poster.setScaleType(ImageView.ScaleType.FIT_START);
        Intent movieIntent=getIntent();
        movie_ID=movieIntent.getIntExtra(getString(R.string.MovieID),0);
        //Recycler View
        GridLayoutManager layoutManager=new GridLayoutManager(DetailsActivity.this,1, LinearLayoutManager.HORIZONTAL,false);
        videoAdapter=new VideoAdapter(this);
       if (savedInstanceState!=null){
           MovieXtraDetails savedDetails=new MovieXtraDetails();
           savedDetails.setBackdropImage(savedInstanceState.getString(getString(R.string.backdrop_image_url)));
           savedDetails.setReleaseData(savedInstanceState.getString(getString(R.string.releaseDate)));
           savedDetails.setSynopsis(savedInstanceState.getString(getString(R.string.synopsis)));
           savedDetails.setAverageVote(savedInstanceState.getDouble(getString(R.string.AverageScore)));
           savedDetails.setPosterImage(savedInstanceState.getString(getString(R.string.PosterPath)));
           savedDetails.setMovieTitle(savedInstanceState.getString(getString(R.string.MovieTitle)));
           savedDetails.setReviews(savedInstanceState.getStringArrayList(getString(R.string.reviews)));
           ArrayList<String> videoPaths=savedInstanceState.getStringArrayList(getString(R.string.videosLists));
           movieXtraDetails=savedDetails;
           setdataDetails(movieXtraDetails);
           videoAdapter.setData(videoPaths);
       }else{
        videoLoad=new BackgroundTasks(DetailsActivity.this,movie_ID,videoAdapter);
        videoLoad.execute(movie_ID);
        new display_Data().execute(movie_ID);

       }
        rv_videos.setLayoutManager(layoutManager);
        rv_videos.setHasFixedSize(true);
        //videoAdapter.resetDate();
        rv_videos.setAdapter(videoAdapter);
        //Floating Action Button
        isLiked=containsMovie(movie_ID);
        if (isLiked)
            likeMovies.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.colorAccent)) );
        else
            likeMovies.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.grey)));
        likeMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLiked){
                    //If the movie is not added to favorites, add it now
                ContentValues values=new ContentValues();
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,movie_ID);
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_NAME,movieXtraDetails.getMovieTitle());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH,movieXtraDetails.getPosterImage());
                Uri uri=getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,values);
                if (uri.toString().equals(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI+"/"+movie_ID))
                Toast.makeText(DetailsActivity.this,"Added to Favourites",Toast.LENGTH_SHORT).show();
                isLiked=true;
                    likeMovies.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailsActivity.this,R.color.colorAccent)) );
                }
                else {
                    //If the movie is added to favorites, remove it
                    String movie=movie_ID+"";
                    Uri uriDelete= FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
                    uriDelete=uriDelete.buildUpon().appendPath(movie).build();
                    int result=getContentResolver().delete(uriDelete,null,null);
                    if (result!=0)
                    {Toast.makeText(DetailsActivity.this,"Removed from Favourites ",Toast.LENGTH_SHORT).show();
                    isLiked=false;
                    likeMovies.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailsActivity.this,R.color.grey)) );
                    }

                }
            }
        });

    }

    private boolean containsMovie(int movie_id) {
        String movieid=movie_id+"";
        Uri uriQuery= FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
        uriQuery=uriQuery.buildUpon().appendPath(movieid).build();
        Cursor cursor=getContentResolver().query(uriQuery,null,null,null,null);

        if (cursor.moveToFirst()){
            String id=cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID));
            cursor.close();
            return (Integer.parseInt(id)==movie_id);
        }
        cursor.close();
        return false;
    }

    public void movieright(View view) {
        int pos=currentPos;
        setReviews(++pos);
    }

    public void moveleft(View view) {
        int pos=currentPos;
        setReviews(--pos);
    }

    //Navigate to Youtube
    @Override
    public void onclick(int index) {
        String key=videoAdapter.videoURLs.get(index);
        String url=getString(R.string.YOUTUBE_URL)+key;
        Uri videoURI=Uri.parse(url);
        Intent videoIntent=new Intent();
        videoIntent.setAction(Intent.ACTION_VIEW);
        videoIntent.setData(videoURI);
        if (videoIntent.resolveActivity(getPackageManager())!=null)
            startActivity(videoIntent);
    }

    class display_Data extends AsyncTask<Integer,Void,MovieXtraDetails> {
        @Override
        protected void onPreExecute() {
            showProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieXtraDetails doInBackground(Integer... ints) {
            movieXtraDetails = NetworkUtils.getDetails(ints[0], DetailsActivity.this);

            return movieXtraDetails;
        }

        @Override
        protected void onPostExecute(MovieXtraDetails movieXtraDetails) {
            showProgress.setVisibility(View.INVISIBLE);
            if (movieXtraDetails != null) {
                setdataDetails(movieXtraDetails);
            }
        }
    }
    public void setdataDetails(MovieXtraDetails movieXtraDetails){
        Picasso.get().load(getString(R.string.image_url)+movieXtraDetails.getPosterImage()).placeholder(R.drawable.default_image).into(movie_Poster);
        Picasso.get().load(getString(R.string.backdrop_image_url)+movieXtraDetails.getBackdropImage()).placeholder(R.drawable.default_image).into(backdrop_image);
        movie_Title.setText(movieXtraDetails.getMovieTitle());
        average_Score.setText(String.valueOf(movieXtraDetails.getAverageVote()) );
        String releaseYear=movieXtraDetails.getReleaseData().split("-")[0];
        release_Date.setText(releaseYear);
        synopsis.setText(movieXtraDetails.getSynopsis());
        if (movieXtraDetails.getReviews().size()==0 || movieXtraDetails.getReviews()==null)
        new get_Reviews_Videos().execute(movie_ID);
        else
            setReviews(0);

    }
    class get_Reviews_Videos extends AsyncTask<Integer,Void,MovieXtraDetails>{

        @Override
        protected MovieXtraDetails doInBackground(Integer... ints) {
            int movieId=ints[0];
            movieXtraDetails=NetworkUtils.getReviews(movieId,DetailsActivity.this,movieXtraDetails);
            return movieXtraDetails;
        }

        @Override
        protected void onPostExecute(MovieXtraDetails movieXtraDetails) {
            super.onPostExecute(movieXtraDetails);
            setReviews(0);
        }
    }
    private  void setReviews(int pos){
        int size=movieXtraDetails.getReviews().size();
        if (pos>=0 && pos<size){
            currentPos=pos;
            currentPage.setText(pos+1 +" / "+size);
            String reviewContent=movieXtraDetails.getReviews().get(pos);
            author.setText(reviewContent.split("-")[0]);
            reviews.setText(reviewContent.split("-")[1]);
            if (currentPos + 1 <size)
                rightReview.setVisibility(View.VISIBLE);
            else
                rightReview.setVisibility(View.INVISIBLE);
            if (currentPos-1>=0)
                leftReview.setVisibility(View.VISIBLE);
            else
                leftReview.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.PosterPath),movieXtraDetails.getPosterImage());
        outState.putString(getString(R.string.backdrop_image_url),movieXtraDetails.getBackdropImage());
        outState.putString(getString(R.string.MovieTitle),movieXtraDetails.getMovieTitle());
        outState.putString(getString(R.string.releaseDate),movieXtraDetails.getReleaseData());
        outState.putDouble(getString(R.string.AverageScore),movieXtraDetails.getAverageVote());
        outState.putString(getString(R.string.synopsis),movieXtraDetails.getSynopsis());
        outState.putStringArrayList(getString(R.string.reviews),movieXtraDetails.getReviews());
        outState.putStringArrayList(getString(R.string.videosLists),videoAdapter.videoURLs);
    }
}

