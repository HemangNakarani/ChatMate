package com.hemangnh18.chatmate.ImageViewer;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.hemangnh18.chatmate.DownloadManager.DirectoryHelper;
import com.hemangnh18.chatmate.DownloadManager.DownloadSongService;
import com.hemangnh18.chatmate.R;

import java.util.ArrayList;

public class FullScreenImageViewActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    public static final String URI_LIST_DATA = "URI_LIST_DATA";
    public static final String URI_DOWNLOAD_DATA = "URI_DOWNLOAD_DATA";
    public static final String IMAGE_FULL_SCREEN_CURRENT_POS = "IMAGE_FULL_SCREEN_CURRENT_POS";
    private ViewPager viewPager;
    private PagerAdapter2 adapter;
    private  ArrayList<String> imagePaths;
    private  ArrayList<String> imageDownloadPaths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_touch_image_view2);

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);

        setupToolbar();

       imagePaths = getIntent().getStringArrayListExtra(URI_LIST_DATA);
       imageDownloadPaths = getIntent().getStringArrayListExtra(URI_DOWNLOAD_DATA);


        int currentPos = getIntent().getIntExtra(IMAGE_FULL_SCREEN_CURRENT_POS, 0);

        FragmentManager manager = getSupportFragmentManager();
        adapter = new PagerAdapter2(manager, imagePaths);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPos);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.imageviewer_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else if(item.getItemId()== R.id.download)
        {
            startService(DownloadSongService.getDownloadService(this, imageDownloadPaths.get(viewPager.getCurrentItem()), DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/")));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

