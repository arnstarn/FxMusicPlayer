package PlayerComponents;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {

        launch(args);

/*        String track_path = "/Users/Arnstarn/Desktop/Little_Waltz.mp3";
        MusicPlayer mp = new MusicPlayer();

        File track = new File(track_path);
        System.out.println("Starting Track " + track_path);
        //mp.playSample(track_path);
        mp.startPlaying(track_path);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Track " + track_path);
        mp.stop();
        System.out.println("Finished Track " + track_path);

        MP3 player = new MP3(track_path);
        player.play();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Stopping Track " + track_path);
        player.close();
        System.out.println("Finished Track " + track_path);*/

    }

  @Override
  public void start(Stage mainStage) {
    mainStage = new AppWindow();
    mainStage.show();

  }

}
