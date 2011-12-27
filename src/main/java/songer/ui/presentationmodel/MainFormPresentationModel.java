package songer.ui.presentationmodel;


import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import javax.swing.*;

import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import songer.parser.InputSys;
import songer.parser.LexAn;
import songer.parser.SyntaxAn;
import songer.exporter.Exporter;
import songer.parser.nodes.SongNode;
import songer.ui.UIDialog;
import songer.util.FileIO;
import songer.util.FileList;

public class MainFormPresentationModel extends BasePresentationModel {
    private static final Logger logger = Logger.getLogger("songer");
    private Action nextAction;
    private Action previousAction;
    private Action editAction;
    private ValueHolder transposeModel = new ValueHolder(0);
    private ValueHolder viewModeModel = new ValueHolder(true);
    private ValueHolder titleModel = new ValueHolder("Songer (no song loaded)");
    private Action newAction;
    private Action saveAction;
    private Action exportHtmlAction;
    private Action exportLatexAction;
    private Action exportPdfAction;
    private SelectionInList<String> encodingModel = new SelectionInList<String>(new String[]{"CP1250", "UTF8"});
    private EditorPanePresentationModel editorModel = new EditorPanePresentationModel();
    private LogObservingPresentationModel logObservingPresentationModel;

    private SongNode songNode;
    private FileList fileList;
    private UIDialog<String> newFilenameDialog;
    private Exporter htmlExporter;
    private Exporter latexExpoter;
    private Exporter pdfExporter;


    public MainFormPresentationModel(
            FileList fileList, 
            UIDialog<String> newFilenameDialog,
            Exporter htmlExporter,
            Exporter latexExporter,
            Exporter pdfExporter) {
        this.fileList = fileList;
        this.newFilenameDialog = newFilenameDialog;
        this.htmlExporter = htmlExporter;
        this.latexExpoter = latexExporter;
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
        titleModel.setValue( "Songer - " + fileList.getCurrent().getAbsolutePath() );
    }
    
    private void rebuildSongNode(String content) {
        // Rebuild song node
        try {
            SyntaxAn syntaxAn = new SyntaxAn(new LexAn(new InputSys( new StringReader(content))));
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
            logger.severe(ex.getMessage());
        }
    }


    private void onNextActionPerformed() {
        logger.info("Next Pressed");
        try {
            fileList.gotoNext();
            rebuildSongNodeFromCurrentFile();
            refreshContent();
            transposeModel.setValue(0);
        } catch (RuntimeException ex) {
            logger.severe(ex.getMessage());
        }
    }


    private void onPreviousActionPerformed() {
        logger.info("Previous Pressed");
        try {
            fileList.gotoPrevious();
            rebuildSongNodeFromCurrentFile();
            refreshContent();
            transposeModel.setValue(0);
        } catch (RuntimeException ex) {
            logger.severe(ex.getMessage());
        }
    }


    private void onEditActionPerformed() {
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
            logger.severe(ex.getMessage());
        }
    }


    private void onNewActionPerformed() {
        logger.info("New Pressed");
        String newFilename = newFilenameDialog.runDialog(getOwnerFrame());
        if (newFilename != null) {
            fileList.addNewFile(newFilename, encodingModel.getValue());
        }
        rebuildSongNodeFromCurrentFile();
        refreshContent();
    }

    private void onSaveActionPerformed() {
        logger.info("Save Pressed");

        try {
            // Save current state
            if (!viewModeModel.booleanValue()) {
                onEditActionPerformed();
            }

            // Save file
            FileIO.writeStringToFile(fileList.getCurrent().getAbsolutePath(),
                    encodingModel.getValue(),
                    songNode.getAsText(transposeModel.intValue()) );

            // Set transpose to 0 and refresh
            rebuildSongNodeFromCurrentFile();
            refreshContent();
            transposeModel.setValue(0);
        } catch (UnsupportedEncodingException ex) {
            logger.severe("Save FAILED - unsupported encoding.");
        } catch (IOException ex) {
            logger.severe("Save FAILED - cannot write to file.");
        }
    }

    private void onExportHtmlActionPerformed() {
        logger.info("Export HTML Pressed");

        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.severe("Exporting is only supported in VIEW MODE !");
                return;
            }

            // Iterate over all songs in list - foreach : pase, convert-to-html, write-it-to-file
            htmlExporter.export(fileList.getBaseDir(), fileList.buildSongBook(encodingModel.getValue()));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
        }
    }

    private void onExportLatexActionPerformed() {
        logger.info("Export-Latex Pressed");

        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.severe("Exporting is only supported in VIEW MODE !");
                return;
            }
            
            latexExpoter.export(fileList.getBaseDir(), fileList.buildSongBook(encodingModel.getValue()));
        } catch (RuntimeException ex) {    
            logger.severe("EXPORT TO LATEX FAILED - " + ex.getMessage());        
        }    
    }

    private void onExportPdfActionPerformed() {
        logger.info("Export PDF pressed !");
        try {
            // Allow save only in view mode.
            if (!viewModeModel.booleanValue()) {
                logger.severe("Exporting is only supported in VIEW MODE !");
                return;
            }
            
            pdfExporter.export(fileList.getBaseDir(), fileList.buildSongBook(encodingModel.getValue()));
        } catch (Exception ex) {
            logger.severe("EXPORT TO PDF FAILED - " + ex.getMessage());
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
