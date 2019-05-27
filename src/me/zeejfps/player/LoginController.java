package me.zeejfps.player;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import me.zeejfps.paw.PandoraClient;
import me.zeejfps.paw.exceptions.AuthenticationException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    Label warningLabel;

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField;

    @FXML
    Pane progressPane;

    private PandoraClient pandoraClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pandoraClient = PandoraPlayer.getInstance().getPandoraClient();
    }

    @FXML
    protected void loginButtonClicked() {

        final String username = usernameField.getText();
        final String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            warningLabel.setText("Please enter a username and a password");
            return;
        }

        Task<Void> loginTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                pandoraClient.login(username, password);
                pandoraClient.syncStations();
                return null;
            }
        };
        loginTask.setOnSucceeded(event -> {
            try {
                PandoraPlayer.getInstance().setScreen(FXMLLoader.load(getClass().getResource("/fxml/PlayerScreen.fxml")));
                PandoraPlayer.getInstance().setTitle("Pandora Player");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loginTask.setOnFailed(event -> {
            if (loginTask.getException() instanceof AuthenticationException) {
                AuthenticationException e = (AuthenticationException) loginTask.getException();
                warningLabel.setText(e.getMessage());
            }
            else {
                warningLabel.setText(loginTask.getException().getMessage());
            }
            progressPane.setVisible(false);
        });
        progressPane.setVisible(true);
        new Thread(loginTask).start();
    }

}
