package com.example.midtermsexam_beauty.views.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.midtermsexam_beauty.R;
import com.example.midtermsexam_beauty.adapters.PopularAndFeaturedAdapter;
import com.example.midtermsexam_beauty.models.Product;

import java.util.ArrayList;

public class FeaturedProducts extends AppCompatActivity {

    private final ArrayList<Product> featuredProducts = new ArrayList<>();

    ImageButton toPrevious, proceedToRatings, proceedToHome, proceedToSkinTypes, proceedToCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_featured_products);
        hideSystemUI();

        toPrevious = findViewById(R.id.back_btn);
        proceedToRatings = findViewById(R.id.nav_ratings);
        proceedToHome = findViewById(R.id.nav_home);
        proceedToSkinTypes = findViewById(R.id.nav_skin_types);
        proceedToCheckout = findViewById(R.id.nav_payment);

        ListView featuredListView = findViewById(R.id.featured_recycler);

        featuredProducts.addAll(Product.getDefaultProducts(this));


        PopularAndFeaturedAdapter adapter = new PopularAndFeaturedAdapter(this, featuredProducts);
        featuredListView.setAdapter(adapter);

        toPrevious.setOnClickListener(view -> startActivity(new Intent(FeaturedProducts.this, Homepage.class)));
        proceedToHome.setOnClickListener(view -> startActivity(new Intent(FeaturedProducts.this, Homepage.class)));
        proceedToRatings.setOnClickListener(view -> startActivity(new Intent(FeaturedProducts.this, PopularProducts.class)));
        proceedToSkinTypes.setOnClickListener(view -> startActivity(new Intent(FeaturedProducts.this, SkinType.class)));
        proceedToCheckout.setOnClickListener(view -> startActivity(new Intent(FeaturedProducts.this, Checkout.class)));
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
