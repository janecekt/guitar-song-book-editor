package com.songbook.android.framework;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class SummaryListPreference extends ListPreference {
    private boolean isInitialized = false;
    private String rawSummary;

    public SummaryListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setValue(String value) {
        // Embed initialization here
        if (!isInitialized) {
            isInitialized = true;
            this.rawSummary = (getSummary() != null) ? getSummary().toString() : null;
        }

        // Set Value
        super.setValue(value);

        // update summary
        updateSummary(value);
    }


    @Override
    public void setEntryValues(CharSequence[] entryValues) {
        super.setEntryValues(entryValues);
        updateSummary(getValue());
    }


    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        updateSummary(getValue());
    }

    private void updateSummary(String newValue) {
        if (rawSummary != null) {
            if (newValue == null) {
                setSummary( String.format(rawSummary, "") );
            }
            else {
                CharSequence[] entries = getEntries();
                CharSequence[] entryValues = getEntryValues();
                if (entries != null && entryValues != null) {
                    for (int i=0; i<entryValues.length; i++) {
                        if (newValue.equals(entryValues[i])) {
                            if (i<entries.length) {
                                setSummary( String.format(rawSummary, entries[i]) );
                                return;
                            }
                            break;
                        }
                    }
                }
                setSummary( String.format(rawSummary, newValue) );
            }
        }
    }
}
