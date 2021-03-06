package state;

import static entities.WorldVariables.HEIGHT;
import static entities.WorldVariables.WIDTH;

import java.text.SimpleDateFormat;

import org.newdawn.slick.SlickException;

import state.Printer.PrinterType;

public class Control {

    public StateType state;

    private int numLives;
    private int numPoints;
    private int numRockets;
    private int numStage;

    private static final String lives = "Lives: ";
    private static final String points = "Points: ";
    private static final String stage = "Stage: ";
    private static final String rockets = "Rockets: ";
    private static final String time = "Time: ";

    private long gameStartTime;
    private long gameEndStageTime;
    private long gameOverTime;
    private long gameExitTime;

    private boolean isNewStage;
    private long timeToPrint;

    private final int gap = 100;

    public Control(long time) throws SlickException {
        Printer.setUpFonts();
        state = StateType.MAIN;
    }

    public void initialize(long time) {
        numLives = 5;
        numPoints = 0;
        numRockets = 0;
        numStage = 0;
        gameStartTime = time;
        isNewStage = false;
        gameEndStageTime = 0;
        gameExitTime = 0;

        setNewStage(time);
    }

    public void increasePoints() {
        numPoints++;
    }

    public void decreaseLives() {
        numLives--;
    }

    public int getNumLives() {
        return numLives;
    }

    public void nextStage() {
        numStage++;
    }
    
    public int getNumStage() {
        return numStage;
    }

    public void increaseRockets() {
        numRockets++;
    }

    public void drawScore(long newTime) {
        /*
         * LIVES
         */
        Printer.write(10, 0, lives, PrinterType.YELLOW);
        Printer.write(10 + 50, 0, String.valueOf(numLives), PrinterType.WHITE);

        /*
         * POINTS
         */
        Printer.write(gap, 0, points, PrinterType.YELLOW);
        Printer.write(gap + 60, 0, String.valueOf(numPoints), PrinterType.WHITE);

        /*
         * ROCKETS
         */
        Printer.write(2*gap, 0, rockets, PrinterType.YELLOW);
        Printer.write(2*gap + 75, 0, String.valueOf(numRockets), PrinterType.WHITE);

        /*
         * STAGE
         */
        Printer.write(4*gap, 0, stage, PrinterType.YELLOW);
        Printer.write(4*gap + 55, 0, String.valueOf(numStage), PrinterType.WHITE);

        /*
         * TIME
         */
        Printer.write(5*gap, 0, time, PrinterType.YELLOW);
        if (state.equals(StateType.GAMEOVER))
            Printer.write(5*gap + 50, 0, getTime(gameOverTime - gameStartTime), PrinterType.WHITE);
        else
            Printer.write(5*gap + 50, 0, getTime(newTime - gameStartTime), PrinterType.WHITE);

        /*
         * New Stage message
         */
        if (isNewStage) {
            if (timeToPrint > newTime)
                Printer.write(WIDTH/2 - 50, HEIGHT/2, "Stage " + numStage, PrinterType.WARN);
            else 
                isNewStage = false;
        }
    }

    private String getTime(long timePlayed) {
        return (new SimpleDateFormat("mm:ss:SSS")).format(timePlayed);   
    }

    public void setNewStage(long newTime) {
        isNewStage = true;
        timeToPrint = newTime + 2000;
        nextStage();
    }

    public void setGameOver(long time) {
        state = StateType.GAMEOVER;
        gameOverTime = time;
    }

    public void writeGameOver() {
        if (state.equals(StateType.GAMEOVER)) {
            Printer.write(WIDTH/2 - 80, HEIGHT/2 - 50, "GAME OVER!",PrinterType. WARN);
            Printer.write(WIDTH/2 - 170, HEIGHT/2, "Would you like to try again? (Y)es (N)o ",
                    PrinterType.YELLOW);
        }
    }

    public void waitEndGameStage(long newTime) {
        if (state.equals(StateType.END_STAGE)) {
            if (gameEndStageTime == 0)
                gameEndStageTime = newTime + 800;

            if (newTime > gameEndStageTime) {
                gameEndStageTime = 0;
                state = StateType.NEXT_STAGE;
            }
        }
    }

    public boolean waitExit(long newTime) {
        if (gameExitTime == 0)
            gameExitTime = newTime + 1000;

        if (gameExitTime > newTime) {
            Printer.write(WIDTH/2 - 170, HEIGHT/2, "Game closing...", PrinterType.YELLOW);
            return true;
        }
        return false;
    }

    public void writeNextLevel() {
        int space = 15;

        Printer.write(WIDTH/2 - 170, HEIGHT/2 - space, "CONGRATULATIONS!!", PrinterType.WARN);

        Printer.write(WIDTH/2 - 170, HEIGHT/2 + space, "Points: ", PrinterType.YELLOW);
        Printer.write(WIDTH/2 - 170 + 60, HEIGHT/2 + space, String.valueOf(numPoints), PrinterType.WHITE);

        Printer.write(WIDTH/2 - 170, HEIGHT/2 + 2*space, "Rockets: ", PrinterType.YELLOW);
        Printer.write(WIDTH/2 - 170 + 75, HEIGHT/2 + 2*space, String.valueOf(numRockets), PrinterType.WHITE);

        Printer.write(WIDTH/2 - 170, HEIGHT/2 + 3*space, "Time: ", PrinterType.YELLOW);
        Printer.write(WIDTH/2 - 170 + 50, HEIGHT/2 + 3*space, getTime(gameOverTime - gameStartTime), PrinterType.WHITE);

        Printer.write(WIDTH/2 - 170, HEIGHT/2 + 4*space, "You are ready for the next level of the game!", PrinterType.YELLOW);
        Printer.write(WIDTH/2 - 170, HEIGHT/2 + 6*space, "Press ESC to quit.", PrinterType.WHITE);
    }

    public enum StateType {
        MAIN, GAME, NEXT_STAGE, END_STAGE, GAMEOVER, EXIT, CONGRATULATIONS;
    }
}
