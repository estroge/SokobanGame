package sokoban.ui;

import java.io.IOException;
import application.Main.SokobanPropertyType;
import sokoban.game.SokobanGameData;
import sokoban.game.SokobanGameStateManager;
import properties_manager.PropertiesManager;
import javafx.stage.Stage;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

public class SokobanDocumentManager {

    private SokobanUI ui;

    public SokobanDocumentManager(SokobanUI initUI) {
        ui = initUI;
    }

    private HTMLDocument gameDoc;
    private HTMLDocument statsDoc;
    // This record the matched string since each game
    public String oldGuessMatch = new String();
    
    //Create a stats button on each level (next to the time) where you render the stats according 
    //to the level being played. You should display the number of the level, the number of games 
    //played on the level, the number of wins, the number of losses, the winning percentage, and the 
    //fastest win (or - if no wins). 

    // TODO: sokoban document manager
    // WE'LL USE THESE TO BUILD OUR HTML
    private final String START_TAG = "<";
    private final String END_TAG = ">";
    private final String SLASH = "/";
    private final String SPACE = " ";
    private final String EMPTY_TEXT = "";
    private final String NL = "\n";
    private final String QUOTE = "\"";
    private final String OPEN_PAREN = "(";
    private final String CLOSE_PAREN = ")";
    private final String COLON = ":";
    private final String EQUAL = "=";
    private final String COMMA = ",";
    private final String RGB = "rgb";

	// THESE ARE IDs IN THE GAME DISPLAY HTML FILE SO THAT WE
    // CAN GRAB THE NECESSARY ELEMENTS AND UPDATE THEM
    private final String GUESSES_SUBHEADER_ID = "guesses_subheader";
    private final String GUESSES_LIST_ID = "guesses_list";
    private final String WIN_DISPLAY_ID = "win_display";
    private final String LOSE_DISPLAY_ID = "lose_display";

	// THESE ARE IDs IN THE STATS HTML FILE SO THAT WE CAN
    // GRAB THE NECESSARY ELEMENTS AND UPDATE THEM
    private final String LEVEL_ID = "level";
    private final String GAMES_PLAYED_ID = "games_played";
    private final String WINS_ID = "wins";
    private final String LOSSES_ID = "losses";
    private final String PERCENT_WON_ID = "percent_won";
    private final String FEWEST_GUESSES_ID = "fewest_guesses";
    private final String FASTEST_WIN_ID = "fastest_win";
    private final String GAME_RESULTS_HEADER_ID = "game_results_header";
    private final String GAME_RESULTS_LIST_ID = "game_results_list";

    /**
     * Accessor method for initializing the game doc, which displays while the
     * game is being played and displays the guesses. Note that this must be
     * done before this object can be used.
     *
     * @param initGameDoc The game document to be displayed while the game is
     * being played.
     */
    public void setGameDoc(HTMLDocument initGameDoc) {
        gameDoc = initGameDoc;
    }

    /**
     * Accessor method for initializing the stats doc, which displays past game
     * results and statistics. Note that this must be done before this object
     * can be used.
     *
     * @param initStatsDoc The stats document to be displayed on the stats
     * screen.
     */
    public void setStatsDoc(HTMLDocument initStatsDoc) {
        statsDoc = initStatsDoc;
    }

    
    /**
     * This private helper method builds the HTML associated with a guess as a
     * list item, adding the proper colors as currently set by the player.
     *
     * @param guess Guess letter .
     * @param newGuessMatch The so-far-guessed word,with correct letters and
     * underlines .
     * @return htmlText
     */
    private String buildGuessHTML(String guess, String guessMatch) {
        // FIRST THE OPENING LIST ITEM TAG WITH THE GUESS
        // AS ITS ID. THIS IS OK SINCE WE DON'T ALLOW
        // DUPLICATE GUESSES
        String htmlText = START_TAG + HTML.Tag.LI + SPACE + HTML.Attribute.ID + EQUAL + QUOTE + QUOTE + END_TAG;
        // NOW WE NEED TO FORMAT THE COLOR FOR EACH CHARACTER IN THE GUESS
        for (int i = 0; i < guessMatch.length(); i++) {
            // GET THE COLOR FOR EACH CHARACTER
            char c = guessMatch.charAt(i);

            // AND BUILD HTML TEXT TO COLOR CODE EACH CHARACTER SEPARATELY
            htmlText += SPACE + c + START_TAG + SLASH + HTML.Tag.SPAN + END_TAG;
        }

        // NOW WE NEED TO FORMAT THE COLOR FOR EACH CHARACTER IN THE GUESS
        for (int i = 0; i < guess.length(); i++) {

        }
        // NOW ADD INFORMATION ABOUT THE NUMBER OF LETTERS IN THE
        // GUESS THAT ARE IN THE SECRET WORD
        htmlText += START_TAG + SLASH + HTML.Tag.LI + END_TAG + NL;
        return htmlText;
    }

    /**
     * When a new game starts the game page should not have a subheader or
     * display guesses or a win state, so all of that has to be cleared out of
     * the DOM at that time. This method does the work of clearing out these
     * nodes.
     */
    public void clearGamePage() {
        try {
            // WE'LL PUT THIS <br /> TAG IN PLACE OF THE CONTENT WE'RE REMOVING
            String lineBreak = START_TAG + HTML.Tag.BR + SPACE + SLASH + END_TAG;

            // CLEAR THE SUBHEADER
            Element h2 = gameDoc.getElement(GUESSES_SUBHEADER_ID);
            gameDoc.setInnerHTML(h2, lineBreak);

            // CLEAR THE GUESS LIST
            Element ol = gameDoc.getElement(GUESSES_LIST_ID);
            gameDoc.setInnerHTML(ol, lineBreak);

            // CLEAR THE WIN DISPLAY
            Element winH2 = gameDoc.getElement(WIN_DISPLAY_ID);
            gameDoc.setInnerHTML(winH2, lineBreak);

            //CLEAR THE LOSS DISPLAY
            Element loseH2 = gameDoc.getElement(LOSE_DISPLAY_ID);
            gameDoc.setInnerHTML(loseH2, lineBreak);
        } // THE ERROR HANDLER WILL DEAL WITH ERRORS ASSOCIATED WITH BUILDING
        // THE HTML FOR THE PAGE, WHICH WOULD LIKELY BE DUE TO BAD DATA FROM
        // AN XML SETUP FILE
        catch (BadLocationException | IOException ex) {
            SokobanErrorHandler errorHandler = ui.getErrorHandler();
            errorHandler.processError(SokobanPropertyType.INVALID_DOC_ERROR_TEXT);
        }
    }

    /**
     * This method adds the data from the completedGame argument to the stats
     * page, as well as loading all the newly computed stats for all the games
     * played.
     *
     * @param completedGame Game whose summary will be added to the stats page.
     */
    public void addGameResultToStatsPage(SokobanGameData completedGame) {
        // GET THE GAME STATS
        SokobanGameStateManager gsm = ui.getGSM();
        int level = gsm.getLevelState();
        int gamesPlayed = gsm.getGamesPlayed();
        int wins = gsm.getWins();
        int losses = gsm.getLosses();
        String fastestWin = gsm.getFastestWin();

        try {
            // USE THE STATS TO UPDATE THE TABLE AT THE TOP OF THE PAGE
            Element levelElement = statsDoc.getElement(LEVEL_ID);
            statsDoc.setInnerHTML(levelElement, EMPTY_TEXT + level);
            
            Element gamePlayedElement = statsDoc.getElement(GAMES_PLAYED_ID);
            statsDoc.setInnerHTML(gamePlayedElement, EMPTY_TEXT + gamesPlayed);

            Element winsElement = statsDoc.getElement(WINS_ID);
            statsDoc.setInnerHTML(winsElement, EMPTY_TEXT + wins);

            Element lossesElement = statsDoc.getElement(LOSSES_ID);
            statsDoc.setInnerHTML(lossesElement, EMPTY_TEXT + losses);
            
            Element percentElement = statsDoc.getElement(PERCENT_WON_ID);
            
            if((wins + losses) == 0){
                statsDoc.setInnerHTML(percentElement, EMPTY_TEXT + "%0.00");
            }else{
            statsDoc.setInnerHTML(percentElement, EMPTY_TEXT + "%" +(((double)wins/(wins+losses))*100));
            }
            Element fastestWinElement = statsDoc.getElement(FASTEST_WIN_ID);
            if ( ! ("" +fastestWin).equals("null")) {
                statsDoc.setInnerHTML(fastestWinElement, fastestWin+"");
            }

            // ADD THE SUBHEADER
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String gameResultsText = props.getProperty(SokobanPropertyType.GAME_RESULTS_TEXT);
            Element h2 = statsDoc.getElement(GAME_RESULTS_HEADER_ID);
            statsDoc.setInnerHTML(h2, gameResultsText);

            // AND NOW ADD THE LATEST GAME TO THE LIST
            Element ol = statsDoc.getElement(GAME_RESULTS_LIST_ID);
            String gameSummary = completedGame.toString();
            String htmlText = START_TAG + HTML.Tag.LI + END_TAG + gameSummary + START_TAG + SLASH + HTML.Tag.LI + END_TAG + NL;
            statsDoc.insertBeforeEnd(ol, htmlText);
        } // WE'LL LET THE ERROR HANDLER TAKE CARE OF ANY ERRORS,
        // WHICH COULD HAPPEN IF XML SETUP FILES ARE IMPROPERLY
        // FORMATTED
        catch (BadLocationException | IOException e) {
            SokobanErrorHandler errorHandler = ui.getErrorHandler();
            errorHandler.processError(SokobanPropertyType.INVALID_DOC_ERROR_TEXT);
        }
    }
}
