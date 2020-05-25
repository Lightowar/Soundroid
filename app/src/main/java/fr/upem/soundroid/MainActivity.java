package fr.upem.soundroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;

    public final static String FILE = "tracks";

    private List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!load())
            premissionScan();
        after();
    }

    public void premissionScan() {
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
                premissionScan();
            }
        }
    }

    private void scan() {
        tracks = Track.index(Environment.getExternalStorageDirectory());
        try {
            FileOutputStream fos = openFileOutput(FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tracks);
        } catch (IOException e) {
            Log.e("can't write tracks file", e.toString());
            return;
        }
    }

    private void after() {

    }

    private boolean load() {
        File file = new File(FILE);
        if (!file.exists()) return false;
        try {
            FileInputStream fis = openFileInput(FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                tracks = (List<Track>) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new AssertionError();
            }
        } catch (IOException e) {
            Log.e("can't read tracks file", e.toString());
            return false;
        }
        return true;
    }
}
