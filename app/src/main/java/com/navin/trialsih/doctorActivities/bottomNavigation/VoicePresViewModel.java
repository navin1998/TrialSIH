package com.navin.trialsih.doctorActivities.bottomNavigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VoicePresViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VoicePresViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}