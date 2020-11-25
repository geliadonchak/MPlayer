package MPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleNode;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class MPlayer extends Application {
    @FXML
    private Label songTitle;

    @FXML
    private Label artist;

    @FXML
    private JFXButton play;

    @FXML
    private JFXButton next;

    @FXML
    private JFXButton prev;

    @FXML
    private Label time;

    @FXML
    private JFXSlider sliderDuration;

    @FXML
    private JFXSlider sliderVolume;

    @FXML
    private TableView<Song> playlist;

    @FXML
    private JFXToggleNode mediaSelect;

    @FXML
    private JFXButton mediaSelectButton;

    @FXML
    private JFXToggleNode mediaDelete;

    @FXML
    private JFXButton mediaDeleteButton;

    @FXML
    private TableColumn<Song, Integer> indexColumn;

    @FXML
    private TableColumn<Song, String> titleColumn;

    @FXML
    private TableColumn<Song, String> artistColumn;

    private MediaPlayer mediaPlayer = null;
    private int index = -1;

    @FXML
    void initialize() {
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));

        this.setImageOnButton(mediaDeleteButton, "remove.png", 40);
        this.setImageOnButton(mediaSelectButton, "add.png", 40);
        this.setImageOnButton(prev, "prev.png", 20);
        this.setImageOnButton(next, "next.png", 20);
        this.setImageOnButton(play, "play.png", 35);

        playlist.setOnKeyPressed(keyEvent -> {
            Song selectedItem = playlist.getSelectionModel().getSelectedItem();

            if (selectedItem == null) {
                return;
            }

            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                playlist.getItems().remove(selectedItem);
                rewindClick(1);
            }
        });

        mediaDelete.setOnAction(actionEvent -> {
            playlist.getItems().clear();
            mediaPlayer = null;
            index = -1;
            mediaDelete.setSelected(false);
            artist.setText("artist");
            time.setText("mm:ss");
            songTitle.setText("title");
            sliderDuration.setValue(0);
            this.setImageOnButton(play, "play.png", 35);
        });

        mediaSelect.setOnAction(actionEvent -> {
            mediaSelectClick();
            mediaSelect.setSelected(false);
            songTitle.requestFocus();
        });

        sliderDuration.valueProperty().addListener(durationListener);
        sliderVolume.valueProperty().addListener(volumeListener);

        TableView.TableViewSelectionModel<Song> selectionModel = playlist.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(tableChangeListener);

        play.setOnAction(actionEvent -> playButtonClick());
        next.setOnAction(actionEvent -> rewindClick(1));
        prev.setOnAction(actionEvent -> rewindClick(-1));
    }

    static class StyleRowFactory<T> implements Callback<TableView<T>, TableRow<T>> {
        @Override
        public TableRow<T> call(TableView<T> tableView) {
            return new TableRow<>() {
                @Override
                protected void updateItem(T paramT, boolean b) {
                    super.updateItem(paramT, b);
                }
            };
        }
    }

    ChangeListener<Duration> currentTimeListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t) {
            if (mediaPlayer == null) {
                return;
            }

            sliderDuration.setValue(mediaPlayer.getCurrentTime().toSeconds());
            changeCurrentTime();
        }
    };

    ChangeListener<Song> tableChangeListener = (val, oldSong, newSong) -> {
        if (newSong != null) {
            setCurrentSong(newSong);
            index = newSong.getIndex() - 1;
            songTitle.setText(newSong.getTitle());
        }
    };

    InvalidationListener durationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            if (sliderDuration.isPressed() && mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(sliderDuration.getValue()));
            }
        }
    };

    InvalidationListener volumeListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(sliderVolume.getValue() / 100);
            }
        }
    };

    void rewindClick(int direction) {
        if (playlist.getItems().isEmpty() && this.mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
            index = -1;
            artist.setText("artist");
            time.setText("mm:ss");
            songTitle.setText("title");
            sliderDuration.setValue(0);
            this.setImageOnButton(play, "play.png", 35);
        }

        if (index == -1) {
            return;
        }

        index = (index + direction) % playlist.getItems().size();
        if (index < 0) {
            index = playlist.getItems().size() - 1;
        }

        playlist.getSelectionModel().select(index);
        playlist.getFocusModel().focus(index);
    }

    void setCurrentSong(Song song) {
        if (song == null) {
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        mediaPlayer = song.getMediaPlayer();
        mediaPlayer.play();
        this.setImageOnButton(play, "pause.png", 20);
        sliderDuration.setMin(0);
        sliderDuration.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
        sliderDuration.setValue(0);
        songTitle.setText(song.getTitle());
        artist.setText(song.getArtist());
        mediaPlayer.setVolume(sliderVolume.getValue() / 100);

        mediaPlayer.currentTimeProperty().addListener(currentTimeListener);
        playlist.setRowFactory(new StyleRowFactory<>());
        playlist.refresh();
    }

    void changeCurrentTime() {
        int sec = ((int) mediaPlayer.getCurrentTime().toSeconds()) % 60;
        time.setText((int) mediaPlayer.getCurrentTime().toMinutes() + (sec < 10 ? ":0" : ":") + sec);
    }

    void playButtonClick() {
        if (mediaPlayer == null) {
            return;
        }

        var status = mediaPlayer.getStatus();
        if (status == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
            this.setImageOnButton(play, "pause.png", 20);
        } else {
            mediaPlayer.pause();
            this.setImageOnButton(play, "play.png", 35);
        }
    }

    void mediaSelectClick() {
        Window window = mediaSelect.getParent().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(window);

        if (files == null) {
            return;
        }

        for (File file : files) {
            Song song = new Song(playlist.getItems().size() + 1, file);
            playlist.getItems().add(song);
        }
    }
    
    private void setImageOnButton(JFXButton node, String icon, int fitHeight) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("icons/" + icon)));
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        node.setGraphic(imageView);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        primaryStage.setTitle("MPlayer");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 950, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}