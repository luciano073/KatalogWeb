package br.com.katalog.katalogweb.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.EditText;
import android.widget.TextView;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.models.Book;

/**
 * Created by luciano on 20/09/2016.
 */
public class TextViewBindingAdapters {

    @BindingAdapter({"android:text"})
    public static void setTextFromInt(TextView textView, int value){
        textView.setText(String.valueOf(value));
    }

    @BindingAdapter({"android:text"})
    public static void setTextFromFloat(TextView textView, float value){
        textView.setText(String.valueOf(value));
    }

    @BindingAdapter({"android:text", "bind:coverType"})
    public static void setTextFromInt(TextView textView, int value, boolean cover){
        Context context = textView.getContext();
        if (cover){
            switch (value){
                case Book.HARDCOVER:
                    textView.setText(context.getString(R.string.hardcover));
                    break;
                case Book.PAPERBACK:
                    textView.setText(context.getString(R.string.paperback));
                    break;
                default:
                    textView.setText("");
            }


        }
    }
}
