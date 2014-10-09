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
import java.util.Arrays;
import java.util.List;
import properties_manager.PropertiesManager;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
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
    GridRenderer gridRenderer = new GridRenderer();
    SokobanGameStateManager gsm;
    private boolean canMove;
    ArrayList<List<Integer>> redPointsInLevel = new ArrayList<>();
    ArrayList<List<Integer>> sokobanPosition = new ArrayList<>();
    //int iGuyPos = -1;
    //int jGuyPos = -1;

    public SokobanUI() {
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
                
                //if statement to repaint red point if guy went over it
                boolean isOverRedPoint = false;
                for(int k = 0; k < redPointsInLevel.size(); k++){
                    if(sokobanPosition.get(0).equals(redPointsInLevel.get(k))){
                        isOverRedPoint = true;
                    }
                }
                //System.out.println("over red pt: " + isOverRedPoint);
                System.out.println("sok loc:" + sokobanPosition);
                System.out.println("red loc:" + redPointsInLevel);
                
                
                boolean nextIsBox = false;
                outerloop:
                if (ke.getCode().equals(KeyCode.UP)) {
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                
                                //gsm.getGrid()[i][j] = 0;
                                nextIsBox = checkUp(gsm.getGrid(), i, j);
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i,j - 1));
                                    if(isOverRedPoint == true){
                                       gsm.getGrid()[i][j] = 3;
                                       gsm.getGrid()[i][j - 1] = 4; 
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j - 1] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i,j - 1));
                                    if(isOverRedPoint == true){
                                       gsm.getGrid()[i][j] = 3;
                                       gsm.getGrid()[i][j - 2] = 2;
                                       gsm.getGrid()[i][j - 1] = 4; 
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j - 2] = 2;
                                        gsm.getGrid()[i][j - 1] = 4;
                                    }
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
                    outerloop:
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                
                                nextIsBox = checkDown(gsm.getGrid(), i, j);
                                //gsm.getGrid()[i][j] = 0;
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i,j + 1));
                                    if(isOverRedPoint){
                                       gsm.getGrid()[i][j] = 3;
                                       gsm.getGrid()[i][j + 1] = 4; 
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j + 1] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i,j + 1));
                                    if(isOverRedPoint){
                                       gsm.getGrid()[i][j] = 3;
                                       gsm.getGrid()[i][j + 2] = 2;
                                       gsm.getGrid()[i][j + 1] = 4; 
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i][j + 2] = 2;
                                        gsm.getGrid()[i][j + 1] = 4;
                                    }
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
                    outerloop:
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                
                                nextIsBox = checkLeft(gsm.getGrid(), i, j);
                                //gsm.getGrid()[i][j] = 0;
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i - 1,j));
                                    if(isOverRedPoint){
                                       gsm.getGrid()[i][j] = 3;
                                       gsm.getGrid()[i - 1][j] = 4; 
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i - 1][j] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i - 1,j));
                                    if(isOverRedPoint){
                                       gsm.getGrid()[i][j] = 3;
                                       gsm.getGrid()[i - 2][j] = 2;
                                       gsm.getGrid()[i - 1][j] = 4; 
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i - 2][j] = 2;
                                        gsm.getGrid()[i - 1][j] = 4;
                                    }
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
                    outerloop:
                    for (int i = 0; i < gsm.getGridColumns(); i++) {
                        for (int j = 0; j < gsm.getGridRows(); j++) {
                            if (gsm.getGrid()[i][j] == 4) {
                                nextIsBox = checkRight(gsm.getGrid(), i, j);
                                //check if can move, then check if next is box
                                if (canMove == true && nextIsBox == false) {
                                    sokobanPosition.add(0, Arrays.asList(i + 1,j));
                                    if(isOverRedPoint){
                                       gsm.getGrid()[i][j] = 3;
                                       gsm.getGrid()[i + 1][j] = 4; 
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i + 1][j] = 4;
                                    }
                                }
                                if (canMove == true && nextIsBox == true) {
                                    sokobanPosition.add(0, Arrays.asList(i + 1,j));
                                    if(isOverRedPoint){
                                        gsm.getGrid()[i][j] = 3;
                                        gsm.getGrid()[i + 2][j] = 2;
                                        gsm.getGrid()[i + 1][j] = 4;
                                    }
                                    else{
                                        gsm.getGrid()[i][j] = 0;
                                        gsm.getGrid()[i + 2][j] = 2;
                                        gsm.getGrid()[i + 1][j] = 4;
                                    }
                                }
                                //System.out.println(i + " " + j);
                                gridRenderer.repaint(gsm.getGridColumns(), gsm.getGridRows(), gsm.getGrid());
                                break outerloop;
                            }
                        }
                    }
                }
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
        });
    }

    public void initSplashScreen() {

        // INIT THE SPLASH SCREEN CONTROLS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props
                .getProperty(SokobanPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        props.addProperty(SokobanPropertyType.INSETS, "5");
        String str = props.getProperty(SokobanPropertyType.INSETS);

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
        //initHelpPane();

        // WE'LL START OUT WITH THE GAME SCREEN
        changeWorkspace(SokobanUIState.PLAY_GAME_STATE);

    }

    private void initGameScreen() {
        gridRenderer.repaint(getGSM().getGridColumns(), getGSM().getGridRows(), getGSM().getGrid());
        gamePanel.setCenter(gridRenderer);

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

        // MAKE AND INIT THE GAME BUTTON
        gameButton = initToolbarButton(northToolbar,
                SokobanPropertyType.GAME_IMG_NAME); //actually back button
        //setTooltip(gameButton, SokobanPropertyType.GAME_TOOLTIP);
        gameButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                System.out.println("in event handler for back button");
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.SPLASH_SCREEN_STATE);
            }
        });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar,
                SokobanPropertyType.STATS_IMG_NAME); //actually undo button
        //setTooltip(statsButton, SokobanPropertyType.STATS_TOOLTIP);

        statsButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.VIEW_STATS_STATE);
            }

        });
        // MAKE AND INIT THE HELP BUTTON
        helpButton = initToolbarButton(northToolbar,
                SokobanPropertyType.HELP_IMG_NAME); //actually stats button
        //setTooltip(helpButton, SokobanPropertyType.HELP_TOOLTIP);
        helpButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.VIEW_HELP_STATE);
            }

        });

        // MAKE AND INIT THE EXIT BUTTON
        exitButton = initToolbarButton(northToolbar,
                SokobanPropertyType.EXIT_IMG_NAME); //actually time button
        //setTooltip(exitButton, SokobanPropertyType.EXIT_TOOLTIP);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler.respondToExitRequest(primaryStage);
            }

        });

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

            case VIEW_HELP_STATE:
                mainPane.setCenter(helpPanel);
                break;
                
            case PLAY_GAME_STATE:
                redPointsInLevel.clear();
                mainPane.setCenter(gamePanel);
                findAllRedPoints();
                findSokobanPosition();
                break;

            case VIEW_STATS_STATE:
                mainPane.setCenter(statsScrollPane);
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
    
    private void findSokobanPosition(){
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
