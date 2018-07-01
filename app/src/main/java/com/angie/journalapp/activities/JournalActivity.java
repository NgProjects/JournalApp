package com.angie.journalapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.angie.journalapp.R;
import com.angie.journalapp.adapter.JournalAdapter;
import com.angie.journalapp.database.AppDatabase;
import com.angie.journalapp.database.JournalEntry;
import com.angie.journalapp.utils.AppConstants;
import com.angie.journalapp.utils.AppExecutors;
import com.angie.journalapp.viewmodels.JournalViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class JournalActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener{

    private RecyclerView journalEntryRecycler;
    private JournalAdapter journalAdapter;
    private AppDatabase appDatabase;
    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;
    private Query currentUserQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String currentUserId = currentUser == null ? "0" : currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("journal_entries");
        databaseReference.keepSynced(true);

        currentUserQuery = databaseReference.orderByChild("user_id").equalTo(currentUserId);
        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null ){
                    startActivity(new Intent(JournalActivity.this, MainActivity.class));
                }
            }
        };

        journalEntryRecycler = (RecyclerView) findViewById(R.id.journal_content_recycler_view);
        journalEntryRecycler.setHasFixedSize(true);
        journalEntryRecycler.setLayoutManager(new LinearLayoutManager(this));

        journalAdapter = new JournalAdapter(this, this);
        journalEntryRecycler.setAdapter(journalAdapter);

        FloatingActionButton createNewJournalEntryButton = findViewById(R.id.fab);

        createNewJournalEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(JournalActivity.this, AddJournalActivity.class);
                startActivity(addTaskIntent);
            }
        });

        appDatabase = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();

    }

    private void setupViewModel() {
        JournalViewModel viewModel = ViewModelProviders.of(this).get(JournalViewModel.class);
        viewModel.getJournalEntries().observe(this, new Observer<List<JournalEntry>>() {
            @Override
            public void onChanged(@Nullable List<JournalEntry> journalEntries) {
                journalAdapter.setJournalEntries(journalEntries);
            }
        });
    }

    /**
     * inflate menu
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(JournalActivity.this, AddJournalActivity.class);
        intent.putExtra(AppConstants.JOURNAL_ID, itemId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout_action){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * logout and clear d
     */
    private void logout() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.journalDao().deleteCurrentTable();
                Intent intent = new Intent(JournalActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        firebaseAuth.signOut();
    }
}
