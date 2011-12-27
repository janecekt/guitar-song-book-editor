package songer.exporter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import songer.parser.nodes.*;

import java.io.*;
import java.util.*;
import java.util.List;



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

            // Build document
            for (int i=0; i<sortedArrayList.size(); i++) {
                // Build chapter
                document.add( buildChapter(sortedArrayList.get(i), i+1) );
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
        Chapter chapter = new Chapter(
                new Paragraph(songNode.getTitle(), songTitleFont),
                chapterNumber);
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
