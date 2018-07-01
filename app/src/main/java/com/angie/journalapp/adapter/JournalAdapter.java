package com.angie.journalapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angie.journalapp.R;
import com.angie.journalapp.database.JournalEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    final private ItemClickListener journalItemOnclickListener;

    private List<JournalEntry> journalEntries;
    private Context context;

    public JournalAdapter(ItemClickListener journalItemOnclickListener, Context context) {
        this.journalItemOnclickListener = journalItemOnclickListener;
        this.context = context;
    }

    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_entry_layout, parent, false);

        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JournalViewHolder holder, int position) {

        JournalEntry journalEntry = journalEntries.get(position);
        String title = journalEntry.getTitle();
        String content = journalEntry.getContent();
        String dateCreated = journalEntry.getDateCreated() == null ? "" : dateFormat.format(journalEntry.getDateCreated());

        //Set values
        holder.titleView.setText(title);
        holder.contentView.setText(content);
        holder.dateCreatedView.setText(dateCreated);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (journalEntries == null) {
            return 0;
        }
        return journalEntries.size();
    }

    public List<JournalEntry> getJournalEntries() {
        return journalEntries;
    }

    /**
     * updates the list of entries, notifies the recycler view when data changes
     * @param journalEntries
     */
    public void setJournalEntries(List<JournalEntry> journalEntries) {
        this.journalEntries = journalEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleView;
        TextView contentView;
        TextView dateCreatedView;

        public JournalViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            contentView = itemView.findViewById(R.id.contentView);
            dateCreatedView = itemView.findViewById(R.id.dateCreatedView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = journalEntries.get(getAdapterPosition()).getId();
            journalItemOnclickListener.onItemClickListener(elementId);
        }
    }
}
