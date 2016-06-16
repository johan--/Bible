package com.papa.bible;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.papa.bible.adapter.BaseAdapterHelper;
import com.papa.bible.adapter.QuickAdapter;
import com.papa.bible.data.DataBaseManager;
import com.papa.bible.data.EntityLoader;
import com.papa.bible.data.db.dao.BookChapterEntityDao;
import com.papa.bible.data.db.database.BookChapterEntity;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.util.Config;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Papa on 2016/4/27.
 */
public class SearchChapterFragment extends Fragment implements LoaderManager
        .LoaderCallbacks<List<BookChapterEntity>>, SearchActivity.SearchListener {
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.progress)
    ProgressBar mProgress;
    @Bind(R.id.empty_tv)
    TextView mEmptyTv;

    QuickAdapter<BookChapterEntity> mAdapter;

    private BookEntity mBookEntity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new QuickAdapter<BookChapterEntity>(getActivity(), R.layout.search_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, BookChapterEntity item) {
                helper.setText(R.id.text, item.getTitle());
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookChapterEntity chapterEntity = mAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
                intent.putExtra(Config.KEY_BOOK, getBookEntity(chapterEntity.getBookId()));
                intent.putExtra(Config.KEY_RESOURCE_ID, chapterEntity.getResourceId());
                startActivity(intent);
            }
        });
    }

    private BookEntity getBookEntity(long bookId) {
        return DataBaseManager.getInstance(getActivity()).getDaoSession().getBookEntityDao().load
                (bookId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public String getContent() {
        if (getActivity() instanceof SearchActivity) {
            SearchActivity activity = (SearchActivity) getActivity();
            return activity.getContent();
        }
        return "";
    }

    @Override
    public Loader<List<BookChapterEntity>> onCreateLoader(int id, Bundle args) {
        mProgress.setVisibility(View.VISIBLE);
        QueryBuilder<BookChapterEntity> queryBuilder = DataBaseManager.getInstance(getActivity())
                .getDaoSession()
                .getBookChapterEntityDao().queryBuilder().where(BookChapterEntityDao.Properties
                        .Title.like("%" + getContent() + "%"));
        Query<BookChapterEntity> query = queryBuilder.build();
        return new EntityLoader(getActivity(), query);
    }


    @Override
    public void onLoadFinished(Loader<List<BookChapterEntity>> loader, List<BookChapterEntity>
            data) {
        mProgress.setVisibility(View.GONE);
        if (data != null)
            mAdapter.replaceAll(data);
        if(mAdapter.isEmpty()){
            mEmptyTv.setVisibility(View.VISIBLE);
        }else{
            mEmptyTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BookChapterEntity>> loader) {

    }

    @Override
    public void onSearch() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
