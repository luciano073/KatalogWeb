package br.com.katalog.katalogweb.utils;

/**
 * Created by luciano on 28/10/2016.
 */

public interface Constants {
    String FILMS_DBREF = "Films";
    String FILM_DIRECTOR_CHILD_DBREF = "Director";
    String FILM_CAST_CHILD_DBREF = "Cast";
    String FILM_WRITER_CHILD_DBREF = "Writer";
    String BOOKS_DBREF = "books";
    String BOOK_WRITER_CHILD_DBREF = "writer";
    String BOOK_COLORS_CHILD_DBREF = "colors";
    String BOOK_DRAWINGS_CHILD_DBREF = "drawings";
    String DIGITAL_MEDIA_DBREF = "digital_media";
    String DIGITAL_MEDIA_CHILD_FILM = "film";
    String ARTISTS_DBREF = "Artists";
    String ARTIST_DATABASE_CHILD_ACTED = "Acted";
    String ARTIST_DATABASE_CHILD_DIRECTED = "Directed";
    String ARTIST_DATABASE_CHILD_WROTE = "Wrote";
    String ARTIST_DATABASE_CHILD_DREW = "Drew";
    String ARTIST_DATABASE_CHILD_PAINTED = "painted";
    String FILMS_DBREF_WITH_SLASH = "/Films/";
    String FILM_CONTRIBUTORS_DBREF = "film-contributors";

    String ARTIST_WORKS_DBREF_WITH_SLASH = "/artist-works/";
    String ARTIST_WORKS_DBREF = "artist-works";
}
