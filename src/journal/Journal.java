/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package journal;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author spric
 */
public class Journal extends Application {
    
    @Override
    public void start(Stage stage) {
        try{
            stage.setTitle("My Journal");
            stage.setAlwaysOnTop(true);
            GridPane grid = FXMLLoader.load(getClass().getResource("Journal.fxml"));
            stage.setScene(new Scene(grid));
            stage.show();
        } catch (IOException ex){
            Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
