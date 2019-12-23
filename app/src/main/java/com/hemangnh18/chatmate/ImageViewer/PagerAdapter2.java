package com.hemangnh18.chatmate.ImageViewer;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.github.tntkhang.fullscreenimageview.library.ImageFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PagerAdapter2 extends FragmentStatePagerAdapter {

    private List<Uri> imagesUri = new ArrayList();

    PagerAdapter2(FragmentManager fragmentManager, List<String> images) {
        super(fragmentManager);
        Iterator var3 = images.iterator();

        while(var3.hasNext()) {
            String image = (String)var3.next();
            this.imagesUri.add(Uri.parse(image));
        }

    }



    public Fragment getItem(int position) {
        return ImageFragment.newInstance(this.imagesUri.get(position));
    }

    public int getCount() {
        return this.imagesUri.size();
    }

    public CharSequence getPageTitle(int position) {
        return "";
    }
}

