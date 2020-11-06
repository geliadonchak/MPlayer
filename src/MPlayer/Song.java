package MPlayer;

import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class Song {
    private String title;
    private String artist;
    private Duration duration;
    private final Media media;
    private final MediaPlayer mediaPlayer;

    public Song(File file) {
        media = new Media(file.toURI().toString());
        media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if (change.getKey().equals("artist")) {
                artist = (String) change.getValueAdded();
            } else if (change.getKey().equals("title")) {
                title = (String) change.getValueAdded();
            }
        });

        this.mediaPlayer = new MediaPlayer(media);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        int minutes = (int) Math.floor(duration.toMinutes());
        int seconds = (int) Math.floor(duration.toSeconds()) - minutes * 60;
        return minutes + ":" + seconds;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Media getMedia() {
        return media;
    }
}
