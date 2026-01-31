package com.copiloto.addblock.vpn;

/**
 * Enum representing the current state of the VPN service.
 */
public enum VpnState {
    /**
     * VPN is not running.
     */
    STOPPED,

    /**
     * VPN is in the process of starting.
     */
    STARTING,

    /**
     * VPN is running and active.
     */
    RUNNING,

    /**
     * VPN encountered an error.
     */
    ERROR
}
