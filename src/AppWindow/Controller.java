package AppWindow;

import PlayerComponents.GLOBAL;
import PlayerComponents.MusicOrganizer;
import PlayerComponents.Track;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

  private MusicOrganizer musicOrganizer;
  ObservableList <Track> observableList;

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
  public void clearSearch() {
    if (search.getText().equals("Search..."))
      search.setText("");
  }

  @Override // This method is called by the FXMLLoader when initialization is complete
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

    musicOrganizer = new MusicOrganizer();
    musicOrganizer.readLibrary(GLOBAL.FILEPATH);
    setList();
    list.getSelectionModel().selectFirst();


    GLOBAL.LISTENER = new PlaybackListener() {
      @Override
      public void playbackStarted(PlaybackEvent playbackEvent) {
        super.playbackStarted(playbackEvent);
        System.out.println(playbackEvent.getId() + " START EVENT");
        setDetailsPane(musicOrganizer.getCurrentTrackIndex());
      }
      @Override
      public void playbackFinished(PlaybackEvent playbackEvent) {
        super.playbackFinished(playbackEvent);
        System.out.println(playbackEvent.getId() + " FINISHED EVENT");
        new Thread(()->{
          musicOrganizer.playNextTrack();
          Platform.runLater(()->{
            list.getSelectionModel().select(musicOrganizer.getCurrentTrackIndex());
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
      playButtonMethod(musicOrganizer.getCurrentTrackIndex());
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
          setList();
          list.getSelectionModel().selectFirst();
        }
      });

    });

    list.setOnMouseClicked((e)->{
      if(e.getClickCount() == 2){
        musicOrganizer.stop();
        play.setText(GLOBAL.PLAY);
        playButtonMethod(list.getSelectionModel().getSelectedIndex());
      }

      if(e.getClickCount() == 1 && play.getText().equals(GLOBAL.PLAY)){
        setDetailsPane(list.getSelectionModel().getSelectedIndex());
      }
    });

    search.setOnMouseClicked((e)->{
      if(e.getClickCount() == 2)
        search.setText("");

      if (e.getClickCount()==1 && search.getText().equalsIgnoreCase("Search..."))
        search.setText("");
    });

    search.textProperty().addListener(new ChangeListener() {
      public void changed(ObservableValue observable, Object oldVal, Object newVal) {
        search((String) oldVal, (String) newVal);
      }
    });

  }

  private void playButtonMethod(int trackIndex){
    if (play.getText().equals(GLOBAL.PLAY)){
      play.setText(GLOBAL.PAUSE);
      musicOrganizer.playTrack(trackIndex);
      list.getSelectionModel().select(trackIndex);
    } else {
      play.setText(GLOBAL.PLAY);
      musicOrganizer.stopPlaying();
    }
  }

  private void setList(){
    observableList  = FXCollections.observableList(musicOrganizer.getTracks());
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

  private void setDetailsPane(int index){
    Platform.runLater(()->{
      Track track = musicOrganizer.getTracks().get(index);
      title.setText(track.getTitle());
      artist.setText(track.getArtist());
      path.setText(track.getFilename());
    });
  }

  private void resetDetailsPane(){
    title.setText("");
    artist.setText("");
    path.setText("");
  }

  public void search(String oldVal, String newVal) {
    if(!newVal.equals("")) {
      if (oldVal != null && (newVal.length() < oldVal.length())) {
        //list.setItems(entries); //work on this
      }

      ObservableList<String> subentries = FXCollections.observableArrayList();
      for (Object entry : list.getItems()) {
        boolean match = true;
        String entryText = (String) entry;
        if (!entryText.toUpperCase().contains(newVal.toUpperCase())) {
          match = false;
          break;
        }
        if (match) {
          subentries.add(entryText);
        }
      }
      list.setItems(subentries);//work on this
    }
  }

}
