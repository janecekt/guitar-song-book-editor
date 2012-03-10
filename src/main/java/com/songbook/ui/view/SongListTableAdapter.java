package com.songbook.ui.view;

import javax.swing.ListModel;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.songbook.model.SongNode;

public class SongListTableAdapter extends AbstractTableAdapter {
     private static final String[] COLUMN_NAMES = { "Title", "Author" };

     public SongListTableAdapter(ListModel listModel) {
         super(listModel, COLUMN_NAMES);
     }

     public Object getValueAt(int rowIndex, int columnIndex) {
         SongNode songNode = (SongNode) getRow(rowIndex);
         switch (columnIndex) {
             case 0 : return songNode.getTitleNode().getTitle();
             case 1 : return songNode.getTitleNode().getSubTitle();
             //case 2 : return songNode.getSourceFile().getName();
             default: return null;
         }
     }
 }