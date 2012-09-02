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
package com.songbook.pc.ui.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.html.HTMLEditorKit;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.SpinnerToValueModelConnector;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.songbook.core.util.FileIO;
import com.songbook.pc.ui.presentationmodel.MainFormPresentationModel;


/** View for the MainForm (implementation of the Model-View-PresentationModel). */
public class MainFormView extends JFrame {
    private final JButton editButton;
    private final JSpinner transposeSpinner;
    private final JButton newButton;
    private final JButton saveButton;
    private final JButton exportHtmlButton;
    private final JButton exportLatexButton;
    private final JButton exportPDFButton;
    private final JButton exportEPubButton;
    private final JComboBox encodingComboBox;

    private final JEditorPane editorPane;
    private final JEditorPane logPane;
    private final JTable songSelectionTable;


    /**
     * Constructor - creates the MainFormView.
     * @param presentationModel Presentation model for the view.
     */
    public MainFormView(final MainFormPresentationModel presentationModel) {
        presentationModel.setFrame(this);

        PropertyConnector.connectAndUpdate(presentationModel.getTitleModel(), this, "title");

        // == LEFT TOOLBAR ==
        editButton = new JButton(presentationModel.getEditAction());
        transposeSpinner = new JSpinner();
        SpinnerToValueModelConnector.connect(transposeSpinner.getModel(), presentationModel.getTransposeModel(), 0);

        // == RIGHT TOOLBAR ==
        newButton = new JButton(presentationModel.getNewAction());
        saveButton = new JButton(presentationModel.getSaveAction());
        exportHtmlButton = new JButton(presentationModel.getExportHtmlAction());
        exportLatexButton = new JButton(presentationModel.getExportLatexAction());
        exportPDFButton = new JButton(presentationModel.getExportPdfAction());
        exportEPubButton = new JButton(presentationModel.getExportEPubAction());
        encodingComboBox = BasicComponentFactory.createComboBox(presentationModel.getEncodingModel());

        // == MODEL ==
        editorPane = createEditorPane(presentationModel.getEditorModel().getTextModel(),
                "/css/editorPane.css");
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getEditableModel(), editorPane, "editable");
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getContentTypeModel(), editorPane, "contentType");
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getTextModel(), editorPane, "text");
        PropertyConnector.connectAndUpdate(presentationModel.getEditorModel().getCaretPositionModel(), editorPane, "caretPosition");

        // == BOTTOM TOOLBAR ==
        logPane = createEditorPane(null, "/css/logPane.css");
        logPane.setContentType("text/html");
        logPane.setEditable(false);
        // One-Way binding of text property
        logPane.setText((String) presentationModel.getLogObservingModel().getTextModel().getValue());
        presentationModel.getLogObservingModel().getTextModel().addValueChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                logPane.setText((String) presentationModel.getLogObservingModel().getTextModel().getValue());
                logPane.setCaretPosition( logPane.getDocument().getLength() );
            }
        });

        // == SONG SELECTION ==
        TableModel tableModel = new SongListTableAdapter(presentationModel.getSongListPresentationModel().getSongListModel());
        songSelectionTable = new JTable(tableModel);
        songSelectionTable.setRowSorter(new TableRowSorter<TableModel>(tableModel));
        songSelectionTable.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        bind2Way(songSelectionTable, presentationModel.getSongListPresentationModel().getSongListModel());
        if (!presentationModel.getSongListPresentationModel().getSongListModel().isEmpty()) {
            songSelectionTable.getSelectionModel().setSelectionInterval(0,0);
        }

        // == DO LAYOUT ==
        initLayout();

        // == OTHER INITIALIZATION ==
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }


    /**
     * @param textModel   ValueModel used to initialization and setup of 2-way binding (if null no binding is set up)
     * @param cssResource Resource path of the CSS resource
     * @return New HTML-enabled editor pane.
     */
    private JEditorPane createEditorPane(final ValueModel textModel, final String cssResource) {
        // HTML Editor Kit
        final HTMLEditorKit kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule(FileIO.readResourceToString(cssResource));

        // Init JEditor Pane
        final JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKitForContentType("text/html", kit);
        editorPane.setDocument(kit.createDefaultDocument());

        // Set up 2-way binding
        if (textModel != null) {
            editorPane.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    textModel.setValue(editorPane.getText());
                }
            });
        }
        return editorPane;
    }


    /** Initialize layout */
    private void initLayout() {
        // TOOLBAR
        Box toolbarPanel = Box.createHorizontalBox();
        toolbarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbarPanel.add(editButton);
        toolbarPanel.add(transposeSpinner);
        toolbarPanel.add(Box.createHorizontalGlue());
        toolbarPanel.add(newButton);
        toolbarPanel.add(saveButton);
        toolbarPanel.add(exportHtmlButton);
        toolbarPanel.add(exportLatexButton);
        toolbarPanel.add(exportPDFButton);
        toolbarPanel.add(exportEPubButton);
        toolbarPanel.add(encodingComboBox);

        // CENTER PANE
        JSplitPane centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerPane.setOneTouchExpandable(true);
        centerPane.setDividerLocation(500);
        centerPane.setResizeWeight(1);
        centerPane.setLeftComponent(new JScrollPane(editorPane));
        centerPane.setRightComponent(new JScrollPane(songSelectionTable));

        // MAIN PANE
        JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPane.setOneTouchExpandable(true);
        mainPane.setDividerLocation(450);
        mainPane.setResizeWeight(1);
        mainPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPane.setTopComponent(centerPane);
        mainPane.setBottomComponent(new JScrollPane(logPane));

        // CONTENT PANE
        Box contentPanel = Box.createVerticalBox();
        contentPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        contentPanel.add(toolbarPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        contentPanel.add(mainPane);
        this.setMinimumSize(new Dimension(900, 600));

        setContentPane(contentPanel);
    }

    private static void bind2Way(final JTable jTable, final SelectionInList<?> selectionInListModel) {
        // Set single row selection
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Bind TABLE => SelectionInList
        jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int viewIndex = jTable.getSelectedRow();
                    if (viewIndex >= 0) {
                        int modelIndex = jTable.convertRowIndexToModel(viewIndex);
                        selectionInListModel.setSelectionIndex(modelIndex);
                    } else {
                        selectionInListModel.clearSelection();
                    }
                }
            }
        });

        // Bind SelectionInList => Table
        selectionInListModel.getSelectionIndexHolder().addValueChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Integer modelIndex = (Integer) evt.getNewValue();
                if (modelIndex > 0) {
                    int rowIndex = jTable.convertRowIndexToView(modelIndex);
                    jTable.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
                } else {
                    jTable.clearSelection();
                }
            }
        });
    }
}
