package com.example.newcardmaker.invite_sticker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;


import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_ViewPagerAdapter_sticker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class invite_sticker_main_category extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private invite_ViewPagerAdapter_sticker adapter;

    EditText search_text;

    public interface Searchable {
        void filterByQuery(String query);
    }
    public interface OnImageSelectedListener {
        void onImageSelected(String imageUrl);
        void onImageSelectedResId(int resId);
        void onImageSelectedPath(String filePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.invite_activity_sticker_main_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        search_text = findViewById(R.id.search_text);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        adapter = new invite_ViewPagerAdapter_sticker(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Fragment fragment = getSupportFragmentManager()
                        .findFragmentByTag("f" + position);
                if (fragment instanceof invite_sticker_fragment) {
                    ((invite_sticker_fragment) fragment).refreshData();
                } else if (fragment instanceof invite_sticker_fragment_common) {
                    ((invite_sticker_fragment_common) fragment).refreshData();
                } else if (fragment instanceof invite_sticker_fragment_all) {
                    ((invite_sticker_fragment_all) fragment).refreshData();
                }

            }
        });

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                dispatchSearch(s.toString());
            }
        });

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    View tabView = LayoutInflater.from(this).inflate(R.layout.invite_custom_tab, null);
                    TextView tabText = tabView.findViewById(R.id.tabText);

                    if (position == 0) {
                        tabText.setText("Category");
                    } else if (position == 1) {
                        tabText.setText("Common");
                    }else if (position == 2) {
                        tabText.setText("All");
                    }

                    tab.setCustomView(tabView);
                }).attach();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void dispatchSearch(String query) {
        int currentItem = viewPager.getCurrentItem();
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag("f" + currentItem);

        // Direct Searchable che?
        if (fragment instanceof Searchable) {
            ((Searchable) fragment).filterByQuery(query);
            return;
        }

        // Child fragments ma search karo
        if (fragment != null) {
            for (Fragment child : fragment.getChildFragmentManager().getFragments()) {
                if (child instanceof Searchable) {
                    ((Searchable) child).filterByQuery(query);
                }
            }
        }
    }
}
