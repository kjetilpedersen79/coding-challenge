package io.bankbridge.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BanksRemoveCallsTest {

    @Test
    public void thatHandleThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> new BanksRemoteCalls().handle(null, null));
    }

}
