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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.songbook.core.comparator.SongNodeSubTitleComparator;
import com.songbook.core.comparator.SongNodeTitleComparator;
import com.songbook.core.model.SongNode;
import com.songbook.core.util.SongNodeLoader;

public class SongListManager {
    public enum Status { LOCATION_DOES_NOT_EXIST_OR_IS_INVALID, LOADED, LOADING }

    private final Context context;
    
    private final PreferencesManager preferencesManager;

    private final SongNodeLoader songNodeLoader;

    private Status status = Status.LOADING;

    private int selectedIndex;

    private List<SongNode> songNodeList;
   
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(preferencesManager.getOrderingKey())
                    || key.equals(preferencesManager.getOrderingLocaleKey())) {
                onOrderingChanged();
            }
            
            if (key.equals(preferencesManager.getSongBookLocationKey())                    
                    || key.equals(preferencesManager.getFileEncoding())) {
                initialize();
            }
        }
    };
    

    @Inject
    public SongListManager(Context context, PreferencesManager preferencesManager, SongNodeLoader songNodeLoader) {
        this.context = context;
        this.preferencesManager = preferencesManager;
        this.songNodeLoader = songNodeLoader;
        preferencesManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        initialize();
    }
    
    
    private Comparator<SongNode> buildComparatorFromPreferences() {
        Locale orderingLocale = preferencesManager.getOrderingLocale();
        switch (preferencesManager.getOrdering()) {
            case BY_SUBTITLE:
                return new SongNodeSubTitleComparator(orderingLocale);
            case BY_TITLE:
            default:
                return new SongNodeTitleComparator(orderingLocale);
        }        
    }
    
    
    private void onOrderingChanged() {
        Comparator<SongNode> comparator = buildComparatorFromPreferences();
        setSongNodeList(songNodeList, comparator);
    }

    

    private void initialize() {
        // Extract preferences
        Comparator<SongNode> comparator = buildComparatorFromPreferences();
        String fileEncoding = preferencesManager.getFileEncoding();
        String location = preferencesManager.getSongBookLocation();

        try {
            // Load from internal storage
            if (location.startsWith("internal://")) {
                String internalFileName = location.replace("internal://", "");
                FileInputStream inputStream = context.openFileInput(internalFileName);                
                setSongNodeList(songNodeLoader.loadSongNodesFromZip(inputStream, fileEncoding), comparator);
                status = Status.LOADED;
                return;
            }
            

            // Load from directory
            File locationFile = new File(location);
            if (locationFile.isDirectory()) {
                List<SongNode> songNodes = songNodeLoader.loadSongNodesFromDirectory(locationFile, fileEncoding);
                setSongNodeList(songNodes, comparator);
                status = Status.LOADED;
                return;
            }

            // Load from ZIP file
            if (locationFile.isFile() && locationFile.getAbsolutePath().endsWith(".zip")) {
                setSongNodeList(songNodeLoader.loadSongNodesFromZip(new FileInputStream(location), fileEncoding), comparator);
                status = Status.LOADED;
                return;
            }

            // Invalid location
            setSongNodeList(Collections.unmodifiableList(new ArrayList<SongNode>()), comparator);
            status = Status.LOCATION_DOES_NOT_EXIST_OR_IS_INVALID;
        } catch (Exception ex) {
            // Invalid location
            setSongNodeList(Collections.unmodifiableList(new ArrayList<SongNode>()), null);
            status = Status.LOCATION_DOES_NOT_EXIST_OR_IS_INVALID;            
        }
    }


    public Status getStatus() {
        return status;
    }


    public int getSelectedIndex() {
        return selectedIndex;
    }


    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public SongNode getSelectedSongNode() {
        return (selectedIndex >= 0) ? songNodeList.get(selectedIndex) : null;
    }


    public List<SongNode> getSongNodeList() {
        return songNodeList;
    }


    public void setSongNodeList(List<SongNode> songList, Comparator<SongNode> comparator) {
        if (comparator != null) {
            Collections.sort(songList, comparator);
        }
        this.songNodeList = songList;
        this.selectedIndex = (songList.size() > 0) ? 0 : -1;
    }
}
