/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package journal;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 */
public class JournalController implements Initializable {

    @FXML
    private Tab newEntry;
    
    @FXML
    private TabPane entryPane;
    
    private ObjectOutputStream sOutput;
    private Socket socket;
    private String server, userName, entryText;
    Optional<String> entryName;
    private int port;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        makeConnection();
        System.out.println("Journal online!");
    }   
    
    private void makeConnection(){
        port = 1707;
        // Get current IP Address (rather, one of them).
        try{
            server = InetAddress.getLocalHost().getHostAddress(); //sleipnir.cs.csub.edu";
        }
        catch(UnknownHostException ex){
            System.err.format("Err host unkown./n");
        }
        userName = System.getProperty("user.name");
        
        boolean isConnected = false;
        
        while(!isConnected){
            // Get Object streams
            try {
                socket = new Socket(server, port);
                isConnected = true;
            } catch(Exception ex) {
                System.err.format("Err connecting to server %s on port %d: %s\n", 
                        this.server, this.port, ex);
            }
        }
        
        System.out.println("Connection accepted " + socket.getInetAddress());
    }

    @FXML
    public void handleNewEntry(MouseEvent event) {
        // Fires when the new entry tab is clicked.
        String node = event.getPickResult().getIntersectedNode().toString();
        if (newEntry.isSelected() && node.contains(newEntry.getText())){
            createNewEntry();
        }
    }
    
    public void createNewEntry(){
        // Text popup input for a new journal entry name.
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("");
        dialog.setHeaderText("Name your journal entry.");
        ((Stage) dialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        entryName = dialog.showAndWait();
        
        if(entryName.isPresent()){
            // Initialize the new entry in the entry pane.
            TextArea text = new TextArea();
            text.setId(String.format("%s_text", entryName.get()));
            text.setWrapText(true);
            Tab entry = new Tab(entryName.get(), text);
            entry.setId(String.format("%s_entry", entryName.get()));
            entryPane.getTabs().add(0, entry);
            entryPane.getSelectionModel().select(entry);
        }
    }
    
    public void saveAsEntry(){
        performJournalAction(JournalEntry.SAVEAS);
    }
    
    public void saveEntry(){
        performJournalAction(JournalEntry.SAVE);
    }
    
    private void performJournalAction(int action){
        Node entryContent = entryPane.getSelectionModel().getSelectedItem().getContent();
        entryText = ((TextArea)entryContent).textProperty().getValue();
        
        // if it doesn't start return
        if(!start()) return;
        
        try {
            System.out.format("Sending journal object.\n");
            sOutput.writeObject(new JournalEntry(entryName.get(), entryText, action));
        } catch(Exception ex) {
            System.err.format("Error sending journal object.\n");
        }
    }
    
    public boolean start(){
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sOutput.reset();
        } catch(Exception ex) {
            System.err.format("Err creating streams %s \n", ex);
            return false;
        }
   
        return true;
    }
    
    public void disconnect(){
        try{
            if(sOutput != null) sOutput.close();
            if(socket != null) socket.close();
            System.out.format("Disconnecting from the server.\n");
        }catch(Exception ex){
            System.err.format("Couldn't disconnect.\n");
        }
    }
    
    public void closeApp(){
        // disconnect the server.
        try {
            System.out.format("Disconnecting from the server.\n");
            sOutput.writeObject(new JournalEntry(entryName.get(), "", JournalEntry.QUIT));
        } catch(Exception ex) {
            System.err.format("Error sending journal object.\n");
        }
        
        // disconnect the client.
        disconnect();
        
        // Close app.
        Platform.exit();
    }
}
