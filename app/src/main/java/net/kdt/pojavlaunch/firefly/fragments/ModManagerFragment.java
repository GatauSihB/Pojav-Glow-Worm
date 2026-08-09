package net.kdt.pojavlaunch.firefly.fragments;

import static com.firefly.utils.ToastUtils.Toast;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.firefly.InstanceManager;
import net.kdt.pojavlaunch.firefly.R;
import net.kdt.pojavlaunch.firefly.Tools;
import net.kdt.pojavlaunch.firefly.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.firefly.value.launcherprofiles.MinecraftProfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Per-instance mod manager. Lists the mods of the currently selected profile
 * and lets the user enable/disable/delete them, or import new ones.
 */
public class ModManagerFragment extends Fragment {
    public static final String TAG = "ModManagerFragment";

    private static final String DISABLED_SUFFIX = ".disabled";

    private File mModsDir;
    private TextView mEmptyView;
    private RecyclerView mRecyclerview;
    private ModAdapter mAdapter;

    private final ActivityResultLauncher<String[]> mOpenModLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                    this::copyModFiles);

    public ModManagerFragment() {
        super(R.layout.fragment_mod_manager);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageButton mBackButton = view.findViewById(R.id.mod_manager_back_button);
        ImageButton mAddButton = view.findViewById(R.id.mod_manager_add_button);
        TextView mProfileNameView = view.findViewById(R.id.mod_manager_profile_name);
        mEmptyView = view.findViewById(R.id.mod_manager_empty_view);
        mRecyclerview = view.findViewById(R.id.mod_manager_list);

        MinecraftProfile profile = LauncherProfiles.getCurrentProfile();
        mModsDir = InstanceManager.getModsDir(profile);
        mProfileNameView.setText(profile.name == null ? "" : profile.name);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ModAdapter();
        mRecyclerview.setAdapter(mAdapter);

        mBackButton.setOnClickListener(v -> Tools.backToMainMenu(requireActivity()));
        mAddButton.setOnClickListener(v -> mOpenModLauncher.launch(new String[]{
                "application/java-archive", "application/zip", "application/octet-stream", "*/*"}));

        refreshList();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        if (mAdapter == null || mModsDir == null) return;
        List<ModFile> mods = new ArrayList<>();
        File[] files = mModsDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && (file.getName().endsWith(".jar")
                        || file.getName().toLowerCase(Locale.ROOT).endsWith(".jar" + DISABLED_SUFFIX))) {
                    mods.add(new ModFile(file));
                }
            }
        }
        mods.sort((a, b) -> a.displayName.compareToIgnoreCase(b.displayName));
        mAdapter.setMods(mods);
        boolean empty = mods.isEmpty();
        mEmptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        mRecyclerview.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void copyModFiles(Uri... uris) {
        if (uris == null || uris.length == 0) return;
        int imported = 0;
        for (Uri uri : uris) {
            try (InputStream input = requireContext().getContentResolver().openInputStream(uri)) {
                if (input == null) continue;
                String fileName = Tools.getFileName(requireContext(), uri);
                if (fileName == null || fileName.isEmpty()) fileName = "mod.jar";
                if (fileName.endsWith(DISABLED_SUFFIX)) {
                    fileName = fileName.substring(0, fileName.length() - DISABLED_SUFFIX.length());
                }
                if (!fileName.endsWith(".jar")) fileName = fileName + ".jar";
                File target = new File(mModsDir, fileName);
                if (target.exists()) target = new File(mModsDir, uniqueName(fileName));
                try (FileOutputStream output = new FileOutputStream(target)) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = input.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                }
                imported++;
            } catch (Exception e) {
                Toast(requireContext(), R.string.mods_import_failed);
            }
        }
        if (imported > 0) Toast(requireContext(), R.string.mods_imported);
        refreshList();
    }

    private String uniqueName(String fileName) {
        String base = fileName.substring(0, fileName.length() - 4);
        String ext = fileName.substring(fileName.length() - 4);
        int i = 1;
        File candidate;
        do {
            candidate = new File(mModsDir, base + " (" + i + ")" + ext);
            i++;
        } while (candidate.exists());
        return candidate.getName();
    }

    private void toggleMod(ModFile mod) {
        File from = mod.file;
        File to = mod.enabled
                ? new File(mModsDir, from.getName() + DISABLED_SUFFIX)
                : new File(mModsDir, from.getName().substring(0, from.getName().length() - DISABLED_SUFFIX.length()));
        if (from.renameTo(to)) refreshList();
        else Toast(requireContext(), R.string.mods_toggle_failed);
    }

    private void deleteMod(ModFile mod) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.mods_delete_title)
                .setMessage(getString(R.string.mods_delete_message, mod.displayName))
                .setPositiveButton(R.string.global_delete, (dialog, which) -> {
                    if (mod.file.delete()) {
                        Toast(requireContext(), R.string.mods_deleted);
                        refreshList();
                    } else {
                        Toast(requireContext(), R.string.mods_delete_failed);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private static class ModFile {
        final File file;
        final String displayName;
        final boolean enabled;

        ModFile(File file) {
            this.file = file;
            String name = file.getName();
            this.enabled = !name.endsWith(DISABLED_SUFFIX);
            if (this.enabled) {
                this.displayName = name;
            } else {
                this.displayName = name.substring(0, name.length() - DISABLED_SUFFIX.length());
            }
        }
    }

    private class ModAdapter extends RecyclerView.Adapter<ModAdapter.ModViewHolder> {

        private final List<ModFile> mMods = new ArrayList<>();

        @NonNull
        @Override
        public ModViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_mod_manager_item, parent, false);
            return new ModViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ModViewHolder holder, int position) {
            ModFile mod = mMods.get(position);
            holder.mNameView.setText(mod.displayName);
            holder.mStatusView.setText(mod.enabled ? R.string.mods_enabled : R.string.mods_disabled);
            holder.mStatusView.setAlpha(mod.enabled ? 1.0f : 0.5f);
            holder.mToggleButton.setText(mod.enabled ? R.string.mods_disable : R.string.mods_enable);
            holder.itemView.setOnClickListener(v -> toggleMod(mod));
            holder.mToggleButton.setOnClickListener(v -> toggleMod(mod));
            holder.mDeleteButton.setOnClickListener(v -> deleteMod(mod));
        }

        @Override
        public int getItemCount() {
            return mMods.size();
        }

        void setMods(List<ModFile> mods) {
            mMods.clear();
            mMods.addAll(mods);
            notifyDataSetChanged();
        }

        class ModViewHolder extends RecyclerView.ViewHolder {
            final TextView mNameView;
            final TextView mStatusView;
            final TextView mToggleButton;
            final ImageButton mDeleteButton;

            ModViewHolder(@NonNull View itemView) {
                super(itemView);
                mNameView = itemView.findViewById(R.id.mod_item_name);
                mStatusView = itemView.findViewById(R.id.mod_item_status);
                mToggleButton = itemView.findViewById(R.id.mod_item_toggle);
                mDeleteButton = itemView.findViewById(R.id.mod_item_delete);
            }
        }
    }
}
