package fr.upem.soundroid;

import java.util.List;
import java.util.function.Consumer;

public interface MusicPlayer {

    public void play(Track t);

    public boolean pause();

    public boolean resume();

    public boolean isPlaying();

    public Track currentlyPlaying();

    public interface OnTracksGenerated {
        public void onTracksGenerated(List<Track> tracks);
    }

    public void consumeAllTracks(OnTracksGenerated c);

    public void onNewTrackListener(Runnable r);

    public void unsub(Runnable r);
}
