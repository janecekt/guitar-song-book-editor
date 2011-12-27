package songer.exporter;

import songer.parser.nodes.*;
import songer.util.FileIO;

import java.io.File;
import java.util.logging.Logger;

public class HtmlExporter implements Exporter {
    private static final Logger logger = Logger.getLogger("songer");
    
    @Override
    public void export(File baseDir, SongBook songBook) {
        for (SongNode songNode : songBook.getSongNodeList()) {
            try {
                String htmlFileName = songNode.getSourceFile().getName().replace(".txt", "") + ".html";
                File outputFileName = new File(baseDir + "/html/" + htmlFileName);

                FileIO.writeStringToFile(outputFileName.getAbsolutePath(), "utf8", buildSongNode(songNode) );
            } catch (Exception ex) {
                logger.severe("EXPORT FAILED: " + ex.getMessage());
            }
        }
    }

    private String buildSongNode(SongNode songNode) {
        StringBuilder builder = new StringBuilder();

        builder.append("<HTML>\n");
        builder.append("<HEAD>\n");
        builder.append("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf8\">\n");
        builder.append("  <link href=\"song.css\" rel=\"stylesheet\" type=\"text/css\">\n");
        builder.append("  <SCRIPT src=\"song.js\" type=\"text/javascript\"></SCRIPT>\n");
        builder.append("  <TITLE>").append(songNode.getTitle()).append("</TITLE>\n");
        builder.append("</HEAD>\n");
        builder.append("<BODY>\n\n");

        builder.append("<DIV class=\"title\">").append(songNode.getTitle()).append("</DIV>\n\n");

        builder.append("<DIV class=\"transpose\">\n");
        builder.append("Transpozice: <SPAN id=\"totaltranspose\">0</SPAN>\n");
        builder.append("[<a href=\"javascript:transpose(1)\">+1</a>]\n");
        builder.append("[<a href=\"javascript:transpose(-1)\">-1</a>]\n");
        builder.append("</DIV>");

        for (VerseNode verseNode : songNode.getVerseList()) {
            builder.append("\n\n");
            appendVerse(verseNode, builder);
        }

        builder.append("</BODY>\n");
        builder.append("</HTML>\n");

        return builder.toString();
    }
    
    
    private void appendVerse(VerseNode verseNode, StringBuilder builder) {
        builder.append("<DIV class=\"verse\">");
        for (LineNode lineNode : verseNode.getLineNodes()) {
            appendLine(lineNode, builder);
            builder.append("<BR/>\n");
        }
        builder.append("</DIV>");
    }
    
    
    private void appendLine(LineNode lineNode, StringBuilder builder) {
        for (Node node : lineNode.getContentList()) {
            if (node instanceof TextNode) {
                builder.append( ((TextNode) node).getText() );
            } else if (node instanceof ChordNode) {
                ChordNode chordNode = (ChordNode) node;
                
                builder.append("<SPAN class=\"chord\">");
                String chord2 = chordNode.getChord2(0);
                if (!chord2.isEmpty()) {
                    builder.append("<SPAN title=\"chord\">");
                    builder.append(chord2);
                    builder.append("</SPAN>");
                }
                builder.append("<SPAN title=\"chord\">");
                builder.append(chordNode.getChord1(0));
                builder.append("</SPAN></SPAN>");
            }
        }
    }
}
