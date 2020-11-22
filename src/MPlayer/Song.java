package MPlayer;

import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Song {
    private final int index;
    private String title;
    private String artist;
    private final MediaPlayer mediaPlayer;

    Song(int index, File file) {
        this.index = index;

        Media media = new Media(file.toURI().toString());
        media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if (change.getKey().equals("artist")) {
                this.artist = (String) change.getValueAdded();
            } else if (change.getKey().equals("title")) {
                this.title = (String) change.getValueAdded();
            }
        });

        mediaPlayer = new MediaPlayer(media);
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public String getArtist() {
        return artist;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
