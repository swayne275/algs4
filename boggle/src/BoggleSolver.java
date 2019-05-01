/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.List;

public class BoggleSolver {
    RWayTrie<Integer> dictTrie = new RWayTrie<Integer>();

    /**
     * Initializes the data structure using the given array of strings as the dictionary (You can
     * assume each word in the dictionary contains only the uppercase letters A through Z.)
     *
     * @param dictionary Valid words for this Boggle game
     */
    public BoggleSolver(String[] dictionary) {
        int cnt = 0;
        for (String word : dictionary) {
            dictTrie.put(word, cnt);
            cnt++;
        }
    }

    private List<String> getWordsFromFirstLetter(BoggleBoard board, int row, int col, int dim) {
        // Track all words we've found starting with the char at (row, col)
        List<String> wordList;
        // track if we've used the letter in this position - init false default
        boolean[][] haveUsed = new boolean[dim][dim];
        // say that we have used our starting letter
        haveUsed[row][col] = true;
        // track if we're stuck (aka complete) and should return
        boolean complete = false;

        while (!complete) {

        }
        return wordList;
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as Iterable (You can assume the
     * word contains only uppercase letters A through Z.)
     *
     * @param board Board to get all valid words from
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        List<String> validWords;
        final int dim = board.rows(); // !!! SW TODO do both dimm if required

        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                // for each letter in board as the starting point
                List<String> results = getWordsFromFirstLetter(board, row, col, dim);
                for (String subWord : results) {
                    if (!validWords.contains(subWord)) {
                        // Only add new words to the master set
                        validWords.add(subWord);
                    }
                }
            }
        }
        return validWords;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise. (You can
     * assume the word contains only the uppercase letters A through Z.)
     *
     * @param word Word to check the score of
     * @return Score of the given word (0 if not in dictionary)
     */
    public int scoreOf(String word) {
        if (dictTrie.get(word) == null) {
            // The word is not in the dictionary
            return 0;
        }
        int wordLength = word.length();
        if (wordLength <= 2) {
            return 0;
        }
        else if (wordLength <= 4) {
            return 1;
        }
        else if (wordLength == 5) {
            return 2;
        }
        else if (wordLength == 6) {
            return 3;
        }
        else if (wordLength == 7) {
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
