package sokoban.ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import application.Main.SokobanPropertyType;
import properties_manager.PropertiesManager;
import xml_utilities.InvalidXMLFileFormatException;
import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameStateManager;

public class SokobanEventHandler {

    private SokobanUI ui;

    /**
     * Constructor that simply saves the ui for later.
     *
     * @param initUI
     */
    public SokobanEventHandler(SokobanUI initUI) {
        ui = initUI;
    }

    /**
     * This method responds to when the user wishes to switch between the Game,
     * Stats, and Help screens.
     *
     * @param uiState The ui state, or screen, that the user wishes to switch
     * to.
     */
    public void respondToSwitchScreenRequest(SokobanUI.SokobanUIState uiState) {
        System.out.println("in respond to switch screen method");
        ui.changeWorkspace(uiState);
    }
    //in event handler for buttons, write code to load a file for each level, 
    //grid renderer will draw the file, so attach to the gamepanel and change 
    //the sokoban screen to the game panel
    //already changeworkspace method in UI
    
    public void respondToSelectLevelRequest(String level){
        //TODO: make this method to handle events.
        SokobanFileLoader fileLoader = new SokobanFileLoader();
        //change workspace to game panel
        switch(level){
            case "Level 1": System.out.println("Level 1 selected");
                fileLoader.FileLoader("./data/level1.sok", ui.getGSM());
                break;
            case "Level 2": fileLoader.FileLoader("./data/level2.sok", ui.getGSM());
                break;
            case "Level 3": fileLoader.FileLoader("./data/level3.sok", ui.getGSM());
                break;
            case "Level 4": fileLoader.FileLoader("./data/level4.sok", ui.getGSM());
                break;
            case "Level 5": fileLoader.FileLoader("./data/level5.sok", ui.getGSM());
                break;
            case "Level 6": fileLoader.FileLoader("./data/level6.sok", ui.getGSM());
                break;
            case "Level 7": fileLoader.FileLoader("./data/level7.sok", ui.getGSM());
                break;
        }
    }

    /**
     * This method responds to when the user presses the new game method.
     */
    public void respondToNewGameRequest() {
        SokobanGameStateManager gsm = ui.getGSM();
        gsm.startNewGame();
    }

    /**
     * This method responds to when the user requests to exit the application.
     *
     * @param window The window that the user has requested to close.
     */
    public void respondToExitRequest(Stage primaryStage) {
        // ENGLIS IS THE DEFAULT
        String options[] = new String[]{"Yes", "No"};
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        options[0] = props.getProperty(SokobanPropertyType.DEFAULT_YES_TEXT);
        options[1] = props.getProperty(SokobanPropertyType.DEFAULT_NO_TEXT);
        String verifyExit = props.getProperty(SokobanPropertyType.DEFAULT_EXIT_TEXT);

        // NOW WE'LL CHECK TO SEE IF LANGUAGE SPECIFIC VALUES HAVE BEEN SET
        if (props.getProperty(SokobanPropertyType.YES_TEXT) != null) {
            options[0] = props.getProperty(SokobanPropertyType.YES_TEXT);
            options[1] = props.getProperty(SokobanPropertyType.NO_TEXT);
            verifyExit = props.getProperty(SokobanPropertyType.EXIT_REQUEST_TEXT);
        }

        // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane exitPane = new BorderPane();
        HBox optionPane = new HBox();
        Button yesButton = new Button(options[0]);
        Button noButton = new Button(options[1]);
        optionPane.setSpacing(10.0);
        optionPane.getChildren().addAll(yesButton, noButton);
        Label exitLabel = new Label(verifyExit);
        exitPane.setCenter(exitLabel);
        exitPane.setBottom(optionPane);
        Scene scene = new Scene(exitPane, 50, 100);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        yesButton.setOnAction(e -> {
            // YES, LET'S EXIT
            System.exit(0);
        });
        noButton.setOnAction(e -> {
            dialogStage.close();
        });

    }

}
