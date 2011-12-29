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

import com.jgoodies.binding.value.ValueHolder;


public class EditorPanePresentationModel {
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    private final ValueHolder editableModel = new ValueHolder();
    private final ValueHolder textModel = new ValueHolder();
    private final ValueHolder contentTypeModel = new ValueHolder();
    private final ValueHolder caretPositionModel = new ValueHolder();

    public ValueHolder getEditableModel() {
        return editableModel;
    }

    public ValueHolder getTextModel() {
        return textModel;
    }

    public ValueHolder getContentTypeModel() {
        return contentTypeModel;
    }

    public ValueHolder getCaretPositionModel() {
        return caretPositionModel;
    }

    public void setHtmlText(String text) {
        editableModel.setValue(false);
        contentTypeModel.setValue(CONTENT_TYPE_TEXT_HTML);
        textModel.setValue(text);
        caretPositionModel.setValue(1);
        caretPositionModel.setValue(0);
    }

    public void setPlainText(String text) {
        contentTypeModel.setValue(CONTENT_TYPE_TEXT_PLAIN);
        textModel.setValue(text);
        editableModel.setValue(true);
        caretPositionModel.setValue(1);
        caretPositionModel.setValue(0);
    }
}
