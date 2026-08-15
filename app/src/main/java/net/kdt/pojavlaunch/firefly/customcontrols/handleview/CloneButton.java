package net.kdt.pojavlaunch.firefly.customcontrols.handleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.kdt.pojavlaunch.firefly.R;
import net.kdt.pojavlaunch.firefly.customcontrols.buttons.ControlInterface;

@SuppressLint("AppCompatCustomView")
public class CloneButton extends Button implements ActionButtonInterface {
    public CloneButton(Context context) {
        super(context);
        init();
    }

    public CloneButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        setOnClickListener(this);
        setAllCaps(false);
        setText(R.string.global_clone);
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.action_button_background));
        setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
    }

    private ControlInterface mCurrentlySelectedButton = null;

    @Override
    public boolean shouldBeVisible() {
        return mCurrentlySelectedButton != null;
    }

    @Override
    public void setFollowedView(ControlInterface view) {
        mCurrentlySelectedButton = view;
    }

    @Override
    public void onClick() {
        if (mCurrentlySelectedButton == null) return;
        mCurrentlySelectedButton.cloneButton();
        mCurrentlySelectedButton.getControlLayoutParent().removeEditWindow();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
