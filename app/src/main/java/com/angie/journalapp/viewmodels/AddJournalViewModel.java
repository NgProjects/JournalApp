package com.angie.journalapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.angie.journalapp.database.AppDatabase;
import com.angie.journalapp.database.JournalEntry;

public class AddJournalViewModel extends ViewModel {

    private LiveData<JournalEntry> jounalEntry;

    public AddJournalViewModel(AppDatabase database, int journalRecordId) {
        jounalEntry = database.journalDao().loadJournalRecordById(journalRecordId);
    }

    public LiveData<JournalEntry> getJounalEntry() {
        return jounalEntry;
    }
}
