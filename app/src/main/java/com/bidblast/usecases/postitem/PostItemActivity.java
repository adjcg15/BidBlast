package com.bidblast.usecases.postitem;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bidblast.R;

public class PostItemActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item_for_auction);
        setContentView(R.layout.activity_post_item_for_auction_second_part);
    }
}
