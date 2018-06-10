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
package com.songbook.pc;

import java.awt.Frame;
import java.io.File;
import java.util.regex.Pattern;

import com.songbook.core.model.SongNode;
import com.songbook.core.parser.ChordProParser;
import com.songbook.core.parser.Parser;
import com.songbook.core.util.SongNodeLoader;
import com.songbook.pc.exporter.EPubExporter;
import com.songbook.pc.exporter.HtmlExporter;
import com.songbook.pc.exporter.JSONExporter;
import com.songbook.pc.exporter.LaTexExporter;
import com.songbook.pc.exporter.PdfExporter;
import com.songbook.pc.ui.UIDialog;
import com.songbook.pc.ui.presentationmodel.MainFormPresentationModel;
import com.songbook.pc.ui.presentationmodel.SongListPresentationModel;
import com.songbook.pc.ui.presentationmodel.TextDialogPresentationModel;
import com.songbook.pc.ui.view.MainFormView;
import com.songbook.pc.ui.view.TextDialogView;

/**
 * Wrapper class for main function.
 * @author Tomas Janecek
 */
public class Main {

    /**
     * Program main function.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("Usage: java -jar ./songbook.jar <docs-directory> ");
                System.exit(1);
            }

            // Build objects (could use DI container but the project is too small for that)

            // Parser
            Parser<SongNode> parser = ChordProParser.createParser();

            // Loader
            SongNodeLoader loader = new SongNodeLoader(parser);

            // Dialog provider
            UIDialog<String> newNextFileDialog = new UIDialog<String>() {
                @Override
                public String runDialog(Frame ownerFrame) {
                    TextDialogPresentationModel textDialogPM = new TextDialogPresentationModel(
                            Pattern.compile("([A-Za-z]+-)?[A-Z][A-Za-z ]*[A-Za-z]"),
                            "Enter song name",
                            "Enter new song name:");
                    new TextDialogView(ownerFrame, textDialogPM);
                    return textDialogPM.runDialog();
                }
            };

            // Main Presentation Model
            MainFormPresentationModel mainPM = new MainFormPresentationModel(
                    parser,
                    new SongListPresentationModel(new File(args[0]), loader),
                    newNextFileDialog,
                    new HtmlExporter(),
                    new LaTexExporter(),
                    new PdfExporter(loader),
                    new EPubExporter(),
                    new JSONExporter());

            MainFormView mainFormView = new MainFormView(mainPM);
            mainFormView.setVisible(true);
        } catch (Exception ex) {
            System.err.println("Exception occurred during initialization :" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
