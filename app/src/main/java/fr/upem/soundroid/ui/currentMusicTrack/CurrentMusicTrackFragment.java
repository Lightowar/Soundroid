package fr.upem.soundroid.ui.currentMusicTrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import fr.upem.soundroid.R;

public class CurrentMusicTrackFragment extends Fragment {

    private CurrentMusicTrackModel currentMusicTrackModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        currentMusicTrackModel =
                ViewModelProviders.of(this).get(CurrentMusicTrackModel.class);
        return inflater.inflate(R.layout.fragment_current_music_track, container, false);
    }
}
