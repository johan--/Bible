package com.papa.bible;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.papa.bible.adapter.BaseAdapterHelper;
import com.papa.bible.adapter.QuickAdapter;
import com.papa.bible.data.DataBaseManager;
import com.papa.bible.data.EntityLoader;
import com.papa.bible.data.db.dao.BookmarkEntityDao;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.data.db.database.BookmarkEntity;
import com.papa.bible.util.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class BookmarkActivity extends BaseActivity implements LoaderManager
        .LoaderCallbacks<List<BookmarkEntity>> {

    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.title_tv)
    TextView mTitleTv;

    private QuickAdapter<BookmarkEntity> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        mTitleTv.setText(R.string.bookmark);
        mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu
                    .ContextMenuInfo contextMenuInfo) {
                contextMenu.add(R.string.delete_bookmark);

            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        deleteBookmark(mAdapter.getItem(info.position));
        return super.onContextItemSelected(item);
    }

    private void deleteBookmark(BookmarkEntity entity) {
        DataBaseManager.getInstance(this)
                .getDaoSession()
                .getBookmarkEntityDao().delete(entity);
        Toast.makeText(this, R.string.delete_bookmark_success, Toast.LENGTH_SHORT).show();
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<List<BookmarkEntity>> onCreateLoader(int id, Bundle args) {
        QueryBuilder<BookmarkEntity> queryBuilder = DataBaseManager.getInstance(this)
                .getDaoSession()
                .getBookmarkEntityDao().queryBuilder().orderDesc(BookmarkEntityDao.Properties.Date);
        Query<BookmarkEntity> query = queryBuilder.build();
        return new EntityLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<BookmarkEntity>> loader, List<BookmarkEntity>
            data) {

        mAdapter = new QuickAdapter<BookmarkEntity>
                (BookmarkActivity
                        .this, R.layout.bookmark_list_item, data) {
            @Override
            protected void convert(BaseAdapterHelper helper, BookmarkEntity item) {
                helper.setText(R.id.resource_tv, item.getResourceId());
                helper.setText(R.id.date_tv, formatTime(item.getDate()));
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookmarkEntity bookmarkEntity = mAdapter.getItem(position);
                Intent intent = new Intent(BookmarkActivity.this, ReaderActivity.class);
                intent.putExtra(Config.KEY_BOOK, getBookEntity(bookmarkEntity.getBookId()));
                intent.putExtra(Config.KEY_RESOURCE_ID, bookmarkEntity.getResourceId());
                intent.putExtra(Config.KEY_SCROLLX, bookmarkEntity.getScrollX());
                intent.putExtra(Config.KEY_SCROLLY, bookmarkEntity.getScrollY());
                startActivity(intent);
            }
        });
    }

    private BookEntity getBookEntity(long id) {
        return DataBaseManager.getInstance(this).getDaoSession()
                .getBookEntityDao().load(id);

    }

    private String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }


    @Override
    public void onLoaderReset(Loader<List<BookmarkEntity>> loader) {

    }

}
