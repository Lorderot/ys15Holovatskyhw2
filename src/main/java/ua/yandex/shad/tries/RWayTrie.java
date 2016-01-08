package ua.yandex.shad.tries;

import ua.yandex.shad.collections.DynamicList;

import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

public class RWayTrie implements Trie {
    /*R is the power of alphabet*/
    public static final int R = 26;
    Node root = new Node(0, "");
    private int size = 0;
    static class Node {
        Node[] next = new Node[R];
        private Tuple tuple;
        private Node(int weight, String name) {
            tuple = new Tuple(weight, name);
        }
        private Node(String name) {
            tuple = new Tuple(name);
        }

        private void setWeight(int value) {
            tuple.setWeight(value);
        }

        int getWeight() {
            return tuple.getWeight();
        }

        private String getName() {
            return tuple.getName();
        }
    }

    private static class LazySearch implements Iterable<String> {
        private DynamicList<Node> nodesContainer;
        private Iterator<Node> nextNodes;

        private LazySearch(Node prefix) throws IllegalArgumentException {
            if (prefix == null) {
                throw new IllegalArgumentException();
            }
            nodesContainer = new DynamicList<>();
            nextNodes = nodesContainer.iterator();
            if (!prefix.getName().equals("")) {
                nodesContainer.add(prefix);
            } else {
                addNextNodes(prefix);
            }
        }

        private void addNextNodes(Node from) {
            for (Node i : from.next) {
                if (i != null) {
                    nodesContainer.add(i);
                }
            }
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                @Override
                public boolean hasNext() {
                    return nextNodes.hasNext();
                }

                @Override
                public String next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    while (true) {
                        Node current = nextNodes.next();
                        addNextNodes(current);
                        if (current.getWeight() != Tuple.NULL) {
                            return current.getName();
                        }
                    }
                }
            };
        }
    }

    @Override
    public void add(String word)
            throws IllegalArgumentException, NullPointerException {
        if (word == null) {
            throw new NullPointerException();
        }
        if (word.equals("")) {
            return;
        }

        if (!checkWordCorrectness(word)) {
            throw new IllegalArgumentException("word contain "
                    + "non-English symbol");
        }
        /*tuple that corresponds symbol in the word*/
        Node currentTuple = root;
        int length = word.length();
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);

        /*if it's necessary, create new Tuple*/
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = wordLowerCase.charAt(i);
            current.append(c);
            if (currentTuple.next[c - 'a'] == null) {
                currentTuple.next[c - 'a'] = new Node(current.toString());
            }
            currentTuple = currentTuple.next[c - 'a'];
        }
        /*the tuple that corresponds the last symbol in word should have
        * not negative weight*/
        if (currentTuple.getWeight() == Tuple.NULL) {
            currentTuple.setWeight(length);
            size++;
        }
    }

    @Override
    public boolean contains(String word)
            throws NullPointerException, IllegalArgumentException {
        if (word == null) {
            throw new NullPointerException();
        }

        if (!checkWordCorrectness(word)) {
            throw new IllegalArgumentException("word contain "
                    + "non-English symbol");
        }

        /*The way of Tuples that corresponds to word*/
        Node[] wordTuples = null;

        wordTuples = wordWayInTrie(word);

        /*if word = "", wordWayInTrie() will return root*/
        int length;
        if (word.length() != 0) {
            length = word.length();
        } else {
            length = 1;
        }

        /*check the weight of the last Tuple*/
        if (wordTuples.length != length
                || wordTuples[length - 1].getWeight() == Tuple.NULL) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(String word)
            throws IllegalArgumentException, NullPointerException {
        /*check whether trie contains such word*/
        if (!contains(word)) {
            return false;
        }

        /*check whether word isn't empty*/
        if (word.length() == 0) {
            throw new IllegalArgumentException("Deleting empty word is equal "
                    + "to deleting trie. If you want to delete trie, do this in"
                    + "another way");
        }

        /*The way of Tuples that corresponds to word*/
        Node[] wordTuples = wordWayInTrie(word);
        int length = word.length();
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);
        size--;
        /*Delete mark which means existing the word from the last tuple */
        wordTuples[length - 1].setWeight(Tuple.NULL);
        /*Delete tuple if it has not any child and isn't end of another word*/
        for (int i = length - 1; i >= 0; i--) {
            boolean empty = true;
            for (int j = 0; j < R; j++) {
                if (wordTuples[i].next[j] != null) {
                    empty = false;
                }
            }
            if (empty) {
                if (wordTuples[i].getWeight() == Tuple.NULL) {
                    int index = wordLowerCase.charAt(i) - 'a';
                    if (i >= 1) {
                        wordTuples[i - 1].next[index] = null;
                    } else {
                        root.next[index] = null;
                    }
                }
            } else {
                break;
            }
        }
        return true;
    }

    @Override
    public Iterable<String> words()
    throws IllegalArgumentException, NullPointerException {
        Iterable<String> iterator = wordsWithPrefix("");
        return iterator;
    }

    @Override
    public Iterable<String> wordsWithPrefix(String prefix)
            throws IllegalArgumentException, NullPointerException {
        if (prefix == null) {
            throw new NullPointerException();
        }

        if (!checkWordCorrectness(prefix)) {
            throw new IllegalArgumentException("word contain "
                    + "non-English symbol");
        }

        Node[] prefixWayInTrie = wordWayInTrie(prefix);

        if (prefixWayInTrie.length == 0) {
            return null;
        }

        final Node lastNodeInPrefix =
                prefixWayInTrie[prefixWayInTrie.length - 1];
        return new LazySearch(lastNodeInPrefix);
    }

    @Override
    public int size() {
        return size;
    }

    /*check whether all symbols are from English alphabet*/
    private boolean checkWordCorrectness(String word) {
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);
        for (int i = 0; i < word.length(); i++) {
            char c = wordLowerCase.charAt(i);
            if (c < 'a' || c > 'z') {
                return false;
            }
        }
        return true;
    }

    /*Return the Tuples way that corresponds to the word.
     If any Tuple does not exist, return the empty way.
      Throw exception if word with non-English symbols*/
    private Node[] wordWayInTrie(String word) {

        if (word.equals("")) {
            Node[] wordTuples = {root};
            return wordTuples;
        }

        int length = word.length();
        Node[] wordTuples = new Node[length];
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);
        /*build the way of Tuples that corresponds the word*/
        char c = wordLowerCase.charAt(0);
        wordTuples[0] = root.next[c - 'a'];
        int i = 1;
        while (wordTuples[i - 1] != null && i < length) {
            c = wordLowerCase.charAt(i);
            wordTuples[i] = wordTuples[i - 1].next[c - 'a'];
            i++;
        }
        if (wordTuples[length - 1] == null) {
            return new Node[0];
        }
        return wordTuples;
    }
}
