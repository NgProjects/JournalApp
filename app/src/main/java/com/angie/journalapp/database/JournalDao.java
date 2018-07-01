package com.angie.journalapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM journal_entry ORDER BY date_created")
    LiveData<List<JournalEntry>> loadAllJournalRecords();

    @Insert
    void insertJournalRecord(JournalEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateJournalRecord(JournalEntry taskEntry);

    @Delete
    void deleteJournalEntry(JournalEntry taskEntry);

    @Query("SELECT * FROM journal_entry WHERE id = :id")
    LiveData<JournalEntry> loadJournalRecordById(int id);

    @Query("DELETE FROM journal_entry")
    public void deleteCurrentTable();

}
