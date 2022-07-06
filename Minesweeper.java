/*
 * Text-based Minesweeper. A pretty robust input with code editable
 * configurations.
 * 
 * Taylor Juve, 7/4/2022
 */

import java.util.Random;
import java.util.Scanner;

public class Minesweeper {
    public static final String UNKNOWN = "?";
    public static final String FLAG = "!";
    public static final String MINE = "*";
    public static final int SIZE = 4;
    public static final int NUM_MINES = 4;
    
    private boolean[][] isMine;
    private String[][] marks;
    private int movesLeft;
    
    // Creates a new game of Minesweeper with random mine locations.
    // Parameters for size and number of mines would be nice but it's quicker
    // for me to just edit the class constants. 
    public Minesweeper() {
        isMine = new boolean[SIZE][SIZE];
        marks = new String[SIZE][SIZE];
        movesLeft = SIZE * SIZE - NUM_MINES;
        // Deep fill marks grid with UNKNOWN marks
        for (int x = 0; x < marks.length; x++) {
            for (int y = 0; y < marks[x].length; y++) {
                marks[x][y] = UNKNOWN;
            }
        }
        
        // Add randomly located mines
        int numMines = NUM_MINES;
        Random rand = new Random();
        while (numMines > 0) {
            int x = rand.nextInt(SIZE);
            int y = rand.nextInt(SIZE);
            
            if (!isMine[x][y]) {
                isMine[x][y] = true;
                numMines--;
            }
        }
    }
    
    // A coordinate labeled grid of visible information about this game.
    // Unknown spaces, flags, mines, and hints have unique identifiers.
    public String toString() {
        String toStr = "  ";
        // populate the top axis starting at 0
        for (int x = 0; x < marks.length; x++) {
            toStr += x;
        }
        toStr += "\n\n";
        
        // Add the grid of marks in their current state for this game with
        // the left axis also
        for (int x = 0; x < marks.length; x++) {
            toStr += x + " ";
            for (int y = 0; y < marks.length; y++) {
                toStr += marks[x][y];
            }
            toStr += "\n";
        }
        return toStr;
    }
    
    // Marks a flag at the given coordinate or unmarks a flag if already 
    // present. Flagging a hint or blank space does nothing.
    public void flag(int x, int y) {
        if (marks[x][y].equals(FLAG)) {
            marks[x][y] = UNKNOWN;
        } else if (marks[x][y].equals(UNKNOWN)) {
            marks[x][y] = FLAG;
        }
    }
    
    // Explores the grid at the given coordinate for a mine. If a mine, return
    // the that the game is over, otherwise returns true. Reveals a coordinate
    // and marks it with a mine, hint, or blank space with neighboring 
    // coordinates as hints or blank (and it's neighbors again.) 
    // Note: See Minesweeper rules for hints and blanks.
    public boolean guess(int x, int y) {
        if (isMine[x][y]) {
            marks[x][y] = MINE;
            return true;
        } else if (marks[x][y].equals(UNKNOWN)) {
            movesLeft--;
            int hint = 0;
            // Count the number of neighboring mines (adjacent coordinates)
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (isValidCoord(x + i, y + j) && isMine[x + i][y + j]) {
                        hint++;
                    }
                }
            }
            
            if (hint == 0) {
                // No neighboring mines, automatically guess each adjacent
                // coordinate (iconic Minesweeper move!)
                marks[x][y] = " ";
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (isValidCoord(x + i, y + j)) {
                            guess(x + i, y + j);
                        }
                    }
                }
            } else {
                marks[x][y] = "" + hint;
            }
        }
        return false;
    }
    
    // Currently static because I'm using class constants currently for ease.
    // Check is given coordinate is in bounds of game grid
    public static boolean isValidCoord(int x, int y) {
        return x >= 0 && y >= 0 && x < SIZE && y < SIZE;
    }
    
    // Create a Minesweeper game and manages user input to play it.
    // Each input is numbered and input is checks for validity for robustness.
    // Displays the info about the game and how to input commands, the game 
    // grid and all it's marks, and win or lose.
    public static void main(String[] args) {
        Minesweeper ms = new Minesweeper();
        Scanner console = new Scanner(System.in);
        int inputNum = 1;
        boolean gameOver = false;
        
        while (!gameOver && ms.movesLeft > 0) {
            printInfo();
            System.out.println(ms);
            System.out.print(inputNum + ": ");
            String line = console.nextLine();
            
            if (!line.equals("")) {
                Scanner lineScan = new Scanner(line);
                String cmd = lineScan.next();
                if (cmd.equals("guess")) {
                    int x = lineScan.nextInt();
                    int y = lineScan.nextInt();
                    if (isValidCoord(x, y)) {
                        gameOver = ms.guess(x, y);
                    } else {
                        System.out.println("Invalid coord");
                    }
                } else if (cmd.equals("flag")) {
                    int x = lineScan.nextInt();
                    int y = lineScan.nextInt();
                    if (isValidCoord(x, y)) {
                        ms.flag(x, y);
                    } else {
                        System.out.println("Invalid coord");
                    }
                } else {
                    System.out.println("Invalid command");
                }
            }
    
            inputNum++;
        }
        
        System.out.println("Game over");
        if (ms.movesLeft == 0) {
            System.out.println("You win!");
        } else {
            System.out.println("You lose...");
        }
        System.out.println();
        System.out.println(ms);
    }
    
    // Prints the game name and prompts for a command (gives examples of
    // command syntax.)
    public static void printInfo() {
        System.out.println("Minesweeper! Guess or flag an x, y coordinate.");
        System.out.println("(eg. \"guess 0 1\" or \"flag 2 2\")");
        System.out.println();
    }
}
