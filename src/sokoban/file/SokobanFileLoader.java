package sokoban.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import application.Main.SokobanPropertyType;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import properties_manager.PropertiesManager;
import sokoban.game.SokobanGameStateManager;
import sokoban.ui.SokobanUI;

public class SokobanFileLoader {

    private int gridColumns;
    private int gridRows;
    private int grid[][];
    //make method to get grid renderer.
    //in event handler for buttons, write code to load a file for what level, gr will draw file so attach 
    //to the gamepanle and change the sokoban screen to the game pa
    //already changeworkspace method in UI

    public void FileLoader(String fileName, SokobanGameStateManager gsm) { //,

        File fileToOpen = new File(fileName);
        try {
            if (fileToOpen != null) {
                    // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
                // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
                // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
                byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                FileInputStream fis = new FileInputStream(fileToOpen);
                BufferedInputStream bis = new BufferedInputStream(fis);

                // HERE IT IS, THE ONLY READY REQUEST WE NEED
                bis.read(bytes);
                bis.close();

                // NOW WE NEED TO LOAD THE DATA FROM THE BYTE ARRAY
                DataInputStream dis = new DataInputStream(bais);

                    // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
                // ORDER AND FORMAT AS WE SAVED IT
                // FIRST READ THE GRID DIMENSIONS
                int initGridColumns = dis.readInt();
                int initGridRows = dis.readInt();
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                gsm.setGridColumns(initGridColumns);
                gsm.setGridRows(initGridRows);
                    //3 instance vars put in GSM, with getters and setters
                //first just get the north toolbar and switch screen and back button working,
                //then load file into GSM, then grid renderer, then put renderer on mainpane in gamepanel
                //then perform actions on guy 

                int[][] newGrid = new int[initGridColumns][initGridRows];
                gsm.setGrid(newGrid);

                // AND NOW ALL THE CELL VALUES
                for (int i = 0; i < initGridColumns; i++) {
                    for (int j = 0; j < initGridRows; j++) {
                        newGrid[i][j] = dis.readInt();
                        // System.out.println(newGrid[i][j]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String loadTextFile(String textFile) throws IOException {
        // ADD THE PATH TO THE FILE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        textFile = props.getProperty(SokobanPropertyType.DATA_PATH) + textFile;

        // WE'LL ADD ALL THE CONTENTS OF THE TEXT FILE TO THIS STRING
        String textToReturn = "";

        // OPEN A STREAM TO READ THE TEXT FILE
        FileReader fr = new FileReader(textFile);
        BufferedReader reader = new BufferedReader(fr);

        // READ THE FILE, ONE LINE OF TEXT AT A TIME
        String inputLine = reader.readLine();
        while (inputLine != null) {
            // APPEND EACH LINE TO THE STRING
            textToReturn += inputLine + "\n";

            // READ THE NEXT LINE
            inputLine = reader.readLine();
        }

        // RETURN THE TEXT
        return textToReturn;
    }

}
