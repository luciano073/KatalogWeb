package br.com.katalog.katalogweb.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.List;

import br.com.katalog.katalogweb.models.Film;

/**
 * Created by luciano on 31/12/2016.
 */

public class SimpleFilmAdapter extends ArrayAdapter<Film> {

    public SimpleFilmAdapter(Context context, int resource, int textViewResourceId, List<Film> objects) {
        super(context, resource, textViewResourceId, objects);
    }


}
