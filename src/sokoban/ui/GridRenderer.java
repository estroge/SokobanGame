/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 *
 * @author etroge
 */
public class GridRenderer extends Canvas {
    private GraphicsContext gc;
    private int gridColumns;
    private int gridRows;
    private int grid[][];

        // PIXEL DIMENSIONS OF EACH CELL
        int cellWidth;
        int cellHeight;

        // images
        Image wallImage = new Image("file:images/wall.png");
        Image boxImage = new Image("file:images/box.png");
        Image placeImage = new Image("file:images/place.png");
        Image sokobanImage = new Image("file:images/Sokoban.png");
        
        public SokobanUI ui;
        /**
         * Default constructor.
         */
        public GridRenderer(SokobanUI ui) {
            this.setWidth(700);
            this.setHeight(700);
            this.ui = ui;
            //repaint();
        }

        public void repaint(int gridColumns, int gridRows, int[][] grid) {
            System.out.println("doing nothing for the repaint");
        }
        public void repaint(int gridColumns, int gridRows, int[][] grid, boolean b) {
            gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());

            // CALCULATE THE GRID CELL DIMENSIONS
            double w = this.getWidth() / gridColumns; 
            double h = this.getHeight() / gridRows;

            //in ui you make gridcols and grid rows, in this class you get those via
            // ui.getgrid() (you have to make this class)
            gc = this.getGraphicsContext2D();

            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            for (int i = 0; i < gridColumns; i++) {
                y = 0;
                for (int j = 0; j < gridRows; j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.LIGHTBLUE);
                    //gc.strokeRoundRect(x, y, w, h, 10, 10);

                    switch (grid[i][j]) {
                        case 0:
                            //gc.strokeRoundRect(x, y, w, h, 10, 10);
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
                            gc.drawImage(boxImage, x, y, w, h);
                            break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                        case 4:
                            gc.drawImage(sokobanImage, x, y, w, h);
                            break;
                    }

                    // THEN RENDER THE TEXT
                    String numToDraw = "" + grid[i][j];
                    double xInc = (w / 2) - (10 / 2);
                    double yInc = (h / 2) + (10 / 4);
                    x += xInc;
                    y += yInc;
                    gc.setFill(Color.RED);
                  //  gc.fillText(numToDraw, x, y);
                    x -= xInc;
                    y -= yInc;

                    // ON TO THE NEXT ROW
                    y += h;
                }
                // ON TO THE NEXT COLUMN
                x += w;
            }
        }
        
        public static boolean initShiftingVariables = false;
        public static double shifterx = 0;
        public static double shiftery = 0;
        public static double offsetY;
        public static double offsetX;
        public void repaint() {
            gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());

            // CALCULATE THE GRID CELL DIMENSIONS
            
            double w = this.getWidth() / ui.getGSM().getGrid().length; 
            double h = this.getHeight() / ui.getGSM().getGrid().length;
            h = w;
            if(initShiftingVariables){
                System.out.println("initilizing variables ");
                shifterx = h/30;
                shiftery = h/30;
                offsetX = h;
                offsetY = h;
                
            }
            //in ui you make gridcols and grid rows, in this class you get those via
            // ui.getgrid() (you have to make this class)
            gc = this.getGraphicsContext2D();

            int [][] grid = ui.getGSM().getGrid();
            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            for (int i = 0; i < grid.length; i++) {
                y = 0;
                for (int j = 0; j < grid[0].length; j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.LIGHTBLUE);
                    //gc.strokeRoundRect(x, y, w, h, 10, 10);

                    switch (grid[i][j]) {
                        case 0:
                            //gc.strokeRoundRect(x, y, w, h, 10, 10);
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
//                            if(ui.isMoving[i][j] != 0){
////                                System.out.println("update for player moving");
//                                int var = ui.isMoving[i][j];
//                                
//                                
//                                switch(var){
//                                    case 1: // up
//                                        if(initShiftingVariables){
//                                        shifterx = 0;
//                                        shiftery *= -1;
//                                        offsetX=0;
//                                        initShiftingVariables = false;
//                                        }
//                                        
////                                        gc.drawImage(placeImage, x, y, w, h);
////                                        gc.drawImage(placeImage, x, y+h, w, h);
//                                        break;
//                                    case 3: // down
//                                        if(initShiftingVariables){
//                                        shifterx = 0;
//                                        offsetY *= -1;
//                                        offsetX=0;
//                                        initShiftingVariables = false;
//                                        }
////                                        gc.drawImage(placeImage, x, y, w, h);
////                                        gc.drawImage(placeImage, x, y-h, w, h);
//                                        break;
//                                    case 2: // left  
//                                        if(initShiftingVariables){
//                                        shiftery = 0;
//                                        shifterx *= -1;
//                                        offsetY = 0;
//                                        initShiftingVariables = false;
//                                        }
//                                        
//                                        
////                                        gc.drawImage(placeImage, x, y, w, h);
////                                        gc.drawImage(placeImage, x+h, y, w, h);
//                                        break;
//                                    case 4: // right
//                                        if(initShiftingVariables){
//                                        shiftery = 0;
//                                        offsetY = 0;
//                                        offsetX *= -1;
//                                        initShiftingVariables = false;
//                                        }
////                                        gc.drawImage(placeImage, x, y, w, h);
////                                        gc.drawImage(placeImage, x-h, y, w, h);
//                                        break;
//                                        
//                                }
//                                offsetX += shifterx;
//                                offsetY += shiftery;
//                                if(Math.abs(offsetY) < 0.001 && Math.abs(offsetX) < 0.001){
//                                    ui.isMoving[i][j] = 0;
//                                    initShiftingVariables = true;
//                                }
//                                    System.out.println("shifting (X,Y) --> (" + offsetX + ", " + offsetY + ")\n"
//                                            + "\tshifterx: " + shifterx + " \tshiftery: " + shiftery);
//                                
////                                gc.drawImage(placeImage, x, y, w, h);
//                                gc.drawImage(boxImage, x + offsetX, y + offsetY, w, h);
//                                // come back to this 
//                                
////                                if(h - )
//                                
//                            }else{
//                                System.out.println("in the else statement for some fucking reason ");
//                                gc.drawImage(boxImage, x, y, w, h);
//                            }
                            gc.drawImage(boxImage, x, y, w, h);
                        break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                         case 4:
                            if(ui.isMoving[i][j] != 0){
//                                System.out.println("update for player moving");
                                int var = ui.isMoving[i][j];
                                
                                
                                switch(var){
                                    case 1: // up
                                        if(initShiftingVariables){
                                        shifterx = 0;
                                        shiftery *= -1;
                                        offsetX=0;
                                        initShiftingVariables = false;
                                        }
                                        
//                                        gc.drawImage(placeImage, x, y, w, h);
//                                        gc.drawImage(placeImage, x, y+h, w, h);
                                        break;
                                    case 3: // down
                                        if(initShiftingVariables){
                                        shifterx = 0;
                                        offsetY *= -1;
                                        offsetX=0;
                                        initShiftingVariables = false;
                                        }
//                                        gc.drawImage(placeImage, x, y, w, h);
//                                        gc.drawImage(placeImage, x, y-h, w, h);
                                        break;
                                    case 2: // left  
                                        if(initShiftingVariables){
                                        shiftery = 0;
                                        shifterx *= -1;
                                        offsetY = 0;
                                        initShiftingVariables = false;
                                        }
                                        
                                        
//                                        gc.drawImage(placeImage, x, y, w, h);
//                                        gc.drawImage(placeImage, x+h, y, w, h);
                                        break;
                                    case 4: // right
                                        if(initShiftingVariables){
                                        shiftery = 0;
                                        offsetY = 0;
                                        offsetX *= -1;
                                        initShiftingVariables = false;
                                        }
//                                        gc.drawImage(placeImage, x, y, w, h);
//                                        gc.drawImage(placeImage, x-h, y, w, h);
                                        break;
                                        
                                }
                                offsetX += shifterx;
                                offsetY += shiftery;
                                if(Math.abs(offsetY) < 0.001 && Math.abs(offsetX) < 0.001){
                                    ui.isMoving[i][j] = 0;
                                    initShiftingVariables = true;
                                }
                                    System.out.println("shifting (X,Y) --> (" + offsetX + ", " + offsetY + ")\n"
                                            + "\tshifterx: " + shifterx + " \tshiftery: " + shiftery);
                                
//                                gc.drawImage(placeImage, x, y, w, h);
                                gc.drawImage(sokobanImage, x + offsetX, y + offsetY, w, h);
                                // come back to this 
                                
//                                if(h - )
                                
                            }else{
                                System.out.println("in the else statement for some fucking reason ");
                                gc.drawImage(sokobanImage, x, y, w, h);
                            }
                            break;
                    }

                    // THEN RENDER THE TEXT
                    String numToDraw = "" + grid[i][j];
                    double xInc = (w / 2) - (10 / 2);
                    double yInc = (h / 2) + (10 / 4);
                    x += xInc;
                    y += yInc;
                    gc.setFill(Color.RED);
                  //  gc.fillText(numToDraw, x, y);
                    x -= xInc;
                    y -= yInc;

                    // ON TO THE NEXT ROW
                    y += h;
                }
                // ON TO THE NEXT COLUMN
                x += w;
            }
        }
        
        

    }
