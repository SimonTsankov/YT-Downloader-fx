package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.getIcons().add(new Image("https://www.pikpng.com/pngl/b/180-1804024_caribbean-blue-youtube-4-icon-free-site-youtube.png"));
        primaryStage.setScene(new Scene(root,944, 507));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
