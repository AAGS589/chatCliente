package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.model.ThreadClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


public class Controller implements Observer {

    private Socket socket;
    private DataOutputStream bufferDeSalida = null;

    @FXML
    private TextArea messagesArea;

    @FXML
    private TextField messageArea;

    @FXML
    private TextField IPServer;

    @FXML
    private TextField PORTServer;

    @FXML
    private Button sendMessage;

    @FXML
    private Circle circleLed;

    @FXML
    private Button btnAbrirConexion;

    @FXML
    private Button btnCerrarConexion;

    @FXML
    private ListView historialMessage;

    @FXML
    private ComboBox listUsers;

    @FXML
    private Label lbUser;

    @FXML
    void abrirConexionOnMouseClicked(MouseEvent event) {
        try {
            socket = new Socket(IPServer.getText(), Integer.valueOf(PORTServer.getText()));
            messagesArea.setText( "Creado");
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();

            ThreadClient cliente = new ThreadClient(socket,messagesArea, historialMessage, listUsers,lbUser);
            cliente.addObserver(this);
            new Thread(cliente).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cerarConexionOnMouseClicked(MouseEvent event) {
        try {
            socket.close();
            System.out.println("Cerrando...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void enviarOnMouseClicked(MouseEvent event) {
        try {
            bufferDeSalida.writeUTF("3:" + lbUser.getText() + ":" + listUsers.getSelectionModel().getSelectedItem() + ":" + messageArea.getText());
            bufferDeSalida.writeUTF("1:1");
            bufferDeSalida.flush();
            historialMessage.getItems().add("Tú " + listUsers.getSelectionModel().getSelectedItem() + ": " + messageArea.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void update(Observable o, Object arg) {
        String color = (String) arg;
        switch (color){
            case "1":
                circleLed.setFill(Color.RED);
                break;
            case "2":
                circleLed.setFill(Color.GREEN);
                break;
            case "3":
                circleLed.setFill(Color.BLUE);
                break;
            case "4":
                circleLed.setFill(Color.YELLOW);
                break;
        }
    }

}
