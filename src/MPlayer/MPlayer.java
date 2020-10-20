package MPlayer;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class MPlayer extends Application {

    private MediaPlayer mediaPlayer;
    private Stage stage;
    private int currentlyPlaying = 0;

    private ArrayList<Song> playlist;

    public MPlayer() {
        playlist = new ArrayList<>();
    }

    @FXML
    public Label title_song;
    @FXML
    public Label artist;
    @FXML
    public Label time;
    @FXML
    public JFXButton play;

    public void playClicked(MouseEvent mouseEvent) {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void addMedia(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac")
        );

        var files = fileChooser.showOpenMultipleDialog(stage);

        if (files == null) {
            return;
        }

        for (File file : files) {
            Song song = new Song(file);
            if (!playlist.contains(song)) {
                playlist.add(song);
            }
        }

        if (mediaPlayer == null) {
            try {
                currentlyPlaying = 0;
                setSong(playlist.get(currentlyPlaying));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        redrawPlaylist();
    }

    private void setSong(Song song) {
        mediaPlayer = song.getMediaPlayer();
        mediaPlayer.setOnReady(() -> {
            song.setDuration(song.getMedia().getDuration());
            title_song.setText(song.getTitle());
            artist.setText(song.getArtist());
            time.setText(song.getDuration());
        });
    }

    private void redrawPlaylist() {
        for (Song song : playlist) {

        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        stage.setTitle("MPlayer");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 950, 450));
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
