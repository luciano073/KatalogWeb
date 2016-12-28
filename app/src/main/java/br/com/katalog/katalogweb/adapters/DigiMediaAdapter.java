package br.com.katalog.katalogweb.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.fragments.ImageShowFragment;
import br.com.katalog.katalogweb.listeners.BookClickListener;
import br.com.katalog.katalogweb.listeners.DigiMediaListener;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.BookDAO;
import br.com.katalog.katalogweb.models.DigitalMedia;
import br.com.katalog.katalogweb.models.DigitalMediaDAO;
import br.com.katalog.katalogweb.utils.BookViewHolder;
import br.com.katalog.katalogweb.utils.DigiMediaViewHolder;

/**
 * Created by luciano on 17/12/2016.
 */
public class DigiMediaAdapter extends FirebaseRecyclerAdapter<DigitalMedia, DigiMediaViewHolder> {
    private DigiMediaListener mListener;


    public DigiMediaAdapter(Query ref, DigiMediaListener listener) {
        super(DigitalMedia.class, R.layout.item_digital_media, DigiMediaViewHolder.class, ref);
        this.mListener = listener;
    }

    @Override
    protected DigitalMedia parseSnapshot(DataSnapshot snapshot) {
        DigitalMedia media = snapshot.getValue(DigitalMedia.class);
        media.setId(snapshot.getKey());

        DigitalMediaDAO mediaDAO = DigitalMediaDAO.getInstance();
        media.setFilm(mediaDAO.retrieveFilm(snapshot));

        return media;
    }

    @Override
    protected void populateViewHolder(DigiMediaViewHolder viewHolder, final DigitalMedia model, int position) {

        viewHolder.setDigitalMedia(model);

        viewHolder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onDigiMediaClicked(model);
                }
            }
        });


    }


}
