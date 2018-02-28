package com.nisaidie.nisaidie1.helper;


import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class FieldValidator extends TextValidator implements View.OnFocusChangeListener{
    public FieldValidator(TextView textView) {
        super(textView);
    }

    @Override
    public boolean validate(TextView textView, String text) {

        if(TextUtils.isEmpty(textView.getText().toString())){
            textView.setError(textView.getHint()+" is require!");
            return false;
        } else {
            textView.setError(null);
            return false;
        }
    }

    @Override
    public void onFocusChange(View textview, boolean hasFocus) {

        if (!hasFocus){
            String text = textview.toString();
            validate((TextView)textview, text);
        }

    }

}
