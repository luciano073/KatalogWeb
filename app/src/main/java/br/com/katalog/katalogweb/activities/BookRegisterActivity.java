package br.com.katalog.katalogweb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.adapters.ArtistArrayAdapter;
import br.com.katalog.katalogweb.adapters.CustomFilterArrayAdapter;
import br.com.katalog.katalogweb.databinding.ActivityBookRegisterBinding;
import br.com.katalog.katalogweb.fragments.DatePickerFragment;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.ArtistDAO;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.BookDAO;
import br.com.katalog.katalogweb.utils.ImageUtils;

public class BookRegisterActivity extends AppCompatActivity
        implements DatePickerFragment.DatePickerFragmentListener {

    public static final String EXTRA_BOOK = "book";
    private static final int GALLERY_INTENT = 0;
    private ActivityBookRegisterBinding mBinding;
    private ProgressDialog mProgressDialog;
    private Book mOldBook;
    private Artist mWriter;
    private Artist mColors;
    private Artist mDrawings;
    private ArtistArrayAdapter mArtistArrayAdapter;
    private boolean isNewBook;
    private byte[] mImageToUpload;
    private StorageReference mStorage;
    private ArtistDAO mArtistDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_book_register);

        mProgressDialog = new ProgressDialog(this);
        mImageToUpload = new byte[0];
        mArtistDAO = ArtistDAO.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorage = FirebaseStorage.getInstance().getReference();

        Book book = getIntent().getParcelableExtra(EXTRA_BOOK);
        isNewBook = book == null;

        if (isNewBook) {
            mBinding.setBook(new Book());
        } else {
            mBinding.setBook(book);
            mOldBook = new Book(book);

            /*if (book.getWriter() != null && book.getWriter().getName() != null) {
                mWriter = mOldBook.getWriter();
            }
            if (book.getColors() != null && book.getColors().getName() != null) {
                mColors = mOldBook.getColors();
            }
            if (book.getDrawings() != null && book.getDrawings().getName() != null) {
                mDrawings = mOldBook.getDrawings();
            }*/
            if (book.getCoverType() == Book.HARDCOVER) {
                mBinding.rbHardcover.setChecked(true);
            }
            if (book.getCoverType() == Book.PAPERBACK) {
                mBinding.rbPaperback.setChecked(true);
            }
        }

        mBinding.imgButtonCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        mBinding.edtRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment =
                        DatePickerFragment.newInstance(mBinding.getBook().getReleaseDate(), 1);
                fragment.show(getFragmentManager(), DatePickerFragment.TAG);
            }
        });

        mArtistArrayAdapter = new ArtistArrayAdapter(
                this,
                R.layout.custom_item_dropdown_adapter,
                R.id.tvName
        );

        mBinding.actWriter.setAdapter(mArtistArrayAdapter);
        mBinding.actWriter.setThreshold(1);
        mBinding.actWriter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //atribuição necessária devido a lógica do handlerNewArtist()
                mWriter = (Artist) adapterView.getItemAtPosition(i);
                mBinding.getBook().setWriter(mWriter);
            }
        });
        mBinding.actWriter.setOnFocusChangeListener(handlerNewArtist);

        mBinding.actColors.setAdapter(mArtistArrayAdapter);
        mBinding.actColors.setThreshold(1);
        mBinding.actColors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mColors = (Artist) adapterView.getItemAtPosition(i);
                mBinding.getBook().setColors(mColors);
            }
        });
        mBinding.actColors.setOnFocusChangeListener(handlerNewArtist);

        mBinding.actDrawings.setAdapter(mArtistArrayAdapter);
        mBinding.actDrawings.setThreshold(1);
        mBinding.actDrawings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDrawings = (Artist) adapterView.getItemAtPosition(i);
                mBinding.getBook().setDrawings(mDrawings);
            }
        });
        mBinding.actDrawings.setOnFocusChangeListener(handlerNewArtist);

        //multiautocompletetext for publishers
        List<String> publishers = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.publishers))
        );
        CustomFilterArrayAdapter adapterPublisher =
                new CustomFilterArrayAdapter(
                        this,
                        R.layout.custom_item_dropdown_adapter,
                        R.id.tvName,
                        publishers
                );
        mBinding.actPublishers.setAdapter(adapterPublisher);
        mBinding.actPublishers.setThreshold(1);
        mBinding.actPublishers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

    }

    View.OnFocusChangeListener handlerNewArtist = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            switch (view.getId()) {
                case R.id.actWriter:
                    if (!hasFocus) {
                        Artist writer = mBinding.getBook().getWriter();
                        String writerName = mBinding.actWriter.getText().toString().trim();
                        if (!TextUtils.isEmpty(writerName) && writerName.length() < 4) {

                            //question solution in stackoverflow.com/questions/
                            // 3003062/focus-issue-with-multiple-edittexts
                            mBinding.actWriter.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.actWriter.setError("Name is too short!");
                                    mBinding.actWriter.selectAll();
                                    mBinding.actWriter.requestFocus();
                                }
                            });
                        } else if (writer == null && !TextUtils.isEmpty(writerName)) {
                            mBinding.getBook().setWriter(mWriter);
                            if (!isNewBook) { //em caso de atualização evita duplicação de artista
                                mBinding.getBook().setWriter(mOldBook.getWriter());
                                mWriter = mOldBook.getWriter();
                            }
                            if (mWriter == null) {
                                mWriter = mArtistDAO.createAndInsert(writerName);
                                mBinding.getBook().setWriter(mWriter);
                            } else if (mWriter != null && !mWriter.getName().equals(writerName)) {
                                mWriter = mArtistDAO.createAndInsert(writerName);
                                mBinding.getBook().setWriter(mWriter);
                            }

                        }
                    } else {
                        //só entra quando recebe o foco.
                        mBinding.getBook().setWriter(null);//Remember request focus to other field
                        // when save the book to avoid field set to null.
                    }
                    break;
                case R.id.actDrawings:
                    if (!hasFocus) {
                        Artist drawings = mBinding.getBook().getDrawings();
                        String drawingsName = mBinding.actDrawings.getText().toString().trim();
                        if (!TextUtils.isEmpty(drawingsName) && drawingsName.length() < 4) {

                            //question solution in stackoverflow.com/questions/
                            // 3003062/focus-issue-with-multiple-edittexts
                            mBinding.actDrawings.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.actDrawings.setError("Name is too short!");
                                    mBinding.actDrawings.selectAll();
                                    mBinding.actDrawings.requestFocus();
                                }
                            });
                        } else if (drawings == null && !TextUtils.isEmpty(drawingsName)) {
                            mBinding.getBook().setWriter(mDrawings);
                            if (!isNewBook) { //em caso de atualização evita duplicação de artista
                                mBinding.getBook().setWriter(mOldBook.getDrawings());
                                mDrawings = mOldBook.getDrawings();
                            }
                            if (mDrawings == null) {
                                mDrawings = mArtistDAO.createAndInsert(drawingsName);
                                mBinding.getBook().setWriter(mDrawings);
                            } else if (mDrawings != null && !mDrawings.getName().equals(drawingsName)) {
                                mDrawings = mArtistDAO.createAndInsert(drawingsName);
                                mBinding.getBook().setWriter(mDrawings);
                            }

                        }
                    } else {
                        //só entra quando recebe o foco.
                        mBinding.getBook().setDrawings(null);//Remember request focus to other field
                        // when save the book to avoid field set to null.
                    }
                    break;
                case R.id.actColors:
                    if (!hasFocus) {
                        Artist colors = mBinding.getBook().getColors();
                        String colorsName = mBinding.actColors.getText().toString().trim();
                        if (!TextUtils.isEmpty(colorsName) && colorsName.length() < 4) {

                            //question solution in stackoverflow.com/questions/
                            // 3003062/focus-issue-with-multiple-edittexts
                            mBinding.actColors.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.actColors.setError("Name is too short!");
                                    mBinding.actColors.selectAll();
                                    mBinding.actColors.requestFocus();
                                }
                            });
                        } else if (colors == null && !TextUtils.isEmpty(colorsName)) {
                            mBinding.getBook().setWriter(mColors);
                            if (!isNewBook) { //em caso de atualização evita duplicação de artista
                                mBinding.getBook().setWriter(mOldBook.getColors());
                                mColors = mOldBook.getColors();
                            }
                            if (mColors == null) {
                                mColors = mArtistDAO.createAndInsert(colorsName);
                                mBinding.getBook().setWriter(mColors);
                            } else if (mColors != null && !mColors.getName().equals(colorsName)) {
                                mColors = mArtistDAO.createAndInsert(colorsName);
                                mBinding.getBook().setWriter(mColors);
                            }

                        }
                    } else {
                        //só entra quando recebe o foco.
                        mBinding.getBook().setColors(null);//Remember request focus to other field
                        // when save the book to avoid field set to null.
                    }
                    break;
            }

        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Book book = mBinding.getBook();
        outState.putParcelable("book", book);
        outState.putByteArray("image", mImageToUpload);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBinding.setBook((Book) savedInstanceState.getParcelable("book"));
        mImageToUpload = savedInstanceState.getByteArray("image");
        mBinding.setImgPoster(mImageToUpload);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        if (isNewBook) {
            actionBar.setTitle(getString(R.string.new_book));
        } else {
            actionBar.setTitle(mBinding.getBook().getTitle());
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
                    .into(mBinding.imgButtonCover);

            //To manipulating image resize bitmap
            String imgPathGallery = ImageUtils.getPathImageGallery(uri, this);
            Bitmap bitmap = ImageUtils.resizeToMaxHeight(imgPathGallery, 400);
            mImageToUpload = ImageUtils.bitmap2Byte(bitmap);

        }
    }

    public void clickSave(View view) {

        if (TextUtils.isEmpty(mBinding.edtTitle.getText().toString().trim())) {
            mBinding.edtTitle.setError(getString(R.string.cant_be_empty));
            mBinding.edtTitle.requestFocus();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.save_book));
        mProgressDialog.show();

        if (mImageToUpload.length > 0) {
            StorageReference filePath =
                    mStorage.child("images")
                            .child(DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString());

            filePath.putBytes(mImageToUpload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mBinding.getBook().setImageUrl(downloadUri.toString());
//                    Log.d(TAG, "putBytes() - imageUrl: " + mBinding.getFilm().getImageUrl());
                    saveBook();
                    Toast.makeText(BookRegisterActivity.this, "Image uploaded.", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            saveBook();
        }

    }

    private void saveBook() {
        mBinding.edtTitle.requestFocus();// force called to handlerNewArtist
        Book book = mBinding.getBook();
        BookDAO bookDAO = BookDAO.getInstance();
        if (TextUtils.isEmpty(mBinding.actDrawings.getText().toString().trim()))
            mBinding.getBook().setDrawings(null);
        if (TextUtils.isEmpty(mBinding.actColors.getText().toString().trim()))
            mBinding.getBook().setColors(null);
        if (TextUtils.isEmpty(mBinding.actWriter.getText().toString().trim()))
            mBinding.getBook().setWriter(null);
        if (mBinding.rbHardcover.isChecked()) {
            book.setCoverType(Book.HARDCOVER);
        }
        if (mBinding.rbPaperback.isChecked()) {
            book.setCoverType(Book.PAPERBACK);
        }

        if (isNewBook) {
            book.setId(bookDAO.insert(book));
        } else {
            bookDAO.update(book, mOldBook);
        }

        finish();
        mProgressDialog.dismiss();
    }


    @Override
    public void onDateListener(int view, int year, int month, int dayOfYear) {
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
        if (locale.getDisplayCountry().matches("(?i).*bra[sz]il.*")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
            mBinding.edtRelease.setText(simpleDateFormat.format(date));
        } else {

            mBinding.edtRelease.setText(dateFormat.format(date));
        }
    }
}
