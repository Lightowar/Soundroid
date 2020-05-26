package fr.upem.soundroid.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.upem.soundroid.R;
import fr.upem.soundroid.Track;
import fr.upem.soundroid.TrackAdapter;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private EditText searchBar;

    private final static String FILE = "tracks";
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;
    private TrackAdapter trackAdapter;
    private RecyclerView recyclerView;

    private List<Track> tracks = new ArrayList<>();
    private List<Track> allTracks = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        root.findViewById(R.id.floatingActionButton).setOnClickListener(v -> {
            permissionScan();
        });

        recyclerView = root.findViewById(R.id.recyclerView);
        trackAdapter = new TrackAdapter(tracks);
        recyclerView.setAdapter(trackAdapter);
        recyclerView.setLayoutManager(createLayoutManager());

        searchBar = root.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null) return;
                tracks.clear();
                for (Track t : allTracks) {
                    if (t.filter(s.toString())) tracks.add(t);
                }
                trackAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        if (!load())
            permissionScan();

        return root;
    }

    private void permissionScan() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
            Toast.makeText(getActivity(), "please allow", Toast.LENGTH_SHORT).show();
        } else {
            scan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan();
            } else {
                permissionScan();
            }
        }
    }

    private void scan() {
        tracks.clear();
        allTracks.clear();
        Log.d("debug", "scanning");
        Track.index(getActivity(), t -> {
            tracks.add(t);
            allTracks.add(t);
        });
        if (trackAdapter != null) {
            trackAdapter.notifyDataSetChanged();
        }
        try {
            FileOutputStream fos = getActivity().openFileOutput(FILE, 0);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allTracks);
        } catch (IOException e) {
            Log.e("can't write tracks file", e.toString());
        }
    }

    private boolean load() {
        if (!Arrays.asList(getActivity().fileList()).contains(FILE)) return false;
        try {
            FileInputStream fis = getActivity().openFileInput(FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                tracks.clear();
                tracks.addAll((List<Track>) ois.readObject());
                allTracks.clear();
                allTracks.addAll(tracks);
            } catch (ClassNotFoundException e) {
                throw new AssertionError();
            }
        } catch (IOException e) {
            Log.e("can't read tracks file", e.toString());
            return false;
        }
        return true;
    }

    private RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }
}
