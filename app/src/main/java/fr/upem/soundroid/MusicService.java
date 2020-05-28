package fr.upem.soundroid;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private final List<Runnable> lst = new ArrayList<>();
    private MediaPlayer player;
    private IBinder binder = new MusicBinder();
    private Track currentTrack;
    private CallBackListener listener;

    public void onNewTrackListener(Runnable r) {
        lst.add(r);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void play(Track t) {
        if (player != null && player.isPlaying()) player.stop();
        player = MediaPlayer.create(MusicService.this, t.toUri());
        if (player != null) {
            currentTrack = t;
            player.start();
            if (listener != null)
                listener.onPlaying(t);
            for (Runnable r : lst) r.run();
        } else {
            Log.e(this.getClass().toString(), "cannot instantiate the media player");
        }
    }

    public boolean pause() {
        if (player == null) return false;
        player.pause();
        return true;
    }

    public boolean resume() {
        if (player == null) return false;
        player.start();
        return true;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public Track currentlyPlaying() {
        return currentTrack;
    }

    public void setOnPlayingListener(CallBackListener listener) {
        this.listener = listener;
    }

    public void unsub(Runnable r) {
        lst.remove(r);
    }

    private interface CallBackListener {
        public void onPlaying(Track t);

        public void onPause();

        public void onResume();
    }

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }
}
