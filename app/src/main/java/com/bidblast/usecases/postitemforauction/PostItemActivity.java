package com.bidblast.usecases.postitemforauction;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bidblast.R;

public class PostItemActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostItemFragment())
                    .commit();
        }
    }
}