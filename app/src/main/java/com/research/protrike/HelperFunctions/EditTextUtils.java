package com.research.protrike.HelperFunctions;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.research.protrike.R;

import java.util.List;

public class EditTextUtils {
    public static void setDisallowedValuesFilter(Context context, final EditText editText, final List<String> disallowedValues) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String newVal = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
                if (disallowedValues.contains(newVal)) {
                    editText.setTextColor(context.getColor(R.color.red_error));
                    return "";
                }
                editText.setTextColor(context.getColor(android.R.color.black));
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }
}
