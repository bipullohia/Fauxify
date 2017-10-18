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

class RestaurantMenuAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragmentsList = new ArrayList<>();
    private ArrayList<String> mTabtitles = new ArrayList<>();
    ArrayList<JSONObject> mJObject = new ArrayList<>();

    void addFragments(Fragment fragments, String titles, JSONObject jobject)
    {
        this.mFragmentsList.add(fragments);
        this.mTabtitles.add(titles);
        this.mJObject.add(jobject);
    }

    RestaurantMenuAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("data", String.valueOf(mJObject.get(position)));
        mFragmentsList.get(position).setArguments(bundle);
        return mFragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabtitles.get(position);
    }
}
