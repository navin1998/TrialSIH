package com.navin.trialsih.patientActivities.ui.prevAppoint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrevAppointmentsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PrevAppointmentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is prev appointments fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}