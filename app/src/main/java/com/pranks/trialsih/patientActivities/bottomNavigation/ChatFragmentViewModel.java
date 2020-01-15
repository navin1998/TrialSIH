package com.pranks.trialsih.patientActivities.bottomNavigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatFragmentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ChatFragmentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is chat fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}