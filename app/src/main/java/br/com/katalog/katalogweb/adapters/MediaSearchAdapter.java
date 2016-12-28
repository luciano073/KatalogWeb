package br.com.katalog.katalogweb.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.databinding.ItemDigitalMediaBinding;
import br.com.katalog.katalogweb.listeners.DigiMediaListener;
import br.com.katalog.katalogweb.models.DigitalMedia;

/**
 * Created by luciano on 28/12/2016.
 */

public class MediaSearchAdapter extends RecyclerView.Adapter<MediaSearchAdapter.ViewHolder> {

    private List<DigitalMedia> mMedias;
    DigiMediaListener mListener;

    public MediaSearchAdapter(List<DigitalMedia> mMedias, DigiMediaListener mListener) {
        this.mMedias = mMedias;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDigitalMediaBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_digital_media,
                parent,
                false
        );
        ViewHolder vh = new ViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DigitalMedia media = mMedias.get(position);

        holder.binding.setMedia(media);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onDigiMediaClicked(media);
                }
            }
        });

        holder.binding.executePendingBindings();
    }


    @Override
    public int getItemCount() {
        return mMedias != null ? mMedias.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemDigitalMediaBinding binding;

        public ViewHolder(ItemDigitalMediaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
