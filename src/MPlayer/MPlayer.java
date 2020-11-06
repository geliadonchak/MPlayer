package MPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
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
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPlayer extends Application {
    public final String pauseStyle = "-fx-background-color: #545454; -fx-background-radius: 50%; -fx-font-size: 15;";
    public final String pauseIcon = "❚❚";
    public final String playStyle = "-fx-background-color: #545454; -fx-background-radius: 50%; -fx-font-size: 18;";
    public final String playIcon = "▶";

    private MediaPlayer mediaPlayer;
    private Stage stage;
    private int currentlyPlaying = 0;

    private final ArrayList<Song> playlist;

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
    @FXML
    public JFXSlider sliderDuration;
    @FXML
    public JFXSlider sliderVolume;

    public void playClicked(MouseEvent mouseEvent) {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.READY || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                play.setText(pauseIcon);
                play.setStyle(pauseStyle);
                mediaPlayer.play();

                mediaPlayer.setOnEndOfMedia(() -> {
                    play.setText(playIcon);
                    play.setStyle(playStyle);
                    mediaPlayer.stop();
                    sliderDuration.setValue(0);
                });

                new Thread(() -> {
                    if (this.mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    while (this.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        if (!this.sliderDuration.valueChangingProperty().getValue()) {
                            double allTime = mediaPlayer.getMedia().getDuration().toSeconds();
                            sliderDuration.setValue(mediaPlayer.getCurrentTime().toSeconds() * 100.0 / allTime);
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            } else {
                play.setText(playIcon);
                play.setStyle(playStyle);
                mediaPlayer.pause();
            }
        }

    }

    public void addMedia(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(stage);

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

//        redrawPlaylist();
    }

//    private void redrawPlaylist() {
//        for (Song song: playlist) {
//            song.getMediaPlayer().setOnReady(() -> {
//                song.setDuration(song.getMedia().getDuration());
//            });
//        }
//    }

    private void setSong(Song song) {
        sliderDuration.minProperty().setValue(0.0);
        sliderDuration.maxProperty().setValue(100.0);

        mediaPlayer = song.getMediaPlayer();
        mediaPlayer.setOnReady(() -> {
            song.setDuration(song.getMedia().getDuration());
            title_song.setText(song.getTitle());
            artist.setText(song.getArtist());
            time.setText(song.getDuration());
            sliderDuration.setValue(0.0);
        });

        mediaPlayer.setVolume(sliderVolume.getValue() / 100);

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
        });

        sliderDuration.setOnMouseReleased((event) -> {
            double allTime = mediaPlayer.getMedia().getDuration().toSeconds();
            double newSongTime = sliderDuration.getValue() * allTime / 100;

            Duration duration = new Duration(newSongTime * 1000);
            mediaPlayer.seek(duration);
        });

        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });
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
