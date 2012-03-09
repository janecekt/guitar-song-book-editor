package com.songbook.ui;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.songbook.exporter.HtmlExporter;
import com.songbook.exporter.LaTexExporter;
import com.songbook.exporter.PdfExporter;
import com.songbook.model.SongNode;
import com.songbook.parser.ChordProParser;
import com.songbook.parser.Parser;
import com.songbook.parser.ParserException;
import com.songbook.ui.presentationmodel.EditorPanePresentationModel;
import com.songbook.ui.presentationmodel.MainFormPresentationModel;
import com.songbook.util.FileIO;
import com.songbook.util.FileList;
import com.songbook.util.FileListImpl;
import org.junit.Assert;

public class MainFormSteps {
    /** Prilis zlutoucky kun upel dabelske ody */
    private static final String CZECH_TEXT = "P\u0159\u00edli\u0161 \u017elu\u0165ou\u010dk\u00fd k\u016f\u0148 \u00fap\u011bl \u010f\u00e1blesk\u00e9 \u00f3dy.";
    private static final String ENCODING = "CP1250";



    public static enum DisplayMode {TEXT, HTML}

    public static enum SongData {
        SONG1("Song1 - Author",
                "[G/C]la [F]la",
                "[G]la",
                CZECH_TEXT),
        SONG1_2UP("Song1 - Author",
                "[A/D]la [G]la",
                "[A]la",
                CZECH_TEXT),
        SONG1_EDITED("Song1 - New author",
                "[G/C]la [F]la",
                "[G]loo [G7]loo"),
        SONG1_EDITED_2UP("Song1 - New author",
                "[A/D]la [G]la",
                "[A]loo [A7]loo"),
        SONG2("Song2 - Author",
                "[Em]aa [Dm]aa [G]aa");

        private final String songData;


        private SongData(String title, String... lines) {
            StringBuilder builder = new StringBuilder();
            builder.append(title).append("\n");
            for (String line : lines) {
                builder.append("\n\n").append(line);
            }
            builder.append("\n");
            this.songData = builder.toString();
        }


        public String getSongData() {
            return songData;
        }
    }

    public static final File TEST_BASEDIR = new File("target/testSongDir");
    private Parser<SongNode> parser;
    private MainFormPresentationModel mainPM;


    public void givenSong(String songFileName, SongData songData) throws IOException {
        File targetFile = new File(TEST_BASEDIR, songFileName);
        FileIO.createDirectory(TEST_BASEDIR);
        FileIO.writeStringToFile(targetFile.getAbsolutePath(), ENCODING, songData.getSongData());
    }


    public void whenApplicationStarted() {
        parser = ChordProParser.createParser();
        FileList fileList = new FileListImpl(TEST_BASEDIR.getAbsolutePath(), new FileListImpl.TxtFileFilter(), new FileListImpl.FileNameComparator(), parser);
        mainPM = new MainFormPresentationModel(parser, fileList, null, new HtmlExporter(), new LaTexExporter(), new PdfExporter());
        mainPM.setPropagateErrors(true);
    }


    public void whenNextButtonPressed() {
        mainPM.getNextAction().actionPerformed(null);
    }


    public void whenPreviousButtonPressed() {
        mainPM.getPreviousAction().actionPerformed(null);
    }


    public void whenEditViewButtonPressed() {
        mainPM.getEditAction().actionPerformed(null);
    }


    public void whenSaveButtonPressed() {
        mainPM.getSaveAction().actionPerformed(null);
    }


    public void whenExportToLatexPressed() {
        mainPM.getExportLatexAction().actionPerformed(null);
    }


    public void whenExportToHtmlPressed() {
        mainPM.getExportHtmlAction().actionPerformed(null);
    }


    public void whenExportToPdfPressed() {
        mainPM.getExportPdfAction().actionPerformed(null);
    }


    public void whenTransposeSetTo(int transposeValue) {
        mainPM.getTransposeModel().setValue(transposeValue);
    }


    public void whenSongEditedTo(SongData songData) {
        Assert.assertEquals("Cannot edit non-TEXT mode",
                EditorPanePresentationModel.CONTENT_TYPE_TEXT_PLAIN,
                mainPM.getEditorModel().getContentTypeModel().getString());

        mainPM.getEditorModel().getTextModel().setValue(songData.getSongData());
    }


    public void thenTitleIs(String expectedTitle) {
        Assert.assertEquals(expectedTitle, mainPM.getTitleModel().getValue());
    }


    public void thenTransposeValueDisplayedIs(int expectedTransposeValue) {
        Assert.assertEquals(expectedTransposeValue, mainPM.getTransposeModel().getValue());
    }


    public void thenSongFileOnDiskContains(String fileName, SongData expectedSongData) throws IOException {
        File file = new File(TEST_BASEDIR, fileName);
        Assert.assertEquals(expectedSongData.getSongData(),
                FileIO.readFileToString(file.getAbsolutePath(), ENCODING));
    }


    public void thenSongDisplayedIs(SongData expectedSongBody, DisplayMode expectedDisplayMode) throws ParserException {
        switch (expectedDisplayMode) {
            case HTML:
                Assert.assertEquals("Display mode should be HTML",
                        EditorPanePresentationModel.CONTENT_TYPE_TEXT_HTML,
                        mainPM.getEditorModel().getContentTypeModel().getString());

                Reader songReader = new StringReader(expectedSongBody.getSongData());
                SongNode expectedSongNode = parser.parse(songReader);
                Assert.assertEquals(expectedSongNode.getAsHTML(0),
                        mainPM.getEditorModel().getTextModel().getString());
                break;
            case TEXT:
                Assert.assertEquals("Display mode should be TEXT",
                        EditorPanePresentationModel.CONTENT_TYPE_TEXT_PLAIN,
                        mainPM.getEditorModel().getContentTypeModel().getString());
                Assert.assertEquals(expectedSongBody.getSongData(),
                        mainPM.getEditorModel().getTextModel().getString());
                break;
        }
    }
}
