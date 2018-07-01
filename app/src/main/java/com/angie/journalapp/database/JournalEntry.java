package com.angie.journalapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "journal_entry")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String content;

    @ColumnInfo(name = "date_created")
    private Date dateCreated;

    @Ignore
    public JournalEntry(String title, String content) {
        this.title = title;
        this.content = content;
    }



    public JournalEntry(int id, String title, String content, Date dateCreated) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
