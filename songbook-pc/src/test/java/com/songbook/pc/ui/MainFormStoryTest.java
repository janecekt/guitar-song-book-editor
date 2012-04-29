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
package com.songbook.pc.ui;

import java.io.IOException;

import com.songbook.core.util.FileIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** BDD like acceptance test for application. */
public class MainFormStoryTest extends MainFormSteps {

    @After
    public void tearDown() {
        // Delete directory
        FileIO.deleteDirectory(TEST_BASEDIR);
    }


    @Before
    public void setUp() throws IOException {
        // Delete directory
        FileIO.deleteDirectory(TEST_BASEDIR);
    }


    @Test
    public void songSelectionScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenTitleIs("Guitar Song Book Editor - song1.txt");
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenSongSelected(SongData.SONG1);
        thenTitleIs("Guitar Song Book Editor - song1.txt");
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenSongSelected(SongData.SONG2);
        thenTitleIs("Guitar Song Book Editor - song2.txt");
        thenSongDisplayedIs(SongData.SONG2, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG2);

        whenSongSelected(SongData.SONG1);
        thenTitleIs("Guitar Song Book Editor - song1.txt");
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);
    }


    @Test
    public void switchBetweenViewAndEditModeScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.TEXT);
        thenSongSelectedIs(SongData.SONG1);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);
    }


    @Test
    public void changeTransposeScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenTransposeSetTo(2);
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.TEXT);
        thenSongSelectedIs(SongData.SONG1);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);
    }


    @Test
    public void editAndSaveSongScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.TEXT);
        thenSongSelectedIs(SongData.SONG1);

        whenSongEditedTo(SongData.SONG1_EDITED);

        whenSaveButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_EDITED, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1_EDITED);
        thenSongFileOnDiskContains("song1.txt", SongData.SONG1_EDITED);
    }


    @Test
    public void transposeAndSaveScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenTransposeSetTo(2);
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenSaveButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1_2UP);
        thenTransposeValueDisplayedIs(0);
        thenSongFileOnDiskContains("song1.txt", SongData.SONG1_2UP);
    }


    @Test
    public void editTransposeAndSaveScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);


        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.TEXT);
        thenSongSelectedIs(SongData.SONG1);


        whenSongEditedTo(SongData.SONG1_EDITED);
        thenSongDisplayedIs(SongData.SONG1_EDITED, DisplayMode.TEXT);
        thenSongSelectedIs(SongData.SONG1);

        whenTransposeSetTo(2);
        thenSongDisplayedIs(SongData.SONG1_EDITED_2UP, DisplayMode.TEXT);
        thenSongSelectedIs(SongData.SONG1);

        whenSaveButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_EDITED_2UP, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1_EDITED_2UP);
        thenTransposeValueDisplayedIs(0);
        thenSongFileOnDiskContains("song1.txt", SongData.SONG1_EDITED_2UP);
    }


    @Test
    public void addNewSongScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);
    }


    @Test
    public void exportToLatexScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenExportToLatexPressed();
    }


    @Test
    public void exportToHTMLScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenExportToHtmlPressed();
    }


    @Test
    public void exportToPDFScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
        thenSongSelectedIs(SongData.SONG1);

        whenExportToPdfPressed();
    }
}
