package br.com.katalog.katalogweb.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.utils.StringUtils;


/**
 * Created by luciano on 17/03/2016.
 */
public class CustomFilterArrayAdapter extends ArrayAdapter<String>
        implements Filterable {

    private List<String> mOriginalList;
    private List<String> mSearchResult;
    private Filter mFilter;

    public CustomFilterArrayAdapter(Context context, int resource,
                                    int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.mOriginalList = objects;
        this.mSearchResult = mOriginalList;
        this.mFilter = new MyFilter();
    }

    @Override
    public int getCount() {
        return mSearchResult.size();
    }

    @Override
    public String getItem(int position) {
        if (mSearchResult != null
                && mSearchResult.size() > 0
                && position < mSearchResult.size()) {
            return mSearchResult.get(position);
        } else {
            return null;
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<String> temp = new ArrayList<String >();

            if (constraint != null) {
                String term = StringUtils.removeDiacritics(constraint.toString().trim().toLowerCase());
                String placeStr;
                for (String p :
                        mOriginalList) {
                    placeStr = StringUtils.removeDiacritics(p.toLowerCase());

                    if (placeStr.indexOf(term) > -1) {
                        temp.add(p);
                    }
                }
            }
            filterResults.values = temp;
            filterResults.count = temp.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSearchResult = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }
}
