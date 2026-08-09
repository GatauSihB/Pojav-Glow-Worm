package com.kdt.mcgui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.firefly.R;

import fr.spse.extended_view.ExtendedButton;

/**
 * A vertical menu tile: the icon is drawn above the label,
 * used to build compact horizontal menus in landscape mode.
 */
public class MenuTileButton extends ExtendedButton {

    public MenuTileButton(@NonNull Context context) {
        super(context);
        setSettings();
    }

    public MenuTileButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSettings();
    }

    private void setSettings() {
        Resources resources = getContext().getResources();

        int padding = resources.getDimensionPixelSize(R.dimen._8sdp);
        setCompoundDrawablePadding(padding);
        setPaddingRelative(0, padding, 0, padding);
        setGravity(Gravity.CENTER);

        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._11ssp));

        // The icon is drawn on top of the label
        int[] sizes = getExtendedViewData().getSizeCompounds();
        sizes[1] = resources.getDimensionPixelSize(R.dimen._30sdp);
        getExtendedViewData().setSizeCompounds(sizes);
        postProcessDrawables();
    }
}
