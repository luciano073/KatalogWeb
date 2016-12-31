package br.com.katalog.katalogweb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.adapters.ArtistArrayAdapter;
import br.com.katalog.katalogweb.databinding.ActivityFilmRegisterBinding;
import br.com.katalog.katalogweb.fragments.CastDialogFragment;
import br.com.katalog.katalogweb.fragments.DatePickerFragment;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.ArtistDAO;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.models.FilmDAO;
import br.com.katalog.katalogweb.utils.ImageUtils;

public class FilmRegisterActivity extends AppCompatActivity
implements DatePickerFragment.DatePickerFragmentListener,
        CastDialogFragment.AddingCast{

    private static final int GALLERY_INTENT = 2;
    private static final String TAG = "FilmRegisterActivity";
    public static final String EXTRA_FILM = "film";
    ActivityFilmRegisterBinding mBinding;
//    private DatabaseReference mArtistDatabase;
//    private DatabaseReference mFilmDatabase;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private Film mOldFilm;
    private Artist mDirector;
    private Artist mWriter;
    private ArtistDAO mArtistDAO;
    private ArtistArrayAdapter mArtistArrayAdapter;
    private boolean isNewFilm;
    private byte[] mImageToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_film_register);

        mProgressDialog     = new ProgressDialog(this);
        mImageToUpload      = new byte[0];
        mArtistDAO          = ArtistDAO.getInstance();
//        mDirector           = new Artist();
//        mWriter             = new Artist();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mDatabase = FirebaseDatabase.getInstance();
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        mFilmDatabase = FirebaseDatabase.getInstance()
//                .getReference(Film.DatabaseFields.ROOT_DATABASE);
//        mFilmDatabase.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();
//                    .child(auth.getCurrentUser().getUid());
//        mArtistDatabase = FirebaseDatabase.getInstance()
//                .getReference(Artist.DatabaseFields.ROOT_DATABASE);
//        mArtistDatabase.keepSynced(true);

        Film film = getIntent().getParcelableExtra(EXTRA_FILM);
        isNewFilm = film == null;

        if (isNewFilm) {
            mBinding.setFilm(new Film());
        } else {
            mBinding.setFilm(film);
            mOldFilm = new Film(film); // clone object


            if (film.getWriter() != null && film.getWriter().getName() != null){
                mWriter = mOldFilm.getWriter();
            }
            if (film.getDirector() != null && film.getDirector().getName() != null){
                mDirector = mOldFilm.getDirector();
            }

            List<Artist> list = mOldFilm.getCast();
            if (list.size() > 0){
                for (Artist artist : list){
                    insertRowArtist(artist);
                }
            }
//            Log.d(TAG, "mDirector onCreate: " + mDirector.getName());
//            Log.d(TAG, "writer id: " + mWriter.getId());
        }

        mBinding.imgButtonPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        mArtistArrayAdapter = new ArtistArrayAdapter(
                this,
                R.layout.custom_item_dropdown_adapter,
                R.id.tvName
        );
        mBinding.actDirection.setAdapter(mArtistArrayAdapter);
        mBinding.actDirection.setThreshold(1);
        mBinding.actDirection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mDirector = (Artist) adapterView.getItemAtPosition(position);
                mBinding.getFilm().setDirector(mDirector);
            }
        });
        mBinding.actDirection.setOnFocusChangeListener(handlerNewArtist);

        mBinding.actWriter.setAdapter(mArtistArrayAdapter);
        mBinding.actWriter.setThreshold(1);
        mBinding.actWriter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mWriter = (Artist) adapterView.getItemAtPosition(position);
                mBinding.getFilm().setWriter(mWriter);
            }
        });
        mBinding.actWriter.setOnFocusChangeListener(handlerNewArtist);

        mBinding.edtRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment =
                        DatePickerFragment.newInstance(mBinding.getFilm().getReleaseDate());
                fragment.show(getFragmentManager(), DatePickerFragment.TAG);
            }
        });

        mBinding.btnAddElenco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CastDialogFragment fragment = new CastDialogFragment();
                fragment.open(getSupportFragmentManager());
            }
        });

    }

    View.OnFocusChangeListener handlerNewArtist = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                switch (view.getId()) {
                    case R.id.actDirection:
                        String directorName = mBinding.actDirection.getText().toString().trim();
                        if (!TextUtils.isEmpty(directorName) && directorName.length() < 4) {
                            mBinding.actDirection.setError("Name is too short!");
                            mBinding.actDirection.selectAll();
                            return;
                        } else if (mDirector == null
                                && !TextUtils.isEmpty(directorName)) {
                            mDirector = mArtistDAO.createAndInsert(directorName);
                            mArtistArrayAdapter.notifyDataSetChanged();
                            mBinding.getFilm().setDirector(mDirector);

                        } else if (mDirector != null && !TextUtils.isEmpty(directorName) &&
                                !directorName.equals(mDirector.getName())) {
                            mDirector = mArtistDAO.createAndInsert(directorName);
                            mArtistArrayAdapter.notifyDataSetChanged();
                            mBinding.getFilm().setDirector(mDirector);
                        } else if (mDirector != null && TextUtils.isEmpty(directorName)){
                            mDirector = null;
                            mBinding.getFilm().setDirector(null);
                        }
                        break;
                    case R.id.actWriter:
                        String writerName   = mBinding.actWriter.getText().toString().trim();
                        if (!TextUtils.isEmpty(writerName) && writerName.length() < 4) {
                            mBinding.actWriter.setError("Name is too short!");
                            mBinding.actWriter.selectAll();
                            return;
                        } else if (mWriter == null
                                && !TextUtils.isEmpty(writerName)) {
                            mWriter = mArtistDAO.createAndInsert(writerName);
                            mArtistArrayAdapter.notifyDataSetChanged();
                            mBinding.getFilm().setWriter(mWriter);

                        } else if (mWriter != null && !TextUtils.isEmpty(writerName) &&
                                !mWriter.getName().equals(writerName)) {
                            mWriter = mArtistDAO.createAndInsert(writerName);
                            mArtistArrayAdapter.notifyDataSetChanged();
                            mBinding.getFilm().setWriter(mWriter);
                        } else if (mWriter != null && TextUtils.isEmpty(writerName)){
                            mWriter = null;
                            mBinding.getFilm().setWriter(null);
                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Film film = mBinding.getFilm();
        outState.putParcelable("film", film);
        outState.putByteArray("image", mImageToUpload);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBinding.setFilm((Film) savedInstanceState.getParcelable("film"));

        List<Artist> list = mBinding.getFilm().getCast();
        if (list.size() > 0){
            for (Artist artist : list){
                insertRowArtist(artist);
            }
        }

        mImageToUpload = savedInstanceState.getByteArray("image");
        mBinding.setImgPoster(mImageToUpload);

        //this didnt work because databinding
        /*Log.d(TAG, "onRestore: " + mImageToUpload.length);
        if (mImageToUpload.length > 0){
            mBinding.imgButtonPoster.setImageBitmap(ImageUtils.byte2Bitmap(mImageToUpload));
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        if (isNewFilm){
            actionBar.setTitle(getString(R.string.new_filme));
        } else{
            actionBar.setTitle(mBinding.getFilm().getTitle());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mArtistArrayAdapter.cleanUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            Picasso.with(this)
                    .load(uri)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.image_select)
                    .into(mBinding.imgButtonPoster);

            //To manipulating image resize bitmap
            String imgPathGallery = ImageUtils.getPathImageGallery(uri, this);
            Bitmap bitmap = ImageUtils.resizeToMaxHeight(imgPathGallery, 400);
            mImageToUpload = ImageUtils.bitmap2Byte(bitmap);

        }
    }


    public void clickSave(View view) {

        if (TextUtils.isEmpty(mBinding.edtTitle.getText().toString().trim())){
            mBinding.edtTitle.setError(getString(R.string.cant_be_empty));
            mBinding.edtTitle.requestFocus();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.save_film));
        mProgressDialog.show();

        if (mImageToUpload.length > 0){
            StorageReference filePath =
                    mStorage.child("images")
                            .child(DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString());

            filePath.putBytes(mImageToUpload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mBinding.getFilm().setImageUrl(downloadUri.toString());
//                    Log.d(TAG, "putBytes() - imageUrl: " + mBinding.getFilm().getImageUrl());
                    saveFilm();
                    Toast.makeText(FilmRegisterActivity.this, "Image uploaded.", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            saveFilm();
        }

    }

    private void saveFilm(){
        Film film = mBinding.getFilm();
        FilmDAO filmDAO = FilmDAO.getInstance();
        film.setWriter(mWriter);
        film.setDirector(mDirector);
//        Log.d(TAG, "saveFilm: " + mDirector.getId());
        if (isNewFilm) {
            film.setId(filmDAO.insert(film));
        } else {
            filmDAO.update(film, mOldFilm);
        }

//        filmDAO.cleanUp();
        finish();
        mProgressDialog.dismiss();
    }

    @Override
    public void onDateListener(int year, int month, int dayOfYear) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfYear);
        Date date = calendar.getTime();
        Locale locale;// = getResources().getConfiguration().locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }

//        Log.d(TAG, "locale: " + locale.getDisplayCountry());
        java.text.DateFormat dateFormat =
                java.text.DateFormat.getDateInstance();
        if (locale.getDisplayCountry().matches("(?i).*bra[sz]il.*")){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
            mBinding.edtRelease.setText(simpleDateFormat.format(date));
        }else {

            mBinding.edtRelease.setText(dateFormat.format(date));
        }
//        java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
    }


    @Override
    public void artistAdded(Artist artist) {
//        Toast.makeText(this, artist.getName()+ " adicionado ao elenco.", Toast.LENGTH_SHORT).show();
        if (!mBinding.getFilm().getCast().contains(artist)) {
            mBinding.getFilm().getCast().add(artist);
            insertRowArtist(artist);
        }

        if (!mBinding.tableCast.isShown()) {
            mBinding.tableCast.setVisibility(View.VISIBLE);
        }
    }

    //Holding cast insert artist
    public void insertRowArtist(Artist artist) {

        final TextView textView = new TextView(this);
        TableRow linha = new TableRow(this);


        textView.setPadding(8, 8, 8, 8);
        textView.setText(artist.getName());
        textView.setTextSize(18);
        textView.setTag(artist);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_24dp, 0);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.getFilm().getCast().remove(v.getTag());
                if (mBinding.getFilm().getCast().size() == 0){
                    mBinding.tableCast.setVisibility(View.GONE);
                }
                v.setVisibility(View.GONE);
            }
        });
        linha.addView(textView);
        mBinding.tableCast.addView(linha);


    }
}
