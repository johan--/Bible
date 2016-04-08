package com.papa.bible;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.papa.bible.adapter.BaseAdapterHelper;
import com.papa.bible.adapter.QuickAdapter;
import com.papa.bible.data.FileLoader;
import com.papa.bible.data.FolioReader;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

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
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @OnClick(R.id.back_tv)
    void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.search_tv)
    void onSearckClick() {

    }

    @OnClick(R.id.notes_tv)
    void onNotesClick() {

    }

    @OnClick(R.id.bookmark_tv)
    void onBookmarkClick() {

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


}
