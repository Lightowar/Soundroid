package fr.upem.soundroid;

import java.util.List;

public interface MusicPlayer {

    public void play(Track t);

    public boolean pause();

    public boolean resume();

    public boolean isPlaying();

    public Track currentlyPlaying();

    public void consumeAllTracks(OnTracksGenerated c);

    public void onNewTrackListener(Runnable r);

    public void unsub(Runnable r);

    public interface OnTracksGenerated {
        public void onTracksGenerated(List<Track> tracks);
    }
}
