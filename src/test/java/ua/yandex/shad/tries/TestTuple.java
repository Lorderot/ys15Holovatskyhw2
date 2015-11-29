package ua.yandex.shad.tries;

import org.junit.Test;

public class TestTuple {

    @Test(expected = IllegalArgumentException.class)
    public void testTuple_WithWeightLessThenNull() {
        new Tuple(-2, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWeight_WithNegativeWeight() {
        Tuple tuple = new Tuple("name");
        tuple.setWeight(-2);
    }
}

