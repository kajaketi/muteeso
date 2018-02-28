package com.nisaidie.nisaidie1.helper;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.nisaidie.nisaidie1.R;

import java.util.ArrayList;
import java.util.List;

public class TextValidatorFactory<T extends Activity> {

    private ViewGroup viewGroup;
    private View view;
    private List<View> possibleTextViews = new ArrayList<View>();

    public void setUpValidators(T t) {
        viewGroup = ((ViewGroup) t.findViewById(R.id.content));
        view = viewGroup.getChildAt(0);
        possibleTextViews = view.getFocusables(View.FOCUS_FORWARD);

        for(View possibleTextView : possibleTextViews) {
            if(possibleTextView instanceof EditText) {
                ((EditText) possibleTextView).addTextChangedListener(new FieldValidator((EditText) possibleTextView));
                ((EditText) possibleTextView).setOnFocusChangeListener(new FieldValidator((EditText)possibleTextView));
            }
        }
    }
}
