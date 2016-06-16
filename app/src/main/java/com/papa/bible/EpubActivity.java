package com.papa.bible;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.papa.bible.adapter.BaseAdapterHelper;
import com.papa.bible.adapter.QuickAdapter;
import com.papa.bible.data.DataBaseManager;
import com.papa.bible.data.FileLoader;
import com.papa.bible.data.FolioReader;
import com.papa.bible.data.db.dao.BookEntityDao;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.util.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.dao.query.Query;

public class EpubActivity extends BaseActivity implements LoaderManager
        .LoaderCallbacks<List<File>> {

    @Bind(R.id.grid_view)
    GridView mGridView;
    @Bind(R.id.back_tv)
    TextView mBackTv;
    @Bind(R.id.search_tv)
    TextView mSearchTv;
    @Bind(R.id.notes_tv)
    TextView mNotesTv;
    @Bind(R.id.bookmark_tv)
    TextView mBookmarkTv;

    private FolioReader mReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReader = new FolioReader(getApplicationContext());
        setContentView(R.layout.activity_epub);
        initData();
//      getSupportLoaderManager().initLoader(0, null, this);
    }

    private void initData() {
        final QuickAdapter<File> adapter = new QuickAdapter<File>(this, R.layout
                .epub_file_list_item, getListFile()) {
            @Override
            protected void convert(BaseAdapterHelper helper, File item) {
                helper.setImageResource(R.id.img, getImgResource(getFileName(item)));
                helper.setText(R.id.tv, getFileName(item));
            }
        };
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//              mReader.openBook(adapter.getItem(i).getPath());
                openEpubFile(adapter.getItem(i));
            }
        });
    }

    private void openEpubFile(File dir) {
        List<File> list = epubList(dir);
        if (list != null && !list.isEmpty()) {
            openBook(list.get(0).getPath());
            //           mReader.openBook(list.get(0).getPath());
        } else {
            Toast.makeText(this, R.string.file_not_exist, Toast.LENGTH_SHORT).show();
        }
    }

    private void openBook(String path) {
        Query<BookEntity> query = DataBaseManager.getInstance(this).getDaoSession()
                .getBookEntityDao()
                .queryBuilder().where
                        (BookEntityDao.Properties.Path.eq(path)).build();
        BookEntity entity = query.unique();
        if (entity != null) {
            openBook(entity);
        } else {
            new InsertTableTask().execute(path);
        }
    }

    private void openBook(BookEntity entity) {
        Intent intent = new Intent(EpubActivity.this, ChapterActivity.class);
        intent.putExtra(Config.KEY_BOOK, entity);
        startActivity(intent);
    }


    private List<File> epubList(File dir) {
        List<File> res = new ArrayList<File>();
        if (dir.isDirectory()) {
            File[] f = dir.listFiles();
            if (f != null) {
                for (int i = 0; i < f.length; i++) {
                    if (f[i].isDirectory()) {
                        res.addAll(epubList(f[i]));
                    } else {
                        String lowerCasedName = f[i].getName().toLowerCase();
                        if (lowerCasedName.endsWith(".epub")) {
                            res.add(f[i]);
                        }
                    }
                }
            }
        }
        Collections.sort(res, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
               return file.getName().compareToIgnoreCase(t1.getName());
              // return 0;
            }
        });
        return res;
    }


    @OnClick(R.id.back_tv)
    void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.search_tv)
    void onSearckClick() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.notes_tv)
    void onNotesClick() {

    }

    @OnClick(R.id.bookmark_tv)
    void onBookmarkClick() {
        Intent intent = new Intent(this, BookmarkActivity.class);
        startActivity(intent);
    }

    private List<File> getListFile() {
        List<File> files = new ArrayList<>();
        files.add(new File(Environment.getExternalStorageDirectory(), Config
                .FILE_NEW_KING_JAMES_VERSION));
        files.add(new File(Environment.getExternalStorageDirectory(), Config.FILE_NEW_INTERMATIONAL_VERSION));
        files.add(new File(Environment.getExternalStorageDirectory(), Config
                .FILE_NUEVA_VERSION_INTERNACIONAL));
        return files;
    }


    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new FileLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        final QuickAdapter<File> adapter = new QuickAdapter<File>(this, R.layout
                .epub_file_list_item, data) {
            @Override
            protected void convert(BaseAdapterHelper helper, File item) {
                helper.setImageResource(R.id.img, getImgResource(getFileName(item)));
                helper.setText(R.id.tv, getFileName(item));
            }
        };
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mReader.openBook(adapter.getItem(i).getPath());
            }
        });
    }


    @Override
    public void onLoaderReset(Loader<List<File>> loader) {

    }


    private String getFileName(File file) {
        return file.getName().replace(".epub", "");
    }


    private int getImgResource(String fileName) {
        int result = R.drawable.img_new_international_nersion;
        if (TextUtils.isEmpty(fileName))
            return result;
        if (fileName.equalsIgnoreCase("New International Version"))
            result = R.drawable.img_new_international_nersion;
        else if (fileName.equalsIgnoreCase("New King James Version"))
            result = R.drawable.img_new_king_james_version;
        else if (fileName.equalsIgnoreCase("Nueva Version Internacional"))
            result = R.drawable.img_nueva_nersion_internacional;
        return result;
    }

    private class InsertTableTask extends AsyncTask<String, Void, Long> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressContentDialogFragment.showProgress(getSupportFragmentManager(), getString(R
                    .string.parsing_file));
        }

        @Override
        protected Long doInBackground(String... strings) {
            FolioReader reader = new FolioReader(getApplicationContext());
            return reader.openBook(strings[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            ProgressContentDialogFragment.dismissProgress(getSupportFragmentManager());
            BookEntity entity = DataBaseManager.getInstance(App.getInstance()).getDaoSession()
                    .getBookEntityDao().load(aLong);
            openBook(entity);

        }
    }




}
