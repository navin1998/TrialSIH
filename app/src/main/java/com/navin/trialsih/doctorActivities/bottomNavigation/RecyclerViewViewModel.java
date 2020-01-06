package com.navin.trialsih.doctorActivities.bottomNavigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecyclerViewViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecyclerViewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is recycler view fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}