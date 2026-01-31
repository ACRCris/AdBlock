package com.copiloto.addblock.ui;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.copiloto.addblock.R;
import com.copiloto.addblock.ui.adapters.AppListAdapter;
import com.copiloto.addblock.ui.viewmodel.AppListViewModel;
import com.copiloto.addblock.util.Logger;
import com.copiloto.addblock.vpn.VpnState;

/**
 * Main activity displaying the list of apps and firewall controls.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AppListViewModel viewModel;
    private AppListAdapter adapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button firewallButton;
    private ImageButton infoButton;
    private TextView statusText;
    private TextView blockedCountText;
    private View emptyView;

    private final ActivityResultLauncher<Intent> vpnPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Logger.d(TAG, "VPN permission granted");
                            viewModel.startFirewall();
                        } else {
                            Logger.w(TAG, "VPN permission denied");
                            Toast.makeText(this, "VPN permission required to block apps",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.apps_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        firewallButton = findViewById(R.id.firewall_button);
        infoButton = findViewById(R.id.info_button);
        statusText = findViewById(R.id.status_text);
        blockedCountText = findViewById(R.id.blocked_count_text);
        emptyView = findViewById(R.id.empty_view);
    }

    private void setupRecyclerView() {
        adapter = new AppListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnAppBlockToggleListener(app -> {
            viewModel.toggleAppBlocked(app);
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AppListViewModel.class);

        // Observe installed apps
        viewModel.getInstalledApps().observe(this, apps -> {
            if (apps != null) {
                adapter.setApps(apps);
                emptyView.setVisibility(apps.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(apps.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe VPN state
        viewModel.getVpnState().observe(this, this::updateVpnStateUI);

        // Observe blocked count
        viewModel.getBlockedCount().observe(this, count -> {
            blockedCountText.setText(getString(R.string.blocked_count, count));
        });
    }

    private void setupListeners() {
        firewallButton.setOnClickListener(v -> {
            VpnState state = viewModel.getVpnState().getValue();

            if (state == VpnState.RUNNING) {
                viewModel.stopFirewall();
            } else {
                requestVpnPermissionAndStart();
            }
        });

        infoButton.setOnClickListener(v -> showHowItWorksDialog());
    }

    private void showHowItWorksDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.how_it_works_title)
                .setMessage(Html.fromHtml(getString(R.string.how_it_works_content), Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(R.string.got_it, null)
                .show();
    }

    private void requestVpnPermissionAndStart() {
        Integer count = viewModel.getBlockedCount().getValue();
        if (count == null || count == 0) {
            Toast.makeText(this, R.string.no_apps_blocked, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            // Need to request VPN permission
            vpnPermissionLauncher.launch(intent);
        } else {
            // Permission already granted
            viewModel.startFirewall();
        }
    }

    private void updateVpnStateUI(VpnState state) {
        switch (state) {
            case STOPPED:
                statusText.setText(R.string.status_stopped);
                firewallButton.setText(R.string.start_firewall);
                firewallButton.setEnabled(true);
                break;
            case STARTING:
                statusText.setText(R.string.status_starting);
                firewallButton.setEnabled(false);
                break;
            case RUNNING:
                statusText.setText(R.string.status_running);
                firewallButton.setText(R.string.stop_firewall);
                firewallButton.setEnabled(true);
                break;
            case ERROR:
                statusText.setText(R.string.status_error);
                firewallButton.setText(R.string.start_firewall);
                firewallButton.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list in case apps were installed/uninstalled
        viewModel.loadInstalledApps();
    }
}
