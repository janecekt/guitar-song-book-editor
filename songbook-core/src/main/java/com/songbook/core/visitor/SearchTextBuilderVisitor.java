package com.songbook.core.visitor;

import com.songbook.core.model.ChordNode;
import com.songbook.core.model.LineNode;
import com.songbook.core.model.SongNode;
import com.songbook.core.model.TextNode;
import com.songbook.core.model.TitleNode;
import com.songbook.core.model.VerseNode;
import com.songbook.core.model.Visitor;
import com.songbook.core.util.StringUtil;

public class SearchTextBuilderVisitor implements Visitor {
    private StringBuilder sb = new StringBuilder();
    private String result;

    public String getResult() {
        if (result == null) {
            result = StringUtil.removeAccentsAndNonStandardCharacters(sb.toString());
        }
        return result;
    }

    @Override
    public void enterSongNode(SongNode songNode) { }


    @Override
    public void exitSongNode(SongNode songNode) { }

    @Override
    public void visitTitleNode(TitleNode titleNode) {
        sb.append(titleNode.getTitle());
        if (titleNode.getSubTitle() != null) {
            sb.append(titleNode.getSubTitle());
        }
    }


    @Override
    public void enterVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast) { }


    @Override
    public void exitVerseNode(VerseNode verseNode, boolean isFirst, boolean isLast) { }


    @Override
    public void enterLineNode(LineNode lineNode, boolean isFirst, boolean isLast) { }


    @Override
    public void exitLineNode(LineNode lineNode, boolean isFirst, boolean isLast) { }

    @Override
    public void visitTextNode(TextNode textNode, boolean isFirst, boolean isLast) {
        sb.append(textNode.getText());
    }

    @Override
    public void visitChordNode(ChordNode chordNode, boolean isFirst, boolean isLast) { }
}
