package fr.upem.soundroid.ui.currentMusicTrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import fr.upem.soundroid.MusicPlayer;
import fr.upem.soundroid.R;

public class CurrentMusicTrackFragment extends Fragment {

    private CurrentMusicTrackModel currentMusicTrackModel;

    private MusicPlayer player;
    private ImageView play, pause;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        currentMusicTrackModel =
                ViewModelProviders.of(this).get(CurrentMusicTrackModel.class);
        View view = inflater.inflate(R.layout.fragment_current_music_track, container, false);

        player = (MusicPlayer) getActivity();

        play = view.findViewById(R.id.playImage);
        pause = view.findViewById(R.id.pauseImage);

        if (player.isPlaying()) {
            play.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
        }

        play.setOnClickListener(v -> {
            player.resume();
            play.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
        });

        pause.setOnClickListener(v -> {
            player.pause();
            pause.setVisibility(View.INVISIBLE);
            play.setVisibility(View.VISIBLE);
        });

        return view;
    }
}
