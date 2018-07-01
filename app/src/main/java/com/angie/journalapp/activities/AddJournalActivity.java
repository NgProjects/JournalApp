package com.angie.journalapp.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.angie.journalapp.R;
import com.angie.journalapp.database.AppDatabase;
import com.angie.journalapp.database.JournalEntry;
import com.angie.journalapp.utils.AppConstants;
import com.angie.journalapp.utils.AppExecutors;
import com.angie.journalapp.viewmodels.AddJournalViewModel;
import com.angie.journalapp.viewmodels.AddJournalViewModelFactory;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;

    private TextInputLayout titleInputLayout, contentInputLayout;

    private AppDatabase appDatabase;

    private int journalId = AppConstants.DEFAULT_JOURNAL_ENTRY_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        setUpViews();

        appDatabase = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(AppConstants.INSTANCE_JOURNAL_ID)) {
            journalId = savedInstanceState.getInt(AppConstants.INSTANCE_JOURNAL_ID, AppConstants.DEFAULT_JOURNAL_ENTRY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(AppConstants.JOURNAL_ID)) {
            saveButton.setText(R.string.update_button);
            if (journalId == AppConstants.DEFAULT_JOURNAL_ENTRY_ID) {
                journalId = intent.getIntExtra(AppConstants.JOURNAL_ID, AppConstants.DEFAULT_JOURNAL_ENTRY_ID);

                AddJournalViewModelFactory factory = new AddJournalViewModelFactory(appDatabase, journalId);
                final AddJournalViewModel viewModel = ViewModelProviders.of(this, factory).get(AddJournalViewModel.class);
                viewModel.getJounalEntry().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getJounalEntry().removeObserver(this);
                        setJournalEntryDetails(journalEntry);
                    }
                });
            }
        }
    }

    /**
     *
     * @param journalEntry
     */
    private void setJournalEntryDetails(JournalEntry journalEntry) {
        if(journalEntry != null){
            titleEditText.setText(journalEntry.getTitle());
            contentEditText.setText(journalEntry.getContent());
        }
    }

    /**
     * 
     */
    private void setUpViews() {
        titleEditText = findViewById(R.id.titleField);
        contentEditText = findViewById(R.id.contentField);

        titleInputLayout = findViewById(R.id.title_text_input);
        contentInputLayout = findViewById(R.id.content_text_input);

        saveButton = findViewById(R.id.add_entry_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveJournalEntry();
            }
        });
    }

    /**
     *
     */
    private void saveJournalEntry() {

        final String title = titleEditText.getText().toString();
        final String content = contentEditText.getText().toString();

        if(!validTitle()){
            return;
        }

        if(!validContent()){
            return;
        }

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (journalId == AppConstants.DEFAULT_JOURNAL_ENTRY_ID) {
                    JournalEntry journalEntry = new JournalEntry(title, content);
                    Date dateCreated = new Date();
                    journalEntry.setDateCreated(dateCreated);
                    appDatabase.journalDao().insertJournalRecord(journalEntry);

                } else {
                    LiveData<JournalEntry> journalEntryData = appDatabase.journalDao().loadJournalRecordById(journalId);

                    JournalEntry journalEntry = journalEntryData == null ? new JournalEntry(title,content) : journalEntryData.getValue();
                    if(journalEntry == null){
                        journalEntry = new JournalEntry(title,content);
                        Date dateCreated = new Date();
                        journalEntry.setDateCreated(dateCreated);
                    }
                    journalEntry.setId(journalId);
                    journalEntry.setContent(content);
                    journalEntry.setTitle(title);
                    journalEntry.setDateCreated(journalEntry.getDateCreated() == null ? new Date() : journalEntry.getDateCreated() );

                    appDatabase.journalDao().updateJournalRecord(journalEntry);
                }
                finish();
            }
        });
    }

    /**
     *
     *
     * @return
     */
    private boolean validTitle() {

        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleInputLayout.setError(getString(R.string.title_error_message));
            requestFocus(titleEditText);
            return false;
        }

        titleInputLayout.setErrorEnabled(false);
        return true;
    }

    /**
     *
     * @return
     */
    private boolean validContent() {

        if (contentEditText.getText().toString().trim().isEmpty()) {
            contentInputLayout.setError(getString(R.string.content_error_message));
            requestFocus(contentEditText);
            return false;
        }

        contentInputLayout.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
