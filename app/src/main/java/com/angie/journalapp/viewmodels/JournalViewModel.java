package com.angie.journalapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.angie.journalapp.database.AppDatabase;
import com.angie.journalapp.database.JournalEntry;

import java.util.List;


public class JournalViewModel extends AndroidViewModel {

    private static final String TAG = JournalViewModel.class.getSimpleName();

    private LiveData<List<JournalEntry>> journalEntries;

    public JournalViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the journal entries from the DataBase");
        journalEntries = database.journalDao().loadAllJournalRecords();
    }

    public LiveData<List<JournalEntry>> getJournalEntries() {
        return journalEntries;
    }
}
