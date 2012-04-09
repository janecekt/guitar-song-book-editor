package com.songbook.pc.ui.presentationmodel;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.jgoodies.binding.value.ValueHolder;

public class TextDialogPresentationModel {
    public static enum Event {VIEW_OPEN, VIEW_CLOSE}

    public static interface EventListener {
        public void onEvent(Object eventType);
    }

    private static final String VALID_MESSAGE = "Valid";
    private final Action okAction;
    private final Action cancelAction;
    private final ValueHolder titleModel = new ValueHolder("title");
    private final ValueHolder descriptionModel = new ValueHolder("description");
    private final ValueHolder textValueModel = new ValueHolder(null);
    private final ValueHolder statusMessageModel = new ValueHolder("");
    private final List<EventListener> eventListenerList = new ArrayList<EventListener>();


    /** Pattern which the entered text has to match. */
    private final Pattern pattern;
    private String returnValue;


    /**
     * Creates new form TextDialog with the specified parameters.
     * @param pattern     Pattern fot the validation of the input text.
     * @param title       Title of the dialog.
     * @param description Description of the text which should be entered.
     */
    public TextDialogPresentationModel(Pattern pattern, String title, String description) {
        this.pattern = pattern;
        titleModel.setValue(title);
        descriptionModel.setValue(description);
        textValueModel.addValueChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onTextFieldChanged();
            }
        });

        // Initialize actions
        okAction = new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOkActionPerformed();
            }
        };

        cancelAction = new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancelActionPerformed();
            }
        };

        // Update textValueModel to trigger re-validation
        textValueModel.setValue("");
    }


    private void onTextFieldChanged() {
        Matcher matcher = pattern.matcher(textValueModel.getString());
        if (matcher.matches()) {
            statusMessageModel.setValue(VALID_MESSAGE);
            okAction.setEnabled(true);
        } else {
            statusMessageModel.setValue("Invalid text supplied - must match " + pattern.pattern());
            okAction.setEnabled(false);
        }
    }


    private void onOkActionPerformed() {
        if (VALID_MESSAGE.equals(statusMessageModel.getValue())) {
            returnValue = textValueModel.getString();
            fireEvent(Event.VIEW_CLOSE);
        }
    }


    private void onCancelActionPerformed() {
        fireEvent(Event.VIEW_CLOSE);
    }


    public String runDialog() {
        returnValue = null;
        textValueModel.setValue("");
        fireEvent(Event.VIEW_OPEN);
        return returnValue;
    }


    public Action getOkAction() {
        return okAction;
    }


    public Action getCancelAction() {
        return cancelAction;
    }


    public ValueHolder getTitleModel() {
        return titleModel;
    }


    public ValueHolder getDescriptionModel() {
        return descriptionModel;
    }


    public ValueHolder getTextValueModel() {
        return textValueModel;
    }


    public ValueHolder getStatusMessageModel() {
        return statusMessageModel;
    }


    public void addEventListener(EventListener event) {
        eventListenerList.add(event);
    }


    public void fireEvent(Object event) {
        for (EventListener listener : eventListenerList) {
            listener.onEvent(event);
        }
    }
}
