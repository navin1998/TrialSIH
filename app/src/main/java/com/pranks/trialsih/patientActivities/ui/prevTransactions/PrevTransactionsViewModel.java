package com.pranks.trialsih.patientActivities.ui.prevTransactions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrevTransactionsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PrevTransactionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is prev transactions fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}