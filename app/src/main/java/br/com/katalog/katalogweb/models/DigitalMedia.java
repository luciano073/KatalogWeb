package br.com.katalog.katalogweb.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Created by luciano on 25/12/2016.
 */

public class DigitalMedia implements Parcelable{
    private String id;
    private String title;
    private Film film;
    private String company;
    private int caseType;
    private String mediaType;


    public static final String BLURAY   = "Blu-ray";
    public static final String DVD      = "DVD";


    public DigitalMedia() {
    }

    public static interface CaseType{
        int AMARAY      = 0;
        int BOX         = 1;
        int DIGIPAK     = 2;
        int DIGISTAK    = 3;
        int STEELBOOK   = 4;
        int BOOKCASE    = 5;
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
    public Film getFilm() {
        return film;
    }

    @Exclude
    public void setFilm(Film film) {
        this.film = film;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getCaseType() {
        return caseType;
    }

    public void setCaseType(int caseType) {
        this.caseType = caseType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Exclude
    public String getCaseTypeAsString(){
        switch (getCaseType()){
            case CaseType.AMARAY:
                return "Amaray";
            case CaseType.BOX:
                return "Box";
            case CaseType.DIGIPAK:
                return "Digipak";
            case CaseType.DIGISTAK:
                return "Digistak";
            case CaseType.STEELBOOK:
                return "SteelBook";
            case CaseType.BOOKCASE:
                return "BookCase";
        }
        return null;
    }

    //Parcelable
    @Override
    public int describeContents() {
        return 4;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeParcelable(film, i);
        parcel.writeString(company);
        parcel.writeInt(caseType);
        parcel.writeString(mediaType);
    }

    protected DigitalMedia(Parcel in) {
        id = in.readString();
        title = in.readString();
        film = in.readParcelable(Film.class.getClassLoader());
        company = in.readString();
        caseType = in.readInt();
        mediaType = in.readString();
    }

    public static final Creator<DigitalMedia> CREATOR = new Creator<DigitalMedia>() {
        @Override
        public DigitalMedia createFromParcel(Parcel in) {
            return new DigitalMedia(in);
        }

        @Override
        public DigitalMedia[] newArray(int size) {
            return new DigitalMedia[size];
        }
    };

}
