package songer.ui.view;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.SpinnerToValueModelConnector;
import com.jgoodies.binding.beans.PropertyConnector;
import songer.ui.presentationmodel.MainFormPresentationModel;
import songer.util.FileIO;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

public class MainFormView extends JFrame {
    private JButton nextButton;
    private JButton previousButton;
    private JButton editButton;
    private JSpinner transposeSpinner;
    private JButton newButton;
    private JButton saveButton;
    private JButton exportHtmlButton;
    private JButton exportLatexButton;
    private JButton exportPDFButton;
    private JComboBox encodingComboBox;

    private JEditorPane editorPane;
    private JTextArea logTextArea;

    
    
    public MainFormView(MainFormPresentationModel presentationModel) {
        presentationModel.setFrame(this);

        PropertyConnector.connectAndUpdate(presentationModel.getTitleModel(), this, "title");

        // == LEFT TOOLBAR ==
        previousButton = new JButton( presentationModel.getPreviousAction() );
        nextButton = new JButton( presentationModel.getNextAction() );
        editButton = new JButton( presentationModel.getEditAction() );
        transposeSpinner = new JSpinner();
        SpinnerToValueModelConnector.connect(transposeSpinner.getModel(), presentationModel.getTransposeModel(), 0);

        // == RIGHT TOOLBAR ==
        newButton = new JButton( presentationModel.getNewAction() );
        saveButton = new JButton( presentationModel.getSaveAction() );
        exportHtmlButton = new JButton( presentationModel.getExportHtmlAction() );
        exportLatexButton = new JButton( presentationModel.getExportLatexAction() );
        exportPDFButton = new JButton( presentationModel.getExportPdfAction() );
        encodingComboBox = BasicComponentFactory.createComboBox( presentationModel.getEncodingModel() );
        

        // == MODEL ==
        editorPane = createEditorPane();
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getEditableModel(), editorPane, "editable" );
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getContentTypeModel(), editorPane, "contentType");
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getTextModel(), editorPane, "text");
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getCaretPositionModel(), editorPane, "caretPosition");

        // == BOTTOM TOOLBAR ==
        logTextArea = BasicComponentFactory.createTextArea(presentationModel.getLogObservingModel().getTextModel());
        logTextArea.setEditable(false);
        PropertyConnector.connectAndUpdate(presentationModel.getLogObservingModel().getCaretPositionModel(), logTextArea, "caretPosition");

        // == DO LAYOUT ==
        initLayout();

        // == OTHER INITIALIZATION ==
       setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }


    private JEditorPane createEditorPane() {
        JEditorPane editorPane = new JEditorPane();
        HTMLEditorKit kit = new HTMLEditorKit();
        editorPane.setEditorKitForContentType("text/html", kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule( FileIO.readResourceToString("/css/songer.css"));
        Document doc = kit.createDefaultDocument();
        editorPane.setDocument(doc);
        return editorPane;
    }

    private void initLayout() {
        Box toolbarPanel = Box.createHorizontalBox();
        toolbarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbarPanel.add( previousButton );
        toolbarPanel.add( nextButton );
        toolbarPanel.add( editButton );
        toolbarPanel.add( transposeSpinner );
        toolbarPanel.add( Box.createHorizontalGlue() );
        toolbarPanel.add( newButton );
        toolbarPanel.add( saveButton );
        toolbarPanel.add( exportHtmlButton );
        toolbarPanel.add( exportLatexButton );
        toolbarPanel.add( exportPDFButton );
        toolbarPanel.add( encodingComboBox );
        
             
        JSplitPane mainPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        mainPane.setDividerLocation(450);
        mainPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPane.setTopComponent( new JScrollPane(editorPane) );
        mainPane.setBottomComponent( new JScrollPane(logTextArea) );
        
        Box contentPanel = Box.createVerticalBox();
        contentPanel.add( Box.createRigidArea(new Dimension(10,10)) );
        contentPanel.add(toolbarPanel);
        contentPanel.add( Box.createRigidArea(new Dimension(10,10)) );
        contentPanel.add(mainPane);
        this.setMinimumSize(new Dimension(900, 600));

        setContentPane(contentPanel);
    }
}
