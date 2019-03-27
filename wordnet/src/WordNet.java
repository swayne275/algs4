/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

import java.util.ArrayList;

public class WordNet {
    private final SAP sap;
    private final Digraph dg;
    private final ST<Integer, String> idToSynset;
    private final ST<String, ArrayList<Integer>> nounToIDs;

    /**
     * Wordnet constructor
     *
     * @param synsets   Name of file containing the synsets
     * @param hypernyms Name of file containing the hypernyms (general category)
     */
    public WordNet(String synsets, String hypernyms) {
        enforceNotNull(synsets);
        enforceNotNull(hypernyms);
        idToSynset = new ST<Integer, String>();
        nounToIDs = new ST<String, ArrayList<Integer>>();
        // !!! TODO throw exception if input not rooted DAG
        processSynsets(synsets);
        dg = new Digraph(idToSynset.size());
        processHypernyms(hypernyms);
        sap = new SAP(dg);
    }

    /**
     * Create the symbol tables to get (ID->synset) and (noun->ID(s))
     *
     * @param synsets Data from the synsets CSV text file
     */
    private void processSynsets(String synsets) {
        In in = new In(synsets);
        while (!in.isEmpty()) {
            String line = in.readLine();
            // [0] - id, [1] - synset, [2] - gloss (can discard)
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            String synset = fields[1];
            idToSynset.put(id, synset);

            // Add nouns - nouns are space-delimited in the synset
            String[] nouns = synset.split(" ");
            for (String noun : nouns) {
                ArrayList<Integer> nounIDs;
                if (nounToIDs.contains(noun)) {
                    nounIDs = nounToIDs.get(noun);
                }
                else {
                    nounIDs = new ArrayList<Integer>();
                }
                nounIDs.add(id);
                nounToIDs.put(noun, nounIDs);
            }
        }
    }

    /**
     * Build the wordnet digraph
     *
     * @param hypernyms Data from the hypernyms CSV text file
     */
    private void processHypernyms(String hypernyms) {
        In in = new In(hypernyms);

        while (!in.isEmpty()) {
            String line = in.readLine();
            // [0] - synset id, following are IDs of synset's hypernyms
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            for (int i = 1; i < fields.length; i++) {
                dg.addEdge(id, Integer.parseInt(fields[i]));
            }
        }
    }

    /**
     * Returns all WordNet nouns
     *
     * @return All WordNet nouns
     */
    public Iterable<String> nouns() {
        return nounToIDs.keys();
    }

    /**
     * Is the word a WordNet noun?
     *
     * @param word Word to check against
     * @return True if word is a WordNet noun
     */
    public boolean isNoun(String word) {
        enforceNotNull(word);
        return nounToIDs.contains(word);
    }

    /**
     * Distance between nounA and nounB
     *
     * @param nounA First noun to check
     * @param nounB Second noun to check
     * @return Distance between nounA and nounB
     */
    public int distance(String nounA, String nounB) {
        enforceNotNull(nounA);
        enforceNotNull(nounB);
        enforceInWordNet(nounA);
        enforceInWordNet(nounB);
        ArrayList<Integer> idsNounA = nounToIDs.get(nounA);
        ArrayList<Integer> idsNounB = nounToIDs.get(nounB);
        return sap.length(idsNounA, idsNounB);
    }

    /**
     * A synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
     *
     * @param nounA First noun to check
     * @param nounB Second noun to check
     * @return Synset common ancestor of nounA and nounB
     */
    public String sap(String nounA, String nounB) {
        enforceNotNull(nounA);
        enforceNotNull(nounB);
        enforceInWordNet(nounA);
        enforceInWordNet(nounB);
        ArrayList<Integer> idsNounA = nounToIDs.get(nounA);
        ArrayList<Integer> idsNounB = nounToIDs.get(nounB);
        int ancestorID = sap.ancestor(idsNounA, idsNounB);
        return idToSynset.get(ancestorID);
    }

    private void enforceNotNull(String test) {
        if (test == null) {
            throw new java.lang.IllegalArgumentException("Null string passed in");
        }
    }

    private void enforceInWordNet(String test) {
        if (!isNoun(test)) {
            throw new java.lang.IllegalArgumentException("Noun not in WordNet");
        }
    }

    // Do unit testing of this class
    public static void main(String[] args) {
        // WordNet test = new WordNet("synsets6.txt", "hypernyms15Tree.txt");
    }
}
