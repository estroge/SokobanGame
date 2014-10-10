package sokoban.game;

import java.util.ArrayList;
import java.util.Iterator;

import sokoban.ui.SokobanUI;

public class SokobanGameStateManager {

    private int gridColumns;
    private int gridRows;
    private int grid[][];

    //added getters and setters and 3 vars
    public void setGridColumns(int initGridColumns) {
        gridColumns = initGridColumns;
    }

    public void setGridRows(int initGridRows) {
        gridRows = initGridRows;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridColumns() {
        return gridColumns;
    }

    public void setGrid(int[][] newGrid) {
        grid = newGrid;
    }

    public int[][] getGrid() {
        return grid;
    }

    // THE GAME WILL ALWAYS BE IN
    // ONE OF THESE STATES

    public enum SokobanGameState {

        GAME_NOT_STARTED, GAME_IN_PROGRESS, GAME_OVER
        

    }
    public enum SokobanLevelState{
        LEVEL1,
        LEVEL2,
        LEVEL3,
        LEVEL4,
        LEVEL5,
        LEVEL6,
        LEVEL7
    }

    // STORES THE CURRENT STATE OF THIS GAME
    private SokobanGameState currentGameState;
    private SokobanLevelState currentLevelState;
    
    public SokobanGameState getGameState(){
//        switch(currentGameState){
//            case GAME_NOT_STARTED:
//                break;
//            case GAME_IN_PROGRESS:
//                break;
//            case GAME_OVER:
//                break;
//        }
        return currentGameState;
    }
    public int getLevelState(){
    
        switch(currentLevelState){
            case LEVEL1:
                return 1;
            case LEVEL2:
                return 2;
            case LEVEL3:
                return 3;
            case LEVEL4:
                return 4;
            case LEVEL5:
                return 5;
            case LEVEL6:
                return 6;
            case LEVEL7:
                return 7;
            default: return 0;
        }
    }
    
    public void setGameState(SokobanGameState currentGameState){
        this.currentGameState = currentGameState;
    }
    
    public void setLevelState(SokobanLevelState currentLevelState){
        this.currentLevelState = currentLevelState;
    }

    // WHEN THE STATE OF THE GAME CHANGES IT WILL NEED TO BE
    // REFLECTED IN THE USER INTERFACE, SO THIS CLASS NEEDS
    // A REFERENCE TO THE UI
    private SokobanUI ui;

    // THIS IS THE GAME CURRENTLY BEING PLAYED
    private SokobanGameData gameInProgress;

    // statistics
    public int[] played = new int[8]; // one element per level
    public int[] wins = new int[8];
    public String[] fastest_win = new String[8];

    private final String NEWLINE_DELIMITER = "\n";

    public SokobanGameStateManager(SokobanUI initUI) {
        ui = initUI;

        // WE HAVE NOT STARTED A GAME YET
        currentGameState = SokobanGameState.GAME_NOT_STARTED;


        // THE FIRST GAME HAS NOT BEEN STARTED YET
        gameInProgress = null;
    }

    // ACCESSOR METHODS
    /**
     * Accessor method for getting the game currently being played.
     *
     * @return The game currently being played.
     */
    public SokobanGameData getGameInProgress() {
        return gameInProgress;
    }

    /**
     * Accessor method for getting the number of games that have been played.
     *
     * @return The total number of games that have been played during this game
     * session.
     */
    public int getGamesPlayed() {
        return played[getLevelState()];
    }

    /**
     * Accessor method for testing to see if any games have been started yet.
     *
     * @return true if at least one game has already been started during this
     * session, false otherwise.
     */
    public boolean isGameNotStarted() {
        return currentGameState == SokobanGameState.GAME_NOT_STARTED;
    }

    /**
     * Accessor method for testing to see if the current game is over.
     *
     * @return true if the game in progress has completed, false otherwise.
     */
    public boolean isGameOver() {
        return currentGameState == SokobanGameState.GAME_OVER;
    }

    /**
     * Accessor method for testing to see if the current game is in progress.
     *
     * @return true if a game is in progress, false otherwise.
     */
    public boolean isGameInProgress() {
        return currentGameState == SokobanGameState.GAME_IN_PROGRESS;
    }

    /**
     * Counts and returns the number of wins during this game session.
     *
     * @return The number of games in that have been completed that the player
     * won.
     */
    public int getWins() {
        return wins[getLevelState()];
    }

    /**
     * Counts and returns the number of losses during this game session.
     *
     * @return The number of games in that have been completed that the player
     * lost.
     */
    public int getLosses() {
        return played[getLevelState()] - wins[getLevelState()];
    }

    /**
     * Finds the completed game that the player won that required the least
     * amount of time.
     *
     * @return The completed game that the player won requiring the least amount
     * of time.
     */
    public String getFastestWin() {
        if (played[getLevelState()] == 0) {
            return 0+"";
        }

        // RETURN THE FASTEST GAME
        return fastest_win[getLevelState()];
    }

    /**
     * This method starts a new game, initializing all the necessary data for
     * that new game as well as recording the current game (if it exists) in the
     * games history data structure. It also lets the user interface know about
     * this change of state such that it may reflect this change.
     */
    public void startNewGame() {
        played[getLevelState()]++;

        // TODO: call the FileLoader to load all the level histories from a file: statistics.data
        
        // IF THERE IS A GAME IN PROGRESS AND THE PLAYER HASN'T WON, THAT MEANS
        // THE PLAYER IS QUITTING, SO WE NEED TO SAVE THE GAME TO OUR HISTORY
        // DATA STRUCTURE. NOTE THAT IF THE PLAYER WON THE GAME, IT WOULD HAVE
        // ALREADY BEEN SAVED SINCE THERE WOULD BE NO GUARANTEE THE PLAYER WOULD
        // CHOOSE TO PLAY AGAIN
        if (isGameInProgress() && !gameInProgress.isWon()) {
            // QUIT THE GAME, WHICH SETS THE END TIME
            gameInProgress.giveUp();

            // TODO: add game result to stats page
        }

        // AND NOW MAKE A NEW GAME
        makeNewGame();

        // AND MAKE SURE THE UI REFLECTS A NEW GAME
        //ui.resetUI();
    }

    /**
     * This method chooses a secret word and uses it to create a new game,
     * effectively starting it.
     */
    public void makeNewGame() {
        // TODO: create a game for a level
        gameInProgress = new SokobanGameData(1);

        // THE GAME IS OFFICIALLY UNDERWAY
        currentGameState = SokobanGameState.GAME_IN_PROGRESS;
    }

}
