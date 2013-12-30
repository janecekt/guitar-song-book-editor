/*
 *  Copyright (c) 2008 - Tomas Janecek.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.songbook.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.inject.Inject;
import com.songbook.android.R;
import com.songbook.android.event.OnSongBookDataChanged;
import com.songbook.android.util.EventBroker;
import com.songbook.android.util.PreferencesManager;
import com.songbook.core.util.FileIO;
import roboguice.activity.RoboPreferenceActivity;
import roboguice.inject.InjectResource;

public class PreferencesActivity extends RoboPreferenceActivity {
    @Inject
    PreferencesManager preferencesManager;

    @Inject
    EventBroker eventBroker;

    @InjectResource(R.string.prefs_orderingLocale_key)
    private String orderingLocaleKey;

    @InjectResource(R.string.prefs_ordering_key)
    private String orderingKey;

    @InjectResource(R.string.prefs_songBookLocation_key)
    private String songBookLocationKey;

    @InjectResource(R.string.prefs_downloadSongbook_key)
    private String downloadSongbookKey;
    
    @InjectResource(R.string.prefs_updateApk_key)
    private String updateApkKey;
    
    @InjectResource(R.string.prefs_message_locationDoesNotExit)
    private String locationDoesNotNotExistMessage;

    @InjectResource(R.string.prefs_message_fileExistsButDoesNotEndWithZip)
    private String fileExistsButDoesNotEndWithZipMessage;
    
    @InjectResource(R.string.prefs_message_downloadingStarted)
    private String downloadingStartedMessage;

    @InjectResource(R.string.prefs_message_downloadingCompleted)
    private String downloadingCompletedMessage;

    @InjectResource(R.string.prefs_message_downloadingFailed)
    private String downloadingFailedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        // Populate ordering preferences
        ListPreference orderingPreference = (ListPreference) findPreference(orderingKey);
        orderingPreference.setEntryValues(new CharSequence[]{
                PreferencesManager.OrderingKey.BY_TITLE.name(),
                PreferencesManager.OrderingKey.BY_SUBTITLE.name(),
                PreferencesManager.OrderingKey.BY_INDEX.name()});
        
        // Populate locale entries selection
        List<String> localeEntries = new ArrayList<String>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!locale.getDisplayName().contains("(")) {
                localeEntries.add(locale.getDisplayName(locale));
            }
        }
        String[] localeEntriesArray = localeEntries.toArray(new String[localeEntries.size()]);
        ListPreference orderingLocalePreference = (ListPreference) findPreference(orderingLocaleKey);
        orderingLocalePreference.setEntries(localeEntriesArray);
        orderingLocalePreference.setEntryValues(localeEntriesArray);
        orderingLocalePreference.setDefaultValue(Locale.US.getDisplayName());

        // Validation listener
        findPreference(songBookLocationKey).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object rawValue) {
                String stringValue = (String) rawValue;                
                if (stringValue.startsWith("internal://")) {
                    String actualFileName = stringValue.replace("internal://", "");
                    List<String> internalFileList = Arrays.asList( fileList() );
                    if (internalFileList.contains(actualFileName)) {
                        return true;
                    } else {
                        Toast.makeText(PreferencesActivity.this,
                                String.format(locationDoesNotNotExistMessage, rawValue),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

                File file = new File((String) rawValue);
                if (!file.exists()) {
                    Toast.makeText(PreferencesActivity.this, 
                            String.format(locationDoesNotNotExistMessage, rawValue),
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                if (file.isFile() && (!file.getAbsolutePath().endsWith(".zip"))) {
                    Toast.makeText(PreferencesActivity.this,
                            String.format(fileExistsButDoesNotEndWithZipMessage, rawValue),
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                return true;
            }
        });

        // Implement long click listeners
        // NOTE: Not supported directly by PreferenceClass - hence we have to do it via the view.
        final Preference downloadSongbookPreference = findPreference(downloadSongbookKey);
        final Preference updateApkPreference = findPreference(updateApkKey);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                ListAdapter listAdapter = listView.getAdapter();
                if (downloadSongbookPreference == listAdapter.getItem(position)) {
                    onDownloadSongBook();
                    return true;
                }
                if (updateApkPreference == listAdapter.getItem(position)) {
                    onUpdateApk();
                    return true;
                }
                return false;
            }
        });
    }


    public void onDownloadSongBook() {
        try {
            // Change songbook location to default (if not set to internal)
            String songBookLocation = (!preferencesManager.getSongBookLocation().startsWith("internal://"))
                    ? preferencesManager.getSongBookLocationDefault()
                    : preferencesManager.getSongBookLocation();

            String actualSongBookLocation = songBookLocation.replace("internal://", "");

            // Display toaster popup
            Toast.makeText(PreferencesActivity.this,
                    String.format(downloadingStartedMessage, preferencesManager.getSongBookUrl(), songBookLocation),
                    Toast.LENGTH_LONG).show();

            // TODO: Do this asynchronously
            // Start download
            URL url = new URL(preferencesManager.getSongBookUrl());
            FileOutputStream outputStream = openFileOutput(actualSongBookLocation, MODE_WORLD_READABLE);

            // Create InputStream from connection
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();

            // Append input stream to output stream
            FileIO.appendInputStreamToOutputStream(inputStream, outputStream);

            // Display toaster popup
            Toast.makeText(PreferencesActivity.this,
                    downloadingCompletedMessage,
                    Toast.LENGTH_LONG).show();

            // Publish event
            eventBroker.publish(new OnSongBookDataChanged());
        } catch (IOException ex) {
            Toast.makeText(PreferencesActivity.this,
                    String.format(downloadingFailedMessage, ex.getMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }


    public void onUpdateApk() {
        Toast.makeText(PreferencesActivity.this,
                String.format(downloadingStartedMessage, preferencesManager.getApplicationApkUrl(), "internal://songbook-android-latest.apk"),
                Toast.LENGTH_LONG).show();

        try {
            URL url = new URL(preferencesManager.getApplicationApkUrl());
            FileOutputStream outputStream = openFileOutput("songbook-android-latest.apk", MODE_WORLD_READABLE);

            // Create InputStream from connection
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();

            // Append input stream to output stream
            FileIO.appendInputStreamToOutputStream(inputStream, outputStream);

            Toast.makeText(PreferencesActivity.this,
                    downloadingCompletedMessage,
                    Toast.LENGTH_LONG).show();

            // Start the activity to trigger installation of the APK
            File apkFile = getFileStreamPath("songbook-android-latest.apk");
            Intent installApkIntent = new Intent(Intent.ACTION_VIEW);
            installApkIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            startActivity(installApkIntent);
        } catch (IOException ex) {
            Toast.makeText(PreferencesActivity.this,
                    String.format(downloadingFailedMessage, ex.getMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }
}
