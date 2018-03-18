package com.asuprun.metertracker.web.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Optional;

import static com.asuprun.metertracker.web.utils.Exceptions.unchecked;

public class ExceptionsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCheckedExceptionInLambda() {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Checked exception thrown");

        Optional.of("string").map(unchecked(s -> throwException()));
    }

    private String throwException() throws IOException {
        throw new IOException("Checked exception thrown");
    }
}
