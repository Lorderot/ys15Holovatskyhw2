package ua.yandex.shad.tries;

import org.junit.Test;

public class TestTuple {

    @Test(expected = IllegalArgumentException.class)
    public void testTuple_WithWeightLessThenNull() {
        new Tuple(-2);
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void testNext_WithNegativeIndex() {
        Tuple tuple = new Tuple();
        tuple.next(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNext_WithIndexGreaterThenPowerOfAlphabet() {
        Tuple tuple = new Tuple();
        tuple.next(400);
    }

    @Test
    public void testSetChild_SetNull() {
        Tuple tuple = new Tuple();
        Tuple child = new Tuple();
        int indexes = 0;
        tuple.setChild(indexes, child);
        tuple.setChild(indexes, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetChild_WithNegativeIndex() {
        Tuple tuple = new Tuple();
        Tuple child = new Tuple();
        int index = -1;
        tuple.setChild(index, child);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetChild_WithIndexGreaterThenPowerOfAlphabet() {
        Tuple tuple = new Tuple();
        Tuple child = new Tuple();
        int index = 400;
        tuple.setChild(index, child);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetChild_SuchChildAlreadyExists() {
        Tuple tuple = new Tuple();
        Tuple child = new Tuple();
        int indexes = 0;
        tuple.setChild(indexes, child);
        tuple.setChild(indexes, child);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWeight_WithNegativeWeight() {
        Tuple tuple = new Tuple();
        tuple.setWeight(-2);
    }
}

