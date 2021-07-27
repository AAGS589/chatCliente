package main.model;

import java.util.Observable;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import java.io.*;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadClient extends Observable implements Runnable {
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private TextArea messagesArea;
    private ListView historialMessage;
    private ComboBox listUsers;
    private Label lbUser;

    public ThreadClient(Socket socket, TextArea log, ListView historialMessage, ComboBox listUsers, Label lbUser ) {
        this.socket = socket;
        this.messagesArea = log;
        this.historialMessage = historialMessage;
        this.listUsers = listUsers;
        this.lbUser = lbUser;
    }

    public void run() {

        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            String st = "";

            do {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(1000L)+100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    st = bufferDeEntrada.readUTF();
                    listaDeUsuarios(st);
                    final String sms = st;
                    Platform.runLater(() ->  { historialMessage.getItems().add(sms);  });
                    String[] array = st.split(":");
                    messagesArea.setText(st);
                    this.setChanged();
                    this.notifyObservers(st);
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }while (!st.equals("FIN"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String[] ArrayUsuarios(String st){
        String[] datagrama;
        datagrama = st.split(":");
        String[] usuarios = new String[datagrama.length];
        for(int i=0; i<datagrama.length; i++){
            usuarios[i] = datagrama[i];
        }
        return usuarios;
    }

    public void listaDeUsuarios(String st){
        String[] datagrama;
        datagrama = st.split(":");

        if (datagrama[0].equals("1")) {
            String[] usersConect;
            usersConect = st.split(":",4);
            final String stt = usersConect[3];
            ArrayUsuarios(stt);
            Platform.runLater(() ->  { lbUser.setText(datagrama[2]); });
            Platform.runLater(() ->  { listUsers.getItems().clear();  });
            Platform.runLater(() ->  { listUsers.getItems().addAll(ArrayUsuarios(stt));  });
        }
    }
}
