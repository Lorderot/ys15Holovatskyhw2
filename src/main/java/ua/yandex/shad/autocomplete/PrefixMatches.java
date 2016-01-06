package ua.yandex.shad.autocomplete;

import ua.yandex.shad.tries.RWayTrie;
import ua.yandex.shad.tries.Trie;

import java.util.Iterator;

public class PrefixMatches {
    /*default setting for wordsWithPrefix(String, int)*/
    private static final int N = 3;

    private Trie trie;

    private static class PrefixIterable implements Iterable<String> {
        private final Iterator<String> allWordsWithPrefix;
        private final int numberOfDifferentLengths;
        private PrefixIterable(Iterable<String> allWordsWithPrefix,
                               int numberOfDifferentLengths) {
            this.allWordsWithPrefix = allWordsWithPrefix.iterator();
            this.numberOfDifferentLengths = numberOfDifferentLengths;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                private int count = 0;
                private int maxLength = 0;
                private String next = null;
                @Override
                public boolean hasNext() {
                    if (count == numberOfDifferentLengths && (next == null
                            || next.length() > maxLength)) {
                        return false;
                    }
                    return allWordsWithPrefix.hasNext();
                }

                @Override
                public String next() {
                    String current = next;
                    if (current == null) {
                        current = allWordsWithPrefix.next();
                    }
                    next = allWordsWithPrefix.next();
                    if (current.length() > maxLength) {
                        maxLength = current.length();
                        count++;
                    }
                    if (count > numberOfDifferentLengths) {
                        current = null;
                    }
                    return current;
                }
            };
        }
    }

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
        /*iterator for words with appropriate prefix*/
        return new PrefixIterable(trie.wordsWithPrefix(pref), k);
    }

    public long size() {
        return trie.size();
    }
}
