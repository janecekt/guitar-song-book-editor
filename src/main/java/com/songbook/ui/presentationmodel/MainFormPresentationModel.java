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
package com.songbook.ui.presentationmodel;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.songbook.exporter.Exporter;
import com.songbook.model.SongNode;
import com.songbook.parser.Parser;
import com.songbook.parser.ParserException;
import com.songbook.ui.UIDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainFormPresentationModel extends BasePresentationModel {
    private static final Logger logger = LoggerFactory.getLogger(MainFormPresentationModel.class);
    private Action editAction;
    private final ValueHolder transposeModel = new ValueHolder(0);
    private final ValueHolder viewModeModel = new ValueHolder(true);
    private final ValueHolder titleModel = new ValueHolder("Guitar Song Book Editor (no song loaded)");
    private Action newAction;
    private Action saveAction;
    private Action exportHtmlAction;
    private Action exportLatexAction;
    private Action exportPdfAction;
    private final SelectionInList<String> encodingModel = new SelectionInList<String>(new String[]{"CP1250", "UTF8"});
    private final EditorPanePresentationModel editorModel = new EditorPanePresentationModel();
    private final LogObservingPresentationModel logObservingPresentationModel;
    private final SongListPresentationModel songListPresentationModel;

    // Dependencies (services)
    private final UIDialog<String> newFilenameDialog;
    private final Exporter htmlExporter;
    private final Exporter latexExporter;
    private final Exporter pdfExporter;
    private final Parser<SongNode> parser;

    // State
    private SongNode songNode;
    private int songNodeTransposition = 0;


    public MainFormPresentationModel(
            Parser<SongNode> parser,
            SongListPresentationModel songListPresentationModel,
            UIDialog<String> newFilenameDialog,
            Exporter htmlExporter,
            Exporter latexExporter,
            Exporter pdfExporter) {
        this.parser = parser;
        this.songListPresentationModel = songListPresentationModel;
        this.newFilenameDialog = newFilenameDialog;
        this.htmlExporter = htmlExporter;
        this.latexExporter = latexExporter;
        this.pdfExporter = pdfExporter;

        // Initialize
        encodingModel.setSelectionIndex(0);
        logObservingPresentationModel = new LogObservingPresentationModel();
        initActions();
        initDependencies();

        // Initialize contents
        songListPresentationModel.reloadFromDisk(encodingModel.getValue());
        songListPresentationModel.getSongListModel().setSelectionIndex(0);
    }


    /*private void rebuildSongNodeFromCurrentFile() {
        rebuildSongNode(fileList.getCurrentFileContent(encodingModel.getValue()), 0);
        titleModel.setValue("Guitar Song Book Editor - " + fileList.getCurrent().getName());
    } */


    private void rebuildSongNode(String content, int songTranspose) {
        // Rebuild song node
        try {
            songNode = parser.parse(new StringReader(content));
            songNodeTransposition = songTranspose;
        } catch (ParserException ex) {
            throw new RuntimeException("Parsing failed - " + ex.getMessage(), ex);
        }
    }


    private void refreshContent() {
        // Update view
        if (viewModeModel.booleanValue()) {
            getEditorModel().setHtmlText(songNode.getAsHTML(transposeModel.intValue() - songNodeTransposition));
        } else {
            getEditorModel().setPlainText(songNode.getAsText(transposeModel.intValue() - songNodeTransposition));
        }
    }


    private void onTransposeValueChanged(int oldValue) {
        try {
            if (!viewModeModel.booleanValue()) {
                rebuildSongNode(editorModel.getTextModel().getString(), oldValue);
            }
            refreshContent();
        } catch (RuntimeException ex) {
            handleError("Transposition failed !", ex);
        }
    }


    private void onEditActionPerformed(boolean handleExceptions) {
        logger.info("Edit Pressed");
        try {
            if (viewModeModel.booleanValue()) {
                // View mode - switching to edit mode
                viewModeModel.setValue(false);
            } else {
                // Edit mode - switching to view mode
                rebuildSongNode(editorModel.getTextModel().getString(), transposeModel.intValue());
                viewModeModel.setValue(true);
            }
            refreshContent();
        } catch (RuntimeException ex) {
            if (handleExceptions) {
                handleError("Switching to EDIT mode failed !", ex);
            } else {
                throw ex;
            }
        }
    }


    private void onNewActionPerformed() {
        logger.info("New Pressed");
        try {
            String newFilename = newFilenameDialog.runDialog(getOwnerFrame());
            if (newFilename != null) {
                songListPresentationModel.addNew(newFilename, encodingModel.getValue());
            }
        } catch (RuntimeException ex) {
            handleError("Creation of new song failed !", ex);
        }
    }


    private void onSaveActionPerformed() {
        logger.info("Save Pressed");

        try {
            // Save current state
            if (!viewModeModel.booleanValue()) {
                onEditActionPerformed(false);
            }

            // Save file
            songListPresentationModel.saveCurrent(
                    encodingModel.getValue(),
                    songNode.getAsText(transposeModel.intValue() - songNodeTransposition));
        } catch (RuntimeException ex) {
            handleError("Saving of a song failed -" + songNode.getTitle(), ex);
        }
    }


    private void onSongSelectionChanged(SongNode selectedSong) {
        if (selectedSong != null) {
            logger.info("Selection changed ...");
            transposeModel.setValue(0);
            songNode = selectedSong;
            songNodeTransposition = 0;
            titleModel.setValue("Guitar Song Book Editor - " + songNode.getSourceFile().getName());
            refreshContent();
        }
    }


    private void onExportHtmlActionPerformed() {
        logger.info("Export HTML Pressed");

        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.error("Exporting is only supported in VIEW MODE !");
                return;
            }

            // Export to HTML files
            htmlExporter.export(songListPresentationModel.getBaseDir(), songListPresentationModel.buildSongBook());
        } catch (RuntimeException ex) {
            handleError("EXPORT TO HTML FAILED !", ex);
        }
    }


    private void onExportLatexActionPerformed() {
        logger.info("Export-Latex Pressed");

        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.error("Exporting is only supported in VIEW MODE !");
                return;
            }

            latexExporter.export(songListPresentationModel.getBaseDir(), songListPresentationModel.buildSongBook());
        } catch (RuntimeException ex) {
            handleError("EXPORT TO LATEX FAILED !", ex);
        }
    }


    private void onExportPdfActionPerformed() {
        logger.info("Export PDF pressed !");
        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.error("Exporting is only supported in VIEW MODE !");
                return;
            }

            pdfExporter.export(songListPresentationModel.getBaseDir(), songListPresentationModel.buildSongBook());
        } catch (Exception ex) {
            handleError("EXPORT TO PDF FAILED !", ex);
        }
    }


    public ValueModel getTitleModel() {
        return titleModel;
    }


    public Action getEditAction() {
        return editAction;
    }


    public ValueModel getTransposeModel() {
        return transposeModel;
    }


    public Action getNewAction() {
        return newAction;
    }


    public Action getSaveAction() {
        return saveAction;
    }


    public Action getExportHtmlAction() {
        return exportHtmlAction;
    }


    public Action getExportLatexAction() {
        return exportLatexAction;
    }


    public Action getExportPdfAction() {
        return exportPdfAction;
    }


    public SelectionInList<String> getEncodingModel() {
        return encodingModel;
    }


    public EditorPanePresentationModel getEditorModel() {
        return editorModel;
    }


    public SongListPresentationModel getSongListPresentationModel() {
        return songListPresentationModel;
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }


    public LogObservingPresentationModel getLogObservingModel() {
        return logObservingPresentationModel;
    }


    private void initDependencies() {
        transposeModel.addValueChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onTransposeValueChanged((Integer) evt.getOldValue());
            }
        });

        songListPresentationModel.getSongListModel().getSelectionHolder().addValueChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onSongSelectionChanged((SongNode) evt.getNewValue());
            }
        });
    }


    private void initActions() {
        editAction = new AbstractAction("Edit/View") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditActionPerformed(true);
            }
        };

        newAction = new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewActionPerformed();
            }
        };

        saveAction = new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveActionPerformed();
            }
        };

        exportHtmlAction = new AbstractAction("Export HTML") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExportHtmlActionPerformed();
            }
        };

        exportLatexAction = new AbstractAction("Export LaTex") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExportLatexActionPerformed();
            }
        };

        exportPdfAction = new AbstractAction("Export PDF") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExportPdfActionPerformed();
            }
        };
    }
}
