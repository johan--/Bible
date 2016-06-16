package com.papa.bible.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import com.papa.bible.ReaderFragment;
import com.papa.bible.data.Configuration;
import com.papa.bible.data.db.database.BookContentEntity;
import com.papa.bible.view.VerticalViewPager;

import java.lang.ref.WeakReference;
import java.util.List;


public class ReaderPagerAdapter extends FragmentStatePagerAdapter {

    private List<BookContentEntity> list;
    private String baseURL;
    private VerticalViewPager pager;
    private final SparseArrayCompat<WeakReference<Fragment>> holder;




    public ReaderPagerAdapter(FragmentManager fm, List<BookContentEntity> list, String baseURL,
                              VerticalViewPager pager) {
        super(fm);
        this.list = list;
        this.baseURL = baseURL;
        this.pager = pager;
        this.holder = new SparseArrayCompat<>(list.size());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        if (item instanceof Fragment) {
            holder.put(position, new WeakReference<Fragment>((Fragment) item));
        }
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        holder.remove(position);
        super.destroyItem(container, position, object);
    }


    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(Configuration.PATH_DECOMPRESSED, list.get(position).getUrlResource());
        bundle.putString(Configuration.BASE_URL, baseURL);
        bundle.putSerializable(Configuration.BOOK_ENTITY, list.get(position));

        ReaderFragment fragment = new ReaderFragment();
        fragment.setArguments(bundle);
        fragment.setPager(pager);

        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    public Fragment getPage(int position) {
        final WeakReference<Fragment> weakRefItem = holder.get(position);
        return (weakRefItem != null) ? weakRefItem.get() : null;
    }

}
