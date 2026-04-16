package com.copiloto.addblock.vpn;
import static org.junit.Assert.*;
import org.junit.Test;
/**
 * Unit tests for VpnState enum.
 * These tests don't require Android context or Robolectric.
 */
public class VpnStateTest {
    @Test
    public void vpnState_hasAllExpectedValues() {
        VpnState[] states = VpnState.values();
        assertEquals(4, states.length);
    }
    @Test
    public void vpnState_containsStopped() {
        assertNotNull(VpnState.STOPPED);
        assertEquals("STOPPED", VpnState.STOPPED.name());
    }
    @Test
    public void vpnState_containsStarting() {
        assertNotNull(VpnState.STARTING);
        assertEquals("STARTING", VpnState.STARTING.name());
    }
    @Test
    public void vpnState_containsRunning() {
        assertNotNull(VpnState.RUNNING);
        assertEquals("RUNNING", VpnState.RUNNING.name());
    }
    @Test
    public void vpnState_containsError() {
        assertNotNull(VpnState.ERROR);
        assertEquals("ERROR", VpnState.ERROR.name());
    }
    @Test
    public void vpnState_valueOf_worksCorrectly() {
        assertEquals(VpnState.STOPPED, VpnState.valueOf("STOPPED"));
        assertEquals(VpnState.STARTING, VpnState.valueOf("STARTING"));
        assertEquals(VpnState.RUNNING, VpnState.valueOf("RUNNING"));
        assertEquals(VpnState.ERROR, VpnState.valueOf("ERROR"));
    }
    @Test
    public void vpnState_ordinal_isCorrect() {
        assertEquals(0, VpnState.STOPPED.ordinal());
        assertEquals(1, VpnState.STARTING.ordinal());
        assertEquals(2, VpnState.RUNNING.ordinal());
        assertEquals(3, VpnState.ERROR.ordinal());
    }
}
