/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package journal;

import javafx.scene.input.InputEvent;
import java.net.URL;
import java.util.Optional;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author spric
 */
public class JournalController implements Initializable {

    @FXML
    private Tab newEntry;
    
    @FXML
    private TabPane entryPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Journal online!");    
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
        Optional<String> result = dialog.showAndWait();
        
        if(result.isPresent()){
            // Initialize the new entry in the entry pane.
            TextArea text = new TextArea();
            text.setId(String.format("%s_text", result.get()));
            text.setWrapText(true);
            Tab entry = new Tab(result.get(), text);
            entry.setId(String.format("%s_entry", result.get()));
            entryPane.getTabs().add(0, entry);
            entryPane.getSelectionModel().select(entry);
        }
    }
    
    public void saveEntry(){
        Node entryContent = entryPane.getSelectionModel().getSelectedItem().getContent();
        String entryText = ((TextArea)entryContent).textProperty().getValue();
    }
    
    public void closeApp(){
        Platform.exit();
    }
}
