package fr.upem.soundroid.ui.currentMusicTrack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CurrentMusicTrackModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CurrentMusicTrackModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}