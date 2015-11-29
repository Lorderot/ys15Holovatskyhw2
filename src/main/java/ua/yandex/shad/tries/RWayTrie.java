package ua.yandex.shad.tries;

import ua.yandex.shad.collections.DynamicList;

import java.util.Locale;

public class RWayTrie implements Trie {
    /*R is the power of alphabet*/
    public static final int R = 26;
    Node root = new Node(0, "");
    private int size = 0;
    static class Node extends Tuple{
        Node[] next = new Node[R];
        private Node(int weight, String name) {
            super(weight, name);
        }
        private Node(String name) {
            super(name);
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
        checkWordCorrectness(word);
        /*tuple that corresponds symbol in the word*/
        Node currentTuple = root;
        int length = word.length();
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);

        /*if it's necessary, create new Tuple*/
        StringBuffer current = new StringBuffer();
        for (int i = 0; i < length - 1; i++) {
            char c = wordLowerCase.charAt(i);
            current.append(c);
            if (currentTuple.next[c - 'a'] == null) {
                currentTuple.next[c - 'a'] = new Node(current.toString());
            }
            currentTuple = currentTuple.next[c - 'a'];
        }
        /*the tuple that corresponds the last symbol in word should have
        * not negative weight*/
        char c = wordLowerCase.charAt(length - 1);
        current.append(c);
        if (currentTuple.next[c - 'a'] == null) {
            currentTuple.next[c - 'a'] = new Node(length, current.toString());
            size++;
        }
        else {
            currentTuple = currentTuple.next[c - 'a'];
            if (currentTuple.getWeight() == Tuple.NULL) {
                size++;
            }
            currentTuple.setWeight(length);
        }
    }

    @Override
    public boolean contains(String word)
            throws NullPointerException, IllegalArgumentException {
        /*The way of Tuples that corresponds to word*/
        Node[] wordTuples = null;
        try {
            wordTuples = checkWordExisting(word);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trie doesn't contain word "
                    + "with non-English symbols");
        }
        /*if word = "", checkWordExisting will return root*/
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
        Node[] wordTuples = checkWordExisting(word);
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
        Iterable<String> container = wordsWithPrefix("");
        return container;
    }

    @Override
    public Iterable<String> wordsWithPrefix(String prefix)
            throws IllegalArgumentException, NullPointerException {

        DynamicList<String> list = new DynamicList<>();
        DynamicList<Node> topNode = new DynamicList<>();
        if (prefix == "") {
            topNode.add(root);
        } else {
            Node[] prefixTuples = checkWordExisting(prefix);
            if (prefixTuples.length < prefix.length()) {
                return new DynamicList<>();
            }
            topNode.add(prefixTuples[prefixTuples.length - 1]);
        }

        /*find all word which begins from prefix*/
        gatherWords(topNode, list);
        return list;
    }

    @Override
    public int size() {
        return size;
    }

    /*check whether all symbols are from English alphabet*/
    private void checkWordCorrectness(String word)
            throws IllegalArgumentException, NullPointerException {
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);
        for (int i = 0; i < word.length(); i++) {
            char c = wordLowerCase.charAt(i);
            if (c < 'a' || c > 'z') {
                throw new IllegalArgumentException("word contain "
                        + "non-English symbol");
            }
        }
    }

    /*Return the Tuples way that corresponds to the word.
     If any Tuple does not exist, return the empty way.
      Throw exception if word with non-English symbols*/
    private Node[] checkWordExisting(String word)
            throws NullPointerException, IllegalArgumentException {
        if (word == null) {
            throw new NullPointerException();
        }

        if (word.equals("")) {
            Node[] wordTuples = {root};
            return wordTuples;
        }
        checkWordCorrectness(word);

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

    private void gatherWords(DynamicList<Node> previousTuples, DynamicList<String> list) {
        if (previousTuples.isEmpty()) {
            return;
        }
        DynamicList<Node> currentTuples = new DynamicList<>();
        for (Node i : previousTuples) {
            if (i.getWeight() > 0) {
                list.add(i.getName());
            }
            for (int j = 0; j < R; j++) {
                if (i.next[j] != null) {
                    currentTuples.add(i.next[j]);
                }
            }
        }
        gatherWords(currentTuples, list);
    }
}
