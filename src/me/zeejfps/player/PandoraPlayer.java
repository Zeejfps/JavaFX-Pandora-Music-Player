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
        scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml")));
        scene.getStylesheets().add("/css/styles.css");
        stage = primaryStage;
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    public void setScreen(Parent root) {
        scene.setRoot(root);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    public PandoraClient getPandoraClient() {
        return pandoraClient;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
