package de.jme;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class DummyTest {

    // assertThat() gibt es seit Junit 4.4 und hört sich für mich recht interessant an (Fluent Interfaces)
    //   http://stefanroock.blogspot.de/2008/03/junit-44-assertthat.html
    //   http://code.google.com/p/hamcrest/wiki/Tutorial
    //   http://stackoverflow.com/questions/1701113/why-should-i-use-hamcrest-matcher-and-assertthat-instead-of-traditional-assert
    //   http://www.vogella.com/articles/JUnit/article.html#juniteclipse_code

    @Test public void dummyTest() {
        // Die 3 folgenden machen alle das Selbe
        assertThat(1+2, is(3));
        assertThat(1+2, equalTo(3));
        assertThat(1+2, is(equalTo(3)));

        // Optional mit Fehlertext
        assertThat("Fehlernachricht", 3+4, equalTo(7));

        // Größer / Kleiner, es gibt auch greaterThanOrEqualTo() und lessThanOrEqualTo()
        assertThat(2, greaterThan(1));
        assertThat(4, lessThan(5));

        // Mehrere Bedingungen Kombinieren mit anyOf() und allOf()
        assertThat(3, anyOf(is(1), is(3)));
        assertThat(3, allOf(greaterThan(2), lessThan(4)));
        assertThat(3, allOf(greaterThan(2), lessThan(4)));

        // Stringvergleiche
        assertThat("Tralala", equalTo("Tralala"));
        assertThat("Tralala", containsString("ala"));
        assertThat("Tralala", stringContainsInOrder(Arrays.asList("ra", "ala")));

        // Prüfung, ob eine Liste bestimmte Elementen beinhaltet
        List<String> list = Arrays.asList("One", "a", "Two", "b", "Three", "c");
        assertThat(list, hasItems("a", "b", "c")); // der Unterschied zwischen hasItems() und contains() ist mir nicht klar..
    }

    @Test public void dummyTest2() {
        // Das ist eher die traditionelle Art. actual / expected sind hier anders herum als bei assertThat
        assertTrue(true);
        assertTrue("Fehlernachricht", true);
        assertFalse(false);
        assertFalse("Fehlernachricht", false);
        assertEquals(1, 1);
        assertEquals("Fehlernachricht", 1, 1);
        assertNotEquals(2, 3);
        assertNotEquals("Fehlernachricht", 2, 3);
    }

}
