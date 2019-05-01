/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class RWayTrie<Value> {
    private static final int RADIX = 25; // uppercase A through Z, Q == Qu
    private Node root = new Node();
    // !!! SW handle q -> qu edge case

    private static class Node {
        private Object value;
        private Node[] next = new Node[RADIX];
    }

    public void put(String word, Value value) {
        root = put(root, word, value, 0);
    }

    private Node put(Node x, String key, Value val, int pos) {
        if (x == null) x = new Node();
        if (pos == key.length()) {
            x.value = val;
            return x;
        }
        char c = key.charAt(pos);
        x.next[c] = put(x, key, val, pos + 1);
        return x;
    }

    public boolean contains(String word) {
        return get(word) != null;
    }

    public Value get(String word) {
        Node x = get(root, word, 0);
        if (x == null) {
            return null;
        }
        return (Value) x.value;
    }

    private Node get(Node x, String key, int pos) {
        if (x == null) {
            return null;
        }
        if (pos == key.length()) {
            return x;
        }
        char c = key.charAt(pos);
        return get(x.next[c], key, pos + 1);
    }
}
