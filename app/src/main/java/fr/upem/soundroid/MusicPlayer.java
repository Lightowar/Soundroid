package fr.upem.soundroid;

public interface MusicPlayer {

    public void play(Track t);

    public boolean pause();

    public boolean resume();

    public boolean isPlaying();

    public Track currentlyPlaying();
}
