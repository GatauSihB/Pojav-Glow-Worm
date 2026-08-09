package net.kdt.pojavlaunch.firefly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import net.kdt.pojavlaunch.firefly.R;
import net.kdt.pojavlaunch.firefly.customcontrols.ControlData;
import net.kdt.pojavlaunch.firefly.customcontrols.ControlDrawerData;
import net.kdt.pojavlaunch.firefly.customcontrols.ControlJoystickData;
import net.kdt.pojavlaunch.firefly.customcontrols.ControlLayout;
import net.kdt.pojavlaunch.firefly.customcontrols.EditorExitable;
import net.kdt.pojavlaunch.firefly.prefs.LauncherPreferences;

import java.io.IOException;

public class CustomControlsActivity extends BaseActivity implements EditorExitable {
    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private LinearLayout mControlsList;
    private LinearLayout mActionsList;
    private ControlLayout mControlLayout;
    private SeekBar mScaleSeekBar;
    private TextView mScaleLabel;
    private TextView mScalePercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_controls);

        mControlLayout = findViewById(R.id.customctrl_controllayout);
        mDrawerLayout = findViewById(R.id.customctrl_drawerlayout);
        mDrawerView = findViewById(R.id.drawer_content); // The drawer content view
        mControlsList = findViewById(R.id.controls_list);
        mActionsList = findViewById(R.id.actions_list);
        ImageButton drawerButton = findViewById(R.id.drawer_button);

        mScaleSeekBar = findViewById(R.id.global_scale_seekbar);
        mScaleLabel = findViewById(R.id.scale_label);
        mScalePercent = findViewById(R.id.scale_percent);

        drawerButton.setOnClickListener(v -> mDrawerLayout.openDrawer(mDrawerView));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // Initialize scale from preferences
        int currentScale = (int) LauncherPreferences.PREF_BUTTONSIZE;
        mScaleSeekBar.setProgress(currentScale - 50); // SeekBar 0-150 maps to 50-200%
        updateScaleDisplay(currentScale);

        mScaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int scale = progress + 50; // Map 0-150 to 50-200%
                updateScaleDisplay(scale);
                LauncherPreferences.PREF_BUTTONSIZE = scale;
                LauncherPreferences.DEFAULT_PREF.edit().putInt("buttonscale", scale).apply();
                applyScaleToControls(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Build controls list (Add Controls section)
        String[] addControlsItems = getResources().getStringArray(R.array.menu_customcontrol_add_controls);
        for (int i = 0; i < addControlsItems.length; i++) {
            final int itemIndex = i;
            View itemView = getLayoutInflater().inflate(R.layout.drawer_menu_item, mControlsList, false);
            TextView tv = itemView.findViewById(R.id.drawer_item_text);
            tv.setText(addControlsItems[i]);
            itemView.setOnClickListener(v -> {
                onAddControlItemClick(itemIndex);
                mDrawerLayout.closeDrawers();
            });
            mControlsList.addView(itemView);
        }

        // Build actions list (Layout Actions section)
        String[] actionItems = getResources().getStringArray(R.array.menu_customcontrol_layout_actions);
        for (int i = 0; i < actionItems.length; i++) {
            final int itemIndex = i;
            View itemView = getLayoutInflater().inflate(R.layout.drawer_menu_item, mActionsList, false);
            TextView tv = itemView.findViewById(R.id.drawer_item_text);
            tv.setText(actionItems[i]);
            itemView.setOnClickListener(v -> {
                onActionItemClick(itemIndex);
                mDrawerLayout.closeDrawers();
            });
            mActionsList.addView(itemView);
        }

        mControlLayout.setModifiable(true);
        try {
            mControlLayout.loadLayout(LauncherPreferences.PREF_DEFAULTCTRL_PATH);
            // Apply current global scale after loading
            applyScaleToControls((int) LauncherPreferences.PREF_BUTTONSIZE);
        } catch (IOException e) {
            Tools.showError(this, e);
        }
    }

    private void updateScaleDisplay(int scale) {
        String scaleText = scale + "%";
        mScaleLabel.setText(scaleText);
        mScalePercent.setText(scaleText);
    }

    private void applyScaleToControls(int scale) {
        // Apply the global scale to all controls in real-time
        for (net.kdt.pojavlaunch.firefly.customcontrols.buttons.ControlInterface button : mControlLayout.getButtonChildren()) {
            button.applyGlobalScale(scale);
        }
        mControlLayout.getLayout().scaledAt = scale;
    }

    private void onAddControlItemClick(int index) {
        switch (index) {
            case 0: // Add button
                mControlLayout.addControlButton(new ControlData("New"));
                break;
            case 1: // Add button drawer
                mControlLayout.addDrawer(new ControlDrawerData());
                break;
            case 2: // Add joystick
                mControlLayout.addJoystickButton(new ControlJoystickData());
                break;
        }
    }

    private void onActionItemClick(int index) {
        switch (index) {
            case 0: // Load
                mControlLayout.openLoadDialog();
                break;
            case 1: // Save
                mControlLayout.openSaveDialog(this);
                break;
            case 2: // Set as default
                mControlLayout.openSetDefaultDialog();
                break;
            case 3: // Export
                exportLayout();
                break;
        }
    }

    private void exportLayout() {
        try {
            Uri contentUri = DocumentsContract.buildDocumentUri(getString(R.string.storageProviderAuthorities), mControlLayout.saveToDirectory(mControlLayout.mLayoutFileName));

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("application/json");
            startActivity(shareIntent);

            Intent sendIntent = Intent.createChooser(shareIntent, mControlLayout.mLayoutFileName);
            startActivity(sendIntent);
        } catch (Exception e) {
            Tools.showError(this, e);
        }
    }

    @Override
    public void onBackPressed() {
        mControlLayout.askToExit(this);
    }

    @Override
    public void exitEditor() {
        super.onBackPressed();
    }
}