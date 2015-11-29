package ua.yandex.shad.autocomplete;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Matchers;
import ua.yandex.shad.collections.DynamicList;
import ua.yandex.shad.tries.RWayTrie;

public class TestPrefixMatches {
    private PrefixMatches controlSystem;
    private RWayTrie trie;
    @Before
    public void initializingBeforeEachTest() {
        trie = mock(RWayTrie.class);
        controlSystem = new PrefixMatches(trie);
    }

    @Test
    public void testPrefixMatches_CheckDefaultConstructor() {
        controlSystem = new PrefixMatches();
        assertNotNull(controlSystem);
    }

    @Test
    public void testLoad_verifyCallingMethod() {
        String[] input = {"test with", "proper words",
                "for confidence",  "alphabet", "hello", "mine references", "references"};
        int result = 11;
        assertEquals(result, controlSystem.load(input));
        verify(trie, times(result)).add(anyString());
    }

    @Test
    public void testLoad_verifyCallingMethodWithAppropriateParameters() {
        String input = "MyWord";
        controlSystem.load(input);
        verify(trie).add(Matchers.eq(input));
    }

    @Test
    public void testLoad_WordsWithLessThanThreeSymbols() {
        String[] input = {"ok few", "tiny words",
                "such as previous", "to identify connections"};
        int result = 7;
        assertEquals(result, controlSystem.load(input));
        verify(trie, times(7)).add(anyString());
    }

    @Test
    public void testLoad_WordsWithUnknownSymbols() {
        String[] input = {"myown!", "aaa $little$ bi$t", "stra~nge words",
                "for^^ verifying&& result ooff load"};
        int result = 5;
        doThrow(new IllegalArgumentException()).when(trie).add("myown!");
        doThrow(new IllegalArgumentException()).when(trie).add("$little$");
        doThrow(new IllegalArgumentException()).when(trie).add("bi$t");
        doThrow(new IllegalArgumentException()).when(trie).add("stra~nge");
        doThrow(new IllegalArgumentException()).when(trie).add("for^^");
        doThrow(new IllegalArgumentException()).when(trie).add("verifying&&");

        assertEquals(result, controlSystem.load(input));
        verify(trie, times(11)).add(anyString());
    }

    @Test
    public void testLoad_StringsWithNullElements() {
        String[] input = new String[5];
        input[0] = "okay lets write some words";
        input[1] = "generally anything";
        input[3] = "cool";
        assertEquals(8, controlSystem.load(input));
    }

    @Test(expected = NullPointerException.class)
    public void testLoad_NullReference() {
        String[] input = null;
        controlSystem.load(input);
    }

    @Test
    public void testContains_VerifyCallingTrieMethod() {
        String word = "anything";
        boolean result = false;
        when(trie.contains(word)).thenReturn(result);
        controlSystem.contains(word);
        verify(trie, times(1)).contains(word);
    }

    @Test
    public void testContains_CheckTheReturningValue() {
        String word = "anything";
        boolean result = true;
        when(trie.contains(word)).thenReturn(result);
        assertEquals(result, controlSystem.contains(word));
    }

    @Test(expected = NullPointerException.class)
    public void testContains_NullReference() {
        String input = null;
        controlSystem.contains(input);
    }

    @Test
    public void testDelete_VerifyingResultCorrectness() {
        String word = "something";
        boolean result = true;
        when(trie.delete(word)).thenReturn(result);
        assertEquals(result, controlSystem.delete(word));
    }

    @Test
    public void testDelete_VerifyCallingMethod() {
        String word = "something";
        controlSystem.delete(word);
        verify(trie, times(1)).delete(word);
    }

    @Test(expected = NullPointerException.class)
    public void testDelete_NullReference() {
        String word = null;
        doThrow(new NullPointerException()).when(trie).delete(word);
                controlSystem.delete(word);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelete_EmptyWord() {
        String word = "";
        doThrow(new IllegalArgumentException()).when(trie).delete(word);
        controlSystem.delete(word);
    }

    @Test
    public void wordsWithPrefix_VerifyCallingMethod() {
        String prefix = "you";
        int k = 2;
        DynamicList<String> trieResult = new DynamicList<>();
        trieResult.add("you");
        when(trie.wordsWithPrefix(prefix)).thenReturn(trieResult);
        controlSystem.wordsWithPrefix(prefix, k);
        verify(trie, times(1)).wordsWithPrefix(prefix);
    }

    @Test
    public void wordsWithPrefix_CheckIfReturnsAllWordsWithThreeDifferentLength() {
        String prefix = "you";
        String[] input = {"you", "your", "young", "yours", "youth", "younker",
                "youngish",  "youngster"};
        int count = 5;
        DynamicList<String> trieResult = new DynamicList<>();

        for (String s : input) {
            trieResult.add(s);
        }

        when(trie.wordsWithPrefix(prefix)).thenReturn(trieResult);
        Iterable<String> actualResult = controlSystem.wordsWithPrefix(prefix);
        int size = 0;
        for(String s : actualResult) {
            assertEquals(true, s.startsWith(prefix));
            size++;
        }
        assertEquals(count, size);
    }

    @Test
    public void wordsWithPrefix_CheckIfReturnsAllWordsWithCertainAmountOfDifferentLength() {
        String prefix = "you";
        String[] input = {"you", "your", "yous", "young", "yours", "youth", "younker",
                "youward", "younler", "youngish",  "youngster"};
        int k = 4;
        int count = 9;
        DynamicList<String> trieResult = new DynamicList<>();

        for (String s : input) {
            trieResult.add(s);
        }

        when(trie.wordsWithPrefix(prefix)).thenReturn(trieResult);
        Iterable<String> actualResult = controlSystem.wordsWithPrefix(prefix, k);
        int size = 0;
        for(String s : actualResult) {
            assertEquals(true, s.startsWith(prefix));
            size++;
        }
        assertEquals(count, size);
    }


    @Test(expected = NullPointerException.class)
    public void wordsWithPrefix_NullReference() {
        String prefix = null;
        controlSystem.wordsWithPrefix(prefix, 5);
    }

    @Test
    public void testSize_CheckTheReturningValue() {
        when(trie.size()).thenReturn(10);
        assertEquals(10, controlSystem.size());
    }

    @Test
    public void testSize_VerifyCallingTrieMethod() {
        controlSystem.size();
        verify(trie,times(1)).size();
    }
}
