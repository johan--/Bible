package com.papa.bible;

import android.os.Bundle;
import android.text.TextUtils;

import com.papa.bible.adapter.ReaderPagerAdapter;
import com.papa.bible.bean.BookDecompressed;
import com.papa.bible.data.Configuration;
import com.papa.bible.view.VerticalViewPager;

import butterknife.Bind;

public class ReaderActivity extends BaseActivity {

    @Bind(R.id.view_pager)
    VerticalViewPager mViewPager;

    BookDecompressed mBookDecompressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookDecompressed = (BookDecompressed) Configuration.getData(Configuration.KEY_BOOK);
        setContentView(R.layout.activity_reading);

        mViewPager.setAdapter(new ReaderPagerAdapter(getSupportFragmentManager(),
                mBookDecompressed.getUrlResources(), mBookDecompressed.getBaseURL(), mViewPager));

        String resourceId = getIntent().getStringExtra("resourceId");
        if (!TextUtils.isEmpty(resourceId)) {
            int moveTo = mBookDecompressed.getBook().getSpine().findFirstResourceById
                    (resourceId);
            mViewPager.setCurrentItem(moveTo);
        }
    }
}
