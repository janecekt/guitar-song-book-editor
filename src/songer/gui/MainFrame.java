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
package songer.gui;

import songer.util.FileList;
import songer.util.FileIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import songer.parser.*;
import songer.parser.SyntaxAn.SyntaxErrorException;
import songer.parser.nodes.SongNode;

/**
 * Main GUI frame of the Songer Application.
 * @author  Tomas Janecek
 */
public class MainFrame extends javax.swing.JFrame {
	/** Display mode information (view-mode vs. edit-mode). */
	protected boolean viewMode;
	
	/** FileList holding the list of files to iterate through. */
	protected FileList fileList;
	
	/** SongNode holding the internal representation of the currenly opened song. */
	protected SongNode songNode;

	
	
	/** Creates new form MainFrame */
	public MainFrame(String cssPath, FileList fileList) throws IOException {
		initComponents();
		setViewMode(true);
		setTitle("Songer");
		Logger.getLogger("songer").addHandler(new JTextAreaLogHandler(statusTA));
		loadCSS(cssPath);
		setFileList(fileList);
	}

	
	
	/** Sets the new file list specified by the parameter and reloads the current file. */
	public void setFileList(FileList fileList) {
		this.fileList = fileList;
		reloadCurrentFile();
	}

	
	
	/** Loads the CSS sylesheet from the specified path and 
	 *  sets the EditorPane component to use it for HTML documents. */
	public void loadCSS(String path) throws IOException {
		HTMLEditorKit kit = new HTMLEditorKit();
		editorPane.setEditorKitForContentType("text/html", kit);
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule(FileIO.readFileToString(path, "utf8"));
		Document doc = kit.createDefaultDocument();
		editorPane.setDocument(doc);
	}

	
	
	// ==== GUI GETTERS ==== //
	
	/** Retruns the current transpose value in GUI. */
	public int getTranspose() {
		return ((Number) transposeSP.getValue()).intValue();
	}

	
	
	/** Returns the text content of the editorPane. */
	public String getEditorText() {
		return editorPane.getText();
	}

	
	
	/** Returns the encoding selected in the GUI. */
	public String getEncoding() {
		return encodingCB.getSelectedItem().toString();
	}

	
	// === GUI SETTERS === //    
	
	/** Sets the text in parameter as HTML content of the editor pane and changes the mode to view-mode. */
	public void setHtmlText(String text) {
		setViewMode(true);
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		editorPane.setText(text);
	}

	
	
	/** Sets the text in the parameter as PLAIN content of the editor pane and changes the mode to edit-mode. */
	public void setPlainText(String text) {
		setViewMode(false);
		editorPane.setContentType("text/plain");
		editorPane.setText(text);
		editorPane.setEditable(true);
	}

	
	
	/** Sets the GUI transpose value to trans specified by the parameter. */
	public void setTranspose(int trans) {
		transposeSP.setValue(trans);
	}

	
	
	/** Sets the view-mode to the value in the parameter. */
	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
		if (viewMode) {
			editViewBT.setText("Edit");
		} else {
			editViewBT.setText("View");
		}
	}

	
	
	/** Sets the carret position to the value in the parameter and switches the focus to editorPane. */
	public void setCaretPosition(int position) {
		editorPane.requestFocusInWindow();
		editorPane.setCaretPosition(position);
	}

	
	
	// === HELPER METHODS === //
	
	/** Rebuilds the songNode form the content of the editorPane. */
	protected void rebuildSongNode() throws SyntaxErrorException {
		rebuildSongNode(new StringReader(editorPane.getText()));
	}

	
	
	/** Rebuilds the songNode form the input provided by Reader in the parameter. */
	protected void rebuildSongNode(Reader reader) throws SyntaxErrorException {
		// Parse editor content
		SyntaxAn syntaxAn = new SyntaxAn(new LexAn(new InputSys(reader)));
		songNode = syntaxAn.parse();
		Logger.getLogger("songer").log(Level.INFO, "Syntax Analysis completed successfully.");
	}

	
	
	// === GUI ACTIIONS === //
	
	/** Clears the status-pane conent. */
	public void clearStatusPane() {
		statusTA.setText("");
	}

	
	
	/** Reloads the current file. */
	protected void reloadCurrentFile() {
		Reader reader;

		setTitle("Songer: " + fileList.getCurrent().getName());

		try {
			reader = new InputStreamReader(new FileInputStream(fileList.getCurrent().getAbsolutePath()), getEncoding());
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger("songer").log(Level.SEVERE, "ERROR: Unsupported Encoding.", ex);
			return;
		} catch (FileNotFoundException ex) {
			Logger.getLogger("songer").log(Level.SEVERE, "File " + fileList.getCurrent().getAbsolutePath(), ex);
			return;
		}

		try {
			rebuildSongNode(reader);
			refreshContent();
		} catch (SyntaxAn.SyntaxErrorException ex) {
			try {
				Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
				setPlainText(FileIO.readFileToString(fileList.getCurrent().getAbsolutePath(), getEncoding()));
				setCaretPosition(ex.getPosition().getPosition());
			} catch (IOException ex1) {
				Logger.getLogger("songer").log(Level.SEVERE, "Cannot read file " + fileList.getCurrent().getAbsolutePath(), ex1);
			}
		}

		Logger.getLogger("songer").log(Level.INFO, "Loaded file " + fileList.getCurrent().getAbsolutePath());
	}

	
	
	/** Refreshes the content of the editorPane from the songNode. */
	protected void refreshContent() {
		if (viewMode) {
			// Refresh HTML
			if (songNode != null) {
				setHtmlText(songNode.getAsHTML(getTranspose()));
			}
		} else {
			// Refresh content
			if (songNode != null) {
				setPlainText(songNode.getAsText(getTranspose()));
			}
		}

		editorPane.setCaretPosition(0);

		Logger.getLogger("songer").log(Level.INFO, "GUI Content was refreshed.");
	}

	
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        editViewBT = new javax.swing.JButton();
        nextBT = new javax.swing.JButton();
        transposeSP = new javax.swing.JSpinner();
        exportHTMLBT = new javax.swing.JButton();
        encodingCB = new javax.swing.JComboBox();
        saveBT = new javax.swing.JButton();
        newBT = new javax.swing.JButton();
        previousBT = new javax.swing.JButton();
        exportLaTexBT = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        editorPaneSP = new javax.swing.JScrollPane();
        editorPane = new javax.swing.JEditorPane();
        statusTASP = new javax.swing.JScrollPane();
        statusTA = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        editViewBT.setText("Edit");
        editViewBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editViewBTActionPerformed(evt);
            }
        });

        nextBT.setText("next");
        nextBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBTActionPerformed(evt);
            }
        });

        transposeSP.setPreferredSize(new java.awt.Dimension(50, 20));
        transposeSP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                transposeSPStateChanged(evt);
            }
        });

        exportHTMLBT.setText("Export HTML");
        exportHTMLBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportHTMLBTActionPerformed(evt);
            }
        });

        encodingCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CP1250", "UTF8" }));
        encodingCB.setPreferredSize(new java.awt.Dimension(80, 23));

        saveBT.setText("Save");
        saveBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBTActionPerformed(evt);
            }
        });

        newBT.setText("New");
        newBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBTActionPerformed(evt);
            }
        });

        previousBT.setText("previous");
        previousBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousBTActionPerformed(evt);
            }
        });

        exportLaTexBT.setText("Export LaTex");
        exportLaTexBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportLaTexBTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addComponent(previousBT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextBT)
                .addGap(18, 18, 18)
                .addComponent(editViewBT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(transposeSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addComponent(newBT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(saveBT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exportHTMLBT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exportLaTexBT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(encodingCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(previousBT)
                .addComponent(nextBT)
                .addComponent(editViewBT)
                .addComponent(transposeSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(encodingCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(exportLaTexBT)
                .addComponent(exportHTMLBT, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(saveBT)
                .addComponent(newBT))
        );

        splitPane.setDividerLocation(400);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(1.0);

        editorPaneSP.setViewportView(editorPane);

        splitPane.setTopComponent(editorPaneSP);

        statusTA.setColumns(20);
        statusTA.setRows(2);
        statusTASP.setViewportView(statusTA);

        splitPane.setRightComponent(statusTASP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
	

	
	/** Event handler for transpose-spin-button. */
    private void transposeSPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_transposeSPStateChanged
		clearStatusPane();
		try {
			if (!viewMode) {
				rebuildSongNode();
			}
			refreshContent();
		} catch (SyntaxAn.SyntaxErrorException ex) {
			Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
			setCaretPosition(ex.getPosition().getPosition());
			viewMode = false;
		}
    }//GEN-LAST:event_transposeSPStateChanged

	
	
	/** Event handler for edit-view button. */
    private void editViewBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editViewBTActionPerformed
		clearStatusPane();
		if (viewMode) {
			setViewMode(!viewMode);
			refreshContent();
			setTranspose(0);
		} else {
			try {
				rebuildSongNode();
				setTranspose(0);
				setViewMode(!viewMode);
				refreshContent();
			} catch (SyntaxAn.SyntaxErrorException ex) {
				Logger.getLogger("songer").log(Level.SEVERE, ex.getMessage(), ex);
				setCaretPosition(ex.getPosition().getPosition());
			}
		}
    }//GEN-LAST:event_editViewBTActionPerformed

	
	
	/** Event handler for next-button. */
    private void nextBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBTActionPerformed
		clearStatusPane();
		if (fileList.hasNext()) {
			setTranspose(0);
			fileList.gotoNext();
			reloadCurrentFile();
		} else {
			Logger.getLogger("songer").log(Level.WARNING, "This is the LAST file in the list");
		}
    }//GEN-LAST:event_nextBTActionPerformed

	
	
	/** Event handler for previous-button. */
    private void previousBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousBTActionPerformed
		clearStatusPane();
		if (fileList.hasPrevious()) {
			setTranspose(0);
			fileList.gotoPrevious();
			reloadCurrentFile();
		} else {
			Logger.getLogger("songer").log(Level.WARNING, "This is the FIRST file in the list");
		}
        
    }//GEN-LAST:event_previousBTActionPerformed

	
	
	/** Event handler for new-button. */
    private void newBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBTActionPerformed
		// Clear Status pane
		clearStatusPane();

		// Make new song name anf filename
		TextDialog textDialog = new TextDialog(this, "Enter song name", "Enter new song name:");
		if (!textDialog.runDialog("([A-Za-z]+-)?[A-Z][A-Za-z ]*[A-Za-z]")) {
			return;
		}
		String songName = textDialog.getText();
		String songFileName = fileList.getBaseDir().getAbsolutePath() + "/" + songName.replaceAll(" ", "_") + ".txt";

		// Write new file to disk and update fileList
		try {
			FileIO.writeStringToFile(songFileName, getEncoding(), songName + "\n\nVerse 1");
			Logger.getLogger("songer").log(Level.INFO, "New file was created " + songFileName);
			fileList.rebuild(new FileList.TxtFileFilter(), new FileList.FileNameComparator());
			fileList.setCurrent(songFileName);
			Logger.getLogger("songer").log(Level.INFO, "File list was updated " + songFileName);
			setViewMode(false);
            reloadCurrentFile();//GEN-LAST:event_newBTActionPerformed
		} catch (Exception ex) {
			Logger.getLogger("songer").log(Level.SEVERE, "ERROR: File not created : " + ex.getMessage(), ex);
		}
	}

	
	
	/** Event handler for save-button. */
    private void saveBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBTActionPerformed
		// Clear Status Pane
		clearStatusPane();

		// Allow save only in view mode.
		if (!viewMode) {
			Logger.getLogger("songer").log(Level.WARNING, "Saving is only supported in VIEW MODE !");
			return;
		}

		// Convert song to plain-text and write it to file.
		try {
			String content = songNode.getAsText(getTranspose());
			FileIO.writeStringToFile(fileList.getCurrent().getAbsolutePath(), getEncoding(), content);
			Logger.getLogger("songer").log(Level.INFO, "Saved to file " + fileList.getCurrent().getAbsolutePath());
		} catch (Exception ex) {
			Logger.getLogger("songer").log(Level.SEVERE, "ERROR: Cannot write to file " + fileList.getCurrent().getAbsolutePath(), ex);
		}
    }//GEN-LAST:event_saveBTActionPerformed

	
	
	/** Event handler for export-html-button. */
    private void exportHTMLBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportHTMLBTActionPerformed
		// Clear Status Pane
		clearStatusPane();

		// Allow save only in view mode.
		if (!viewMode) {
			Logger.getLogger("songer").log(Level.WARNING, "Exporting is only supported in VIEW MODE !");
			return;
		}

		// Iterate over all songs in list - foreach : pase, convert-to-html, write-it-to-file
		for (File file : fileList) {
			try {
				SyntaxAn syntaxAn = new SyntaxAn(new LexAn(new InputSys(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), getEncoding()))));
				SongNode tmpSongNode = syntaxAn.parse();
				String content = tmpSongNode.getAsExportHTML(0);
				String targetFileName = fileList.getBaseDir() + "/html/" + file.getName().replace(".txt", "") + ".html";
				FileIO.writeStringToFile(targetFileName, "utf8", content);
				Logger.getLogger("songer").log(Level.INFO, "Export OK: " + file.getName());
			} catch (SyntaxAn.SyntaxErrorException ex) {
				Logger.getLogger("songer").log(Level.SEVERE, "Export FAILED - SyntaxError : " + file.getName() + " : " + ex.getMessage());
			} catch (Exception ex) {
				Logger.getLogger("songer").log(Level.SEVERE, "Export FAILED - IO Error : " + file.getName() + " : " + ex.getMessage());
			}
		}        
}//GEN-LAST:event_exportHTMLBTActionPerformed

	
	
	/** Event handler for export-latex-button. */
    private void exportLaTexBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportLaTexBTActionPerformed
		// Clear Status Pane
		clearStatusPane();

		// Allow save only in view mode.
		if (!viewMode) {
			Logger.getLogger("songer").log(Level.WARNING, "Exporting is only supported in VIEW MODE !");
			return;
		}

		// Read and parse all songs
		ArrayList<SongNode> list = new ArrayList<SongNode>();
		for (File file : fileList) {
			try {
				SyntaxAn syntaxAn = new SyntaxAn(new LexAn(new InputSys(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), getEncoding()))));
				SongNode tmpSongNode = syntaxAn.parse();
				list.add(tmpSongNode);
				Logger.getLogger("songer").log(Level.SEVERE, "Export OK: Successfully parsed " + file.getName());
			} catch (SyntaxAn.SyntaxErrorException ex) {
				Logger.getLogger("songer").log(Level.SEVERE, "Export FAILED - SyntaxError : " + file.getName() + " : " + ex.getMessage());
			} catch (Exception ex) {
				Logger.getLogger("songer").log(Level.SEVERE, "Export FAILED - IO Error : " + file.getName() + " : " + ex.getMessage());
			}
		}

		// Sort and output songs
		String documentBody = "";
		Collections.sort(list, new SongNode.TitleComparator());
		for (SongNode tmpSongNode : list) {
			documentBody += tmpSongNode.getAsLaTex(0);
			Logger.getLogger("songer").log(Level.INFO, "Export OK: Exported " + tmpSongNode.getTitle());
		}


		// Write back
		String targetFileName = fileList.getBaseDir() + "/tex/allsongs.tex";
		try {
			FileIO.writeStringToFile(targetFileName, "utf8", documentBody);
			Logger.getLogger("songer").log(Level.SEVERE, "Export COMPLETED : Wrote output to " + targetFileName);
		} catch (Exception ex) {
			Logger.getLogger("songer").log(Level.SEVERE, "Export FAILED - IO Error : Failed to write to " + targetFileName + " : " + ex.getMessage());
		}
    }//GEN-LAST:event_exportLaTexBTActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton editViewBT;
    private javax.swing.JEditorPane editorPane;
    private javax.swing.JScrollPane editorPaneSP;
    private javax.swing.JComboBox encodingCB;
    private javax.swing.JButton exportHTMLBT;
    private javax.swing.JButton exportLaTexBT;
    private javax.swing.JButton newBT;
    private javax.swing.JButton nextBT;
    private javax.swing.JButton previousBT;
    private javax.swing.JButton saveBT;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTextArea statusTA;
    private javax.swing.JScrollPane statusTASP;
    private javax.swing.JSpinner transposeSP;
    // End of variables declaration//GEN-END:variables
}
