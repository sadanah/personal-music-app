package com.sadanah.boombastic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context context;
    private List<File> musicFiles;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(File musicFile);
    }

    public MusicAdapter(Context context, List<File> musicFiles, OnItemClickListener listener) {
        this.context = context;
        this.musicFiles = musicFiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        File musicFile = musicFiles.get(position);
        holder.txtMusicTitle.setText(musicFile.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(musicFile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView txtMusicTitle;
        ImageView imgMusicIcon;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMusicTitle = itemView.findViewById(R.id.txtMusicTitle);
            imgMusicIcon = itemView.findViewById(R.id.imgMusicIcon);
        }
    }
}
