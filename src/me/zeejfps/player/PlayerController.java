package me.zeejfps.player;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;
import me.zeejfps.paw.PandoraClient;
import me.zeejfps.paw.models.Station;
import me.zeejfps.paw.models.Track;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML
    ListView<Station> stationsListView;

    @FXML
    Slider timeSlider;

    @FXML
    Pane tackImageViewContainer;

    @FXML
    ImageView trackImageView;

    @FXML
    Button playButton;

    @FXML
    Label trackLabel;

    private PandoraClient pandoraClient;
    private Track[] tracks;
    private int currTrack = 0;
    private MediaPlayer player;
    private Station currStation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pandoraClient = PandoraPlayer.getInstance().getPandoraClient();
        ObservableList<Station> stationsList = FXCollections.observableArrayList(pandoraClient.getStations());
        stationsListView.setItems(stationsList);
        stationsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
            fetchNewPlaylist(newValue);
        });
        stationsListView.getSelectionModel().select(0);
        trackImageView.fitWidthProperty().bind(tackImageViewContainer.widthProperty());
        trackImageView.fitHeightProperty().bind(tackImageViewContainer.heightProperty());
        Font font = Font.loadFont(PandoraPlayer.class.getClassLoader().getResource("fonts/fontawesome-webfont.ttf").toExternalForm(), 32);
        System.out.println(font);
        //playButton.setFont(font);
    }

    @FXML
    protected void playButtonClicked() {
        if (player != null) {
            switch (player.statusProperty().getValue()) {
                case PLAYING:
                    player.pause();
                    break;
                case STOPPED:
                case PAUSED:
                    player.play();
                    break;
            }
        }
    }

    @FXML
    protected void skipButtonClicked() {
        playNextTrack();
    }

    private void playNextTrack() {
        playTrack(tracks[currTrack++]);
    }

    private void playTrack(Track track) {
        if (player != null) {
            player.stop();
            player.dispose();
        }
        trackLabel.setText(track.getArtistName() + "\n" + track.getSongTitle());
        Image image = new Image(track.getAlbumArt()[2].getUrl());
        trackImageView.setImage(image);
        player = new MediaPlayer(new Media(track.getAudioUrl()));
        player.statusProperty().addListener((observable, oldStatus, newStatus) -> {
            switch (newStatus) {
                case PLAYING:
                    playButton.setText("\uF04C");
                    break;
                case PAUSED:
                case STOPPED:
                    playButton.setText("\uF04B");
                    break;
            }
        });
        player.setOnReady(player::play);
        player.setOnEndOfMedia(this::onEndOfMedia);
        player.currentTimeProperty().addListener(observable -> Platform.runLater(() -> {
            Duration currentTime = player.getCurrentTime();
            timeSlider.setDisable(player.getMedia().getDuration().isUnknown());
            if (!timeSlider.isDisabled()
                    && player.getMedia().getDuration().greaterThan(Duration.ZERO)
                    && !timeSlider.isValueChanging()) {
                timeSlider.setValue(currentTime.divide(player.getMedia().getDuration()).toMillis()* 100.0);
            }
        }));
        timeSlider.setValue(0);
        timeSlider.valueProperty().addListener(observable -> {
            if (timeSlider.isValueChanging()) {
                player.seek(player.getMedia().getDuration()
                        .multiply(timeSlider.getValue() / 100.0));
            }
        });
    }

    private void fetchNewPlaylist(Station station) {
        currStation = station;
        FetchPlaylistTask task = new FetchPlaylistTask(pandoraClient, station);
        task.setOnSucceeded(event -> onPlaylistLoaded(task.getValue()));
        new Thread(task).start();
    }

    private void onPlaylistLoaded(Track[] tracks) {
        this.tracks = tracks;
        currTrack = 0;
        playNextTrack();
    }

    private void onEndOfMedia() {
        if (currTrack >= tracks.length) {
            fetchNewPlaylist(currStation);
        }
        else {
            playTrack(tracks[currTrack++]);
        }
    }

    private static class FetchPlaylistTask extends Task<Track[]> {

        private final PandoraClient pandoraClient;
        private final Station station;

        public FetchPlaylistTask(PandoraClient pandoraClient, Station station) {
            this.pandoraClient = pandoraClient;
            this.station = station;
        }

        @Override
        protected Track[] call() throws Exception {
            return pandoraClient.fetchPlaylistFragment(station);
        }
    }

}
