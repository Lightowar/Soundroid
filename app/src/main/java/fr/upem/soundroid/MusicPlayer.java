package fr.upem.soundroid;

public interface MusicPlayer {

    public void play(Track t);

    public void pause();

    public void resume();

    public boolean isPlaying();
}
