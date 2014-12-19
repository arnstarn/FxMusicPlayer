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

        //Parent root = FXMLLoader.load(getClass().getResource("/player.fxml"));

        //Above replaced by next 2 lines
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/player.fxml"));
        Parent root = fxmlLoader.load();
        //used to get stage for exit in controller class
        Controller controller = fxmlLoader.getController();
        controller.setStage(primaryStage);

        //back to normal
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/player.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
