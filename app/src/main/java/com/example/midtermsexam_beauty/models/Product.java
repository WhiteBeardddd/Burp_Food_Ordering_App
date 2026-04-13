package com.example.midtermsexam_beauty.models;

import android.content.Context;
import android.content.res.Resources;

import com.example.midtermsexam_beauty.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private final int imageId;
    private final String name;
    private final float price;
    private final String description;
    private int counter;
    private final float rating;
    private final String category;
    private final String skin_type;

    public Product(int imageId, String name, float price, String description, int counter, float rating, String category, String skin_type) {
        this.imageId = imageId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.counter = counter;
        this.rating = rating;
        this.category = category;
        this.skin_type = skin_type;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public float getRating() {
        return rating;
    }

    public String getCategory() {
        return category;
    }

    public String getSkin_type() {
        return skin_type;
    }

    public static List<Product> getPopularProducts(Context context) {
        return loadProductsFromJSON(context, "products.json", "popularProducts");
    }

    public static List<Product> getDefaultProducts(Context context) {
        return loadProductsFromJSON(context, "products.json", "featuredProducts");
    }

    private static List<Product> loadProductsFromJSON(Context context, String fileName, String key) {
        List<Product> productList = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray productsArray = jsonObject.getJSONArray(key);

            Resources res = context.getResources();

            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject obj = productsArray.getJSONObject(i);

                int imageId = res.getIdentifier(obj.getString("imageId"), "drawable", context.getPackageName());
                String name = obj.getString("name");
                float price = (float) obj.getDouble("price");
                String description = obj.getString("description");
                int counter = obj.getInt("counter");
                float rating = (float) obj.getDouble("rating");
                String category = obj.getString("category");
                String skinType = obj.getString("skin_type");

                Product product = new Product(imageId, name, price, description, counter, rating, category, skinType);
                productList.add(product);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return productList;
    }
}

