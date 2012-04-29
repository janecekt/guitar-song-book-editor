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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import com.google.inject.Inject;
import com.songbook.android.R;
import com.songbook.android.util.PreferencesManager;
import com.songbook.android.util.SongListManager;
import com.songbook.core.model.SongNode;
import com.songbook.core.util.FileIO;
import com.songbook.core.visitor.HtmlBuilderVisitor;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

public class SongViewActivity extends RoboActivity {
    private static final Pattern SONG_TEMPLATE_TOKEN_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}"); 
    private static final String SONG_TEMPLATE = FileIO.readResourceToString("/song-template.html");

    @Inject
    PreferencesManager preferencesManager;

    @Inject
    SongListManager songListManager;
    
    @InjectResource(R.string.label_noSongsLoaded)
    String labelNoSongsLoaded;

    @InjectView(R.id.song_view_web_view)
    WebView webView;

    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_view_layout);
        gestureDetector = new GestureDetector(new SwipeGestureDetector());

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPrefs:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
        }
        // True = menu event fully handled
        return true;
    }

    
    private void refresh() {
        SongNode songNode = songListManager.getSelectedSongNode();               
        String data = buildSongHtml(songNode);
        webView.loadData(data, "text/html", "UTF8");
    }

    private void onSwipeLeft() {
        if (songListManager.getSongNodeList().size() > 0) {
            int songIndex = songListManager.getSelectedIndex();
            songIndex = (songIndex + 1) % songListManager.getSongNodeList().size();
            songListManager.setSelectedIndex(songIndex);
            refresh();
        }
    }

    private void onSwipeRight() {
        if (songListManager.getSongNodeList().size() > 0) {
            int songIndex = songListManager.getSelectedIndex();
            songIndex = (songIndex - 1 + songListManager.getSongNodeList().size()) % songListManager.getSongNodeList().size();
            songListManager.setSelectedIndex(songIndex);
            refresh();
        }
    }



    
    
    private String buildSongHtml(SongNode songNode) {
        Matcher matcher = SONG_TEMPLATE_TOKEN_PATTERN.matcher(SONG_TEMPLATE);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String tokenName = matcher.group(1);
            if ("VERSE_SPACING".equals(tokenName)) {
                matcher.appendReplacement(sb, Integer.toString(preferencesManager.getVerseSpacing()));
            } else if ("LINE_SPACING".equals(tokenName)) {
                matcher.appendReplacement(sb, Integer.toString(preferencesManager.getLineSpacing()));
            } else if ("FONT_SIZE".equals(tokenName)) {
                matcher.appendReplacement(sb, Integer.toString(preferencesManager.getFontSize()));
            } else if ("SONG_BODY".equals(tokenName)) {
                // Build SongHtml
                StringBuilder songHtml = new StringBuilder();
                if (songNode != null) {                
                    songNode.accept(new HtmlBuilderVisitor(songHtml, preferencesManager.isChordsOn(), 0, true));
                } else {
                    songHtml.append("<DIV class=\"text\">").append(labelNoSongsLoaded).append("</DIV>");
                }

                // Replace placeholder in the template
                matcher.appendReplacement(sb, songHtml.toString());
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        // Swipe properties, you can change it to make the swipe
        // longer or shorter and speed
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("TRACE","OnFling called");
            float diffAbs = Math.abs(e1.getY() - e2.getY());
            float diff = e1.getX() - e2.getX();

            if (diffAbs > SWIPE_MAX_OFF_PATH)
                return false;


            if ((diff > SWIPE_MIN_DISTANCE) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
                // Left swipe
                onSwipeLeft();
            } else if ((-diff > SWIPE_MIN_DISTANCE) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
                // Right swipe
                onSwipeRight();

            }
            return false;
        }
    }
}