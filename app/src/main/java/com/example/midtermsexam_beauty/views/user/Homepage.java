package com.example.midtermsexam_beauty.views.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midtermsexam_beauty.R;
import com.example.midtermsexam_beauty.adapters.ProductCard;
import com.example.midtermsexam_beauty.models.Product;
import com.example.midtermsexam_beauty.utilities.NavbarHelper;

import java.util.ArrayList;

public class Homepage extends AppCompatActivity {

    private final ArrayList<Product> popularProducts = new ArrayList<>();
    private final ArrayList<Product> featuredProducts = new ArrayList<>();
    ImageButton proceedToProfile;


    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        hideSystemUI();

        RecyclerView featuredListView = findViewById(R.id.featured_recycler);
        RecyclerView popularListView = findViewById(R.id.popular_recycler);

        proceedToProfile = findViewById(R.id.toProfile);

        // Setup common navbar
        NavbarHelper.setupNavbar(this);

        featuredProducts.addAll(Product.getDefaultProducts(this));
        popularProducts.addAll(Product.getPopularProducts(this));


        ProductCard featuredAdapter = new ProductCard(this, featuredProducts, product -> {
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ViewProductDetails.class);
            intent.putExtra("imageId", product.getImageId());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("rating", product.getRating());
            intent.putExtra("category", product.getCategory());
            intent.putExtra("skin_type", product.getSkin_type());
            startActivity(intent);
        });

        ProductCard popularAdapter = new ProductCard(this, popularProducts,  product -> {
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ViewProductDetails.class);
            intent.putExtra("imageId", product.getImageId());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("rating", product.getRating());
            intent.putExtra("category", product.getCategory());
            intent.putExtra("skin_type", product.getSkin_type());
            startActivity(intent);
        });

        featuredListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        featuredListView.setAdapter(featuredAdapter);
        popularListView.setAdapter(popularAdapter);

        proceedToProfile.setOnClickListener(view -> {
            Intent intent = new Intent(Homepage.this, UserProfile.class);
            startActivity(intent);
        });
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
