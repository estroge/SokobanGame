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
    //String time = String.format("%2d", seconds);
    //String s = String.format("Duke's Birthday: %1$tm %1$te,%1$tY", c);

    
    
    public DisplayTime(Label levelButton){
        lvlButton = levelButton;
    }
    
    public void run(){
        Platform.runLater(()->{
            seconds++;
            //lvlButton.setText("Time: " + hours + ":" + minutes + ":" + seconds);
            lvlButton.setText(String.format("Time: %02d : %02d : %02d", ((seconds/60)/60)%60, (seconds/60)%60, seconds%60));
        });
    }
   
       
        //then sleep later 
    }
    
    

