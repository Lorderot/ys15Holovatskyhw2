package ua.yandex.shad.autocomplete;

import ua.yandex.shad.collections.DynamicList;
import ua.yandex.shad.tries.RWayTrie;
import ua.yandex.shad.tries.Trie;

import java.util.Iterator;

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
        DynamicList<String> words = new DynamicList<>();
        /*find the least words with k difference length*/
        int count = 0;
        int maxLength = 0;
        for (String s : allWordsWithPrefix) {
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
        final Iterator<String> iterator = words.iterator();
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return iterator;
            }
        };
    }

    public long size() {
        return trie.size();
    }
}
