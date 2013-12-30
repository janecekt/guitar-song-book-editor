package com.songbook.android.framework;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.songbook.android.R;

public class ResetableEditTextPreference extends DialogPreference {
    private EditText editText;
    private String defaultValue;
    private int inputType;
    private boolean isInitialized = false;
    private String rawSummary;
    private String value;


    public ResetableEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.inputType = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "inputType", 0);
        setDialogLayoutResource(R.layout.resetable_edit_text_layout);
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        // Embed initialization here
        if (!isInitialized) {
            isInitialized = true;
            this.rawSummary = (getSummary() != null) ? getSummary().toString() : null;
        }

        final boolean wasBlocking = shouldDisableDependents();

        this.value = value;
        persistString(value);
        updateSummary( value );

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        // Access inflated widgets
        super.onBindDialogView(view);
        this.editText = (EditText) view.findViewById(R.id.resetable_edit_text_edit_box);
        ImageButton button = (ImageButton) view.findViewById(R.id.resetable_edit_text_revet_button);

        // Initialize widgets
        this.editText.setText( getValue() );
        this.editText.setInputType(inputType);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRevertClick();
            }
        });
    }


    private void onRevertClick() {
        this.editText.setText(defaultValue);
    }


    private void updateSummary(String newValue) {
        if (rawSummary != null) {
            if (newValue != null) {
                setSummary( String.format(rawSummary, newValue) );
            } else {
                setSummary( String.format(rawSummary, "") );
            }
        }
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // Close dialog
        super.onDialogClosed(positiveResult);
        // Save settings
        if (positiveResult) {
            String newValue = editText.getText() != null ? editText.getText().toString() : null;
            if (callChangeListener(newValue)) {
                this.setValue(newValue);
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);
        this.setValue(restore ? getPersistedString(value) : (String) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        this.defaultValue = a.getString(index);
        return defaultValue;
    }

    @Override
    public boolean shouldDisableDependents() {
        return TextUtils.isEmpty(value) || super.shouldDisableDependents();
    }
}