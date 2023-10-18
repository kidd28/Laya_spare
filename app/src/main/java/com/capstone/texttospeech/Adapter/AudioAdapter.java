package com.capstone.texttospeech.Adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.capstone.texttospeech.Model.AudioModel;
import com.capstone.texttospeech.R;

import java.io.IOException;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.HolderAdapter> {

    Context context;
    ArrayList<AudioModel> audioModels;

    public AudioAdapter(Context context, ArrayList<AudioModel> audioModels){
    this.context = context;
    this.audioModels = audioModels;
    }

    @NonNull
    @Override
    public HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);

        return new HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAdapter holder, int position) {
        AudioModel model = audioModels.get(position);
        String name= model.getName();
        String image= model.getImageLink();

        holder.text.setText(name);
        Glide.with(context).load(image).centerCrop().into(holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(model.getFileLink());
            }
        });

    }

    @Override
    public int getItemCount() {
        return audioModels.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        CardView card;
        public HolderAdapter(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            text = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
        }
    }
    private void playAudio(String audioUrl) {
        // initializing media player
        MediaPlayer mediaPlayer = new MediaPlayer();

        // below line is use to set the audio stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // below line is use to set our
            // url to our media player.
            mediaPlayer.setDataSource(audioUrl);

            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

            // below line is use to display a toast message.
        } catch (IOException e) {
            // this line of code is use to handle error while playing our audio file.
     }
    }
}
