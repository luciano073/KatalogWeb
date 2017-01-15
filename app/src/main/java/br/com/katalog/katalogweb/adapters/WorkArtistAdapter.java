package br.com.katalog.katalogweb.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.databinding.ItemWorkArtistBinding;

/**
 * Created by luciano on 03/01/2017.
 */

public class WorkArtistAdapter extends RecyclerView.Adapter<WorkArtistAdapter.ViewHolder> {
    private List<Object> mObjects;

    public WorkArtistAdapter(List<Object> mObjects) {
        this.mObjects = mObjects;
    }

    @Override
    public WorkArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemWorkArtistBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_work_artist,
                parent,
                false
        );
        ViewHolder vh = new ViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(WorkArtistAdapter.ViewHolder holder, int position) {
        Object o = mObjects.get(position);
        holder.binding.setObject(o);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mObjects != null ? mObjects.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ItemWorkArtistBinding binding;
        public ViewHolder(ItemWorkArtistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
