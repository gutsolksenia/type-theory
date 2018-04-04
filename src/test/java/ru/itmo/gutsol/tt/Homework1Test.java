package ru.itmo.gutsol.tt;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Homework1Test {
    @Test
    public void testGetResult() {
        testGetResultWith("(\\x.\\y.x) (\\y.\\x. x y) f", "\\y.\\x.x y");
        testGetResultWith("(\\y.\\x.y) (\\x.x) r", "\\x.x");
    }

    private void testGetResultWith(String input, String expected) {
        assertEquals(Homework1.getResult(input), expected);
    }
}
