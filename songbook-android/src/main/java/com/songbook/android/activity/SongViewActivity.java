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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
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

    @InjectResource(R.string.label_transposeTitle)
    String labelTransposeTitle;

    @InjectView(R.id.song_view_web_view)
    WebView webView;

    private GestureDetector gestureDetector;
    private Dialog transposeDialog;
    private SongNode songNode;
    private int transposition = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep the screen saver
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize layout
        setContentView(R.layout.song_view_layout);

        // Setup gestures
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
        refresh(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_view_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPrefs:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
            case R.id.itemTranspose:
                openTransposeDialog();
                break;
        }
        // True = menu event fully handled
        return true;
    }


    private void onTransposeUp() {
        transposition++;
        refresh(false);
    }


    private void onTransposeNone() {
        transposition = 0;
        refresh(false);
    }


    private void onTransposeDown() {
        transposition--;
        refresh(false);
    }

    private void onTransposeSave() {
        songListManager.saveTransposition(songNode.getSourceFile().getName(), transposition);
    }


    private void refresh(boolean updateTransposition) {
        songNode = songListManager.getSelectedSongNode();
        if (updateTransposition) {
            transposition = songListManager.getTransposition(songNode.getSourceFile().getName());
        }
        String data = buildSongHtml(songNode);
        webView.loadData(data, "text/html", "UTF8");
        transposeDialogRefreshTransposeTitle();
    }


    private void onSwipeLeft() {
        if (songListManager.getSongNodeList().size() > 0) {
            int songIndex = songListManager.getSelectedIndex();
            songIndex = (songIndex + 1) % songListManager.getSongNodeList().size();
            songListManager.setSelectedIndex(songIndex);
            refresh(true);
        }
    }


    private void onSwipeRight() {
        if (songListManager.getSongNodeList().size() > 0) {
            int songIndex = songListManager.getSelectedIndex();
            songIndex = (songIndex - 1 + songListManager.getSongNodeList().size()) % songListManager.getSongNodeList().size();
            songListManager.setSelectedIndex(songIndex);
            refresh(true);
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
                StringBuffer songHtml = new StringBuffer();
                if (songNode != null) {
                    songNode.accept(new HtmlBuilderVisitor(songHtml, transposition,
                            HtmlBuilderVisitor.Mode.TWO_LINE_TITLE,
                            HtmlBuilderVisitor.Mode.DISPLAY_TRANSPOSITION,
                            HtmlBuilderVisitor.Mode.HTML_ESCAPING,
                            HtmlBuilderVisitor.Mode.DISPLAY_SONG_INDEX,
                            preferencesManager.isChordsOn() ? HtmlBuilderVisitor.Mode.CHORDS_ON : null));
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


    private void openTransposeDialog() {
        transposeDialog = new Dialog(this);
        transposeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transposeDialog.setContentView(R.layout.transpose_dialog_layout);

        View transposeUp = transposeDialog.findViewById(R.id.transposeUpButton);
        transposeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTransposeUp();
            }
        });

        View transposeNone = transposeDialog.findViewById(R.id.transposeNoneButton);
        transposeNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTransposeNone();
            }
        });

        View transposeDown = transposeDialog.findViewById(R.id.transposeDownButton);
        transposeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTransposeDown();
            }
        });

        View transposeSave = transposeDialog.findViewById(R.id.transposeSaveButton);
        transposeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTransposeSave();
            }
        });

        View transposeClose = transposeDialog.findViewById(R.id.titleBarCloseButton);
        transposeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transposeDialog.hide();
                transposeDialog = null;
            }
        });

        transposeDialogRefreshTransposeTitle();

        Window window = transposeDialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setGravity(Gravity.BOTTOM);

        transposeDialog.show();
    }


    private void transposeDialogRefreshTransposeTitle() {
        if (transposeDialog != null) {
            String transposeString = (transposition > 0) ? ("+" + transposition) : Integer.toString(transposition);
            TextView title = (TextView) transposeDialog.findViewById(R.id.titleBarTitle);
            title.setText(String.format(labelTransposeTitle, transposeString));
        }
    }


    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        // Swipe properties, you can change it to make the swipe
        // longer or shorter and speed
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("TRACE", "OnFling called");
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