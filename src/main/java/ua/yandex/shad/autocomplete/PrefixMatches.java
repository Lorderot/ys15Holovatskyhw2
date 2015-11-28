package ua.yandex.shad.autocomplete;

import ua.yandex.shad.tries.RWayTrie;
import ua.yandex.shad.tries.Trie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PrefixMatches {
    /*default setting for wordsWithPrefix(String, int)*/
    private static final int N = 3;

    private Trie trie;

    public PrefixMatches(Trie trie) {
            this.trie = trie;
    }

    public PrefixMatches() {
        trie = new RWayTrie();
    }

    public int load(String... strings) throws NullPointerException {
        if (strings == null) {
            throw new NullPointerException();
        }

        int count = 0;
        /*In every string search for words with length > 2. Assume that words
        * are separated just with space.*/
        String[] add;
        int i = 0;
        int error = 0;
        while (i < strings.length) {
            while (i < strings.length && strings[i] == null) {
                i++;
            }
            if (i >= strings.length) {
                break;
            }

            add = strings[i].split(" ");
            for (int j = 0; j < add.length; j++) {
                if (add[j].length() > 2) {
                    /*count if word is added (no exception).*/
                    try {
                        trie.add(add[j]);
                        count++;
                    } catch (IllegalArgumentException e) {
                        error++;
                    }
                }
            }
            i++;
        }
        if (error != 0) {
            System.out.println("Cannot add word with non-English symbols: "
                    + error + " words aren't added");
        }
        return count;
    }

    public boolean contains(String word) throws NullPointerException {
        if (word == null) {
            throw new NullPointerException();
        }
        return trie.contains(word);
    }

    public boolean delete(String word)
            throws IllegalArgumentException, NullPointerException {
        return trie.delete(word);
    }

    public Iterable<String> wordsWithPrefix(String pref) {
        return wordsWithPrefix(pref, N);
    }

    public Iterable<String> wordsWithPrefix(String pref, int k)
            throws NullPointerException {
        if (pref == null) {
            throw new NullPointerException();
        }
        /*all words with appropriate prefix*/
        Iterable<String> allWordsWithPrefix = trie.wordsWithPrefix(pref);
        List<String> sorted = new ArrayList<String>();
        Comparator<String> myComparator = new LengthComparator();
        /*sample of words with difference length.*/
        List<String> words = new ArrayList<String>();

        for (String s : allWordsWithPrefix) {
            sorted.add(s);
        }

        /*sort words:
        * 1st priority by length
        * 2nd priority by alphabet*/
        sorted.sort(myComparator);
        /*find the least words with k difference length*/
        int count = 0;
        int maxLength = 0;
        for (String s : sorted) {
            if (s.length() <= maxLength) {
                words.add(s);
            } else {
                if (count < k) {
                    maxLength = s.length();
                    count++;
                    words.add(s);
                }
            }
        }
        return words;
    }

    public long size() {
        return trie.size();
    }

    /*own comparator to compare word's length*/
    private static class LengthComparator implements Comparator<String> {
        public int compare(String first, String second) {
            if (first.length() != second.length()) {
                return first.length() - second.length();
            } else {
                return first.compareTo(second);
            }
        }
    }


}
