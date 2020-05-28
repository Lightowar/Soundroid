package fr.upem.soundroid;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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

import fr.upem.soundroid.ui.currentMusicTrack.CurrentMusicTrackFragment;
import fr.upem.soundroid.ui.gallery.GalleryFragment;
import fr.upem.soundroid.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity implements MusicPlayer {
    private final static String FILE = "tracks";
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;
    private AppBarConfiguration mAppBarConfiguration;
    private MusicService musicService;
    private boolean bounded;
    private List<Track> allTracks;
    private HomeFragment home;
    private CurrentMusicTrackFragment current;
    private GalleryFragment galleryFragment;
    private OnTracksGenerated onTracksGenerated;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        load();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_current_music_track)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        bounded = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bounded) {
            unbindService(connection);
            bounded = false;
            //stopService(new Intent(this, MusicService.class));
        }
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
        Log.d("debug", "permissionScan");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            scan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("debug", "onRequestPermissionsResult");
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan();
            } else {
                permissionScan();
            }
        }
    }

    private void scan() {
        Log.d("debug", "scanning");
        allTracks = new ArrayList<>();
        Track.index(this, t -> {
            allTracks.add(t);
        });
        if (onTracksGenerated != null) onTracksGenerated.onTracksGenerated(allTracks);
        try {
            FileOutputStream fos = openFileOutput(FILE, 0);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allTracks);
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
                allTracks = (List<Track>) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new AssertionError();
            }
        } catch (IOException e) {
            Log.e("can't read tracks file", e.toString());
            return false;
        }
        return true;
    }

    public void play(Track t) {
        Log.d("debug", "ged");
        if (musicService == null) {
            Log.e(MainActivity.class.toString(), "no service");
            return;
        }
        musicService.play(t);
    }

    @Override
    public boolean pause() {
        return musicService.pause();
    }

    @Override
    public boolean resume() {
        return musicService.resume();
    }

    @Override
    public boolean isPlaying() {
        return musicService != null && musicService.isPlaying();
    }

    @Override
    public Track currentlyPlaying() {
        return musicService.currentlyPlaying();
    }

    @Override
    public void consumeAllTracks(OnTracksGenerated c) {
        if (allTracks != null || load()) {
            c.onTracksGenerated(allTracks);
        } else {
            onTracksGenerated = c;
            permissionScan();
        }
    }

    @Override
    public void onNewTrackListener(Runnable r) {
        musicService.onNewTrackListener(r);
    }

    @Override
    public void unsub(Runnable r) {
        musicService.unsub(r);
    }

    public void setHome(HomeFragment home) {
        this.home = home;
    }

    public void setCurrent(CurrentMusicTrackFragment current) {
        this.current = current;
    }

    public void setGalleryFragment(GalleryFragment galleryFragment) {
        this.galleryFragment = galleryFragment;
    }
}