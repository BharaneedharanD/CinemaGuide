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

public class DetailsActivity extends AppCompatActivity implements VideoAdapter.viewVideos {
private TextView movie_Title;
private ImageView movie_Poster;
private TextView average_Score;
private TextView release_Date;
private TextView synopsis;
private ImageView backdrop_image;
private ProgressBar showProgress;
private TextView reviews;
private TextView currentPage;
private TextView author;
private ImageButton leftReview,rightReview;
private RecyclerView rv_videos;
private BackgroundTasks videoLoad;
private VideoAdapter videoAdapter;
private int currentPos=-1;
private FloatingActionButton likeMovies;
private boolean isLiked=false;
private int movie_ID;
MovieXtraDetails movieXtraDetails=null;
    NetworkUtils networkUtils=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        reviews=findViewById(R.id.txt_reviews);
        movie_Title= findViewById(R.id.tv_movie_name);
        movie_Poster= findViewById(R.id.image_poster);
        leftReview=findViewById(R.id.left_review);
        leftReview.setAlpha(0.4f);
        rightReview=findViewById(R.id.right_review);
        rightReview.setAlpha(0.4f);
        average_Score= findViewById(R.id.tv_vote_average);
        release_Date= findViewById(R.id.tv_release_date);
        currentPage=findViewById(R.id.currentPage);
        showProgress=findViewById(R.id.pb_load_status);
        backdrop_image=findViewById(R.id.img_backdrop);
        author=findViewById(R.id.author_name);
        backdrop_image.setScaleType(ImageView.ScaleType.FIT_XY);
        movie_Poster.setScaleType(ImageView.ScaleType.FIT_START);
        synopsis=findViewById(R.id.tv_synopsis);
        NetworkUtils.resetObject();
        networkUtils=NetworkUtils.getNetworkObject();
        Intent movieIntent=getIntent();
        movie_ID=movieIntent.getIntExtra(getString(R.string.MovieID),0);
        new display_Data().execute(movie_ID);

        //Recycler View
         rv_videos=findViewById(R.id.list_videos);
        GridLayoutManager layoutManager=new GridLayoutManager(DetailsActivity.this,1, LinearLayoutManager.HORIZONTAL,false);
        videoAdapter=new VideoAdapter(this);
        videoLoad=new BackgroundTasks(DetailsActivity.this,movie_ID,videoAdapter);
        videoLoad.execute(movie_ID);
        rv_videos.setLayoutManager(layoutManager);
        rv_videos.setHasFixedSize(true);
        //videoAdapter.resetDate();
        rv_videos.setAdapter(videoAdapter);

        //Floating Action Button
         likeMovies=findViewById(R.id.fab_add_favorites);
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
            movieXtraDetails = networkUtils.getDetails(ints[0], DetailsActivity.this);

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
            new get_Reviews_Videos().execute(movie_ID);

    }
    class get_Reviews_Videos extends AsyncTask<Integer,Void,MovieXtraDetails>{

        @Override
        protected MovieXtraDetails doInBackground(Integer... ints) {
            int movieId=ints[0];
            movieXtraDetails=networkUtils.getReviews(movieId,DetailsActivity.this,movieXtraDetails);
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

}

