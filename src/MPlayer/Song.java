package MPlayer;

import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Song {
    private String title;
    private String artist;
    private final Duration duration;
    private final Media media;
    private boolean currentlyPlaying;

    public Song(File file) {
        this.media = new Media(file.toURI().toString());
        duration = this.media.getDuration();

        media.getMetadata().forEach((key, value) -> {
            if (key.equals("artist")) {
                artist = (String) value;
            } else if (key.equals("title")) {
                title = (String) value;
            }
        });

        System.out.println(title);
        System.out.println(artist);
        System.out.println(getDuration());

        currentlyPlaying = false;
    }

    public Media getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return Math.floor(duration.toMinutes()) + ":" + Math.floor(duration.toSeconds());
    }

    public boolean isCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(boolean currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }
}
