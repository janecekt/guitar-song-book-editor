package songer.ui.presentationmodel;


import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import songer.parser.InputSys;
import songer.parser.LexAn;
import songer.parser.SyntaxAn;
import songer.parser.nodes.SongNode;
import songer.ui.UIDialog;
import songer.util.FileIO;
import songer.util.FileList;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class MainFormPresentationModel extends BasePresentationModel {
    private static final Logger logger = Logger.getLogger("songer");
    private Action nextAction;
    private Action previousAction;
    private Action editAction;
    private ValueHolder transposeModel = new ValueHolder(0);
    private ValueHolder viewModeModel = new ValueHolder(true);
    private Action newAction;
    private Action saveAction;
    private Action exportHtmlAction;
    private Action exportLatexAction;
    private SelectionInList<String> encodingModel = new SelectionInList<String>(new String[]{"CP1250", "UTF8"});
    private EditorPanePresentationModel editorModel = new EditorPanePresentationModel();
    private LogObservingPresentationModel logObservingPresentationModel;

    private FileList fileList;
    private SongNode songNode;
    private UIDialog<String> newFilenameDialog;


    public MainFormPresentationModel(FileList fileList, UIDialog<String> newFilenameDialog) {
        this.fileList = fileList;
        this.newFilenameDialog = newFilenameDialog;
        encodingModel.setSelectionIndex(0);
        logObservingPresentationModel = new LogObservingPresentationModel();
        initActions();
        initDependencies();
        
        // Initialize contents
        rebuildSongNode( fileList.getCurrentFileContent(encodingModel.getValue()) );
        refreshContent();
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
            rebuildSongNode( fileList.getCurrentFileContent(encodingModel.getValue()) );
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
            rebuildSongNode( fileList.getCurrentFileContent(encodingModel.getValue()) );
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
                //refreshContent();
                //transposeModel.setValue(0);
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
        rebuildSongNode( fileList.getCurrentFileContent(encodingModel.getValue()) );
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
            rebuildSongNode( fileList.getCurrentFileContent(encodingModel.getValue()) );
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

        // Allow save only in view mode.
        if (!viewModeModel.booleanValue()) {
            logger.severe("Exporting is only supported in VIEW MODE !");
            return;
        }

        // Iterate over all songs in list - foreach : pase, convert-to-html, write-it-to-file
        for (File file : fileList) {
            try {
                InputSys inputSys = new InputSys(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), encodingModel.getValue()));
                SyntaxAn syntaxAn = new SyntaxAn(new LexAn(inputSys));
                SongNode tmpSongNode = syntaxAn.parse();
                String content = tmpSongNode.getAsExportHTML(0);
                String targetFileName = fileList.getBaseDir() + "/html/" + file.getName().replace(".txt", "") + ".html";
                FileIO.writeStringToFile(targetFileName, "utf8", content);
                logger.info("Export OK: " + file.getName());
            } catch (SyntaxAn.SyntaxErrorException ex) {
                logger.severe("Export FAILED - SyntaxError : " + file.getName() + " : " + ex.getMessage());
            } catch (Exception ex) {
                logger.severe("Export FAILED - IO Error : " + file.getName() + " : " + ex.getMessage());
            }
        }
    }

    private void onExportLatexActionPerformed() {
        logger.info("Export-Latex Pressed");

        // Allow save only in view mode.
        if (!viewModeModel.booleanValue()) {
            logger.severe("Exporting is only supported in VIEW MODE !");
            return;
        }

        // Read and parse all songs
        ArrayList<SongNode> list = new ArrayList<SongNode>();
        for (File file : fileList) {
            try {
                InputSys inputSys = new InputSys(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), encodingModel.getValue()));
                SyntaxAn syntaxAn = new SyntaxAn(new LexAn(inputSys));
                SongNode tmpSongNode = syntaxAn.parse();
                list.add(tmpSongNode);
                logger.info("Export OK: Successfully parsed " + file.getName());
            } catch (SyntaxAn.SyntaxErrorException ex) {
                logger.info("Export FAILED - SyntaxError : " + file.getName() + " : " + ex.getMessage());
            } catch (Exception ex) {
                logger.info("Export FAILED - IO Error : " + file.getName() + " : " + ex.getMessage());
            }
        }

        // Sort and output songs
        String documentBody = "";
        Collections.sort(list, new SongNode.TitleComparator());
        for (SongNode tmpSongNode : list) {
            documentBody += tmpSongNode.getAsLaTex(0);
            logger.info("Export OK: Exported " + tmpSongNode.getTitle());
        }

        // Write back
        String targetFileName = fileList.getBaseDir() + "/tex/allsongs.tex";
        try {
            FileIO.writeStringToFile(targetFileName, "utf8", documentBody);
            logger.info("Export COMPLETED : Wrote output to " + targetFileName);
        } catch (Exception ex) {
            logger.severe("Export FAILED - IO Error : Failed to write to " + targetFileName + " : " + ex.getMessage());
        }
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
    }
}
