package com.songbook.ui;

import java.io.IOException;

import com.songbook.util.FileIO;
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
    public void songPagingScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenTitleIs("Guitar Song Book Editor - song1.txt");
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenPreviousButtonPressed();
        thenTitleIs("Guitar Song Book Editor - song1.txt");
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenNextButtonPressed();
        thenTitleIs("Guitar Song Book Editor - song2.txt");
        thenSongDisplayedIs(SongData.SONG2, DisplayMode.HTML);

        whenNextButtonPressed();
        thenTitleIs("Guitar Song Book Editor - song2.txt");
        thenSongDisplayedIs(SongData.SONG2, DisplayMode.HTML);

        whenPreviousButtonPressed();
        thenTitleIs("Guitar Song Book Editor - song1.txt");
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
    }


    @Test
    public void switchBetweenViewAndEditModeScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.TEXT);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);
    }


    @Test
    public void changeTransposeScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenTransposeSetTo(2);
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.TEXT);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);
    }


    @Test
    public void editAndSaveSongScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.TEXT);

        whenSongEditedTo(SongData.SONG1_EDITED);
        whenSaveButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_EDITED, DisplayMode.HTML);
        thenSongFileOnDiskContains("song1.txt", SongData.SONG1_EDITED);
    }


    @Test
    public void transposeAndSaveScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenTransposeSetTo(2);
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);

        whenSaveButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_2UP, DisplayMode.HTML);
        thenTransposeValueDisplayedIs(0);
        thenSongFileOnDiskContains("song1.txt", SongData.SONG1_2UP);
    }


    @Test
    public void editTransposeAndSaveScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenEditViewButtonPressed();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.TEXT);

        whenSongEditedTo(SongData.SONG1_EDITED);
        thenSongDisplayedIs(SongData.SONG1_EDITED, DisplayMode.TEXT);

        whenTransposeSetTo(2);
        thenSongDisplayedIs(SongData.SONG1_EDITED_2UP, DisplayMode.TEXT);

        whenSaveButtonPressed();
        thenSongDisplayedIs(SongData.SONG1_EDITED_2UP, DisplayMode.HTML);
        thenTransposeValueDisplayedIs(0);
        thenSongFileOnDiskContains("song1.txt", SongData.SONG1_EDITED_2UP);
    }


    @Test
    public void addNewSongScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);


    }


    @Test
    public void exportToLatexScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenExportToLatexPressed();
    }


    @Test
    public void exportToHTMLScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenExportToHtmlPressed();
    }


    @Test
    public void exportToPDFScenario() throws Exception {
        givenSong("song1.txt", SongData.SONG1);
        givenSong("song2.txt", SongData.SONG2);

        whenApplicationStarted();
        thenSongDisplayedIs(SongData.SONG1, DisplayMode.HTML);

        whenExportToPdfPressed();
    }
}
