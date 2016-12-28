package br.com.katalog.katalogweb.adapters;

import android.content.Context;
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

import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.utils.StringUtils;

/**
 * Created by luciano on 02/03/2016.
 */
public class ArtistArrayAdapter extends ArrayAdapter<Artist>
        implements Filterable {

    private List<Artist> mOriginalList;
    private List<Artist> mSearchResult;
    private Filter mFilter;
    private DatabaseReference mArtistRef;
    private ArtistChildEventListener mListener;

    public ArtistArrayAdapter(Context context, int resource,
                              int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.mOriginalList = new ArrayList<>();
        this.mSearchResult = new ArrayList<>();
        this.mFilter = new MyFilter();
        this.mArtistRef = FirebaseDatabase.getInstance()
                .getReference(Artist.DatabaseFields.ROOT_DATABASE);
        mListener = new ArtistChildEventListener();
        mArtistRef.addChildEventListener(mListener);
    }


    @Override
    public int getCount() {
        return mSearchResult.size();
    }

    @Override
    public Artist getItem(int position) {
        if (mSearchResult != null && mSearchResult.size() > 0
                && position < mSearchResult.size()) {
            return mSearchResult.get(position);
        } else {
            return null;
        }
    }


   /*@Override
    public long getItemId(int position) {
        return super();
//        return this.mSearchResult.get(position).getId();
    }*/

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Artist> temp = new ArrayList<Artist>();

            if (constraint != null) {
                String term = StringUtils.removeDiacritics(constraint.toString().trim().toLowerCase());
                String placeStr;
                for (Artist a : mOriginalList) {
                    placeStr = StringUtils.removeDiacritics(a.getName().toLowerCase());

                    if (placeStr.indexOf(term) > -1) {
                        temp.add(a);
                    }
                }
            }
            filterResults.values = temp;
            filterResults.count = temp.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSearchResult = (ArrayList<Artist>) results.values;
            notifyDataSetChanged();
        }
    }

    class ArtistChildEventListener implements ChildEventListener {


        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Artist artist = dataSnapshot.getValue(Artist.class);
            artist.setId(dataSnapshot.getKey());
            mOriginalList.add(artist);
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

    public void cleanUp() {
        mArtistRef.removeEventListener(mListener);
    }

}

