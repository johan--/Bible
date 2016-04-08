package com.papa.bible;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.papa.bible.adapter.BaseAdapterHelper;
import com.papa.bible.adapter.QuickAdapter;
import com.papa.bible.bean.BookDecompressed;
import com.papa.bible.data.Configuration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;

public class ChapterActivity extends BaseActivity {

    @Bind(R.id.title_tv)
    TextView mTitleTv;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.favorite_tv)
    TextView mFavoriteTv;
    @Bind(R.id.note_tv)
    TextView mNoteTv;

    private BookDecompressed mBookDecompressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        mBookDecompressed = (BookDecompressed) Configuration.getData(Configuration.KEY_BOOK);
        mTitleTv.setText(mBookDecompressed.getBook().getTitle());
        new CreateIndex().execute(mBookDecompressed.getBook().getTableOfContents());
    }


    /**
     * *** Class AsyncTask *****
     */
    private class CreateIndex extends AsyncTask<TableOfContents, Void, List<TOCReference>> {

        private int lastPaging = 0;
        private List<TOCReference> indexes;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indexes = new ArrayList<>();
        }

        @Override
        protected List<TOCReference> doInBackground(TableOfContents... tableOfContentses) {
            for (TOCReference tocReference : tableOfContentses[0].getTocReferences()) {
                getIndexRecursive(tocReference);
            }
            return indexes;
        }

        @Override
        protected void onPostExecute(final List<TOCReference> indexes) {
            super.onPostExecute(indexes);
            final QuickAdapter<TOCReference> adapter = new QuickAdapter<TOCReference>
                    (ChapterActivity
                    .this, R.layout.epub_chapter_list_iten, indexes) {
                @Override
                protected void convert(BaseAdapterHelper helper, TOCReference item) {
                    helper.setText(R.id.tv, item.getTitle());
                }
            };

            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ChapterActivity.this, ReaderActivity.class);
                    intent.putExtra("resourceId", adapter.getItem(position).getResource().getId());
                    startActivity(intent);


//                    Resource resource = indexes.get(position).getResource();
//                    int moveTo = mBookDecompressed.getBook().getSpine().findFirstResourceById
//                            (resource.getId());
//                    viewPager.setCurrentItem(moveTo);
//
//                    menuDrawer.closeMenu();
                }
            });
        }

        //Recursive create index
        private void getIndexRecursive(TOCReference tocReference) {
            if (tocReference != null)
                indexes.add(tocReference);

            for (TOCReference item : tocReference.getChildren()) {
                getIndexRecursive(item);
            }
        }
    }
}
