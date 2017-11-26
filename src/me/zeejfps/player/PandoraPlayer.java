package me.zeejfps.player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import me.zeejfps.paw.PandoraClient;

public class PandoraPlayer extends Application {

    private static PandoraPlayer instance;

    public static PandoraPlayer getInstance() {
        return instance;
    }

    private PandoraClient pandoraClient;
    private Scene scene;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        instance = this;
        pandoraClient = new PandoraClient();
        Font.loadFont("/fonts/FontAwesome.otf", 32);
        scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml")));
        scene.getStylesheets().add("/css/styles.css");
        stage = primaryStage;
        stage.setTitle("Pandora Player v0.0.1");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    public void setScreen(Parent root) {
        scene.setRoot(root);
        stage.sizeToScene();
    }

    public PandoraClient getPandoraClient() {
        return pandoraClient;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
