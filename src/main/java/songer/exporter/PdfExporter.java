package songer.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import songer.parser.nodes.ChordNode;
import songer.parser.nodes.LineNode;
import songer.parser.nodes.Node;
import songer.parser.nodes.SongBook;
import songer.parser.nodes.SongNode;
import songer.parser.nodes.TextNode;
import songer.parser.nodes.VerseNode;



public class PdfExporter implements Exporter {
    private static final float POINTS_PER_MM = 2.83464567f;

    private final Font songTitleFont;
    private final Font textFont;
    private final Font chordFont;
    private final Paragraph verseSpacing;


    public PdfExporter() {
        FontFactory.registerDirectories();
        songTitleFont = FontFactory.getFont(FontFactory.TIMES, BaseFont.CP1250, 14f, Font.BOLD);
        textFont = FontFactory.getFont(FontFactory.TIMES, BaseFont.CP1250, 11f, Font.NORMAL);
        chordFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, 11f, Font.BOLD);
        verseSpacing = new Paragraph(" ");
        verseSpacing.setLeading(5f, 0.5f);
    }



    @Override
    public void export(File baseDir, SongBook songBook) {
        // Output
        File outputFile = new File(baseDir.getAbsoluteFile(),  "/pdf/song-book.pdf" );
        
        try {
            // Sort songs
            List<SongNode> sortedArrayList = new ArrayList<SongNode>(songBook.getSongNodeList());
            Collections.sort(sortedArrayList, new SongNode.TitleComparator() );

            // Initialize exporter
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.setPageSize(PageSize.A4);
            document.setMargins(35*POINTS_PER_MM, 10*POINTS_PER_MM, 7*POINTS_PER_MM, 7*POINTS_PER_MM);
            document.setMarginMirroring(true);

            document.open();

            // Build TOC
            Chunk tocTitle = new Chunk("SONG BOOK - TABLE OF CONTENTS", songTitleFont);
            tocTitle.setLocalDestination("TOC");

            document.add(new Paragraph(tocTitle));
            for (int i=0; i<sortedArrayList.size(); i++) {
                SongNode songNode = sortedArrayList.get(i);
                int chapterNumber = i+1;
                Chunk tocEntry = new Chunk(chapterNumber+". " + songNode.getTitle(), textFont);
                tocEntry.setLocalGoto("SONG::"+chapterNumber);
                document.add(new Paragraph(tocEntry));
            }
            document.newPage();

            // Build document
            for (int i=0; i<sortedArrayList.size(); i++) {
                // Build chapter
                SongNode songNode = sortedArrayList.get(i);
                document.add( buildChapter(songNode, i+1) );
                document.newPage();
            }
            
            document.close();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write to file " + outputFile.getName(), ex);
        } catch (DocumentException ex) {
            throw new RuntimeException("Document exception " + ex.getMessage(), ex);
        }
    }
    
    private Chapter buildChapter(SongNode songNode, int chapterNumber) {
        // Title
        Chunk chapterTitle = new Chunk(songNode.getTitle(), songTitleFont);
        chapterTitle.setLocalDestination("SONG::" + chapterNumber);
        chapterTitle.setLocalGoto("TOC");
        
        Chapter chapter = new Chapter(new Paragraph(chapterTitle), chapterNumber);
        for (VerseNode verseNode : songNode.getVerseList()) {
            processVerse(verseNode, chapter);
        }
        return chapter;
    }
    
    private void processVerse(VerseNode verseNode, Chapter chapter) {
        chapter.add(verseSpacing);
        for (LineNode lineNode : verseNode.getLineNodes()) {
            chapter.add( buildLine(lineNode) );
        }

    }
    
    private Paragraph buildLine(LineNode lineNode) {
        Paragraph paragraph = new Paragraph();
        for (Node node : lineNode.getContentList()) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                paragraph.add( new Phrase(" " + textNode.getText() + " ", textFont) );
            } else if (node instanceof ChordNode) {
                ChordNode chordNode = (ChordNode) node;
                Chunk chunk = new Chunk( chordNode.getText(), chordFont);
                chunk.setTextRise(4f);
                paragraph.add(chunk);
                paragraph.setLeading(4f, 1.2f);
            }
        }
        return paragraph;
    }
}