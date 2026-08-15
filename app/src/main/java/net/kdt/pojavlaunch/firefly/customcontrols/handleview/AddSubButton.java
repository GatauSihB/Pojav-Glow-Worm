package net.kdt.pojavlaunch.firefly.customcontrols.handleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.kdt.pojavlaunch.firefly.R;
import net.kdt.pojavlaunch.firefly.customcontrols.ControlData;
import net.kdt.pojavlaunch.firefly.customcontrols.buttons.ControlDrawer;
import net.kdt.pojavlaunch.firefly.customcontrols.buttons.ControlInterface;

@SuppressLint("AppCompatCustomView")
public class AddSubButton extends Button implements ActionButtonInterface {
    public AddSubButton(Context context) {
        super(context);
        init();
    }

    public AddSubButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        setText(R.string.customctrl_addsubbutton);
        setOnClickListener(this);
        setAllCaps(false);
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.action_button_background));
        setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
    }

    private ControlInterface mCurrentlySelectedButton = null;

    @Override
    public boolean shouldBeVisible() {
        return mCurrentlySelectedButton != null && mCurrentlySelectedButton instanceof ControlDrawer;
    }

    @Override
    public void setFollowedView(ControlInterface view) {
        mCurrentlySelectedButton = view;
    }

    @Override
    public void onClick() {
        if (mCurrentlySelectedButton instanceof ControlDrawer) {
            ((ControlDrawer) mCurrentlySelectedButton).getControlLayoutParent().addSubButton(
                    (ControlDrawer) mCurrentlySelectedButton,
                    new ControlData()
            );
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
