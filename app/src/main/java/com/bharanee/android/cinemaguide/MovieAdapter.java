package com.bharanee.android.cinemaguide;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
public ArrayList<MovieDetails> movielist=null;
private Context resourceContext;
private final poster_click click_handler;

    public MovieAdapter(poster_click poster_click_event,Context context) {
        click_handler=poster_click_event;
        resourceContext=context;
    }

    interface poster_click{
     void on_movie_poster_clicked(int index);
}

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView poster_button;
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            poster_button=itemView.findViewById(R.id.poster_clickable);
            itemView.setOnClickListener(this);
        }
        public void bind(String url){
            Picasso.get().load(url).placeholder(R.drawable.default_image).into(poster_button);
        }

        @Override
        public void onClick(View v) {
            int clicked_Position=getAdapterPosition();
            click_handler.on_movie_poster_clicked(clicked_Position);
        }
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        int layoutid=R.layout.movie_poster;
        LayoutInflater inflater= LayoutInflater.from(context);
        boolean shouldattachimmediatetoroot=false;
        View view= inflater.inflate( layoutid,parent,shouldattachimmediatetoroot);
        MovieAdapterViewHolder viewHolder=new MovieAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
    //download image from string array path
        String url=resourceContext.getString(R.string.image_url)+movielist.get(position).getPoster_Path();
        holder.bind(url);

        //holder.poster_button.setImageResource(R.drawable.kaala);
    }

    @Override
    public int getItemCount() {
        if (movielist==null)
            return 0;
        else return movielist.size();
    }
    public void setData(MovieDetails[] result,Boolean isFavourites){
        if (result==null)return;
        if (movielist==null )movielist=new ArrayList<>();
        if (isFavourites)movielist=new ArrayList<>();
        for (MovieDetails m:result)
            movielist.add(m);
        notifyDataSetChanged();

    }

    public void reset(){
        movielist=null;
        NetworkUtils.curr_page=1;
        NetworkUtils.final_page=0;
        //notifyDataSetChanged();
    }



}
