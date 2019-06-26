/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class RWayTrie {
    /// Valid range of characters is ASCII 'A' to ASCII 'Z'
    private static final int RADIX = 26;
    /// ASCII representation for 'A'
    private static final int ASCII_UPPER_A = 65;
    private Node root;

    private static class Node {
        // value is encoded into the index from that node of the trie
        private Node[] next = new Node[RADIX];
    }

    public boolean contains(String key) {
        Node x = get(root, key, 0);
        return (x != null);
    }

    private Node get(Node x, String key, int position) {
        if (x == null) {
            return null;
        }
        if (key.length() == position) {
            // End the recursion if we've gone deep enough
            return x;
        }
        final char c = key.charAt(position);
        // index into trie, where 'A' is 0-indexed
        return get(x.next[c - ASCII_UPPER_A], key, position + 1);
    }

    public void put(String key) {
        root = put(root, key, 0);
    }

    private Node put(Node x, String key, int position) {
        if (x == null) {
            x = new Node();
        }
        if (key.length() == position) {
            // End the recursion if we've gone deep enough
            return x;
        }

        final char c = key.charAt(position);
        final int triePos = c - ASCII_UPPER_A;
        x.next[triePos] = put(x.next[triePos], key, position + 1);
        return x;
    }

    /// Return true if {key} is contained in the Trie
    public boolean keyExists(String key) {
        Node x = get(root, key, 0);
        return (x != null);
    }
}
