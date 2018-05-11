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
import java.util.ArrayList;

public class JournalServer extends Thread{
    private ArrayList<JournalThread> entries;
    private int port;
    private boolean kill;
    
    public JournalServer(int port){
        this.port = port;
        this.kill = false;
    }
    
    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(this.port);
            
            while(!kill){
                System.out.format("Server waiting for connections on port: %d\n", this.port);
                Socket socket = serverSocket.accept();
                
                //Exit if kill
                if(kill)break;
                
                JournalThread tmp = new JournalThread(socket);
                entries.add(tmp);
                tmp.start();
            }
            
            // Exit server
            try{
                serverSocket.close();
                for(int i=0; i< entries.size(); i++){
                    //Kill all clients nicely
                    JournalThread tmp = entries.get(i);
                    tmp.sInput.close();
                    tmp.socket.close();
                }
            }catch(Exception ex){
                System.err.format("Closing server crashed: %s\n", ex);
            }
            
        }catch(Exception ex){
            System.err.println(ex);
        }
    }
    
    // Make one entry per save.
    class JournalThread extends Thread{
        Socket socket;
        ObjectInputStream sInput;
        JournalEntry entry;
        
        private JournalThread(Socket socket) {
            this.socket = socket;
            try{
                sInput = new ObjectInputStream(socket.getInputStream());
            }catch(Exception ex){
                System.err.format(ex + " Error creating streams in journal user.\n");
            }
        }
        
        public void run(){
            boolean kill = false;
            while(!kill){
                try{
                    entry = (JournalEntry) sInput.readObject();
                } catch(Exception ex){
                    System.err.format(ex + " Error reading journal entry.\n");
                    break;
                }
                
                switch(entry.getAction()){
                    case JournalEntry.SAVE:
                        try {
                            saveEntry(entry.getName(), entry.getContent());
                        } catch (IOException ex) {
                            System.out.format("Error saving journal.\n");
                        }
                        System.out.format("Journal is saving.\n");
                        break;
                    case JournalEntry.QUIT:
                        System.out.format("Journal is closing.\n");
                        kill = true;
                        break;
                }
            }
            close();
        }
        
        private void saveEntry(String entryName, String entryText) throws IOException{
            
            File file = new File(entryName + ".txt");
            file.createNewFile();
            System.out.format("Journal file has been created.\n");

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
}
