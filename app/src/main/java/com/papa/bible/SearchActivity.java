package com.papa.bible;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.EditText;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Papa on 2016/4/20.
 */
public class SearchActivity extends BaseActivity {

    @Bind(R.id.edit)
    EditText mEdit;
    @Bind(R.id.search_btn)
    Button mSearchBtn;
//    @Bind(R.id.list_view)
//    ListView mListView;
//
//    @Bind(R.id.progress)
//    ProgressBar mProgressBar;

    @Bind(R.id.viewpager_tab)
    SmartTabLayout mSmartTabLayout;

    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    FragmentPagerItemAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //       mBookEntity = (BookEntity) getIntent().getSerializableExtra(Config.KEY_BOOK);
        setContentView(R.layout.activity_search);
        mAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.chapter, SearchChapterFragment.class)
//                .add(R.string.audio, SearchAudioFragment.class)
                .create());
        mViewPager.setAdapter(mAdapter);
        mSmartTabLayout.setViewPager(mViewPager);
    }




    @OnClick(R.id.search_btn)
    void search() {
        int count = mAdapter.getCount();
        for(int index =0;index<count;index++){
            Fragment fragment = mAdapter.getPage(index);
            if(fragment instanceof  SearchListener){
                ((SearchListener)fragment).onSearch();
            }
        }

//        String content = mEdit.getEditableText().toString();
//        if (!TextUtils.isEmpty(content)) {
//            getSupportLoaderManager().restartLoader(0, null, this);
//        }
    }




    public String getContent() {
        return mEdit.getEditableText().toString();
    }


    public interface SearchListener{
        void onSearch();
    }

}
