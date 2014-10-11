package sokoban.ui;

import application.Main;
import application.Main.SokobanPropertyType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameData;
import sokoban.game.SokobanGameStateManager;
import application.Main.SokobanPropertyType;
import java.net.MalformedURLException;
import javafx.scene.media.AudioClip;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import properties_manager.PropertiesManager;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.Action;
import javax.swing.JScrollPane;

public class SokobanUI extends Pane {

    /**
     * The SokobanUIState represents the four screen states that are possible
     * for the Sokoban game application. Depending on which state is in current
     * use, different controls will be visible.
     */
    public enum SokobanUIState {

        SPLASH_SCREEN_STATE, PLAY_GAME_STATE, VIEW_STATS_STATE, VIEW_HELP_STATE,
        HANG1_STATE, HANG2_STATE, HANG3_STATE, HANG4_STATE, HANG5_STATE, HANG6_STATE,
    }

    // mainStage
    private Stage primaryStage;

    // mainPane
    private BorderPane mainPane;
    private BorderPane hmPane;

    // SplashScreen
    private ImageView splashScreenImageView;
    private Pane splashScreenPane;
    private Label splashScreenImageLabel;
    private HBox levelSelectionPane;
    private ArrayList<Button> levelButtons;

    // NorthToolBar
    private HBox northToolbar;
    private Button gameButton;
    private Button statsButton;
    private Button helpButton;
    private Button exitButton;

    // GamePane
    private Label SokobanLabel;
    private Button newGameButton;
    private HBox letterButtonsPane;
    private HashMap<Character, Button> letterButtons;
    private BorderPane gamePanel = new BorderPane();

    //StatsPane
    private ScrollPane statsScrollPane;
    private JEditorPane statsPane;

    //HelpPane
    private BorderPane helpPanel;
    private JScrollPane helpScrollPane;
    private JEditorPane helpPane;
    private Button homeButton;
    private Pane workspace;

    // Padding
    private Insets marginlessInsets;

    // Image path
    private String ImgPath = "file:images/";

    // mainPane weight && height
    private int paneWidth;
    private int paneHeigth;

    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private SokobanEventHandler eventHandler;
    private SokobanErrorHandler errorHandler;
    private SokobanDocumentManager docManager;
    
    public GridRenderer gridRenderer;// = new GridRenderer();
    SokobanGameStateManager gsm;
    private boolean canMove;
    ArrayList<List<Integer>> redPointsInLevel = new ArrayList<>();
    ArrayList<List<Integer>> sokobanPosition = new ArrayList<>();
    ArrayList<List<Integer>> boxesInLevel = new ArrayList<>();
    public Timer timer;
    ArrayList<int[][]> historyOfGrids = new ArrayList<>();
    public int isMoving[][];
    
    //audio files
    AudioClip winClip = new AudioClip("file:data/win.wav");
    AudioClip loseClip = new AudioClip("file:data/lose.wav");
    AudioClip mainClip = new AudioClip("file:data/themeSong.mp3");
    AudioClip moveClip = new AudioClip("file:data/step.wav");
    AudioClip wallClip = new AudioClip("file:data/wall.wav");
    
    TimerTask timertask;
    private String endOfGameTime;

    public SokobanUI() {
        gridRenderer = new GridRenderer(this);
        
        gsm = new SokobanGameStateManager(this); //only can be made once.
        eventHandler = new SokobanEventHandler(this);
        errorHandler = new SokobanErrorHandler(primaryStage);
        docManager = new SokobanDocumentManager(this);
        initMainPane();
        initSplashScreen();
        //initSokobanUI(); //added this 
        
    }

    public void SetStage(Stage stage) {
        primaryStage = stage;
    }

    public BorderPane GetMainPane() {
        return this.mainPane;
    }

    public SokobanGameStateManager getGSM() {
        return gsm;
    }

    public SokobanDocumentManager getDocManager() {
        return docManager;
    }

    public SokobanErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public JEditorPane getHelpPane() {
        return helpPane;
    }

    public void initMainPane() {
        marginlessInsets = new Insets(5, 5, 5, 5);
        mainPane = new BorderPane();

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        paneWidth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_HEIGHT));
        mainPane.resize(paneWidth, paneHeigth);
        mainPane.setPadding(marginlessInsets);

        //have keep track of red points right in the beginning of the game
        //so win when all boxes are over red points
        //method runs once to find all red points
        //when guy moves off red point resets to red points
        //box can overwrite red points to win all red points, must have boxes
        //if box, have to check beyond box
        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                //System.out.println("Key Pressed: " + ke.getCode());

                if(!gsm.isGameInProgress())
                    return;
                //if statement to repaint red point if guy went over it
                boolean isOverRedPoint = false;
                for (int k = 0; k < redPointsInLevel.size(); k++) {
                    if (sokobanPosition.get(0).equals(redPointsInLevel.get(k))) {
                        isOverRedPoint = true;
                    }
                }
                boxesInLevel.clear();
                findAllBoxes();
                
                //System.out.println("over red pt: " + isOverRedPoint);
                System.out.println("sok loc:" + sokobanPosition);
                System.out.println("red loc:" + redPointsInLevel);

                if (ke.getCode().equals(KeyCode.U)) {
                    undoEachMove();
                }
                
                boolean nextIsBox = false;
                outerloop:
                if (ke.getCode().equals(KeyCode.UP)) {
                    //add current grid to the arraylist, when need to undo
                    int[][] temp = new int[gsm.getGridColumns()][gsm.getGridRows()];
                    //add current grid to the arraylist, when need to undo
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            temp[i][j] = gsm.getGrid()[i][j];
                        }
                    }
                    historyOfGrids.add(temp);
                    
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                moveClip.play();
                                moveClip.setCycleCount(2);
                                //gsm.getGrid()[i][j] = 0;
                                nextIsBox = checkUp(gsm.getGrid(), i, j);
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i, j - 1));
                                    if (isOverRedPoint == true) {
                                        isMoving[i][j-1] = 1;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i][j - 1] = 4;
                                    } else {
                                        isMoving[i][j-1] = 1;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j - 1] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i, j - 1));
                                    if (isOverRedPoint == true) {
                                        isMoving[i][j-1] = 1;
                                        isMoving[i][j-2] = 1;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i][j - 2] = 2;
                                        gsm.getGrid()[i][j - 1] = 4;
                                    } else {
                                        isMoving[i][j-1] = 1;
                                        isMoving[i][j-2] = 1;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j - 2] = 2;
                                        gsm.getGrid()[i][j - 1] = 4;
                                    }
                                }
                                if(canMove == false){
                                    wallClip.play();
                                }

                                //make boolean class var, switch statement, if true, check case, 2 cases
                                //make second copy of grid, everytime he moves compare with old grid, to repaint red dots
                                System.out.println("sok loc:" + sokobanPosition);
                                System.out.println("red loc:" + redPointsInLevel);
                                gridRenderer.repaint(gsm.getGridColumns(), gsm.getGridRows(), gsm.getGrid());
                                break outerloop;
                            }
                        }
                    }
                    //go through grid find 4 the guy and then move him in the direction
                    //store guys pos as var to know here he is at all time
                    //nested for loop, find guy, sotre in var, method to check everything around
                    //cases wall box red point empty 0-4, box has sub cases, if pushing box into box or box into wall
                    //change value in grid 
                    //then call grid rend to repaint
                }
                if (ke.getCode().equals(KeyCode.DOWN)) {
                    //add current grid to the arraylist, when need to undo
                    int[][] temp = new int[gsm.getGridColumns()][gsm.getGridRows()];
                    //add current grid to the arraylist, when need to undo
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            temp[i][j] = gsm.getGrid()[i][j];
                        }
                    }
                    historyOfGrids.add(temp);
                    
                    outerloop:
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                moveClip.play();
                                moveClip.setCycleCount(2);
                                nextIsBox = checkDown(gsm.getGrid(), i, j);
                                //gsm.getGrid()[i][j] = 0;
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i, j + 1));
                                    if (isOverRedPoint) {
                                        isMoving[i][j+1] = 3;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i][j + 1] = 4;
                                    } else {
                                        isMoving[i][j+1] = 3;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j + 1] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i, j + 1));
                                    if (isOverRedPoint) {
                                        isMoving[i][j+1] = 3;
                                        isMoving[i][j+2] = 3;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i][j + 2] = 2;
                                        gsm.getGrid()[i][j + 1] = 4;
                                    } else {
                                        isMoving[i][j+1] = 3;
                                        isMoving[i][j+2] = 3;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j + 2] = 2;
                                        gsm.getGrid()[i][j + 1] = 4;
                                    }
                                }
                                if(canMove == false){
                                    wallClip.play();
                                }
                                //System.out.println(i + " " + j);
                                System.out.println("sok loc:" + sokobanPosition);
                                System.out.println("red loc:" + redPointsInLevel);
                                gridRenderer.repaint(gsm.getGridColumns(), gsm.getGridRows(), gsm.getGrid());
                                break outerloop;
                            }
                        }
                    }
                }
                if (ke.getCode().equals(KeyCode.LEFT)) {
                    int[][] temp = new int[gsm.getGridColumns()][gsm.getGridRows()];
                    //add current grid to the arraylist, when need to undo
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            temp[i][j] = gsm.getGrid()[i][j];
                        }
                    }
                    historyOfGrids.add(temp);
                    //add current grid to the arraylist, when need to undo
                 
                    
                    outerloop:
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                moveClip.play();
                                moveClip.setCycleCount(2);

                                nextIsBox = checkLeft(gsm.getGrid(), i, j);
                                //gsm.getGrid()[i][j] = 0;
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i - 1, j));
                                    if (isOverRedPoint) {
                                        isMoving[i-1][j] = 2;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i - 1][j] = 4;
                                    } else {
                                        isMoving[i-1][j] = 2;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i - 1][j] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i - 1, j));
                                    if (isOverRedPoint) {
                                        isMoving[i-1][j] = 2;
                                        isMoving[i-2][j] = 2;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i - 2][j] = 2;
                                        gsm.getGrid()[i - 1][j] = 4;
                                    } else {
                                        isMoving[i-1][j] = 2;
                                        isMoving[i-2][j] = 2;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i - 2][j] = 2;
                                        gsm.getGrid()[i - 1][j] = 4;
                                    }
                                }
                                if(canMove == false){
                                    wallClip.play();
                                }
                                //System.out.println(i + " " + j);
                                System.out.println("sok loc:" + sokobanPosition);
                                System.out.println("red loc:" + redPointsInLevel);
                                gridRenderer.repaint(gsm.getGridColumns(), gsm.getGridRows(), gsm.getGrid());
                                break outerloop;
                            }
                        }
                    }
                }
                if (ke.getCode().equals(KeyCode.RIGHT)) {
                    int[][] temp = new int[gsm.getGridColumns()][gsm.getGridRows()];
                    //add current grid to the arraylist, when need to undo
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            temp[i][j] = gsm.getGrid()[i][j];
                        }
                    }
                    historyOfGrids.add(temp);
                    System.out.println("grid in right key" + temp);
                    
                    outerloop:
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                moveClip.play();
                                moveClip.setCycleCount(2);
                                nextIsBox = checkRight(gsm.getGrid(), i, j);
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i + 1, j));
                                    if (isOverRedPoint) {
                                        isMoving[i+1][j] = 4;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i + 1][j] = 4;
                                    } else {
                                        isMoving[i+1][j] = 4;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i + 1][j] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i + 1, j));
                                    if (isOverRedPoint) {
                                        isMoving[i+1][j] = 4;
                                        isMoving[i+2][j] = 4;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i + 2][j] = 2;
                                        gsm.getGrid()[i + 1][j] = 4;
                                    } else {
                                        isMoving[i+1][j] = 4;
                                        isMoving[i+2][j] = 4;
                                        gridRenderer.initShiftingVariables = true;
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i + 2][j] = 2;
                                        gsm.getGrid()[i + 1][j] = 4;
                                    }
                                }
                                if(canMove == false){
                                    wallClip.play();
                                }
                                //System.out.println(i + " " + j);
                                gridRenderer.repaint(gsm.getGridColumns(), gsm.getGridRows(), gsm.getGrid());
                                break outerloop;
                            }
                        }
                    }
                }

                //CHECK IF WON
                //now see if all boxes are on all red points
                //make an arraylist of boxes moving as well? how to undo?
                //loop, if all red points in array have boxes on top the win dialog should pop up
//                int redPointsCounter = 0;
//                for (int t = 0; t < redPointsInLevel.size(); t++) {
//                    if (gsm.getGrid()[redPointsInLevel.get(t).get(0)][redPointsInLevel.get(t).get(1)] == 2) {
//                        redPointsCounter++;
//                    }
//                }
//                if (redPointsCounter == redPointsInLevel.size()) {
//                    //POP BOX TO WIN MESSAGE
//                    System.out.println("YOU WIN!");
//                    gsm.wins[gsm.getLevelState()]++;
//                    // TODO : also check if the current time is faster than the fastest time and chenge it if it is
//                    //set game is over
//
//                    
//                    //MAKE WINNING SOUND!
//                    winClip.play();
//                    historyOfGrids.clear();
//                    //initaudio load all audio files, instance vars up top
//                    //call stop, has to be an instance var 
//
//                    endOfGameTime = timertask.toString();
//                    System.out.println("timer: " + endOfGameTime);
//                    timer.cancel();
//                    timer.purge();
//
//                    //check if fastest
//                    
//                    //check if fastest
//                    for(int i = 1; i < gsm.fastest_win.length; i++){
//                        if(gsm.fastest_win[i] == null){
//                            gsm.fastest_win[i] = endOfGameTime;
//                        }
//                        if(endOfGameTime.compareTo(gsm.fastest_win[i]) == 1){
//                            gsm.fastest_win[i] = endOfGameTime;
//                        }
//                    }
//                    
//                    docManager.addGameResultToStatsPage(gsm.getGameInProgress());
//
//                    gsm.setGameState(SokobanGameStateManager.SokobanGameState.GAME_OVER);
//                    respondToWin(primaryStage);
//                    
//
//                }
//
//                //CHECK IF LOST
//                //have to check for everybox not just one
//                //for each box in the grid if condition true 
////                for (int t = 0; t < boxesInLevel.size(); t++) {
////                    boolean up = false, down = false, left = false, right = false;
////
////                    if (gsm.getGrid()[boxesInLevel.get(t).get(0)][boxesInLevel.get(t).get(1) - 1] != 1) {
////                        up = true;
////                    }
////                    if (gsm.getGrid()[boxesInLevel.get(t).get(0)][boxesInLevel.get(t).get(1) + 1] != 1) {
////                        down = true;
////                    }
////                    if (gsm.getGrid()[boxesInLevel.get(t).get(0) - 1][boxesInLevel.get(t).get(1)] != 1) {
////                        left = true;
////                    }
////                    if (gsm.getGrid()[boxesInLevel.get(t).get(0) + 1][boxesInLevel.get(t).get(1) - 1] != 1) {
////                        right = true;
////                    }
////                    boolean isRedPoint = false;
////                    for (int y = 0; y < redPointsInLevel.size(); y++) {
////                        if (gsm.getGrid()[redPointsInLevel.get(y).get(0)][redPointsInLevel.get(y).get(1)]
////                                == gsm.getGrid()[boxesInLevel.get(t).get(0)][boxesInLevel.get(t).get(1)]) {
////                            isRedPoint = true;
////                        }
////                    }
////                    if (((up == false && left == false) || (down == false && right == false)
////                            || (up == false && right == false) || (down == false && left == false))
////                            //and not red point
////                            && isRedPoint == false) {
////                        System.out.println("YOU LOSE!");
////                        gsm.setGameState(SokobanGameStateManager.SokobanGameState.GAME_OVER);
////                        respondToLoss(primaryStage);
////
////                    }
////                }
//                //TODO: CHECK IF LOST
//                for (int i = 0; i < gsm.getGridColumns(); i++) {
//                    for (int j = 0; j < gsm.getGridRows(); j++) {
//                        if (gsm.getGrid()[i][j] == 2) {
//                            //System.out.println("box pos:" + gsm.getGrid()[i][j]);
//
//                            //boolean up = checkUp(gsm.getGrid(), i, j);
//                            boolean up = false, down = false, left = false, right = false;
//
//                            if (gsm.getGrid()[i][j - 1] != 1) {
//                                up = true;
//                            }
//                            if (gsm.getGrid()[i][j + 1] != 1) {
//                                down = true;
//                            }
//                            if (gsm.getGrid()[i - 1][j] != 1) {
//                                left = true;
//                            }
//                            if (gsm.getGrid()[i + 1][j] != 1) {
//                                right = true;
//                            }
//
//                            boolean isRedPoint = false;
//                            for (int t = 0; t < redPointsInLevel.size(); t++) {
//                                if (gsm.getGrid()[redPointsInLevel.get(t).get(0)][redPointsInLevel.get(t).get(1)]
//                                        == gsm.getGrid()[i][j]) {
//                                    isRedPoint = true;
//                                }
//                            }
//
//                            if (((up == false && left == false) || (down == false && right == false)
//                                    || (up == false && right == false) || (down == false && left == false))
//                                    //and not red point
//                                    && isRedPoint == false) {
//                                System.out.println("YOU LOSE!");
//                                gsm.setGameState(SokobanGameStateManager.SokobanGameState.GAME_OVER);
//                                historyOfGrids.clear();
//                                loseClip.play(); 
//                                respondToLoss(primaryStage);
//
//                            }
//                        }
//                    }
//                }
                
            }
            



            
        });
    }
    
    public void respondToLoss(Stage primaryStage) {
                // ENGLIS IS THE DEFAULT
                String options[] = new String[]{"Yes", "No"};
                PropertiesManager props = PropertiesManager.getPropertiesManager();
                options[0] = props.getProperty(SokobanPropertyType.DEFAULT_YES_TEXT);
                options[1] = props.getProperty(SokobanPropertyType.DEFAULT_NO_TEXT);
                //String verifyExit = props.getProperty(SokobanPropertyType.WIN_DISPLAY_TEXT);

                String winImagePath = props
                        .getProperty(SokobanPropertyType.LOST_IMAGE_NAME);
                props.addProperty(SokobanPropertyType.INSETS, "5");
                //String str = props.getProperty(SokobanPropertyType.INSETS);

                Image winImage = loadImage(winImagePath);
                Label winLabel = new Label("YOU LOST! Go back to splash screen?");
                winLabel.setGraphic(new ImageView(winImage));

                // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                BorderPane exitPane = new BorderPane();
                HBox optionPane = new HBox();
                Button yesButton = new Button(options[0]);
                Button noButton = new Button(options[1]);
                optionPane.setSpacing(25.0);
                optionPane.setAlignment(Pos.CENTER);
                optionPane.getChildren().addAll(yesButton, noButton);
                //Label exitLabel = new Label(verifyExit);
                exitPane.setCenter(winLabel);

                exitPane.setBottom(optionPane);
                Scene scene = new Scene(exitPane, 420, 200);
                dialogStage.setScene(scene);
                dialogStage.show();
                // WHAT'S THE USER'S DECISION?
                yesButton.setOnAction(e -> {
                    //CHANGE TO GO BACK TO SPLASH SCRREN
                    changeWorkspace(SokobanUIState.SPLASH_SCREEN_STATE);
                    dialogStage.close();
                });
                noButton.setOnAction(e -> {
                    dialogStage.close();
                });

            }

            public void respondToWin(Stage primaryStage) {
                // ENGLIS IS THE DEFAULT
                String options[] = new String[]{"Yes", "No"};
                PropertiesManager props = PropertiesManager.getPropertiesManager();
                options[0] = props.getProperty(SokobanPropertyType.DEFAULT_YES_TEXT);
                options[1] = props.getProperty(SokobanPropertyType.DEFAULT_NO_TEXT);
                //String verifyExit = props.getProperty(SokobanPropertyType.WIN_DISPLAY_TEXT);

                String winImagePath = props
                        .getProperty(SokobanPropertyType.WIN_IMAGE_NAME);
                props.addProperty(SokobanPropertyType.INSETS, "5");
                //String str = props.getProperty(SokobanPropertyType.INSETS);

                Image winImage = loadImage(winImagePath);
                Label winLabel = new Label("YOU WON! Go back to splash screen?");
                winLabel.setGraphic(new ImageView(winImage));

                // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                BorderPane exitPane = new BorderPane();
                HBox optionPane = new HBox();
                Button yesButton = new Button(options[0]);
                Button noButton = new Button(options[1]);
                optionPane.setSpacing(25.0);
                optionPane.setAlignment(Pos.CENTER);
                optionPane.getChildren().addAll(yesButton, noButton);
                //Label exitLabel = new Label(verifyExit);
                exitPane.setCenter(winLabel);

                exitPane.setBottom(optionPane);
                Scene scene = new Scene(exitPane, 420, 200);
                dialogStage.setScene(scene);
                dialogStage.show();
                // WHAT'S THE USER'S DECISION?
                yesButton.setOnAction(e -> {
                    //CHANGE TO GO BACK TO SPLASH SCRREN
                    changeWorkspace(SokobanUIState.SPLASH_SCREEN_STATE);
                    dialogStage.close();
                });
                noButton.setOnAction(e -> {
                    dialogStage.close();
                });

            }

            //make flag
            //wall first, then check box (j-2), if 0 can move, if 1 cannot
            //else move 
            //boolean returns true if can move and is a box
            //canMove says if player can move to next position
            private boolean checkUp(int grid[][], int i, int j) {
                //if theres a wall
                if (grid[i][j - 1] == 1) {
                    canMove = false;
                    return false;
                } //if box behind box
                else if (grid[i][j - 1] == 2 && grid[i][j - 2] == 2) {
                    canMove = false;
                    return false;
                } //if wall behind box
                else if (grid[i][j - 1] == 2 && grid[i][j - 2] == 1) {
                    canMove = false;
                    return false;
                } //if next square is empty and not a box
                else if (grid[i][j - 1] == 0) {
                    canMove = true;
                    return false;
                } //if just box
                else if (grid[i][j - 1] == 2) {
                    canMove = true;
                    return true;
                } //any other condition
                else {
                    return false;
                }
            }

            private boolean checkDown(int[][] grid, int i, int j) {
                //if theres a wall
                if (grid[i][j + 1] == 1) {
                    canMove = false;
                    return false;
                } //if box behind box
                else if (grid[i][j + 1] == 2 && grid[i][j + 2] == 2) {
                    canMove = false;
                    return false;
                } //if wall behind box
                else if (grid[i][j + 1] == 2 && grid[i][j + 2] == 1) {
                    canMove = false;
                    return false;
                } //if next square is empty and not a box
                else if (grid[i][j + 1] == 0) {
                    canMove = true;
                    return false;
                } //if just box
                else if (grid[i][j + 1] == 2) {
                    canMove = true;
                    return true;
                } //any other condition
                else {
                    return false;
                }
            }

            private boolean checkLeft(int[][] grid, int i, int j) {
                //if theres a wall
                if (grid[i - 1][j] == 1) {
                    canMove = false;
                    return false;
                } //if box behind box
                else if (grid[i - 1][j] == 2 && grid[i - 2][j] == 2) {
                    canMove = false;
                    return false;
                } //if wall behind box
                else if (grid[i - 1][j] == 2 && grid[i - 2][j] == 1) {
                    canMove = false;
                    return false;
                } //if next square is empty and not a box
                else if (grid[i - 1][j] == 0) {
                    canMove = true;
                    return false;
                } //if just box
                else if (grid[i - 1][j] == 2) {
                    canMove = true;
                    return true;
                } //any other condition
                else {
                    return false;
                }
            }

            private boolean checkRight(int[][] grid, int i, int j) {
                //if theres a wall
                if (grid[i + 1][j] == 1) {
                    canMove = false;
                    return false;
                } //if box behind box
                else if (grid[i + 1][j] == 2 && grid[i + 2][j] == 2) {
                    canMove = false;
                    return false;
                } //if wall behind box
                else if (grid[i + 1][j] == 2 && grid[i + 2][j] == 1) {
                    canMove = false;
                    return false;
                } //if next square is empty and not a box
                else if (grid[i + 1][j] == 0) {
                    canMove = true;
                    return false;
                } //if just box
                else if (grid[i + 1][j] == 2) {
                    canMove = true;
                    return true;
                } //any other condition
                else {
                    return false;
                }
            }
    
    public void checkForWin(){
    //CHECK IF WON
                //now see if all boxes are on all red points
                //make an arraylist of boxes moving as well? how to undo?
                //loop, if all red points in array have boxes on top the win dialog should pop up
                int redPointsCounter = 0;
                for (int t = 0; t < redPointsInLevel.size(); t++) {
                    if (gsm.getGrid()[redPointsInLevel.get(t).get(0)][redPointsInLevel.get(t).get(1)] == 2) {
                        redPointsCounter++;
                    }
                }
                if (redPointsCounter == redPointsInLevel.size()) {
                    //POP BOX TO WIN MESSAGE
                    System.out.println("YOU WIN!");
                    gsm.wins[gsm.getLevelState()]++;
                    // TODO : also check if the current time is faster than the fastest time and chenge it if it is
                    //set game is over

                    
                    //MAKE WINNING SOUND!
                    winClip.play();
                    historyOfGrids.clear();
                    //initaudio load all audio files, instance vars up top
                    //call stop, has to be an instance var 

                    endOfGameTime = timertask.toString();
                    System.out.println("timer: " + endOfGameTime);
                    timer.cancel();
                    timer.purge();

                    //check if fastest
                    
                    //check if fastest
                    for(int i = 1; i < gsm.fastest_win.length; i++){
                        if(gsm.fastest_win[i] == null){
                            gsm.fastest_win[i] = endOfGameTime;
                        }
                        if(endOfGameTime.compareTo(gsm.fastest_win[i]) == 1){
                            gsm.fastest_win[i] = endOfGameTime;
                        }
                    }
                    
                    docManager.addGameResultToStatsPage(gsm.getGameInProgress());

                    gsm.setGameState(SokobanGameStateManager.SokobanGameState.GAME_OVER);
                    respondToWin(primaryStage);
                    

                }

                //CHECK IF LOST
                //have to check for everybox not just one
                //for each box in the grid if condition true 
//                for (int t = 0; t < boxesInLevel.size(); t++) {
//                    boolean up = false, down = false, left = false, right = false;
//
//                    if (gsm.getGrid()[boxesInLevel.get(t).get(0)][boxesInLevel.get(t).get(1) - 1] != 1) {
//                        up = true;
//                    }
//                    if (gsm.getGrid()[boxesInLevel.get(t).get(0)][boxesInLevel.get(t).get(1) + 1] != 1) {
//                        down = true;
//                    }
//                    if (gsm.getGrid()[boxesInLevel.get(t).get(0) - 1][boxesInLevel.get(t).get(1)] != 1) {
//                        left = true;
//                    }
//                    if (gsm.getGrid()[boxesInLevel.get(t).get(0) + 1][boxesInLevel.get(t).get(1) - 1] != 1) {
//                        right = true;
//                    }
//                    boolean isRedPoint = false;
//                    for (int y = 0; y < redPointsInLevel.size(); y++) {
//                        if (gsm.getGrid()[redPointsInLevel.get(y).get(0)][redPointsInLevel.get(y).get(1)]
//                                == gsm.getGrid()[boxesInLevel.get(t).get(0)][boxesInLevel.get(t).get(1)]) {
//                            isRedPoint = true;
//                        }
//                    }
//                    if (((up == false && left == false) || (down == false && right == false)
//                            || (up == false && right == false) || (down == false && left == false))
//                            //and not red point
//                            && isRedPoint == false) {
//                        System.out.println("YOU LOSE!");
//                        gsm.setGameState(SokobanGameStateManager.SokobanGameState.GAME_OVER);
//                        respondToLoss(primaryStage);
//
//                    }
//                }
                //TODO: CHECK IF LOST
                for (int i = 0; i < gsm.getGridColumns(); i++) {
                    for (int j = 0; j < gsm.getGridRows(); j++) {
                        if (gsm.getGrid()[i][j] == 2) {
                            //System.out.println("box pos:" + gsm.getGrid()[i][j]);

                            //boolean up = checkUp(gsm.getGrid(), i, j);
                            boolean up = false, down = false, left = false, right = false;

                            if (gsm.getGrid()[i][j - 1] != 1) {
                                up = true;
                            }
                            if (gsm.getGrid()[i][j + 1] != 1) {
                                down = true;
                            }
                            if (gsm.getGrid()[i - 1][j] != 1) {
                                left = true;
                            }
                            if (gsm.getGrid()[i + 1][j] != 1) {
                                right = true;
                            }

                            boolean isRedPoint = false;
                            for (int t = 0; t < redPointsInLevel.size(); t++) {
                                if (gsm.getGrid()[redPointsInLevel.get(t).get(0)][redPointsInLevel.get(t).get(1)]
                                        == gsm.getGrid()[i][j]) {
                                    isRedPoint = true;
                                }
                            }

                            if (((up == false && left == false) || (down == false && right == false)
                                    || (up == false && right == false) || (down == false && left == false))
                                    //and not red point
                                    && isRedPoint == false) {
                                System.out.println("YOU LOSE!");
                                gsm.setGameState(SokobanGameStateManager.SokobanGameState.GAME_OVER);
                                historyOfGrids.clear();
                                loseClip.play(); 
                                respondToLoss(primaryStage);

                            }
                        }
                    }
                }
    }

    public void initSplashScreen() {

        // INIT THE SPLASH SCREEN CONTROLS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props
                .getProperty(SokobanPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        props.addProperty(SokobanPropertyType.INSETS, "5");
        String str = props.getProperty(SokobanPropertyType.INSETS);
        
        mainClip.play();

        splashScreenPane = new FlowPane();
        //splashScreenPane.setScaleY(0.1);
        //get function for size y value smaller so height is less, then bottom will showup

        Image splashScreenImage = loadImage(splashScreenImagePath);
        splashScreenImageView = new ImageView(splashScreenImage);
        splashScreenImageView.setFitHeight(700);

        splashScreenImageLabel = new Label();
        splashScreenImageLabel.setGraphic(splashScreenImageView);
        // move the label position to fix the pane
        splashScreenImageLabel.setLayoutX(-45);
        splashScreenPane.getChildren().add(splashScreenImageLabel);

        // GET THE LIST OF LEVEL OPTIONS
        ArrayList<String> levels = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelImages = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_IMAGE_NAMES);
        //ArrayList<String> levelFiles = props
        //       .getPropertyOptionsList(SokobanPropertyType.LEVEL_FILES);

        levelSelectionPane = new HBox();
        levelSelectionPane.setSpacing(10.0);
        levelSelectionPane.setAlignment(Pos.CENTER);
        // add key listener
        levelButtons = new ArrayList<Button>();
        for (int i = 0; i < levels.size(); i++) {

            // GET THE LIST OF LEVEL OPTIONS
            String level = levels.get(i);
            String levelImageName = levelImages.get(i);
            Image levelImage = loadImage(levelImageName);
            ImageView levelImageView = new ImageView(levelImage);

            // AND BUILD THE BUTTON
            Button levelButton = new Button();
            levelButton.setGraphic(levelImageView);

            // CONNECT THE BUTTON TO THE EVENT HANDLER
            levelButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    eventHandler.respondToSelectLevelRequest(level);
                    initSokobanUI(); //wrong place?

                    //didn't want this since in initSokobanUI it changes the workspace
                    //changeWorkspace(SokobanUIState.PLAY_GAME_STATE);
                    isMoving = new int[getGSM().getGrid().length][getGSM().getGrid()[0].length];
                }
            });
            levelSelectionPane.getChildren().add(levelButton);
            // TODO: enable only the first level
            //levelButton.setDisable(true);
        }

        mainPane.setBottom(levelSelectionPane);
        mainPane.setCenter(splashScreenPane);
        // mainPane.setCenter(levelSelectionPane);

    }

    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     */
    public void initSokobanUI() {
        // FIRST REMOVE THE SPLASH SCREEN

        mainPane.getChildren().clear();

        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(SokobanPropertyType.GAME_TITLE_TEXT);
        primaryStage.setTitle(title);

        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
        initNorthToolbar(); //just add the back button, then make the connection between select level and game pane in the mainpane
        //use change workspace

        // OUR WORKSPACE WILL STORE EITHER THE GAME, STATS,
        // OR HELP UI AT ANY ONE TIME
        initWorkspace();
        initGameScreen();
        //initStatsPane();
        initHelpPane();

        // WE'LL START OUT WITH THE GAME SCREEN
        changeWorkspace(SokobanUIState.PLAY_GAME_STATE);

    }

    public void initGameScreen() {
        gridRenderer.repaint(getGSM().getGridColumns(), getGSM().getGridRows(), getGSM().getGrid());
        gamePanel.setCenter(gridRenderer);

    }

    private void initHelpPane() {
        // WE'LL DISPLAY ALL STATS IN A JEditorPane
        statsPane = new JEditorPane();
        statsPane.setEditable(false);
        statsPane.setContentType("text/html");

        // LOAD THE STARTING STATS PAGE, WHICH IS JUST AN OUTLINE
        // AND DOESN"T HAVE ANY OF THE STATS, SINCE THOSE WILL 
        // BE DYNAMICALLY ADDED
        loadPage(statsPane, SokobanPropertyType.STATS_FILE_NAME);
        HTMLDocument statsDoc = (HTMLDocument) statsPane.getDocument();
        docManager.setStatsDoc(statsDoc);
        SwingNode swingNode = new SwingNode();
        JScrollPane statJScrollPane = new JScrollPane(statsPane);
        swingNode.setContent(statJScrollPane);
        statsScrollPane = new ScrollPane();
        statsScrollPane.setContent(swingNode);
        statsScrollPane.setFitToHeight(true);
        statsScrollPane.setFitToWidth(true);

    }

    private void loadPage(JEditorPane jep, SokobanPropertyType fileProperty) {
        // GET THE FILE NAME
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String fileName = props.getProperty(fileProperty);
        try {
            // LOAD THE HTML INTO THE EDITOR PANE
            String fileHTML = SokobanFileLoader.loadTextFile(fileName);
            jep.setText(fileHTML);
        } catch (IOException ioe) {
            errorHandler.processError(SokobanPropertyType.INVALID_URL_ERROR_TEXT);
        }
    }

    //go through all moves and revert 
    //arraylist or stack of arrays[][] (of grids[][])
    //when move push(gsm.getGrid())
    //gsm.getGrid() = var.pop();
       //method called in two places, one for u key in initmain and one for undo button in northtoolbar
    private void undoEachMove() {
        if (historyOfGrids.size() != 0) {
            gsm.setGrid(historyOfGrids.get(historyOfGrids.size() - 1));
            gridRenderer.repaint(gsm.getGridColumns(), gsm.getGridRows(), gsm.getGrid());
            historyOfGrids.remove(historyOfGrids.size() - 1);
        }

    }

    /**
     * This function initializes all the controls that go in the north toolbar.
     */
    private void initNorthToolbar() {
        // MAKE THE NORTH TOOLBAR, WHICH WILL HAVE FOUR BUTTONS
        northToolbar = new HBox();
        northToolbar.setStyle("-fx-background-color:lightgray");
        northToolbar.setAlignment(Pos.CENTER);
        northToolbar.setPadding(marginlessInsets);
        northToolbar.setSpacing(10.0);

        //BACK BUTTON
        gameButton = initToolbarButton(northToolbar,
                SokobanPropertyType.GAME_IMG_NAME); //actually back button
        //setTooltip(gameButton, SokobanPropertyType.GAME_TOOLTIP);
        gameButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            //BACK TO SPLASH SCREEN BUTTON
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                System.out.println("in event handler for back button");
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.SPLASH_SCREEN_STATE);
            }
        });

        // UNDO BUTTON
        statsButton = initToolbarButton(northToolbar,
                SokobanPropertyType.STATS_IMG_NAME); //actually undo button
        //setTooltip(statsButton, SokobanPropertyType.STATS_TOOLTIP);
        //when button clicked, simulate the u being pressed

        statsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                undoEachMove();
            }
        });

        // STATS BUTTON
        helpButton = initToolbarButton(northToolbar,
                SokobanPropertyType.HELP_IMG_NAME);
        //setTooltip(helpButton, SokobanPropertyType.HELP_TOOLTIP);
        helpButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.VIEW_HELP_STATE);

            }

        });

        // TIMER/ EXIT BUTTON
        timer = new Timer();
        Label timerLabel = new Label("Time: ");
        northToolbar.getChildren().add(timerLabel);
        timertask = new DisplayTime(timerLabel);
        timer.schedule(timertask, 0, 1000);
        
        //exitButton = initToolbarButton(northToolbar,
        // SokobanPropertyType.EXIT_IMG_NAME); //actually time button
        //setTooltip(exitButton, SokobanPropertyType.EXIT_TOOLTIP);
//        exitButton.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                // TODO Auto-generated method stub
//                eventHandler.respondToExitRequest(primaryStage);
//            }
//
//        });

        // AND NOW PUT THE NORTH TOOLBAR IN THE FRAME
        mainPane.setTop(northToolbar);
        //mainPane.getChildren().add(northToolbar);
    }

    /**
     * This method helps to initialize buttons for a simple toolbar.
     *
     * @param toolbar The toolbar for which to add the button.
     *
     * @param prop The property for the button we are building. This will
     * dictate which image to use for the button.
     *
     * @return A constructed button initialized and added to the toolbar.
     */
    private Button initToolbarButton(HBox toolbar, SokobanPropertyType prop) {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);

        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageView imageIcon = new ImageView(image);

        // MAKE THE BUTTON
        Button button = new Button();
        button.setGraphic(imageIcon);
        button.setPadding(marginlessInsets);

        // PUT IT IN THE TOOLBAR
        toolbar.getChildren().add(button);

        // AND SEND BACK THE BUTTON
        return button;
    }

    /**
     * The workspace is a panel that will show different screens depending on
     * the user's requests.
     */
    private void initWorkspace() {
        // THE WORKSPACE WILL GO IN THE CENTER OF THE WINDOW, UNDER THE NORTH
        // TOOLBAR
        workspace = new Pane();
        mainPane.setCenter(workspace);
        //mainPane.getChildren().add(workspace);
        System.out.println("in the initWorkspace");
    }

    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     *
     * @param uiScreen The screen to be switched to.
     */
    public void changeWorkspace(SokobanUIState uiScreen) {
        switch (uiScreen) {
            //splash creen state, or play game state
            case SPLASH_SCREEN_STATE:
                mainClip.play();
                gsm.setGameState(SokobanGameStateManager.SokobanGameState.GAME_NOT_STARTED);
                System.out.println("in change workspace method");
                mainPane.getChildren().clear();
                //reput title cause in main method

                PropertiesManager props = PropertiesManager.getPropertiesManager();
                String title = props.getProperty(SokobanPropertyType.SPLASH_SCREEN_TITLE_TEXT);
                primaryStage.setTitle(title);

                //initSplashScreen();
                mainPane.setBottom(levelSelectionPane);
                mainPane.setCenter(splashScreenPane);
                break;
            //ACTUALLY THE STATS PANE
            case VIEW_HELP_STATE:
                props = PropertiesManager.getPropertiesManager();
                primaryStage.setTitle(props.getProperty(SokobanPropertyType.STATS_PANE_TEXT));
                mainPane.setCenter(statsScrollPane);
                //takes timer out of toolbar
                northToolbar.getChildren().remove(3);
                docManager.addGameResultToStatsPage(gsm.getGameInProgress());
                break;

            case PLAY_GAME_STATE:
                mainClip.stop();
                redPointsInLevel.clear();
                mainPane.setCenter(gamePanel);
                
                findAllRedPoints();
                findSokobanPosition();
                
                break;
            //ACTUALLY THE UNDO BUTTON SO NO NEED FOR THIS CASE
            case VIEW_STATS_STATE:
                //mainPane.setCenter(statsScrollPane);
                break;
            default:
        }

    }
    //what if different level? how to clear array and reset?

    private void findAllRedPoints() {
        for (int i = 0; i < gsm.getGridColumns(); i++) {
            for (int j = 0; j < gsm.getGridRows(); j++) {
                if (gsm.getGrid()[i][j] == 3) {
                    redPointsInLevel.add(Arrays.asList(i, j));
                }
            }
        }
        System.out.println(redPointsInLevel);
    }
    
        private void findAllBoxes() {
        for (int i = 0; i < gsm.getGridColumns(); i++) {
            for (int j = 0; j < gsm.getGridRows(); j++) {
                if (gsm.getGrid()[i][j] == 2) {
                    boxesInLevel.add(Arrays.asList(i, j));
                }
            }
        }
        System.out.println(boxesInLevel);
    }

    private void findSokobanPosition() {
        for (int i = 0; i < gsm.getGridColumns(); i++) {
            for (int j = 0; j < gsm.getGridRows(); j++) {
                if (gsm.getGrid()[i][j] == 4) {
                    sokobanPosition.add(Arrays.asList(i, j));
                }
            }
        }
        System.out.println(sokobanPosition);
    }

}
