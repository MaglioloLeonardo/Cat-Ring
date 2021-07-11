package App;

import App.ui.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CatERingApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        /*BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();*/
        try {
            Main controller = new Main();
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("ui/main.fxml"));
            loader.setController(controller);
            Parent root = loader.load();
            primaryStage.setTitle("Cat&Ring");
            primaryStage.setScene(new Scene(root, 1080, 720));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
