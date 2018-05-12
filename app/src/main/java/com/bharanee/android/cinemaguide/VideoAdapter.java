package com.bharanee.android.cinemaguide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
public ArrayList<String> videoURLs=new ArrayList<String>();
public  viewVideos videosInterface;
 interface viewVideos{
     void onclick(int index);
}
//private Context context;
    Context context;
    public VideoAdapter(viewVideos object){
        videosInterface=object;
    }
    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView videoImage;
        public VideoViewHolder(View itemView) {
            super(itemView);
            videoImage=itemView.findViewById(R.id.video_images);
            itemView.setOnClickListener(this);
        }
        public void bind(String url){
            Picasso.get().load(url).placeholder(R.drawable.default_image).into(videoImage);
        }

        @Override
        public void onClick(View v) {
            int indexPos=getAdapterPosition();
            videosInterface.onclick(indexPos);
        }
    }
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         context=parent.getContext();
        int layoutid=R.layout.videos_lists;
        LayoutInflater inflater= LayoutInflater.from(context);
        boolean shouldattachimmediatetoroot=false;
        View view= inflater.inflate( layoutid,parent,shouldattachimmediatetoroot);
        VideoViewHolder viewHolder=new VideoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
    String url=context.getString(R.string.videoThumbnailUrl)+videoURLs.get(position)+"/hqdefault.jpg";
    holder.bind(url);
    }

    @Override
    public int getItemCount() {
        return (videoURLs==null)?0:videoURLs.size();
    }
    public void resetDate(){
        videoURLs=null;

    }
    public void setData(ArrayList<String> movieKeys) {
        if (videoURLs==null)
            videoURLs=new ArrayList<String>();
            for (String s:movieKeys)
                videoURLs.add(s);

        notifyDataSetChanged();

    }


}
