package fr.upem.soundroid;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Track implements Comparable<Track>, Serializable {

    private static final long serialVersionUID = 4206969694207101685L;

    private final String path;
    private final String title;
    private final String author;
    private final String album;
    private final int count;

    private Track(String path, String title, String author, String album, int count) {
        this.path = path;
        this.title = title;
        this.author = author;
        this.album = album;
        this.count = count;
    }

    public static Track fromPath(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String s = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        int num = (s == null || s.isEmpty()) ? 0 : Integer.parseInt(s);
        return new Track(path,
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                num
        );
    }

    public static List<Track> deSerialize(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        return (List<Track>) ois.readObject();
    }

    public static void serialize(File file, List<Track> tracks) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(tracks);
    }

    public static List<Track> index(File root) {
        List<Track> lst = new ArrayList<Track>();
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                lst.addAll(index(file));
            } else if (file.getName().endsWith(".mp3")) {
                lst.add(fromPath(file.getAbsolutePath()));
            }
        }
        return lst;
    }

    public static void index(File root, Consumer<Track> consumer) {
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                index(file, consumer);
            } else if (file.getName().endsWith(".mp3")) {
                consumer.accept(fromPath(file.getAbsolutePath()));
            }
        }
    }

    public static void index(Context context, Consumer<Track> consumer) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TRACK,
        };
        Cursor c = context.getContentResolver().query(uri, projection, "", new String[0], null);

        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                String name = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);
                String trackNumber = c.getString(4);

                Track t = new Track(path, name, artist, album, (trackNumber == null || trackNumber.isEmpty()) ? 0 : Integer.parseInt(trackNumber));

                consumer.accept(t);
            }
            c.close();
        }
    }

    public static List<Track> index(Context context) {
        List<Track> lst = new ArrayList<>();
        index(context, lst::add);
        return lst;
    }


    public static List<Track> index(String root) {
        return index(new File(root));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((album == null) ? 0 : album.hashCode());
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Track other = (Track) obj;
        if (album == null) {
            if (other.album != null)
                return false;
        } else if (!album.equals(other.album))
            return false;
        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (title == null) {
            return other.title == null;
        } else return title.equals(other.title);
    }

    @Override
    public int compareTo(Track o) {
        int res;
        res = author.compareTo(o.author);
        if (res != 0) return res;
        res = album.compareTo(o.album);
        if (res != 0) return res;
        res = count - o.count;
        if (res != 0) return res;
        res = title.compareTo(o.title);
        if (res != 0) return res;
        res = path.compareTo(o.path);
        return res;
    }

    @NonNull
    @Override
    public String toString() {
        return author + " - " + title;
    }

    private static String removeAccentsUpper(String src) {
        if (src == null) return "";
        return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
    }

    public boolean filter(String filter) {
        String s = removeAccentsUpper(filter);
        return removeAccentsUpper(title).contains(s)
                || removeAccentsUpper(album).contains(s)
                || removeAccentsUpper(author).contains(s);
    }
}
