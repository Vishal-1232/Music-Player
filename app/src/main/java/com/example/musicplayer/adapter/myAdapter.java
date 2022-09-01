package com.example.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.playSong;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {
    public Context context;
    final ArrayList<File> mysongs;

    public myAdapter(Context context, ArrayList<File> fileArrayList) {
        this.context = context;
        this.mysongs = fileArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mylayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //String song = songsList.get(position);
        String song = mysongs.get(position).getName();
        holder.songName.setText(song);
        holder.Album.setImageResource(R.drawable.i);
    }

    @Override
    public int getItemCount() {
        return mysongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView songName;
        public ImageView Album;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            songName = itemView.findViewById(R.id.songName);
            Album = itemView.findViewById(R.id.Album);

            Album.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, playSong.class);
            int position = this.getAdapterPosition();
            String currentSong = mysongs.get(position).getName();
            intent.putExtra("songList", mysongs);
            intent.putExtra("currentSong",currentSong);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
}
