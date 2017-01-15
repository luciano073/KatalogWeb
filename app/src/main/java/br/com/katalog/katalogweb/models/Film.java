package br.com.katalog.katalogweb.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import br.com.katalog.katalogweb.utils.StringUtils;

/**
 * Created by luciano on 22/08/2016.
 */
public class Film implements Parcelable {

    @Exclude
    public String artistId;
    @Exclude
    private String uid;
    @Exclude
    private String id;

    private String title;
    private String noDiacriticsTitle;
    //    @PropertyName("Original Title")
    private String originalTitle;
    private long releaseDate;
    private int length;
    private float stars;
    private String companyProduction;
    private String genres;
    private String videoReview;
    private String imageUrl;
    private long watchDate;
    @Exclude
    private Artist director;
    @Exclude
    private Artist writer;
    @Exclude
    private List<Artist> cast;


    public Film() {
        cast = new ArrayList<Artist>();
    }

    //duplicate an object
    public Film(Film other) {
        this.title = other.title;
        this.originalTitle = other.originalTitle;
        this.id = other.id;
        this.length = other.length;
        this.releaseDate = other.releaseDate;
        this.imageUrl = other.imageUrl;
        this.videoReview = other.videoReview;
        this.stars = other.stars;
        this.watchDate = other.watchDate;
        this.director = other.director;
        this.writer = other.writer;
        this.cast = new ArrayList<Artist>(other.cast);
    }

    public Film(String title, String originalTitle) {
        this.title = title;
        this.noDiacriticsTitle = StringUtils.removeDiacritics(title);
        this.originalTitle = originalTitle;

    }

    //Parcelable
    protected Film(Parcel in) {
        artistId = in.readString();
        uid = in.readString();
        id = in.readString();
        title = in.readString();
        noDiacriticsTitle = in.readString();
        originalTitle = in.readString();
        releaseDate = in.readLong();
        length = in.readInt();
        stars = in.readFloat();
        companyProduction = in.readString();
        genres = in.readString();
        videoReview = in.readString();
        imageUrl = in.readString();
        watchDate = in.readLong();
        director = in.readParcelable(Artist.class.getClassLoader());
        writer = in.readParcelable(Artist.class.getClassLoader());
        cast = in.createTypedArrayList(Artist.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(artistId);
        parcel.writeString(uid);
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(noDiacriticsTitle);
        parcel.writeString(originalTitle);
        parcel.writeLong(releaseDate);
        parcel.writeInt(length);
        parcel.writeFloat(stars);
        parcel.writeString(companyProduction);
        parcel.writeString(genres);
        parcel.writeString(videoReview);
        parcel.writeString(imageUrl);
        parcel.writeLong(watchDate);
        parcel.writeParcelable(director, i);
        parcel.writeParcelable(writer, i);
        parcel.writeTypedList(cast);
    }

    public static final Creator<Film> CREATOR = new Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    // End Parcelable


    public interface DatabaseFields {
        String ROOT_DATABASE = "Films";
        String DIRECTOR = "Director";
        String WRITER = "Writer";
        String CAST = "Cast";
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", this.title);
        result.put("originalTitle", this.originalTitle);
        result.put("noDiacriticsTitle", this.noDiacriticsTitle);
        result.put("releaseDate", this.releaseDate);
        result.put("watchDate", this.watchDate);
        result.put("length", this.length);
        result.put("stars", this.stars);
        result.put("imageUrl", this.imageUrl);
        result.put("videoReview", this.videoReview);
        result.put("companyProduction", this.companyProduction);

        /*if (this.director != null && this.director.getName() != null)
            result.put(this.director.getId(), this.director.getName());
        if (this.writer != null && this.writer.getId() != null) {
            result.put(this.writer.getId(), this.writer.getName());
        }
        if (this.cast.size() > 0){
            for (Artist artist : cast){
                result.put(artist.getId(), artist.getName());
            }
        }*/
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public Artist getDirector() {
        return director;
    }

    public void setDirector(Artist director) {
        this.director = director;
    }

    @Exclude
    public Artist getWriter() {
        return writer;
    }

    public void setWriter(Artist writer) {
        this.writer = writer;
    }

    public String getTitle() {
        return this.title; // != null ? StringUtils.normalizeTitle(title) : null;
    }

    //Usar WordUtils.capitalize da lib: compile 'org.apache.commons:commons-lang3:3.5'
    public void setTitle(String title) {
//        String aux = WordUtils.capitalize(title);
        this.title = StringUtils.alphabeticTitle(title);
        this.noDiacriticsTitle = StringUtils.removeDiacritics(title);
    }


    public String getOriginalTitle() {
        return this.originalTitle != null ? StringUtils.normalizeTitle(originalTitle) : null;
    }

    public void setOriginalTitle(String originalTitle) {
//        String aux = WordUtils.capitalize(originalTitle);
        this.originalTitle = StringUtils.alphabeticTitle(originalTitle);
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getWatchDate(){
        return watchDate;
    }

    public void setWatchDate(long watchDate) {
        this.watchDate = watchDate;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getCompanyProduction() {
        return companyProduction;
    }

    public void setCompanyProduction(String companyProduction) {
        this.companyProduction = companyProduction;
    }

    public String getNoDiacriticsTitle() {
        return noDiacriticsTitle;
    }


    public String getVideoReview() {
        return videoReview;
    }

    public void setVideoReview(String videoReview) {
        this.videoReview = videoReview;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public List<Artist> getCast() {
        return cast;
    }

    public void setCast(List<Artist> cast) {
        this.cast = cast;
    }

    @Exclude
    public String castToListViewShow() {
        StringBuilder builder = new StringBuilder();
        List<Artist> artists = getCast();

        Collections.shuffle(artists);

        Iterator<Artist> iterator = artists.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getName());
            if (iterator.hasNext()) {
                builder.append(", ");
            }

        }
        return builder.toString();
    }

    /*@Exclude
    public String get2RandomCast() {
        Set<String> list        = new HashSet<String>();
        StringBuilder builder   = new StringBuilder();
        Random random           = new Random();
        int listSize            = getCast().size();

        switch (listSize){
            case 0: return "";
            case 1: return getCast().get(0).getName();
            case 2:
                builder.append(getCast().get(0).getName());
                builder.append(", ");
                builder.append(getCast().get(1).getName());
                return builder.toString();
            default:
                for (int i = 0; i < 2; i++) {
                    while (list.size() < 2) {
                        list.add(getCast().get(random.nextInt(getCast().size())).getName());
                    }
                }
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()){
                    builder.append(iterator.next());
                    if (iterator.hasNext()) {
                        builder.append(", ");
                    }
                }


                return builder.toString();
        }

    }
*/
    @Exclude
    public String getWatchDateAsString(){
        long date = this.watchDate;
        String formatDate = null;
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = DateFormat.getDateInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        formatDate = simpleDateFormat.format(date);
        return date != 0 ? formatDate : null;
    }
    @Exclude
    public int getReleaseYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.getReleaseDate());
        return calendar.get(Calendar.YEAR);
    }

    @Exclude
    public String getNormalizedTitle() {
        return this.title != null ? StringUtils.normalizeTitle(title) : null;
    }

    //Comparator for sorting the list by film title
    public static Comparator<Film> titleComparetor = new Comparator<Film>() {
        @Override
        public int compare(Film film, Film t1) {
            String filmTitle1 = film.getTitle().toUpperCase();
            String filmTitle2 = t1.getTitle().toUpperCase();

            //ascending order
//            return filmTitle1.compareTo(filmTitle2);

            //descending order
            return filmTitle2.compareTo(filmTitle1);
        }
    };

    @Override
    public String toString() {
        return getNormalizedTitle();
    }
}
