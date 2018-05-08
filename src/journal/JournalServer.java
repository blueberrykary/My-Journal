/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package journal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JournalServer extends Thread{
    private SimpleDateFormat df;
    private int port;
    Socket socket;
    ObjectInputStream sInput;
    private boolean kill;
    
    public JournalServer(int port){
        this.port = port;
        this.df = new SimpleDateFormat("HH:mm:ss");
        this.kill = false;
    }
    
    public void run(){
        try{
            JournalEntry entry;
            ServerSocket serverSocket = new ServerSocket(this.port);
            
            System.out.format("Server waiting for connections on port: %d\n", this.port);
            socket = serverSocket.accept();
            sInput = new ObjectInputStream(socket.getInputStream());
                
            while(!kill && !socket.isClosed()){
                
                //Exit if kill
                if(kill)break;
                
                try{
                    entry = (JournalEntry) sInput.readObject();
                } catch(Exception ex){
                    System.err.format(ex + " Error reading journal entry.\n");
                    break;
                }
                
                switch(entry.getAction()){
                    case JournalEntry.SAVE:
                        saveEntry(entry.getName(), entry.getContent(), false);
                        System.out.format("Journal is saving.\n");
                        break;
                    case JournalEntry.SAVEAS:
                        saveAsEntry(entry.getName(), entry.getContent());
                        System.out.format("Journal is savedAs.\n");
                        break;
                    case JournalEntry.QUIT:
                        System.out.format("Journal is closing.\n");
                        kill = true;
                        break;
                }
            }
            
            close();
            
            // Exit server
            try{
                serverSocket.close();
            }catch(Exception ex){
                System.err.format("Closing server crashed: %s\n", ex);
            }
            
        }catch(Exception ex){
            System.err.println(ex);
        }
    }
    
    public void saveAsEntry(String entryName, String entryText) throws IOException{
        saveEntry(entryName, entryText, true);
    }
    
    public void saveEntry(String entryName, String entryText, boolean saveAs) throws IOException{
        if (saveAs){
            // creates the file.
            File file = new File(entryName + ".txt");
            file.createNewFile();
            System.out.format("Journal file has been created.\n");
        }
        
        // Save journal entry to server.
        try(FileWriter fr = new FileWriter(entryName + ".txt", false);
                BufferedWriter bw = new BufferedWriter(fr)){
            bw.write(entryText);
            System.out.format("Journal has been saved.\n");
        }catch(Exception ex){
            
        }
    }
    
    private void close(){
            //close things that are not null
            try{
                if(sInput != null) sInput.close();
                if(socket != null) socket.close();
            }catch (Exception ex){
                System.err.format("Error closing server.");
            }
        }
}
