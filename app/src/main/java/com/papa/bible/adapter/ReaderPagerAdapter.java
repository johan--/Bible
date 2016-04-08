package com.papa.bible.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.papa.bible.ReaderFragment;
import com.papa.bible.data.Configuration;
import com.papa.bible.view.VerticalViewPager;

import java.util.List;




public class ReaderPagerAdapter extends FragmentStatePagerAdapter {

    public ReaderPagerAdapter(FragmentManager fm, List<String> list, String baseURL,
                              VerticalViewPager pager) {
        super(fm);
        this.list = list;
        this.baseURL = baseURL;
        this.pager = pager;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(Configuration.PATH_DECOMPRESSED, list.get(position));
        bundle.putString(Configuration.BASE_URL, baseURL);

        ReaderFragment fragment = new ReaderFragment();
        fragment.setArguments(bundle);
        fragment.setPager(pager);

        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    private List<String> list;
    private String baseURL;
    private VerticalViewPager pager;
}
