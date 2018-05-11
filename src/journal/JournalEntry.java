 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package journal;

import java.io.Serializable;

public class JournalEntry implements Serializable{
    // Types
    static final int SAVE = 0, QUIT = 1;
    private String entryName;
    private String entryContent;
    private int action;
    
    public JournalEntry(String enName, String enContent, int acn){
        this.entryName = enName;
        this.entryContent = enContent;
        this.action = acn;
    }
    
    String getName(){
        return this.entryName;
    }
    
    String getContent(){
        return this.entryContent;
    }
    
    int getAction(){
        return this.action;
    }
}
