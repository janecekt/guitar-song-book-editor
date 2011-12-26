package songer.ui.presentationmodel;

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
