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

import com.papa.bible.adapter.BaseAdapterHelper;
import com.papa.bible.adapter.QuickAdapter;
import com.papa.bible.data.DataBaseManager;
import com.papa.bible.data.EntityLoader;
import com.papa.bible.data.db.dao.AudioEntityDao;
import com.papa.bible.data.db.database.AudioEntity;
import com.papa.bible.data.db.database.BookContentEntity;
import com.papa.bible.data.db.database.BookEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Papa on 2016/4/27.
 */
public class SearchAudioFragment extends Fragment implements LoaderManager
        .LoaderCallbacks<List<BookContentEntity>>, SearchActivity.SearchListener {

    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.progress)
    ProgressBar mProgress;

    QuickAdapter<AudioEntity> mAdapter;

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
        mAdapter = new QuickAdapter<AudioEntity>(getActivity(), R.layout.search_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, AudioEntity item) {
                helper.setText(R.id.text, item.getAudioName());
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                BookContentEntity bookContentEntity = mAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
//                intent.putExtra(Config.KEY_BOOK, getBookEntity(bookContentEntity.getBookId()));
//                intent.putExtra(Config.KEY_RESOURCE_ID, bookContentEntity.getResourceId());
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
    public Loader<List<BookContentEntity>> onCreateLoader(int id, Bundle args) {
        mProgress.setVisibility(View.VISIBLE);
        QueryBuilder<BookContentEntity> queryBuilder = DataBaseManager.getInstance(getActivity())
                .getDaoSession()
                .getBookContentEntityDao().queryBuilder().where(AudioEntityDao.Properties
                        .AudioName.like("%" + getContent() + "%"));
        Query<BookContentEntity> query = queryBuilder.build();
        return new EntityLoader(getActivity(), query);
    }


    @Override
    public void onLoadFinished(Loader<List<BookContentEntity>> loader, List<BookContentEntity>
            data) {
        mProgress.setVisibility(View.GONE);
//        if (data != null)
//            mAdapter.replaceAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<BookContentEntity>> loader) {

    }

    @Override
    public void onSearch() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
