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
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jgoodies.binding.adapter.TextComponentConnector;
import com.jgoodies.binding.beans.PropertyConnector;
import com.songbook.pc.ui.presentationmodel.TextDialogPresentationModel;


public class TextDialogView extends JDialog {
    private final JLabel descriptionLabel;
    private final JTextField textField;
    private final JLabel statusLabel;
    private final JButton okButton;
    private final JButton cancelButton;


    /**
     * Creates new form TextDialog with the specified parameters.
     * @param parent            Parent frame
     * @param presentationModel Presentation model displayed by this view
     */
    public TextDialogView(Frame parent, TextDialogPresentationModel presentationModel) {
        super(parent, true);
        setResizable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // Bind title & visibility
        PropertyConnector.connectAndUpdate(presentationModel.getTitleModel(), this, "title");

        // Description label
        descriptionLabel = new JLabel();
        PropertyConnector.connectAndUpdate(presentationModel.getDescriptionModel(), descriptionLabel, "text");

        // Text field
        textField = new JTextField();
        TextComponentConnector.connect(presentationModel.getTextValueModel(), textField);

        // Status label
        statusLabel = new JLabel();
        PropertyConnector.connectAndUpdate(presentationModel.getStatusMessageModel(), statusLabel, "text");

        // Buttons
        okButton = new JButton(presentationModel.getOkAction());
        cancelButton = new JButton(presentationModel.getCancelAction());

        // Handle OPEN/CLOSE events from presentation-model
        presentationModel.addEventListener(new TextDialogPresentationModel.EventListener() {
            @Override
            public void onEvent(Object eventType) {
                if (TextDialogPresentationModel.Event.VIEW_OPEN.equals(eventType)) {
                    setVisible(true);
                } else if (TextDialogPresentationModel.Event.VIEW_CLOSE.equals(eventType)) {
                    setVisible(false);
                }
            }
        });

        initLayout();
    }


    private void initLayout() {
        // == BOTTOM PANEL ==
        Box bottomComponent = Box.createHorizontalBox();
        bottomComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomComponent.add(Box.createHorizontalGlue());
        bottomComponent.add(okButton);
        bottomComponent.add(Box.createRigidArea(new Dimension(200, 10)));
        bottomComponent.add(cancelButton);
        bottomComponent.add(Box.createHorizontalGlue());

        // == Center Panel ==
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Box mainPane = Box.createVerticalBox();
        mainPane.add(Box.createRigidArea(new Dimension(10, 10)));
        mainPane.add(descriptionLabel);
        mainPane.add(textField);
        mainPane.add(statusLabel);
        mainPane.add(Box.createVerticalGlue());
        mainPane.add(Box.createRigidArea(new Dimension(10, 10)));
        mainPane.add(bottomComponent);
        mainPane.add(Box.createRigidArea(new Dimension(10, 10)));

        Dimension size = new Dimension(500, 150);
        setMinimumSize(size);
        setMaximumSize(size);
        setContentPane(mainPane);
    }
}
