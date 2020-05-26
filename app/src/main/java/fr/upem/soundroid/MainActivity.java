package fr.upem.soundroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String FILE = "tracks";

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;

    private List<Track> tracks = new ArrayList<>();
    private List<Track> allTracks = new ArrayList<>();
    private AppBarConfiguration mAppBarConfiguration;
    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!load())
            permissionScan();

        findViewById(R.id.floatingActionButton).setOnClickListener(v -> {
            tracks.clear();
            permissionScan();
        });

        recyclerView = findViewById(R.id.recyclerView);
        trackAdapter = new TrackAdapter(tracks);
        recyclerView.setAdapter(trackAdapter);
        recyclerView.setLayoutManager(createLayoutManager());

        searchBar = findViewById(R.id.searchBar);
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


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void permissionScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
            Toast.makeText(this, "please allow", Toast.LENGTH_SHORT).show();
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
        Track.index(this, t -> {
            tracks.add(t);
            allTracks.add(t);
            if (trackAdapter != null) {
                trackAdapter.notifyDataSetChanged();
            }
        });
        try {
            FileOutputStream fos = openFileOutput(FILE, 0);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tracks);
        } catch (IOException e) {
            Log.e("can't write tracks file", e.toString());
        }
    }

    private boolean load() {
        if (!Arrays.asList(fileList()).contains(FILE)) return false;
        try {
            FileInputStream fis = openFileInput(FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                tracks = (List<Track>) ois.readObject();
                allTracks = new ArrayList<>(tracks);
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
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }
}