package ua.yandex.shad.tries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RWayTrie implements Trie {
    /*R is the power of alphabet*/
    public static final int R = 26;

    //public static final int Null = -1;
    private Tuple root = new Tuple(0);
    private int size = 0;

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
        Tuple currentTuple = root;
        int length = word.length();
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);

        /*if it's necessary, create new Tuple*/
        for (int i = 0; i < length - 1; i++) {
            char c = wordLowerCase.charAt(i);
            if (currentTuple.next(c - 'a') == null) {
                currentTuple.setChild(c - 'a', new Tuple());
            }
            currentTuple = currentTuple.next(c - 'a');
        }
        /*the tuple that corresponds the last symbol in word should have
        * not negative weight*/
        char c = wordLowerCase.charAt(length - 1);
        if (currentTuple.next(c - 'a') == null) {
            currentTuple.setChild(c - 'a', new Tuple(length));
            size++;
        }
        else {
            currentTuple = currentTuple.next(c - 'a');
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
        Tuple[] wordTuples = null;
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
        Tuple[] wordTuples = checkWordExisting(word);
        int length = word.length();
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);
        size--;
        /*Delete mark which means existing the word from the last tuple */
        wordTuples[length - 1].setWeight(Tuple.NULL);
        /*Delete tuple if it has not any child and isn't end of another word*/
        for (int i = length - 1; i >= 0; i--) {
            boolean empty = true;
            for (int j = 0; j < R; j++) {
                if (wordTuples[i].next(j) != null) {
                    empty = false;
                }
            }
            if (empty) {
                if (wordTuples[i].getWeight() == Tuple.NULL) {
                    int index = wordLowerCase.charAt(i) - 'a';
                    if (i >= 1) {
                        wordTuples[i - 1].setChild(index, null);
                    } else {
                        root.setChild(index, null);
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

        Tuple[] prefixTuples = checkWordExisting(prefix);
        if (prefixTuples.length < prefix.length()) {
            return new ArrayList<String>();
        }
        String prefixLowerCase = prefix.toLowerCase(Locale.ENGLISH);
        int length;
        if (prefix.length() != 0) {
            length = prefix.length();
        } else {
            length = 1;
        }

        List<String> list = new ArrayList<String>();
        /*find all word which begins from prefix*/
        gatherWords(prefixTuples[length - 1], prefixLowerCase, list);
        return list;
    }

    @Override
    public int size() {
        return size;
    }

    public Tuple getRoot() {
        return root;
    }

    /*if null is set, trie will be deleted*/
    public void setRoot(Tuple value) {
        this.root = value;
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
    private Tuple[] checkWordExisting(String word)
            throws NullPointerException, IllegalArgumentException {
        if (word == null) {
            throw new NullPointerException();
        }

        if (word.equals("")) {
            Tuple[] wordTuples = {root};
            return wordTuples;
        }
        checkWordCorrectness(word);

        int length = word.length();
        Tuple[] wordTuples = new Tuple[length];
        String wordLowerCase = word.toLowerCase(Locale.ENGLISH);
        /*build the way of Tuples that corresponds the word*/
        char c = wordLowerCase.charAt(0);
        wordTuples[0] = root.next(c - 'a');
        int i = 1;
        while (wordTuples[i - 1] != null && i < length) {
            c = wordLowerCase.charAt(i);
            wordTuples[i] = wordTuples[i - 1].next(c - 'a');
            i++;
        }
        if (wordTuples[length - 1] == null) {
            return new Tuple[0];
        }
        return wordTuples;
    }

    /*find all words with prefix recursively*/
    private void gatherWords(Tuple top, String prefix, List<String> list) {
        if (top.getWeight() > 0) {
            list.add(prefix);
        }
        for (int i = 0; i < R; i++) {
            if (top.next(i) != null) {
                gatherWords(top.next(i), prefix + (char) ('a' + i), list);
            }
        }
    }
}
