/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.ui;

import javafx.scene.control.Button;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 *
 * @author etroge
 */

public class DisplayTime extends TimerTask{
    public Label lvlButton;
    //change this to string format for time
    
    
    public int seconds = 0;
    public int minutes = seconds/60;
    public int hours = minutes/60;

    
    
    public DisplayTime(Label levelButton){
        lvlButton = levelButton;
    }
    
    public void run(){
        Platform.runLater(()->{
            seconds++;
            lvlButton.setText("Time: " + hours + ":" + minutes + ":" + seconds);
        });
    }
   
       
        //then sleep later 
    }
    
    

