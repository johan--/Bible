package com.papa.bible;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.papa.bible.adapter.BaseAdapterHelper;
import com.papa.bible.adapter.QuickAdapter;
import com.papa.bible.data.DataBaseManager;
import com.papa.bible.data.EntityLoader;
import com.papa.bible.data.db.dao.AudioEntityDao;
import com.papa.bible.data.db.database.AudioEntity;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.util.Config;

import java.util.List;

import butterknife.Bind;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class AudioListActivity extends BaseActivity implements LoaderManager
        .LoaderCallbacks<List<AudioEntity>> {

    @Bind(R.id.title_tv)
    TextView mTitleTv;
    @Bind(R.id.list_view)
    ListView mListView;


    private BookEntity mBookEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        mBookEntity = (BookEntity) getIntent().getSerializableExtra(Config.KEY_BOOK);
        mTitleTv.setText(R.string.audio_list);
        initData();
    }

    private void initData() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<AudioEntity>> onCreateLoader(int id, Bundle args) {
        QueryBuilder<AudioEntity> queryBuilder = DataBaseManager.getInstance(this)
                .getDaoSession()
                .getAudioEntityDao().queryBuilder().where(AudioEntityDao.Properties
                        .BookId.eq(mBookEntity.getId()));
        Query<AudioEntity> query = queryBuilder.build();
        return new EntityLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<AudioEntity>> loader, List<AudioEntity>
            data) {

        final QuickAdapter<AudioEntity> adapter = new QuickAdapter<AudioEntity>
                (AudioListActivity
                        .this, R.layout.epub_chapter_list_iten, data) {
            @Override
            protected void convert(BaseAdapterHelper helper, AudioEntity item) {
                helper.setText(R.id.tv, item.getAudioName());
            }
        };

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Config.KEY_DATA, adapter.getItem(position));
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<AudioEntity>> loader) {

    }

}
