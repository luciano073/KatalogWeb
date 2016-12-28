package br.com.katalog.katalogweb.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import br.com.katalog.katalogweb.utils.StringUtils;

/**
 * Created by luciano on 30/08/2016.
 */
public class Artist implements Parcelable {
    private String name;
    private String birthDate;
    private String noDiacriticsName;
    private String workName;
    @Exclude
    private String id;
    private Map<String, Object> directed;
    private Map<String, Object> wrote;
    private Map<String, Object> acted;
    private Map<String, Object> drew;


    public Artist() {
    }

    public Artist(Artist other){
        this.name       = other.name;
        this.id         = other.id;

    }

    public Artist(String name) {
        this.name       = name;
    }

    // Parcelable
    protected Artist(Parcel in) {
        name                = in.readString();
        birthDate           = in.readString();
        noDiacriticsName    = in.readString();
        id                  = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(birthDate);
        parcel.writeString(noDiacriticsName);
        parcel.writeString(id);
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 1;
    }
    // End Parcelable


    public String getName() {
        return name;
    }

    public void setName(String name) {
        String aux = org.apache.commons.lang3.text.WordUtils.capitalize(name);
        this.name = aux;
        this.noDiacriticsName = StringUtils.removeDiacritics(aux);
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Exclude
    public String getId() {
        return id;
    }
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getNoDiacriticsName() {
        return noDiacriticsName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (!getName().equals(artist.getName())) return false;
        return getId() != null ? getId().equals(artist.getId()) : artist.getId() == null;

    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    public interface DatabaseFields{
        String ROOT_DATABASE = "Artists";
        String DIRECTED = "Directed";
        String WROTE = "Wrote";
        String ACTED = "Acted";
        String DREW = "Drew";
        String PAINTED = "Painted";
    }
}
