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
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.swing.*;

import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.songbook.exporter.Exporter;
import com.songbook.parser.InputSys;
import com.songbook.parser.LexAn;
import com.songbook.parser.SyntaxAn;
import com.songbook.parser.nodes.SongNode;
import com.songbook.ui.UIDialog;
import com.songbook.util.FileIO;
import com.songbook.util.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainFormPresentationModel extends BasePresentationModel {
    private static final Logger logger = LoggerFactory.getLogger(MainFormPresentationModel.class);
    private Action nextAction;
    private Action previousAction;
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

    private final UIDialog<String> newFilenameDialog;
    private final Exporter htmlExporter;
    private final Exporter latexExporter;
    private final Exporter pdfExporter;

    private final FileList fileList;
    private SongNode songNode;


    public MainFormPresentationModel(
            FileList fileList,
            UIDialog<String> newFilenameDialog,
            Exporter htmlExporter,
            Exporter latexExporter,
            Exporter pdfExporter) {
        this.fileList = fileList;
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
        rebuildSongNodeFromCurrentFile();
        refreshContent();
    }


    private void rebuildSongNodeFromCurrentFile() {
        rebuildSongNode( fileList.getCurrentFileContent(encodingModel.getValue()) );
        titleModel.setValue( "Guitar Song Book Editor - " + fileList.getCurrent().getAbsolutePath() );
    }

    private void rebuildSongNode(String content) {
        // Rebuild song node
        try {
            SyntaxAn syntaxAn = new SyntaxAn(new LexAn(new InputSys(new StringReader(content))));
            songNode = syntaxAn.parse();
        } catch (SyntaxAn.SyntaxErrorException ex) {
            throw new RuntimeException("Syntax analysis failed - " + ex.getMessage());
        }
    }


    private void refreshContent() {
        // Update view
        if (viewModeModel.booleanValue()) {
            getEditorModel().setHtmlText(songNode.getAsHTML(transposeModel.intValue()));
        } else {
            getEditorModel().setPlainText(songNode.getAsText(transposeModel.intValue()));
        }
    }


    private void onTransposeValueChanged() {
        try {
            if (!viewModeModel.booleanValue()) {
                rebuildSongNode(editorModel.getTextModel().getString());
            }
            refreshContent();
        } catch (RuntimeException ex) {
            logger.error(ex.getMessage());
        }
    }


    private void onNextActionPerformed() {
        logger.info("");
        logger.info("Next Pressed");
        try {
            fileList.gotoNext();
            rebuildSongNodeFromCurrentFile();
            refreshContent();
            transposeModel.setValue(0);
        } catch (RuntimeException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    private void onPreviousActionPerformed() {
        logger.info("");
        logger.info("Previous Pressed");
        try {
            fileList.gotoPrevious();
            rebuildSongNodeFromCurrentFile();
            refreshContent();
            transposeModel.setValue(0);
        } catch (RuntimeException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    private void onEditActionPerformed() {
        logger.info("");
        logger.info("Edit Pressed");
        try {
            if (viewModeModel.booleanValue()) {
                // View mode - switching to edit mode
                viewModeModel.setValue(false);
            } else {
                // Edit mode - switching to view mode
                rebuildSongNode(editorModel.getTextModel().getString());
                viewModeModel.setValue(true);
                transposeModel.setValue(0);
            }
            refreshContent();
        } catch (RuntimeException ex) {
            logger.error(ex.getMessage());
        }
    }


    private void onNewActionPerformed() {
        logger.info("");
        logger.info("New Pressed");
        String newFilename = newFilenameDialog.runDialog(getOwnerFrame());
        if (newFilename != null) {
            fileList.addNewFile(newFilename, encodingModel.getValue());
        }
        rebuildSongNodeFromCurrentFile();
        refreshContent();
    }

    private void onSaveActionPerformed() {
        logger.info("");
        logger.info("Save Pressed");

        try {
            // Save current state
            if (!viewModeModel.booleanValue()) {
                onEditActionPerformed();
            }

            // Save file
            FileIO.writeStringToFile(fileList.getCurrent().getAbsolutePath(),
                    encodingModel.getValue(),
                    songNode.getAsText(transposeModel.intValue()));

            // Set transpose to 0 and refresh
            rebuildSongNodeFromCurrentFile();
            refreshContent();
            transposeModel.setValue(0);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Save FAILED - unsupported encoding.");
        } catch (IOException ex) {
            logger.error("Save FAILED - cannot write to file.");
        }
    }

    private void onExportHtmlActionPerformed() {
        logger.info("");
        logger.info("Export HTML Pressed");

        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.error("Exporting is only supported in VIEW MODE !");
                return;
            }

            // Export to HTML files
            htmlExporter.export(fileList.getBaseDir(), fileList.buildSongBook(encodingModel.getValue()));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void onExportLatexActionPerformed() {
        logger.info("");
        logger.info("Export-Latex Pressed");

        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.error("Exporting is only supported in VIEW MODE !");
                return;
            }

            latexExporter.export(fileList.getBaseDir(), fileList.buildSongBook(encodingModel.getValue()));
        } catch (RuntimeException ex) {
            logger.error("EXPORT TO LATEX FAILED - " + ex.getMessage());
        }
    }

    private void onExportPdfActionPerformed() {
        logger.info("");
        logger.info("Export PDF pressed !");
        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.error("Exporting is only supported in VIEW MODE !");
                return;
            }

            pdfExporter.export(fileList.getBaseDir(), fileList.buildSongBook(encodingModel.getValue()));
        } catch (Exception ex) {
            logger.error("EXPORT TO PDF FAILED !", ex);
        }
    }


    public ValueModel getTitleModel() {
        return titleModel;
    }


    public Action getPreviousAction() {
        return previousAction;
    }

    public Action getNextAction() {
        return nextAction;
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

    public LogObservingPresentationModel getLogObservingModel() {
        return logObservingPresentationModel;
    }

    private void initDependencies() {
        transposeModel.addValueChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onTransposeValueChanged();
            }
        });
    }

    private void initActions() {
        previousAction = new AbstractAction("Previous") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPreviousActionPerformed();
            }
        };

        nextAction = new AbstractAction("Next") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNextActionPerformed();
            }
        };

        editAction = new AbstractAction("Edit/View") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditActionPerformed();
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
