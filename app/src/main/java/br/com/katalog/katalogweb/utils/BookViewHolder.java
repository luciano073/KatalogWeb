package br.com.katalog.katalogweb.utils;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.katalog.katalogweb.databinding.ItemBookBinding;
import br.com.katalog.katalogweb.models.Book;

/**
 * Created by luciano on 17/12/2016.
 */
public class BookViewHolder extends RecyclerView.ViewHolder{
    public ItemBookBinding mBinding;

    public BookViewHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
    }

    public void setBook(Book book){
        mBinding.setBook(book);
    }
}
