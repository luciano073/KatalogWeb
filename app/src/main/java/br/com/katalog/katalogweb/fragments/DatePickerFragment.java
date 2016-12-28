package br.com.katalog.katalogweb.fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

import br.com.katalog.katalogweb.R;


/**
 * Created by luciano on 20/03/2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "DatePickerFragment";
    private long mDateTime;
    private Calendar mCalendar;

    public static DatePickerFragment newInstance(long date) {
        Bundle bundle = new Bundle();
        bundle.putLong("date", date);
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCalendar = Calendar.getInstance();
        mDateTime = getArguments().getLong("date");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Calendar calendar = Calendar.getInstance();

        if (mDateTime != 0) {
            calendar.setTimeInMillis(mDateTime);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            return dialog;
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(
                getActivity(),
                this, year, month, day);
    }



    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        DatePickerFragmentListener listener = (DatePickerFragmentListener) getActivity();
        listener.onDateListener(year, monthOfYear, dayOfMonth);
    }

    public interface DatePickerFragmentListener {
        void onDateListener(int year, int month, int dayOfYear);
    }
}
