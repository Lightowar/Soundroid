package fr.upem.soundroid;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MusicService extends Service {

    public final static String PLAY_ACTION = "play";

    private MediaPlayer player;

    private IBinder binder = new MusicBinder();

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
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
        player.start();
    }

    public void pause() {
        player.pause();
    }

    public void resume() {
        player.start();
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }
}
