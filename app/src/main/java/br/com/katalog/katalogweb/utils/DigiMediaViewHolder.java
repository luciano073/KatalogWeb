package br.com.katalog.katalogweb.utils;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.katalog.katalogweb.databinding.ItemDigitalMediaBinding;
import br.com.katalog.katalogweb.models.DigitalMedia;

/**
 * Created by luciano on 25/12/2016.
 */
public class DigiMediaViewHolder extends RecyclerView.ViewHolder {
    public ItemDigitalMediaBinding mBinding;

    public DigiMediaViewHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
    }

    public void setDigitalMedia (DigitalMedia media){
        mBinding.setMedia(media);
    }
}
