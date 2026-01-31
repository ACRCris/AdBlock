package com.copiloto.addblock.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.copiloto.addblock.R;
import com.copiloto.addblock.ui.model.InstalledApp;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying installed apps with block toggle.
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private List<InstalledApp> apps = new ArrayList<>();
    private OnAppBlockToggleListener listener;

    public interface OnAppBlockToggleListener {
        void onAppBlockToggle(InstalledApp app);
    }

    public void setOnAppBlockToggleListener(OnAppBlockToggleListener listener) {
        this.listener = listener;
    }

    public void setApps(List<InstalledApp> newApps) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return apps.size();
            }

            @Override
            public int getNewListSize() {
                return newApps.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return apps.get(oldItemPosition).getPackageName()
                        .equals(newApps.get(newItemPosition).getPackageName());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                InstalledApp oldApp = apps.get(oldItemPosition);
                InstalledApp newApp = newApps.get(newItemPosition);
                return oldApp.getPackageName().equals(newApp.getPackageName())
                        && oldApp.isBlocked() == newApp.isBlocked();
            }
        });

        this.apps = new ArrayList<>(newApps);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        InstalledApp app = apps.get(position);
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iconView;
        private final TextView labelView;
        private final TextView packageView;
        private final Switch blockSwitch;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.app_icon);
            labelView = itemView.findViewById(R.id.app_label);
            packageView = itemView.findViewById(R.id.app_package);
            blockSwitch = itemView.findViewById(R.id.block_switch);
        }

        void bind(InstalledApp app) {
            iconView.setImageDrawable(app.getIcon());
            labelView.setText(app.getLabel());
            packageView.setText(app.getPackageName());

            // Remove listener temporarily to avoid triggering during setChecked
            blockSwitch.setOnCheckedChangeListener(null);
            blockSwitch.setChecked(app.isBlocked());

            blockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onAppBlockToggle(app);
                }
            });

            // Also allow clicking the whole item to toggle
            itemView.setOnClickListener(v -> {
                blockSwitch.setChecked(!blockSwitch.isChecked());
            });
        }
    }
}
