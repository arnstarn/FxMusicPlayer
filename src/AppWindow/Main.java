package AppWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("FXMusicPlayer");
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/player.fxml"));
        //fxmlLoader.setController(new Controller());
        Parent root = FXMLLoader.load(getClass().getResource("/player.fxml"));
        //Parent root = (Parent)fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/player.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
