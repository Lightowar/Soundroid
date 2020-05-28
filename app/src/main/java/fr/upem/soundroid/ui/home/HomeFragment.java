package fr.upem.soundroid.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import fr.upem.soundroid.MainActivity;
import fr.upem.soundroid.MusicPlayer;
import fr.upem.soundroid.R;
import fr.upem.soundroid.Track;
import fr.upem.soundroid.TrackAdapter;

public class HomeFragment extends Fragment {

    private EditText searchBar;
    private TrackAdapter trackAdapter;
    private RecyclerView recyclerView;

    private List<Track> tracks;

    private MusicPlayer player;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        root.findViewById(R.id.floatingActionButton).setOnClickListener(v -> {
            ((MainActivity) getActivity()).permissionScan();
        });

        player = ((MusicPlayer) getActivity());
        player.consumeAllTracks(
                t -> {
                    tracks = t;
                    recyclerView = root.findViewById(R.id.recyclerView);
                    trackAdapter = new TrackAdapter(tracks, (MainActivity) getActivity());
                    recyclerView.setAdapter(trackAdapter);
                    recyclerView.setLayoutManager(createLayoutManager());
                });


        ((MainActivity) getActivity()).setHome(this);

        searchBar = root.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null) return;
                tracks.clear();
                for (Track t : tracks) {
                    if (t.filter(s.toString())) tracks.add(t);
                }
                trackAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return root;
    }

    private RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }
}
