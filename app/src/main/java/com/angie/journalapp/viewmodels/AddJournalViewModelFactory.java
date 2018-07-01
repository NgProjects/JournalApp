package com.angie.journalapp.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.angie.journalapp.database.AppDatabase;


public class AddJournalViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase appDatabase;
    private final int journalEntryId;

    public AddJournalViewModelFactory(AppDatabase database, int journalId) {
        appDatabase = database;
        journalEntryId = journalId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddJournalViewModel(appDatabase, journalEntryId);
    }
}
