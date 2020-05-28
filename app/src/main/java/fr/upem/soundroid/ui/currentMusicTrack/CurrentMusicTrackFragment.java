package fr.upem.soundroid.ui.currentMusicTrack;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import fr.upem.soundroid.MainActivity;
import fr.upem.soundroid.MusicPlayer;
import fr.upem.soundroid.R;
import fr.upem.soundroid.Track;

public class CurrentMusicTrackFragment extends Fragment {

    private CurrentMusicTrackModel currentMusicTrackModel;

    private MusicPlayer player;
    private ImageView play, pause, currentTrackImage;
    private TextView textView;
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            update(player.currentlyPlaying());
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        currentMusicTrackModel =
                ViewModelProviders.of(this).get(CurrentMusicTrackModel.class);
        View view = inflater.inflate(R.layout.fragment_current_music_track, container, false);

        ((MainActivity) getActivity()).setCurrent(this);

        player = (MusicPlayer) getActivity();

        play = view.findViewById(R.id.playImage);
        pause = view.findViewById(R.id.pauseImage);
        currentTrackImage = view.findViewById(R.id.currentTrackImage);
        textView = view.findViewById(R.id.textView);

        update(player.currentlyPlaying());

        player.onNewTrackListener(r);

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

    private void update(Track t) {
        if (t == null) {
            pause.setVisibility(View.INVISIBLE);
            play.setVisibility(View.VISIBLE);
            currentTrackImage.setImageResource(R.drawable.ic_menu_slideshow);
            textView.setText("");
        } else {
            play.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
            Bitmap bm = player.currentlyPlaying().computeAndGetBitmap(getContext());
            if (bm != null)
                currentTrackImage.setImageBitmap(bm);
            else
                currentTrackImage.setImageResource(R.drawable.ic_menu_slideshow);
            textView.setText(player.currentlyPlaying().toString());

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.unsub(r);
    }
}
