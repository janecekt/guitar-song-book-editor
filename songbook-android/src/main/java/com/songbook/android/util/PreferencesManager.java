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
package com.songbook.android.util;

import java.nio.charset.Charset;
import java.util.Locale;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.songbook.android.R;
import com.songbook.android.event.OnPreferencesChanged;
import roboguice.inject.InjectResource;

@SuppressWarnings("UnusedDeclaration")
public class PreferencesManager {
    public static enum OrderingKey {BY_TITLE, BY_SUBTITLE}

    private final SharedPreferences sharedPreferences;

    /** NOTE: This field CANNOT be local because SharedPreferences use WeakReferences. */
    @SuppressWarnings("FieldCanBeLocal")
    private final SharedPreferences.OnSharedPreferenceChangeListener listener;


    @Inject
    public PreferencesManager(final SharedPreferences sharedPreferences, final EventBroker eventBroker) {
        this.sharedPreferences = sharedPreferences;
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preference) {
                eventBroker.publish(new OnPreferencesChanged());
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }


    @InjectResource(R.string.prefs_chordsOn_key)
    private String chordsOnKey;

    @InjectResource(R.string.prefs_fontSize_key)
    private String fontSizeKey;

    @InjectResource(R.string.prefs_groupingEnabled_key)
    private String groupingEnabledKey;

    @InjectResource(R.string.prefs_lineSpacing_key)
    private String lineSpacingKey;

    @InjectResource(R.string.prefs_ordering_key)
    private String orderingKey;

    @InjectResource(R.string.prefs_orderingLocale_key)
    private String orderingLocaleKey;

    @InjectResource(R.string.prefs_verseSpacing_key)
    private String verseSpacingKey;
    
    @InjectResource(R.string.prefs_songBookLocation_key)
    private String songBookLocationKey;
    
    @InjectResource(R.string.prefs_fileEncoding_key)
    private String fileEncodingKey;


    public boolean isChordsOn() {
        return sharedPreferences.getBoolean(chordsOnKey, false);
    }


    public int getFontSize() {
        return Integer.parseInt(sharedPreferences.getString(fontSizeKey, "0"));
    }


    public boolean isGroupingEnabled() {
        return sharedPreferences.getBoolean(groupingEnabledKey, false);
    }


    public int getLineSpacing() {
        return Integer.parseInt(sharedPreferences.getString(lineSpacingKey, "0"));
    }


    public OrderingKey getOrdering() {
        return OrderingKey.valueOf(sharedPreferences.getString(orderingKey, OrderingKey.BY_TITLE.name()));
    }


    public Locale getOrderingLocale() {
        String localeDisplayName = sharedPreferences.getString(orderingLocaleKey, null);
        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getDisplayName(locale).equals(localeDisplayName)) {
                return locale;
            }
        }
        return Locale.getDefault();
    }


    public int getVerseSpacing() {
        return Integer.parseInt(sharedPreferences.getString(verseSpacingKey, "0"));
    }
    
    
    public String getSongBookLocation() {
        return sharedPreferences.getString(songBookLocationKey, "/sdcard/songbook");
    }
    
    public Charset getFileEncoding() {
        return Charset.forName(sharedPreferences.getString(fileEncodingKey, "CP1250"));
    }


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }


    public String getChordsOnKey() {
        return chordsOnKey;
    }


    public String getFontSizeKey() {
        return fontSizeKey;
    }


    public String getGroupingEnabledKey() {
        return groupingEnabledKey;
    }


    public String getLineSpacingKey() {
        return lineSpacingKey;
    }


    public String getOrderingKey() {
        return orderingKey;
    }


    public String getOrderingLocaleKey() {
        return orderingLocaleKey;
    }


    public String getVerseSpacingKey() {
        return verseSpacingKey;
    }


    public String getSongBookLocationKey() {
        return songBookLocationKey;
    }


    public String getFileEncodingKey() {
        return fileEncodingKey;
    }
}
