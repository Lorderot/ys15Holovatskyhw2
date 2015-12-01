package ua.yandex.shad.tries;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class TestRWayTrie {
    private RWayTrie trie = new RWayTrie();

    @Before
    public void initializingBeforeEachTest() {
        this.trie = new RWayTrie();
    }

    @Test
    public void testAdd_CheckTheWeightOfAddedTuple() {
        String word = "atestz";
        trie.add(word);
        RWayTrie.Node temporary = trie.root;
        int i;
        for (i = 0; i < word.length() - 1; i++) {
            temporary = temporary.next[word.charAt(i) - 'a'];
            assertEquals(Tuple.NULL, temporary.getWeight());
        }
        temporary = temporary.next[word.charAt(i) - 'a'];
        assertNotEquals(Tuple.NULL, temporary.getWeight());
    }

    @Test
    public void testAdd_CheckTheWeightChangingAfterAdding() {
        String existing= "reference";
        String added = "references";

        trie.add(existing);
        trie.add(added);

        RWayTrie.Node temporary = trie.root;
        int i;
        for (i = 0; i < existing.length() - 1; i++) {
            temporary = temporary.next[existing.charAt(i) - 'a'];
            assertEquals(Tuple.NULL, temporary.getWeight());
        }
        temporary = temporary.next[existing.charAt(i) - 'a'];
        assertNotEquals(Tuple.NULL, temporary.getWeight());
    }

    @Test
    public void testAdd_CheckTheWeightOfTheLastAddedTuple() {
        String existing = "references";
        String added = "reference";

        trie.add(existing);
        trie.add(added);

        RWayTrie.Node temporary = trie.root;
        int i;
        for (i = 0; i < added.length() - 1; i++) {
            temporary = temporary.next[added.charAt(i) - 'a'];
            assertEquals(Tuple.NULL, temporary.getWeight());
        }
        temporary = temporary.next[added.charAt(i) - 'a'];
        assertNotEquals(Tuple.NULL, temporary.getWeight());
    }

    @Test
    public void testAdd_emptyWord() {
        String word = "";
        trie.add(word);
    }

    @Test(expected = NullPointerException.class)
    public void testAdd_NullWord() {
        String word = null;
        trie.add(word);
    }

    @Test
    public void testAdd_WordWithUpperCaseLetter() {
        String word = "tEsT";
        trie.add(word);
        word = word.toLowerCase(Locale.ENGLISH);
        RWayTrie.Node temporary = trie.root;
        for (int i = 0; i < word.length(); i++) {
            temporary = temporary.next[word.charAt(i) - 'a'];
            assertNotNull(temporary);
        }
    }

    @Test
    public void testAdd_WithHugeAmountOfWordsFromDictionary() {
        //Dictionary Length = 333 333; n should be < 333 333
        int n = 3000;
        try {
            addingHugeAmountOfWordsFromDictionary(n);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        int result = trie.size();
        assertEquals(n, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_WordWithSymbolLeftOfEnglishAlphabet() {
        char[] word = {'a' - 1, 'a' - 2, 'a' -3};
        trie.add(word.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_WordWithSymbolRightOfEnglishAlphabet () {
        char[] word = {'z' + 1, 'z' + 2, 'z' + 3};
        trie.add(word.toString());
    }

    @Test
    public void testContains_WordWithUppercaseLetter() {
        String word = "WoRdWitHUpperCaseLetter";
        trie.add(word);
        boolean result = trie.contains(word);
        assertEquals(true, result);
    }

    @Test
    public void testContains_TrieDoesNotContainWord() {
        Boolean result;
        String inTrie = "test";
        String notInTrie = "tests";

        trie.add(inTrie);
        result = trie.contains(notInTrie);

        assertEquals(false, result);
    }

    @Test
    public void testContains_EmptyWord() {
        String word = "";
        assertEquals(true, trie.contains(word));
    }

    @Test
    public void testContains_TrieContainsLongerWord() {
        String exist = "references";
        String contain = "reference";
        trie.add(exist);
        assertEquals(false, trie.contains(contain));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContains_WordWithNonEnglishSymbols () {
        String word = "word  with spaces";
        trie.contains(word);
    }

    @Test(expected = NullPointerException.class)
    public void testContains_NullWord() {
        String word = null;
        trie.contains(word);
    }

    @Test
    public void testDelete_WordWithUppercaseLetter() {
        String word = "UPpeRCaSe";

        trie.add(word);
        boolean result = trie.delete(word);
        assertEquals(true, result);

        word = word.toLowerCase(Locale.ENGLISH);
        Tuple deletedTuple = trie.root.next[word.charAt(0) - 'a'];
        assertEquals(null, deletedTuple);
    }

    @Test
    public void testDelete_TrieDoesNotContainWord() {
        String word = "word";
        boolean result = trie.delete(word);
        assertEquals(false, result);
    }

    @Test
    public void testDelete_CheckTheWeightOfTheLastTuple() {
        String delete = "reference";
        String exist = "references";

        trie.add(delete);
        trie.add(exist);
        boolean result = trie.delete(delete);

        RWayTrie.Node temporary = trie.root;
        int i;
        char c;
        for (i = 0; i < delete.length() - 1; i++) {
            c = delete.charAt(i);
            temporary = temporary.next[c - 'a'];
        }
        c = delete.charAt(i);
        temporary = temporary.next[c - 'a'];
        assertEquals(Tuple.NULL, temporary.getWeight());
        assertEquals(true, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelete_EmptyWord() {
        String word = "";
        boolean result = trie.delete(word);
        assertEquals(false, result);
    }

    @Test(expected = NullPointerException.class)
    public void testDelete_NullWord() {
        String word = null;
        trie.add(word);
    }

    @Test
    public void testDelete_CheckTheReferenceOfAppropriatedTuple () {
        String delete = "references";
        String exist = "reference";

        trie.add(delete);
        trie.add(exist);
        boolean result = trie.delete(delete);
        assertEquals(true, result);

        RWayTrie.Node temporary = trie.root;
        for (int i = 0; i < exist.length(); i++) {
            char c = exist.charAt(i);
            temporary = temporary.next[c - 'a'];
            assertNotEquals(null, temporary);
        }

        for (int i = exist.length(); i < delete.length(); i++) {
            char c = delete.charAt(i);

            temporary = temporary.next[c - 'a'];
            assertEquals(null, temporary);
        }
    }

    @Test
    public void testDelete_WithHugeAmountOfWordsFromDictionary() {
        //Dictionary Length = 333 333; n should be < 333 333
        int n = 3000;
        int result;
        try {
            addingHugeAmountOfWordsFromDictionary(n);
            result = deletingHugeAmountOfWordsFromDictionary(n);
            assertEquals(n, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelete_WordWithNonEnglishSymbols() {
        String word = "word$";
        trie.delete(word);
    }

   @Test
    public void testSize_EmptyTrie() {
        assertEquals(0, trie.size());
    }

    @Test
    public void testSize_AfterAddingTheSameWord() {
        trie.add("Reference");
        trie.add("reference");
        assertEquals(1, trie.size());
    }

    @Test
    public void testSize_AfterAddingAndDeleting() {
        trie.add("reference");
        trie.add("references");
        trie.delete("reference");
        assertEquals(1, trie.size());
    }

    @Test
    public void testSize_WithSimilarWordsInTrie() {
        trie.add("reference");
        trie.add("references");
        trie.add("refer");
        trie.add("referencing");
        trie.add("ref");
        assertEquals(5, trie.size());
    }

    @Test
    public void testWordsWithPrefix_EmptyTrie() {
        String prefix = "prefix";
        Iterable<String> container = trie.wordsWithPrefix(prefix);
        int size = 0;
        for (String s : container) {
            size++;
        }
        assertEquals(0, size);
    }

    @Test
    public void testWordsWithPrefix_VerifyingAmountOfWordsWithPrefix() {
        trie.add("first");
        trie.add("second");
        trie.add("third");
        trie.add("fourth");
        trie.add("fifth");
        trie.add("sixth");
        trie.add("f");
        trie.add("fif");

        Iterable<String> container = trie.words();
        assertNotNull(container);

        int count = 0;
        for(String s : container) {
            count++;
        }

        assertEquals(trie.size(), count);
    }

    @Test
    public void testWordsWithPrefix_VerifyingTheOrderOfWords() {
        String prefix = "you";
        String[] input = {"you", "your", "young", "yours", "youth", "younker",
                "youngish",  "youngster"};
        for (String i : input) {
            trie.add(i);
        }

        Iterable<String> container = trie.wordsWithPrefix(prefix);
        assertNotNull(container);

        int i = 0;
        for(String s : container) {
            assertEquals(input[i++], s);
        }
    }

    @Test
    public void testWords_CheckIfReturnsAllWords() {
        String[] input = {"you", "your", "young", "yours", "youth", "younker",
                "youngish",  "youngster", "let", "write", "a", "little", "bit",
                "more", "words", "maybe", "there", "is", "enough", "words", "now"};
        for (String i : input) {
            trie.add(i);
        }

        Iterable<String> container = trie.words();
        assertNotNull(container);

        int count = 0;
        for (String i : container) {
            count++;
        }
        assertEquals(trie.size(), count);
    }

    @Test
    public void testWordsWithPrefix_GettingTwoIteratorsWithDifferentPrefix() {
        String[] input = {"you", "your", "young", "yours", "youth", "younker",
                "youngish",  "youngster", "let", "write", "a", "little", "bit",
                "more", "words", "maybe", "there", "is", "enough", "words", "now"};
        for (String i : input) {
            trie.add(i);
        }

        Iterable<String> container1 = trie.wordsWithPrefix("you");
        assertNotNull(container1);
        int result1 = 8;

        Iterable<String> container2 = trie.wordsWithPrefix("l");
        assertNotNull(container2);
        int result2 = 2;

        int count = 0;
        for (String i : container1) {
            count++;
        }
        assertEquals(result1, count);

        count = 0;
        for (String i : container2) {
            count++;
        }
        assertEquals(result2, count);
    }

    @Test(expected = NullPointerException.class)
    public void testWordsWithPrefix_NullWord() {
        String word = null;
        trie.wordsWithPrefix(word);
    }

    @Test
    public void testWordsWithPrefix_NoWordsWithSuchPrefixInTrie() {
        String prefix = "references";

        trie.add("reference");
        trie.add("refer");
        trie.add("referee");

        Iterable<String> container = trie.wordsWithPrefix(prefix);
        int size = 0;
        for (String s : container) {
            size++;
        }
        assertEquals(0, size);
    }

    @Test
    public void testWordsWithPrefix_ReturnedValue() {
        String prefix = "ref";

        trie.add(prefix);
        Iterable<String> container = trie.wordsWithPrefix(prefix);

        assertEquals(true, container instanceof Iterable);
        assertEquals("ref", container.iterator().next());
    }

    @Test
    public void testWordsWithPrefix_WithHugeAmountOfWordsFromDictionary() {
        int n = 3000;
        String prefix = "you";

        try {
            addingHugeAmountOfWordsFromDictionary(n);
            Iterable<String> container = trie.wordsWithPrefix(prefix);

            for(String s : container) {
                assertEquals(true, s.startsWith(prefix, 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWordsWithPrefix_PrefixWithNonEnglishSymbols() {
        String prefix = "my Own Word!";
        Iterable<String> container = trie.wordsWithPrefix(prefix);
    }

    private void addingHugeAmountOfWordsFromDictionary(int n)
            throws IOException {

        FileReader input = new FileReader(new File("TestDictionary.txt"));
        BufferedReader reader = new BufferedReader(input);
        int i = 0;
        String check;
        String line = "";
        do {
            check = reader.readLine().toLowerCase(Locale.ENGLISH);
            if (check != null) {
                for(int j = 0; j < check.length(); j++) {
                    if (check.charAt(j) >= 'a' && check.charAt(j) <= 'z')
                        line += check.charAt(j);
                    else {
                        if (check.charAt(j) == ' '
                                || check.charAt(j) == '\t') {
                            trie.add(line);
                            line = "";
                        }
                    }
                }
                trie.add(line);
            }
            i++;
        }while (check!=null && i < n);
    }

    private int deletingHugeAmountOfWordsFromDictionary(int n)
            throws IOException {

        FileReader input = new FileReader(new File("TestDictionary.txt"));
        BufferedReader reader = new BufferedReader(input);
        int count = 0;
        int i = 0;
        String check;
        String line = "";
        do {
            check = reader.readLine().toLowerCase(Locale.ENGLISH);
            if (check != null) {
                for(int j = 0; j < check.length(); j++) {
                    if (check.charAt(j) >= 'a' && check.charAt(j) <= 'z')
                        line += check.charAt(j);
                    else {
                        if (check.charAt(j) == ' '
                                || check.charAt(j) == '\t') {
                            if (line != "") {
                                if (trie.delete(line)) {
                                    count++;
                                }
                                line = "";
                            }
                        }
                    }
                }
                if (trie.delete(line)) {
                    count++;
                }
            }
            i++;
        }while (check!=null && i < n);
        return count;
    }


}

