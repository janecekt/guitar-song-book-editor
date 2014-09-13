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
package com.songbook.pc.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.codec.PngImage;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.songbook.core.comparator.SongNodeIndexComparator;
import com.songbook.core.model.ChordNode;
import com.songbook.core.model.LineNode;
import com.songbook.core.model.Node;
import com.songbook.core.model.SongBook;
import com.songbook.core.model.SongNode;
import com.songbook.core.model.TextNode;
import com.songbook.core.model.VerseNode;
import com.songbook.core.util.FileIO;
import com.songbook.core.util.SongNodeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PdfExporter implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(PdfExporter.class);
    private static final float POINTS_PER_MM = 2.83464567f;

    private final Font songTitleFont;
    private final Font textFont;
    private final Font chordFont;
    private final Paragraph verseSpacing;
    private final SongNodeLoader loader;


    public PdfExporter(SongNodeLoader loader) {
        this.loader = loader;

        // Load fonts
        FontFactory.registerDirectories();
        BaseFont timesFont = null;
        try {
            timesFont = BaseFont.createFont("C:/Windows/Fonts/times.ttf", BaseFont.CP1250, true);
            logger.info("Embedded TTF fonts from C:/Windows/Fonts");
        } catch (Exception ex) {
            try {
                timesFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, true);
                logger.info("Embedded default fonts");
            } catch (Exception ex1) {
                logger.error("Failed to load fonts ...");
            }
        }
                     
        // Initialize fonts
        if (timesFont != null) {
            songTitleFont = new Font(timesFont, 14f, Font.BOLD);
            textFont = new Font(timesFont, 11f, Font.NORMAL);
            chordFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, 11f, Font.BOLD);
        } else {
            songTitleFont = null;
            textFont = null;
            chordFont = null;
        }
        
        verseSpacing = new Paragraph(" ");
        verseSpacing.setLeading(5f, 0.5f);
    }



    @Override
    public void export(File baseDir, SongBook songBook) {
        if (songTitleFont == null) {
            throw new RuntimeException("Fonts not correcly loaded - cannot generate PDF !");
        }

        // Output
        File outputDir = new File(baseDir, "pdf");
        File outputFile = new File(outputDir, "song-book.pdf");
        File songIndexFile = new File(outputDir, SongNodeLoader.SONG_INDEX_FILE_NAME);
        FileIO.createDirectory(outputDir);

        try {
            // Sort songs alphabetically
            List<SongNode> orderedList = new ArrayList<SongNode>(songBook.getSongNodeList());
            Collections.sort(orderedList, new SongNodeIndexComparator(Locale.getDefault()));

            // Generate PDF - pass 1 (collect page stats)
            PageStats pageStats = generatePDF(orderedList, outputFile);

            // Reorder songs so that 2-page song always starts on even page number
            List<SongNode> reorderedList = reorderList(orderedList, pageStats);

            // Generate PDF - pass 2
            generatePDF(reorderedList, outputFile);

            // Write song index file
            loader.saveSongIndexFile(songIndexFile, reorderedList);

            // Open document
            openPDF(outputFile);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write to file " + outputFile.getName(), ex);
        } catch (DocumentException ex) {
            throw new RuntimeException("Document exception " + ex.getMessage(), ex);
        }
    }


    private PageStats generatePDF(List<SongNode> songList, File outputFile) throws IOException, DocumentException {
        logger.info("Starting export to PDF file {}.", outputFile.getAbsolutePath());

        // Initialize Writer
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        PageStats pageStats = new PageStats();
        writer.setPageEvent(pageStats);

        // Initialize document
        document.setPageSize(PageSize.A4);
        document.setMargins(35 * POINTS_PER_MM, 10 * POINTS_PER_MM, 7 * POINTS_PER_MM, 7 * POINTS_PER_MM);
        document.setMarginMirroring(true);

        document.open();

        // Add QR codes
        Element qrCodeSection = buildQrCodeSection();
        document.add(qrCodeSection);

        // Line separator
        document.add(verseSpacing);
        document.add(new LineSeparator());
        document.add(verseSpacing);

        // Build TOC
        Chunk tocTitle = new Chunk("SONG BOOK - TABLE OF CONTENTS", songTitleFont);
        tocTitle.setLocalDestination("TOC");
        document.add(new Paragraph(tocTitle));
        for (int i = 0; i < songList.size(); i++) {
            SongNode songNode = songList.get(i);
            int chapterNumber = i + 1;
            Chunk tocEntry = new Chunk(chapterNumber + ". " + songNode.getTitle(), textFont);
            tocEntry.setLocalGoto("SONG::" + chapterNumber);
            document.add(new Paragraph(tocEntry));
        }
        document.newPage();
        pageStats.setSectionLength("TOC", pageStats.getCurrentPage() - 1);

        // Build document
        for (int i = 0; i < songList.size(); i++) {
            // Get song node
            SongNode songNode = songList.get(i);

            // Mark song start
            int songStartPage = pageStats.getCurrentPage();

            // Write song
            document.add(buildChapter(songNode, i + 1));
            document.newPage();

            // Record song length
            pageStats.setSectionLength(songNode.getTitle(), pageStats.getCurrentPage() - songStartPage);
        }

        // Close document
        document.close();

        logger.info("COMPLETED export to PDF file {}.", outputFile.getAbsolutePath());

        return pageStats;
    }


    private List<SongNode> reorderList(List<SongNode> initialList, PageStats pageStats) {
        // Reorder songs so that 2-page song always starts on even page number
        List<SongNode> reorderedList = new ArrayList<SongNode>();
        Queue<SongNode> songQueue = new LinkedList<SongNode>(initialList);
        int currentPage = 1 + pageStats.getSectionLength("TOC");
        while (!songQueue.isEmpty()) {
            // Find the first song that can be placed
            SongNode nextSongNode = null;

            // Find the next song to with length = 1 or any song we are on an even page
            for (Iterator<SongNode> iterator = songQueue.iterator(); iterator.hasNext(); ) {
                SongNode songNode = iterator.next();

                if ((pageStats.getSectionLength(songNode.getTitle()) == 1) || (currentPage % 2 == 0)) {
                    iterator.remove();
                    nextSongNode = songNode;
                    break;
                }
            }

            // If no song was selected - use the first one in the alphabetical list
            if (nextSongNode == null) {
                nextSongNode = songQueue.poll();
            }

            // Place song
            reorderedList.add(nextSongNode);
            currentPage += pageStats.getSectionLength(nextSongNode.getTitle());
        }

        return reorderedList;
    }


    private void openPDF(File outputFile) {
        logger.info("OPENING PDF ...");
        String osName = System.getProperty("os.name");
        try {
            if (osName.contains("Linux")) {
                Runtime.getRuntime().exec("acroread " + outputFile.getAbsolutePath());
            } else {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + outputFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            logger.warn("Failed to open PDF !", ex);
        }
    }


    private Element buildQrCodeSection() throws IOException, DocumentException {
        // Load images
        Image qrApkImage = PngImage.getImage(PdfExporter.class.getResourceAsStream("/export/qr/songbook_apk_qr.png"));
        Image qrPdfImage = PngImage.getImage(PdfExporter.class.getResourceAsStream("/export/qr/songbook_pdf_qr.png"));

        PdfPCell cell = new PdfPCell((Phrase)null);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1,2,1});

        cell.setImage(qrApkImage);
        table.addCell(cell);
        cell.setImage(null);

        table.addCell(cell);

        cell.setImage(qrPdfImage);
        table.addCell(cell);
        cell.setImage(null);

        cell.setPhrase(new Phrase("APP"));
        table.addCell(cell);
        cell.setPhrase(null);

        table.addCell(cell);

        cell.setPhrase(new Phrase("PDF"));
        table.addCell(cell);
        cell.setPhrase(null);

        return table;
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
            chapter.add(buildLine(lineNode));
        }

    }


    private Paragraph buildLine(LineNode lineNode) {
        Paragraph paragraph = new Paragraph();
        for (Node node : lineNode.getContentList()) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                paragraph.add(new Chunk(textNode.getText(), textFont));
            } else if (node instanceof ChordNode) {
                ChordNode chordNode = (ChordNode) node;
                Chunk chunk = new Chunk(" " + chordNode.getText() + " ", chordFont);
                chunk.setTextRise(4f);
                paragraph.add(chunk);
                paragraph.setLeading(4f, 1.2f);
            }
        }
        return paragraph;
    }


    private static class PageStats extends PdfPageEventHelper {
        private int currentPage = 1;
        private final Map<String, Integer> map = new HashMap<String, Integer>();


        public int getCurrentPage() {
            return currentPage;
        }


        public void setSectionLength(String songName, int length) {
            map.put(songName, length);
        }


        public int getSectionLength(String songName) {
            return map.get(songName);
        }


        @Override
        public void onEndPage(PdfWriter pdfWriter, Document document) {
            currentPage++;
        }
    }
}
