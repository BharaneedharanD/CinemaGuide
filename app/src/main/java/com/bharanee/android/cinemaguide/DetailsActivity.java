package com.bharanee.android.cinemaguide;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
private TextView movie_Title;
private ImageView movie_Poster;
private TextView average_Score;
private TextView release_Date;
private TextView synopsis;
private ImageView backdrop_image;
private ProgressBar showProgress;
private TextView reviews;
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
        average_Score= findViewById(R.id.tv_vote_average);
        release_Date= findViewById(R.id.tv_release_date);
        showProgress=findViewById(R.id.pb_load_status);
        backdrop_image=findViewById(R.id.img_backdrop);
        backdrop_image.setScaleType(ImageView.ScaleType.FIT_XY);
        movie_Poster.setScaleType(ImageView.ScaleType.FIT_START);
        synopsis=findViewById(R.id.tv_synopsis);
        NetworkUtils.resetObject();

        networkUtils=NetworkUtils.getNetworkObject();
        Intent movieIntent=getIntent();
        movie_ID=movieIntent.getIntExtra(getString(R.string.MovieID),0);
        new display_Data().execute(movie_ID);
    }
    class display_Data extends AsyncTask<Integer,Void,MovieXtraDetails>{
        @Override
        protected void onPreExecute() {
            showProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieXtraDetails doInBackground(Integer... ints) {
            movieXtraDetails=networkUtils.getDetails(ints[0],DetailsActivity.this);

            return movieXtraDetails;
        }

        @Override
        protected void onPostExecute(MovieXtraDetails movieXtraDetails) {
            showProgress.setVisibility(View.INVISIBLE);
            if (movieXtraDetails!=null)
            {
            Picasso.get().load(getResources().getString(R.string.image_url)+movieXtraDetails.getPosterImage()).placeholder(R.drawable.default_image).into(movie_Poster);
            Picasso.get().load(getString(R.string.image_url)+movieXtraDetails.getBackdropImage()).placeholder(R.drawable.default_image).into(backdrop_image);
            movie_Title.setText(movieXtraDetails.getMovieTitle());
            average_Score.setText(String.valueOf(movieXtraDetails.getAverageVote()) );
            String releaseYear=movieXtraDetails.getReleaseData().split("-")[0];
            release_Date.setText(releaseYear);
            synopsis.setText(movieXtraDetails.getSynopsis());}
            new get_Reviews_Videos().execute(movie_ID);
        }
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
            if (movieXtraDetails!=null && movieXtraDetails.getReviews().size()>0){
                reviews.setText(movieXtraDetails.getReviews().get(0));
            }
        }
    }
}
