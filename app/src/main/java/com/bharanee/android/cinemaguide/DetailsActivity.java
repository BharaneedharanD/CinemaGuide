package com.bharanee.android.cinemaguide;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
        Picasso.get().load(getResources().getString(R.string.image_url)+movieXtraDetails.getPosterImage()).placeholder(R.drawable.default_image).into(movie_Poster);
        Picasso.get().load(getString(R.string.image_url)+movieXtraDetails.getBackdropImage()).placeholder(R.drawable.default_image).into(backdrop_image);
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

