package com.copiloto.addblock.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.copiloto.addblock.data.BlocklistRepository;
import com.copiloto.addblock.ui.model.InstalledApp;
import com.copiloto.addblock.util.Logger;
import com.copiloto.addblock.util.PackageUtils;
import com.copiloto.addblock.util.Threading;
import com.copiloto.addblock.vpn.FirewallVpnService;
import com.copiloto.addblock.vpn.VpnController;
import com.copiloto.addblock.vpn.VpnState;

import java.util.List;
import java.util.Set;

/**
 * ViewModel for the app list screen.
 * Handles loading apps, managing block state, and VPN state.
 */
public class   AppListViewModel extends AndroidViewModel implements FirewallVpnService.StateListener {

    private static final String TAG = "AppListViewModel";

    private final BlocklistRepository blocklistRepository;

    private final MutableLiveData<List<InstalledApp>> installedApps = new MutableLiveData<>();
    private final MutableLiveData<VpnState> vpnState = new MutableLiveData<>(VpnState.STOPPED);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> blockedCount = new MutableLiveData<>(0);

    public AppListViewModel(@NonNull Application application) {
        super(application);
        blocklistRepository = new BlocklistRepository(application);

        // Register for VPN state changes
        FirewallVpnService.setStateListener(this);
        vpnState.setValue(VpnController.getState());

        // Load apps on creation
        loadInstalledApps();
    }

    /**
     * Get the list of installed apps.
     */
    public LiveData<List<InstalledApp>> getInstalledApps() {
        return installedApps;
    }

    /**
     * Get the current VPN state.
     */
    public LiveData<VpnState> getVpnState() {
        return vpnState;
    }

    /**
     * Get loading state.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Get the count of blocked apps.
     */
    public LiveData<Integer> getBlockedCount() {
        return blockedCount;
    }

    /**
     * Load installed user apps in background.
     */
    public void loadInstalledApps() {
        isLoading.setValue(true);

        Threading.runOnBackground(() -> {
            List<InstalledApp> apps = PackageUtils.getInstalledUserApps(getApplication());
            Set<String> blocked = blocklistRepository.getBlockedPackages();

            // Mark blocked apps
            for (InstalledApp app : apps) {
                app.setBlocked(blocked.contains(app.getPackageName()));
            }

            Threading.runOnMain(() -> {
                installedApps.setValue(apps);
                blockedCount.setValue(blocked.size());
                isLoading.setValue(false);
                Logger.d(TAG, "Loaded " + apps.size() + " apps, " + blocked.size() + " blocked");
            });
        });
    }

    /**
     * Toggle the blocked state of an app.
     *
     * @param app The app to toggle
     */
    public void toggleAppBlocked(InstalledApp app) {
        boolean newState = !app.isBlocked();
        app.setBlocked(newState);

        blocklistRepository.setBlocked(app.getPackageName(), newState);
        blockedCount.setValue(blocklistRepository.getBlockedCount());

        Logger.d(TAG, "App " + app.getPackageName() + " blocked: " + newState);

        // If VPN is running, restart it to apply changes
        if (VpnController.isRunning()) {
            VpnController.restartIfRunning(getApplication());
        }
    }

    /**
     * Start the VPN firewall.
     */
    public void startFirewall() {
        if (blocklistRepository.getBlockedCount() == 0) {
            Logger.w(TAG, "No apps blocked, cannot start firewall");
            return;
        }
        VpnController.start(getApplication());
    }

    /**
     * Stop the VPN firewall.
     */
    public void stopFirewall() {
        VpnController.stop(getApplication());
    }

    /**
     * Toggle the firewall on/off.
     */
    public void toggleFirewall() {
        if (VpnController.isRunning()) {
            stopFirewall();
        } else {
            startFirewall();
        }
    }

    @Override
    public void onStateChanged(VpnState state) {
        Threading.runOnMain(() -> vpnState.setValue(state));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        FirewallVpnService.setStateListener(null);
    }
}
