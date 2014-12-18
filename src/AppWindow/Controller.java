package AppWindow;

import PlayerComponents.GLOBAL;
import PlayerComponents.MusicOrganizer;
import PlayerComponents.Track;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;


public class Controller implements Initializable {

  private MusicOrganizer musicOrganizer;
  private ObservableList <Track> observableList;
  private ArrayList <Track> mainTrackList;

  @FXML //  fx:id="prev"
  private Button prev;
  @FXML
  private Button play;
  @FXML
  private Button stop;
  @FXML
  private Button next;
  @FXML
  private Button dir;
  @FXML
  private ListView list;
  @FXML
  private Text title;
  @FXML
  private Text artist;
  @FXML
  private TextField path;
  @FXML
  private TextField search;
  @FXML
  private VBox window;

  @FXML
  public void copyToClipboard(Event e) {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    if (content.putString(path.getText()) && !path.getText().equals(GLOBAL.EMPTY)){
      clipboard.setContent(content);
      path.setText(GLOBAL.CLIPBOARD_OK);
    } else{
      //set status bar
    }
  }

  @FXML
  public void shuffle(){
    ArrayList<Track>shuffledList = new ArrayList<>(mainTrackList);
    Collections.shuffle(shuffledList);
    musicOrganizer.setTracks(shuffledList);
    musicOrganizer.stop();
    refreshTrackList(shuffledList);
    if (musicOrganizer.getNumberOfTracks() !=0) {
      list.getSelectionModel().select(1);//deselect all
      play.setText(GLOBAL.PLAY);
      playButton(musicOrganizer.getCurrentTrackIndex());
    }
  }

  @Override // This method is called by the FXMLLoader when initialization is complete
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

    musicOrganizer = new MusicOrganizer();
    musicOrganizer.readLibrary(GLOBAL.FILEPATH);
    mainTrackList = new ArrayList<>(musicOrganizer.getTrackList());
    //searchResutlsList = new ArrayList<>(musicOrganizer.getTrackList());
    refreshTrackList(mainTrackList);
    list.getSelectionModel().selectFirst();

    GLOBAL.LISTENER = new PlaybackListener() {
      @Override
      public void playbackStarted(PlaybackEvent playbackEvent) {
        super.playbackStarted(playbackEvent);
        System.out.println(playbackEvent.getId() + " START EVENT");
        setDetailsPane(musicOrganizer.getTrackList().get(musicOrganizer.getCurrentTrackIndex()));
      }
      @Override
      public void playbackFinished(PlaybackEvent playbackEvent) {
        super.playbackFinished(playbackEvent);
        System.out.println(playbackEvent.getId() + " FINISHED EVENT");
        new Thread(()->{
          if (musicOrganizer.getNumberOfTracks() != 0) {
            musicOrganizer.playNextTrack();
            Platform.runLater(()->{
              list.getSelectionModel().select(musicOrganizer.getCurrentTrackIndex());
            });
          } else
            Platform.runLater(()->{
              play.setText(GLOBAL.PLAY);
            });
        }).start();
      }
    };

    //assert play != null : "fx:id=\"play\" was not injected: check your FXML file 'player.fxml'.";

    /*play.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        if (play.getText().equals(">")){
          play.setText("ll");
          GLOBAL.musicOrganizer.playTrack(GLOBAL.musicOrganizer.getCurrentTrackIndex());
        } else {
          play.setText(">");
          GLOBAL.musicOrganizer.stopPlaying();
        }
      }
    });*/

    play.setOnAction((e)->{
      if (musicOrganizer.getNumberOfTracks() !=0)
        playButton(musicOrganizer.getCurrentTrackIndex());
    });

    stop.setOnAction((e)->{
      musicOrganizer.stop();
      play.setText(GLOBAL.PLAY);
      list.getSelectionModel().select(musicOrganizer.getCurrentTrackIndex());
      resetDetailsPane();
    });

    prev.setOnAction((e)->{
      play.setText(GLOBAL.PAUSE);
      musicOrganizer.playPrevTrack();
      list.getSelectionModel().select(musicOrganizer.getCurrentTrackIndex());
    });

    next.setOnAction((e)->{
      play.setText(GLOBAL.PAUSE);
      musicOrganizer.playNextTrack();
      list.getSelectionModel().select(musicOrganizer.getCurrentTrackIndex());
    });

    dir.setOnAction((e)->{
      Platform.runLater(() -> { //Proper way to lanch thread for JavaFx Components
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(GLOBAL.OPEN_DIALOG);
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
          play.setText(GLOBAL.PLAY);
          musicOrganizer.stop();
          musicOrganizer.loadNewDir(dir.getPath());
          mainTrackList = new ArrayList<>(musicOrganizer.getTrackList());
          refreshTrackList(mainTrackList);
          list.getSelectionModel().selectFirst();
        }
      });
    });

    list.setOnMouseClicked((e) -> {
      if (e.getClickCount() == 2) {
        musicOrganizer.stop();
        play.setText(GLOBAL.PLAY);
        playButton(list.getSelectionModel().getSelectedIndex());
      }

      if (e.getClickCount() == 1 && play.getText().equals(GLOBAL.PLAY)) {
        setDetailsPane(musicOrganizer.getTrackList().get(list.getSelectionModel().getSelectedIndex()));
      }
    });

    list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      setDetailsPane((Track) newValue);\
    });

    list.setOnKeyPressed((e)->{
      if(e.getCode() == KeyCode.ENTER){
        musicOrganizer.stop();
        play.setText(GLOBAL.PLAY);
        playButton(list.getSelectionModel().getSelectedIndex());
      }
      if(e.getCode() == KeyCode.SPACE)
        playButton(list.getSelectionModel().getSelectedIndex());
    });

    search.setOnKeyPressed((e)->{
      if(e.getCode() == KeyCode.ENTER){
        list.requestFocus();
        list.getSelectionModel().selectFirst();
      }
    });

    search.setOnMouseClicked((e)->{
      if(e.getClickCount() == 2) {
        search.setText(GLOBAL.EMPTY);
        refreshTrackList(mainTrackList);
        resetDetailsPane();
      }

      if (e.getClickCount()==1 && search.getText().equalsIgnoreCase(GLOBAL.SEARCH_PROMPT))
        search.setText(GLOBAL.EMPTY);
    });

    search.textProperty().addListener(new ChangeListener() {
      public void changed(ObservableValue observable, Object oldVal, Object newVal) {
        search((String) newVal);
      }
    });

    window.setOnKeyPressed((e) -> {
      if ( e.getCode() == KeyCode.S || e.getCode() == KeyCode.F && e.isShortcutDown())
        search.requestFocus();
      if(e.getCode() == KeyCode.O && e.isShortcutDown())
        dir.fire();
      if(e.getCode() == KeyCode.R && e.isShortcutDown())
        shuffle();
      if(e.getCode() == KeyCode.RIGHT) {
        next.fire();
      }

    });

  }

  private void playButton(int trackIndex){
    if (!musicOrganizer.getTrackList().isEmpty()) {
      if (play.getText().equals(GLOBAL.PLAY)) {
        play.setText(GLOBAL.PAUSE);
        musicOrganizer.playTrack(trackIndex);
        list.getSelectionModel().select(trackIndex);
      } else {
        play.setText(GLOBAL.PLAY);
        musicOrganizer.stopPlaying();
      }
    }
  }

  private void refreshTrackList(ArrayList<Track> trackList){
    observableList = FXCollections.observableList(trackList);
    list.setItems(observableList);
    list.setCellFactory(new Callback<ListView<Track>, ListCell<Track>>(){

      @Override
      public ListCell<Track> call(ListView<Track> p) {
        ListCell<Track> cell = new ListCell<Track>(){
          @Override
          protected void updateItem(Track t, boolean bln) {
            super.updateItem(t, bln);
            if (t != null) {
              setText(t.getTitle());
            }
          }
        };
        return cell;
      }
    });
  }

  private void setDetailsPane(Track track){
    Platform.runLater(()->{
      try{
        if (musicOrganizer.getTrackList().contains(track)){
          title.setText(track.getTitle());
          artist.setText(track.getArtist());
          path.setText(track.getFilename());
          path.positionCaret(path.getText().length());
        } else
          resetDetailsPane();
      } catch (ArrayIndexOutOfBoundsException ex){
        resetDetailsPane();
      }
    });
  }

  private void resetDetailsPane(){
    title.setText(GLOBAL.EMPTY);
    artist.setText(GLOBAL.EMPTY);
    path.setText(GLOBAL.EMPTY);
  }

  public void search(String newVal) {
    if(!newVal.equals(GLOBAL.EMPTY)) {
      refreshTrackList(mainTrackList);
      ArrayList <Track> searchResutlsList = new ArrayList<>();

      ObservableList<Track> subentries = FXCollections.observableArrayList();
      for(Object entry : list.getItems()){
        Track track = (Track) entry;
        if(track.getTitle().toUpperCase().contains(newVal.toUpperCase())){
          subentries.add(track);
          searchResutlsList.add(track);
        }
      }
      musicOrganizer.setTracks(searchResutlsList);
      musicOrganizer.setCurrentTrackIndex(-1);
      list.getSelectionModel().select(-1);//deselect all
      refreshTrackList(searchResutlsList);
      //resetDetailsPane();
    } else {
      musicOrganizer.setTracks(mainTrackList);
      refreshTrackList(mainTrackList);
    }
  }


  public void setStage(Stage stage) {
    stage.setOnCloseRequest((e)->{
      musicOrganizer.stopPlaying();
    });
  }
}
