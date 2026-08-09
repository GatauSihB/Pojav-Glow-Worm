package net.kdt.pojavlaunch.firefly.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.firefly.R;
import net.kdt.pojavlaunch.firefly.Tools;

public class AboutFragment extends Fragment {
    public static final String TAG = "ABOUT_FRAGMENT";
    private static final String[][] CONTRIBUTORS = {
            {"PojavLauncherTeam", "https://github.com/PojavLauncherTeam", R.drawable.image_about_pojavteam},
            {"Vera-Firefly", "https://github.com/Vera-Firefly", R.drawable.image_about_verafirefly},
            {"MovTery", "https://github.com/MovTery", R.drawable.image_about_movtery},
            {"Eurya2233369", "https://github.com/Eurya2233369", R.drawable.image_about_lingmuqiuzhu},
            {"Coloryr", "https://github.com/Coloryr", R.drawable.image_about_coloryr},
            {"ShirosakiMio", "https://github.com/ShirosakiMio", R.drawable.image_about_mio},
            {"Tungstend", "https://github.com/Tungstend", R.drawable.image_about_tungstend},
            {"aaaapai", "https://github.com/aaaapai", R.drawable.image_about_aaaapai},
            {"WesleyVanNeck", "https://github.com/WesleyVanNeck", R.drawable.image_about_wvn}
    };

    public AboutFragment() {
        super(R.layout.fragment_about);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout contributorsList = view.findViewById(R.id.contributors_list);
        if (contributorsList == null) return;

        for (String[] c : CONTRIBUTORS) {
            String name = c[0];
            String url = c[1];
            int iconRes = Integer.parseInt(c[2]);
            View itemView = getLayoutInflater().inflate(R.layout.about_contributor_item, contributorsList, false);
            ((TextView) itemView.findViewById(R.id.contributor_name)).setText(name);
            itemView.findViewById(R.id.contributor_icon).setBackgroundResource(iconRes);
            itemView.setOnClickListener(v -> Tools.openURL(requireActivity(), url));
            contributorsList.addView(itemView);
        }
    }
}