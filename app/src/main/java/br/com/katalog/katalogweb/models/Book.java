package br.com.katalog.katalogweb.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Collections2;
import com.google.firebase.database.Exclude;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.katalog.katalogweb.utils.StringUtils;

import static br.com.katalog.katalogweb.R.string.artists;

/**
 * Created by luciano on 17/12/2016.
 */

public class Book implements Parcelable {
    private String id;
    private String title;
    private String originalTitle;
    private String noDiacriticsTitle;
    private String publisher;
    private String imageUrl;
    private int coverType;
    private String reviewUrl;
    private long releaseDate;
    private int pages;
    private Artist writer;
    private Artist drawings;
    private Artist colors;

    public static final int HARDCOVER = 1;
    public static final int PAPERBACK = 2;

    enum CoverType {
        HARDCOVER(0), PAPERBACK(1);

        private int valor;

        CoverType(int valor) {
            this.valor = valor;
        }

        public int getValor() {
            return this.valor;
        }
    }

    public Book() {
    }

    //duplicate an object
    public Book(Book other) {
        this.id = other.id;
        this.title = other.title;
        this.originalTitle = other.originalTitle;
        this.noDiacriticsTitle = other.noDiacriticsTitle;
        this.publisher = other.publisher;
        this.imageUrl = other.imageUrl;
        this.coverType = other.coverType;
        this.reviewUrl = other.reviewUrl;
        this.releaseDate = other.releaseDate;
        this.pages = other.pages;
        this.writer = other.writer;
        this.drawings = other.drawings;
        this.colors = other.colors;
    }


    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title; //!= null ? StringUtils.normalizeTitle(this.title) : "";
    }

    public void setTitle(String title) {
        String aux = WordUtils.capitalize(title);
        this.title = StringUtils.alphabeticTitle(aux);
        this.noDiacriticsTitle = StringUtils.removeDiacritics(aux);
    }

    public String getOriginalTitle() {
        return this.originalTitle != null ? StringUtils.normalizeTitle(originalTitle) : null;
    }

    public void setOriginalTitle(String originalTitle) {
        String aux = WordUtils.capitalize(originalTitle);
        this.originalTitle = StringUtils.alphabeticTitle(aux);
    }

    public String getNoDiacriticsTitle() {
        return noDiacriticsTitle;
    }


    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCoverType() {
        return coverType;
    }

    public void setCoverType(int coverType) {
        this.coverType = coverType;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Exclude
    public Artist getWriter() {
        return writer;
    }

    @Exclude
    public void setWriter(Artist writer) {
        this.writer = writer;
    }

    @Exclude
    public Artist getDrawings() {
        return drawings;
    }

    @Exclude
    public void setDrawings(Artist drawings) {
        this.drawings = drawings;
    }

    @Exclude
    public Artist getColors() {
        return colors;
    }

    @Exclude
    public void setColors(Artist colors) {
        this.colors = colors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (getId() != null ? !getId().equals(book.getId()) : book.getId() != null) return false;
        return getNoDiacriticsTitle().equals(book.getNoDiacriticsTitle());

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getNoDiacriticsTitle().hashCode();
        return result;
    }

    @Exclude
    public String getArtistsAsList() {
        String writer = getWriter().getName();
        String drawings = getDrawings().getName();
        String colors = getColors().getName();
        if (writer == null && drawings == null && colors == null)
            return "";
        StringBuilder builder = new StringBuilder();
        Set<String> artistSet = new HashSet<>();
        if (colors != null)
            artistSet.add(colors);
        if (drawings != null)
            artistSet.add(drawings);
        if (writer != null)
            artistSet.add(writer);

//        Collections.shuffle(artists);

        Iterator<String> iterator = artistSet.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(", ");
            }

        }
        return builder.toString();
    }

    @Exclude
    public String getDateAsString() {

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = DateFormat.getDateInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM/yyyy");
        return simpleDateFormat.format(getReleaseDate());
    }

    @Exclude
    public String getPublishersAsList() {
        String publishers = getPublisher();
        if (publishers == null)
            return "";

        int indice = publishers.lastIndexOf(",");
        if (indice != -1)
            publishers = new StringBuffer(publishers).replace(indice, indice + 1, "").toString();
        return publishers.replaceAll(",", " \\/");
    }

    @Exclude
    public String getNormalizedTitle() {
        return this.title != null ? StringUtils.normalizeTitle(this.title) : "";
    }

    public static Comparator<Book> tilteComparator = new Comparator<Book>() {
        @Override
        public int compare(Book book, Book t1) {
            String titleBook1 = book.getTitle();
            String titleBook2 = t1.getTitle();

            //descending order
            return titleBook2.compareTo(titleBook1);
        }
    };

    //Parcelable
    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        originalTitle = in.readString();
        noDiacriticsTitle = in.readString();
        publisher = in.readString();
        imageUrl = in.readString();
        coverType = in.readInt();
        reviewUrl = in.readString();
        releaseDate = in.readLong();
        pages = in.readInt();
        writer = in.readParcelable(Artist.class.getClassLoader());
        drawings = in.readParcelable(Artist.class.getClassLoader());
        colors = in.readParcelable(Artist.class.getClassLoader());
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(noDiacriticsTitle);
        parcel.writeString(publisher);
        parcel.writeString(imageUrl);
        parcel.writeInt(coverType);
        parcel.writeString(reviewUrl);
        parcel.writeLong(releaseDate);
        parcel.writeInt(pages);
        parcel.writeParcelable(writer, i);
        parcel.writeParcelable(drawings, i);
        parcel.writeParcelable(colors, i);
    }
}
