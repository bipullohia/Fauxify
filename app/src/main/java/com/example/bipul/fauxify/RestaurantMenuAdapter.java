package com.example.bipul.fauxify;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 9/4/2016.
 */
public class RestaurantMenuAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabtitles = new ArrayList<>();
    ArrayList<JSONObject> jobject = new ArrayList<>();


    public void addFragments(Fragment fragments, String titles, JSONObject jobject)
    {
        this.fragments.add(fragments);
        this.tabtitles.add(titles);
        this.jobject.add(jobject);
    }

    public RestaurantMenuAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("data", String.valueOf(jobject.get(position)));
        fragments.get(position).setArguments(bundle);
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles.get(position);
    }
}
