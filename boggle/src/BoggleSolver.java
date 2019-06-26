/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    /// Dictionary of all valid words, as provided at construction
    private final RWayTrie dictionary;
    /// Track if this cell has been visited or not
    private boolean[][] cellVisited;
    /// The current word we are building from the board
    private StringBuilder currentWord;
    /// The words (in the dictionary) seen so far in this board
    private SET<String> words;
    /// Use a trie for faster searching of words seen so far
    private RWayTrie wordsTrie;
    /// Track number of rows in board
    private int numRows;
    /// Track number of columns in board;
    private int numCols;

    /**
     * Initializes the data structure using the given array of strings as the dictionary (You can
     * assume each word in the dictionary contains only the uppercase letters A through Z.)
     *
     * @param dictionary Valid words for this Boggle game
     */
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new RWayTrie();
        for (String word : dictionary) {
            this.dictionary.put(word);
        }
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as Iterable (You can assume the
     * word contains only uppercase letters A through Z.)
     *
     * @param board Board to get all valid words from
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        numRows = board.rows();
        numCols = board.cols();

        // init cellVisited to all false (not visited)
        cellVisited = new boolean[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                cellVisited[row][col] = false;
            }
        }

        words = new SET<String>();
        wordsTrie = new RWayTrie();
        currentWord = new StringBuilder();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                addChar(board.getLetter(row, col), row, col);
                findWord(board, row, col);
                removeChar(row, col);
            }
        }
        return words;
    }

    /**
     * Append char to the current word, mark board position as visited
     *
     * @param c   Character to append to current word
     * @param row Character's row in the boggle board
     * @param col Character's column in the boggle board
     */
    private void addChar(final char c, final int row, final int col) {
        // Handle 'Q' -> "QU" edge case
        if (c == 'Q') {
            currentWord.append("QU");
        }
        else {
            currentWord.append(c);
        }
        cellVisited[row][col] = true;
    }

    /**
     * Recursively search all valid words (per dictionary) from given position
     *
     * @param board BoggleBoard to search
     * @param row   Row to start search from in {board}
     * @param col   Column to start search from in {board}
     */
    private void findWord(final BoggleBoard board, final int row, final int col) {
        // loop through valid moves for a given cell
        for (int moveIdx = 0; moveIdx <= 7; moveIdx++) {
            // Index of the next cell to look at
            final int newRow = row + calcAvailableMove(moveIdx, true);
            final int newCol = col + calcAvailableMove(moveIdx, false);

            if (rowInvalid(newRow) || colInvalid(newCol) ||
                    cellVisited[newRow][newCol]) {
                continue;
            }

            addChar(board.getLetter(newRow, newCol), newRow, newCol);
            String currentWordStr = currentWord.toString();
            if (wordValid(currentWordStr)) {
                storeWord(currentWordStr);
            }
            if (dictionary.keyExists(currentWordStr)) {
                // This could be a valid or invalid word that is a prefix to a
                // valid word in the dictionary. If so, keep searching
                findWord(board, newRow, newCol);
            }

            // Once we are done searching the current position, remove the char
            // from the current word and search another path
            removeChar(newRow, newCol);
        }
    }

    private void storeWord(final String word) {
        if (!wordsTrie.contains(word)) {
            words.add(word);
            wordsTrie.put(word);
        }
    }

    /**
     * Remove the last char added to the current word, unmark as visited
     *
     * @param row Character's row in the boggle board
     * @param col Character's column in the boggle board
     */
    private void removeChar(final int row, final int col) {
        // Handle the 'Q' -> "QU" edge case
        final int len = currentWord.length();
        if ((len >= 2) && (currentWord.charAt(len - 2) == 'Q')) {
            // remove the trailing "QU"
            currentWord.delete(len - 2, len);
        }
        else {
            // remove the last character
            currentWord.delete(len - 1, len);
        }

        cellVisited[row][col] = false;
    }

    /// Return true if {row} is outside of the range of the boggle board
    private boolean rowInvalid(final int row) {
        return ((row < 0) || (row >= numRows));
    }

    /// Return true if {col} is outside of the range of the boggle board
    private boolean colInvalid(final int col) {
        return ((col < 0) || (col >= numCols));
    }

    /// Return true if {word} is long enough and in the given dictionary
    private boolean wordValid(String word) {
        return ((word.length() > 2) && (dictionary.contains(word)));
    }

    /**
     * Calculate a valid move for a row or column 0 1 2 3 x 4 5 6 7
     *
     * @param moveIdx Index of valid move from x from diagram above
     * @param isRow   True if moving in row, false if moving in column
     * @return Increment/decrement for the move
     * @throw IllegalArgumentException if moveIdx out of [0, 7]
     */
    private int calcAvailableMove(final int moveIdx, final boolean isRow) {
        // I don't like declaring uninit, but fewer warnings this way
        int move;
        switch (moveIdx) {
            case 0:
                if (isRow) move = -1;
                else move = -1;
                break;
            case 1:
                if (isRow) move = 0;
                else move = -1;
                break;
            case 2:
                if (isRow) move = 1;
                else move = -1;
                break;
            case 3:
                if (isRow) move = -1;
                else move = 0;
                break;
            case 4:
                if (isRow) move = 1;
                else move = 0;
                break;
            case 5:
                if (isRow) move = -1;
                else move = 1;
                break;
            case 6:
                if (isRow) move = 0;
                else move = 1;
                break;
            case 7:
                if (isRow) move = 1;
                else move = 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid move index");
        }

        return move;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise. (You can
     * assume the word contains only the uppercase letters A through Z.)
     *
     * @param word Word to check the score of
     * @return Score of the given word (0 if not in dictionary)
     */
    public int scoreOf(String word) {
        if (!dictionary.keyExists(word)) {
            // The word is not in the dictionary
            return 0;
        }

        final int len = word.length();
        if (len <= 2) {
            return 0;
        }
        else if (len <= 4) {
            return 1;
        }
        else if (len == 5) {
            return 2;
        }
        else if (len == 6) {
            return 3;
        }
        else if (len == 7) {
            return 5;
        }
        else {
            return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
