package com.example.midtermsexam_beauty.utilities;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.example.midtermsexam_beauty.R;
import com.example.midtermsexam_beauty.views.user.Checkout;
import com.example.midtermsexam_beauty.views.user.FeaturedProducts;
import com.example.midtermsexam_beauty.views.user.Homepage;
import com.example.midtermsexam_beauty.views.user.PopularProducts;
import com.example.midtermsexam_beauty.views.user.SkinType;

public class NavbarHelper {

    public static void setupNavbar(Activity activity) {
        ImageButton navHome = activity.findViewById(R.id.nav_home);
        ImageButton navRatings = activity.findViewById(R.id.nav_ratings);
        ImageButton navSkinTypes = activity.findViewById(R.id.nav_skin_types);
        ImageButton navFeatured = activity.findViewById(R.id.nav_featured);
        ImageButton navPayment = activity.findViewById(R.id.nav_payment);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                if (!(activity instanceof Homepage)) {
                    activity.startActivity(new Intent(activity, Homepage.class));
                }
            });
        }

        if (navRatings != null) {
            navRatings.setOnClickListener(v -> {
                if (!(activity instanceof FeaturedProducts)) {
                    activity.startActivity(new Intent(activity, FeaturedProducts.class));
                }
            });
        }

        if (navSkinTypes != null) {
            navSkinTypes.setOnClickListener(v -> {
                if (!(activity instanceof SkinType)) {
                    activity.startActivity(new Intent(activity, SkinType.class));
                }
            });
        }

        if (navFeatured != null) {
            navFeatured.setOnClickListener(v -> {
                if (!(activity instanceof FeaturedProducts)) {
                    activity.startActivity(new Intent(activity, FeaturedProducts.class));
                }
            });
        }

        if (navPayment != null) {
            navPayment.setOnClickListener(v -> {
                if (!(activity instanceof Checkout)) {
                    activity.startActivity(new Intent(activity, Checkout.class));
                }
            });
        }
        
        // Highlight active icon
        highlightActiveIcon(activity, navHome, navRatings, navSkinTypes, navFeatured, navPayment);
    }

    private static void highlightActiveIcon(Activity activity, ImageButton... buttons) {
        int orange = 0xFFFF5722; // Hardcoded from your layouts
        int gray = 0xFF9E9E9E;

        if (activity instanceof Homepage && buttons[0] != null) buttons[0].setColorFilter(orange);
        else if (activity instanceof PopularProducts && buttons[1] != null) buttons[1].setColorFilter(orange);
        else if (activity instanceof SkinType && buttons[2] != null) buttons[2].setColorFilter(orange);
        else if (activity instanceof FeaturedProducts && (buttons[3] != null || buttons[1] != null)) {
            if (buttons[3] != null) buttons[3].setColorFilter(orange);
            if (buttons[1] != null) buttons[1].setColorFilter(orange);
        }
        else if (activity instanceof Checkout && buttons[4] != null) buttons[4].setColorFilter(orange);
    }
}
