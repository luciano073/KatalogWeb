package br.com.katalog.katalogweb.binding;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by luciano on 08/09/2016.
 */
public class EditTextBindingAdapters {
    @InverseBindingAdapter(attribute = "android:text")
    public static int getTextAsInt(EditText editText){
        try {
            return Integer.parseInt(editText.getText().toString());
        }catch (Exception e){
            return 0;
        }
    }

    @BindingAdapter({"android:text"})
    public static void setTextFromInt(EditText editText, int value){
        if (getTextAsInt(editText) != value){
            editText.setText(String.valueOf(value));
        }
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static long getTextAsLong(EditText editText){
        Locale locale = editText.getContext().getResources().getConfiguration().locale;
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = DateFormat.getDateInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        try {
            if (locale.getDisplayCountry().matches("(?i).*bra[sz]il.*")){
                calendar.setTime(simpleDateFormat.parse(editText.getText().toString()));
            }else {
                calendar.setTime(dateFormat.parse(editText.getText().toString()));
            }
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            return 0;
        }

    }

    @BindingAdapter({"android:text", "bind:longToDate"})
    public static void setTextFromLong(EditText editText, long value, boolean isDate){
        if (getTextAsLong(editText) != value && isDate){
            Locale locale = editText.getContext().getResources().getConfiguration().locale;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(value);
            DateFormat dateFormat = DateFormat.getDateInstance();
            String date;
            if (locale.getDisplayCountry().matches("(?i).*bra[sz]il.*")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                date = simpleDateFormat.format(calendar.getTime());
                editText.setText(date);
            }else{
                date = dateFormat.format(calendar.getTime());
                editText.setText(date);
            }
        }
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static List<String> getTextAsList(EditText editText){
        List<String> list = new ArrayList<>();
        list.add(editText.getText().toString());
        return list;
    }

    @BindingAdapter({"android:text"})
    public static void setTextFromList(EditText editText, List<String> value){
        String result = null;
        if (value != null && value.size() > 0){
            result = value.toString().replaceAll("\\[|\\]", "");
        }
        editText.setText(result);
    }
}
