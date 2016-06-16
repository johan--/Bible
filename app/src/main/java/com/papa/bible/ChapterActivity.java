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
import com.papa.bible.data.db.dao.BookChapterEntityDao;
import com.papa.bible.data.db.database.BookChapterEntity;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.util.Config;

import java.util.List;

import butterknife.Bind;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class ChapterActivity extends BaseActivity implements LoaderManager
        .LoaderCallbacks<List<BookChapterEntity>> {

    @Bind(R.id.title_tv)
    TextView mTitleTv;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.favorite_tv)
    TextView mFavoriteTv;
    @Bind(R.id.note_tv)
    TextView mNoteTv;


    private BookEntity mBookEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        mBookEntity = (BookEntity) getIntent().getSerializableExtra(Config.KEY_BOOK);
        mTitleTv.setText(mBookEntity.getName());
        initData();
    }

    private void initData() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<BookChapterEntity>> onCreateLoader(int id, Bundle args) {
        QueryBuilder<BookChapterEntity> queryBuilder = DataBaseManager.getInstance(this)
                .getDaoSession()
                .getBookChapterEntityDao().queryBuilder().where(BookChapterEntityDao.Properties
                        .BookId.eq(mBookEntity.getId()));
        Query<BookChapterEntity> query = queryBuilder.build();
        return new EntityLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<BookChapterEntity>> loader, List<BookChapterEntity>
            data) {

        final QuickAdapter<BookChapterEntity> adapter = new QuickAdapter<BookChapterEntity>
                (ChapterActivity
                        .this, R.layout.epub_chapter_list_iten, data) {
            @Override
            protected void convert(BaseAdapterHelper helper, BookChapterEntity item) {
                helper.setText(R.id.tv, item.getTitle());
            }
        };

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChapterActivity.this, ReaderActivity.class);
                intent.putExtra(Config.KEY_BOOK, mBookEntity);
                intent.putExtra(Config.KEY_RESOURCE_ID, adapter.getItem(position).getResourceId());
//              intent.putExtra("resourceId", adapter.getItem(position).getResource().getId());
                if (getIntent().getIntExtra(Config.KEY_TYPE, 0) > 0) {
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    startActivity(intent);
                }

//                    Resource resource = indexes.get(position).getResource();
//                    int moveTo = mBookDecompressed.getBook().getSpine().findFirstResourceById
//                            (resource.getId());
//                    viewPager.setCurrentItem(moveTo);
//
//                    menuDrawer.closeMenu();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<BookChapterEntity>> loader) {

    }

}
