package br.com.katalog.katalogweb.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.utils.Constants;
import br.com.katalog.katalogweb.utils.StringUtils;

/**
 * Created by luciano on 25/12/2016.
 */

public class FilmArrayAdapter extends ArrayAdapter<Film>
        implements Filterable {

    private List<Film> mOriginalList;
    private List<Film> mSearchResult;
    private Filter mFilter;
    private DatabaseReference mFilmRef;
    private FilmChildEventListener mListener;

    public FilmArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.mOriginalList = new ArrayList<>();
        this.mSearchResult = new ArrayList<>();
        this.mFilter = new MyFilter();
        this.mFilmRef = FirebaseDatabase.getInstance()
                .getReference(Constants.FILMS_DBREF);
        mListener = new FilmChildEventListener();
        mFilmRef.addChildEventListener(mListener);
    }

    @Override
    public int getCount() {
        return mSearchResult.size();
    }

    @Nullable
    @Override
    public Film getItem(int position) {
        if (mSearchResult != null && mSearchResult.size() > 0
                && position < mSearchResult.size()) {
            return mSearchResult.get(position);
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class MyFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Film> temp = new ArrayList<Film>();
            if (constraint != null) {
                String term = StringUtils.removeDiacritics(constraint.toString().trim().toLowerCase());
                String placeStr;
                for (Film film : mOriginalList) {
                    placeStr = StringUtils.removeDiacritics(film.getNormalizedTitle().toLowerCase());

                    if (placeStr.indexOf(term) > -1) {
                        temp.add(film);
                    }
                }
            }
            filterResults.values = temp;
            filterResults.count = temp.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mSearchResult = (ArrayList<Film>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    private class FilmChildEventListener implements ChildEventListener{
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Film film = dataSnapshot.getValue(Film.class);
            film.setId(dataSnapshot.getKey());
            mOriginalList.add(film);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
