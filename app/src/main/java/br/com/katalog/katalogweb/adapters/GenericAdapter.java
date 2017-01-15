package br.com.katalog.katalogweb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.Film;

/**
 * Created by luciano on 02/01/2017.
 */

public class GenericAdapter extends BaseAdapter {
    private Context context;
    private List<Object> objects;

    public GenericAdapter(Context context, List<Object> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Object o = objects.get(i);
        ViewHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(context)
                    .inflate(R.layout.simple_generic_item, null);
            holder = new ViewHolder();
            holder.tvObject = (TextView) view.findViewById(R.id.tvName);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (o instanceof Film) {
            holder.tvObject.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_movie_18dp, 0, 0, 0
            );
            Film film = (Film) o;
            holder.tvObject.setText(film.getNormalizedTitle());
        }
        if (o instanceof Book) {
            holder.tvObject.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_book_16dp, 0, 0, 0
            );
            Book book = (Book) o;
            holder.tvObject.setText(book.getNormalizedTitle());
        }
        if (o instanceof Artist) {
            holder.tvObject.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_person_gray_18dp, 0, 0, 0
            );
            Artist artist = (Artist) o;
            holder.tvObject.setText(artist.getName());
        }
        return view;
    }

    static class ViewHolder{
        TextView tvObject;
    }
}
