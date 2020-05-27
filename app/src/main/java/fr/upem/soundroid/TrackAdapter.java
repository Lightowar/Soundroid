package fr.upem.soundroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for RecyclerView displaying dessert pictures
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private final List<Track> tracks;
    private final MusicPlayer player;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.trackLabel);
        }

        private void update(Track track) {
            textView.setText(track.toString());
            if (player != null)
                itemView.setOnClickListener(v -> player.play(track));
        }
    }

    public TrackAdapter(List<Track> tracks, MusicPlayer player) {
        super();
        this.tracks = tracks;
        this.player = player;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.update(tracks.get(i));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }
}