package com.papa.bible;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.papa.bible.adapter.ReaderPagerAdapter;
import com.papa.bible.data.DataBaseManager;
import com.papa.bible.data.EntityLoader;
import com.papa.bible.data.db.dao.BookContentEntityDao;
import com.papa.bible.data.db.database.AudioEntity;
import com.papa.bible.data.db.database.BookContentEntity;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.data.db.database.BookmarkEntity;
import com.papa.bible.events.FrontSizeEvent;
import com.papa.bible.events.PlayStatusEvent;
import com.papa.bible.service.PlayerService;
import com.papa.bible.util.Config;
import com.papa.bible.util.SharePreferenceUtil;
import com.papa.bible.view.VerticalViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class ReaderActivity extends BaseActivity implements LoaderManager
        .LoaderCallbacks<List<BookContentEntity>>, SeekBar.OnSeekBarChangeListener,
        SearchDialogFragment
                .OnSearchCompleteListener {

    private static final int REQUEST_CODE_CHAPTER = 1;
    private static final int REQUEST_CODE_AUDIO = 2;

    @Bind(R.id.view_pager)
    VerticalViewPager mViewPager;

    @Bind(R.id.img_play)
    ImageView mPlayImg;

    @Bind(R.id.audio_title_text)
    TextView mAudioTitleTv;

    @Bind(R.id.size_seek_bar)
    SeekBar mSeekBar;

    private List<BookContentEntity> mBookContentEntities;

    private ReaderPagerAdapter mReaderPagerAdapter;
    private BookEntity mBookEntity;
    private String mResourceId;


    private PlayerServiceConnection serviceConnection;
    private PlayerService.IPlayerService playerService = null;
    private boolean serviceAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBookEntity = (BookEntity) getIntent().getSerializableExtra(Config.KEY_BOOK);
        mResourceId = getIntent().getStringExtra(Config
                .KEY_RESOURCE_ID);
        bindPlayerService();
        setContentView(R.layout.activity_reading);
        initWebViewSize();
        initData();
    }

    private void initWebViewSize() {
        mAudioTitleTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(72);
        int size = SharePreferenceUtil.getInt(this, "font_size");
        if (size == 0)
            size = 25;
        mSeekBar.setProgress(size);
    }

    private void initData() {
        getSupportLoaderManager().initLoader(0, null, this);
    }


    public PlayerService.IPlayerService getPlayerService() {
        return playerService;
    }

    public void setPlayerService(PlayerService.IPlayerService playerService) {
        this.playerService = playerService;
    }


    private ReaderFragment getCurrentPageFragment() {
//        return (ReaderFragment) Config.getFragmentForPosition(getSupportFragmentManager(),
//                mViewPager.getId(), mViewPager.getCurrentItem());
        return (ReaderFragment) mReaderPagerAdapter.getPage(mViewPager.getCurrentItem());

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        bindPlayerService();
    }


    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }


    public void bindPlayerService() {
        serviceConnection = new PlayerServiceConnection();
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra(Config.KEY_BOOK, mBookEntity);
        bindService(intent, getServiceConnection(), Activity
                .BIND_AUTO_CREATE);

    }

    public void unbindPlayerService() {
        if (serviceAvailable) {
            serviceAvailable = false;
            unbindService(getServiceConnection());
        }
    }

    @Override
    protected void onDestroy() {
        saveAudio();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindPlayerService();
    }

    private void saveAudio() {
        String audio = getPlayerService().getAudioPath() + ":" + String.valueOf(getPlayerService
                ().getProgress());
        SharePreferenceUtil.setValue(this, mBookEntity.getPath(), audio);
    }

    public void selectChapter() {
        Intent intent = new Intent(this, ChapterActivity.class);
        intent.putExtra(Config.KEY_BOOK, mBookEntity);
        intent.putExtra(Config.KEY_TYPE, 1);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            mResourceId = data.getStringExtra(Config
                    .KEY_RESOURCE_ID);
            if (!TextUtils.isEmpty(mResourceId)) {
                mViewPager.setCurrentItem(getIndex(mBookContentEntities, mResourceId));
            }
        } else if (requestCode == REQUEST_CODE_AUDIO && resultCode == RESULT_OK) {
            if (data != null) {
                AudioEntity entity = (AudioEntity) data.getSerializableExtra(Config.KEY_DATA);
                if (entity != null) {
                    getPlayerService().play(entity.getAudioPath(), 0);
                }
            }
        }
    }

    public boolean isServiceAvailable() {
        return serviceAvailable;
    }

    @Override
    public Loader<List<BookContentEntity>> onCreateLoader(int id, Bundle args) {
        QueryBuilder<BookContentEntity> queryBuilder = DataBaseManager.getInstance(this)
                .getDaoSession()
                .getBookContentEntityDao().queryBuilder().where(BookContentEntityDao.Properties
                        .BookId.eq(mBookEntity.getId()));
        Query<BookContentEntity> query = queryBuilder.build();
        return new EntityLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<BookContentEntity>> loader, List<BookContentEntity>
            data) {
        mBookContentEntities = data;
        mReaderPagerAdapter = new ReaderPagerAdapter(getSupportFragmentManager(),
                data, mBookEntity.getBaseUrl(), mViewPager);
        mViewPager.setAdapter(mReaderPagerAdapter);
        if (!TextUtils.isEmpty(mResourceId)) {
            mViewPager.setCurrentItem(getIndex(data, mResourceId));
        }
    }


    @Override
    public void onLoaderReset(Loader<List<BookContentEntity>> loader) {

    }

    public boolean openUrl(String url) {
        if (mBookContentEntities == null || TextUtils.isEmpty(url))
            return false;
        for (int index = 0; index < mBookContentEntities.size(); index++) {
            if (url.endsWith(mBookContentEntities.get(index).getUrlResource())) {
                mViewPager.setCurrentItem(index);
                return true;
            }
        }
        return false;
    }


    private int getIndex(List<BookContentEntity> list, String resourceId) {
        if (list != null) {
            for (int index = 0; index < list.size(); index++) {
                if (resourceId.equals(list.get(index).getResourceId()))
                    return index;
            }
        }
        return 0;
    }

    @OnClick(R.id.audio_title_text)
    public void onSelectAudio() {
        Intent intent = new Intent(this, AudioListActivity.class);
        intent.putExtra(Config.KEY_BOOK, mBookEntity);
        startActivityForResult(intent, REQUEST_CODE_AUDIO);
    }


    @OnClick(R.id.img_play)
    public void onPlayPause() {
        PlayerService.IPlayerService playerService = getPlayerService();
        if (playerService.getProgress() > 0) {
            if (playerService.isPlay()) {
                playerService.onPause();
            } else {
                playerService.onResume();
            }
        } else {
            String cacheAudio = SharePreferenceUtil.getString(this, mBookEntity.getPath());
            String path = "";
            int progress = 0;
            if (!TextUtils.isEmpty(cacheAudio)) {
                String[] array = cacheAudio.split(":");
                if (array.length == 2) {
                    path = array[0];
                    progress = Integer.valueOf(array[1]);
                }
            }
            playerService.play(path, progress);
        }
    }

    @OnClick(R.id.img_next)
    public void onPlayNext() {
        getPlayerService().next();
    }

    @OnClick(R.id.img_pre)
    public void onPlayPre() {
        getPlayerService().pre();
    }

    @OnClick(R.id.img_chapter)
    public void onSelectChapter() {
        selectChapter();
    }

    @OnClick(R.id.img_search)
    public void onSearch() {
        SearchDialogFragment.showSearchDialog(this);
    }

    @OnClick(R.id.img_bookmark)
    public void onSaveBookMark() {
        BookmarkEntity bookmarkEntity = new BookmarkEntity();
        bookmarkEntity.setBookEntity(mBookEntity);
        bookmarkEntity.setDate(new Date());
        ReaderFragment fragment = getCurrentPageFragment();
        if (fragment != null) {
            bookmarkEntity.setResourceId(fragment.getBookContentEntity().getResourceId());
            bookmarkEntity.setScrollX(fragment.getWebView().getScrollX());
            bookmarkEntity.setScrollY(fragment.getWebView().getScrollY());
        }
        DataBaseManager.getInstance(App.getInstance()).getDaoSession().getBookmarkEntityDao()
                .insertOrReplace(bookmarkEntity);
        Toast.makeText(this, R.string.save_bookmark, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onEvent(PlayStatusEvent event) {
        mPlayImg.setImageResource(event.getStatus() > 0 ? R
                .drawable.ic_play_play : R.drawable
                .ic_play_pause);
        if (event.getEntity() != null) {
            mAudioTitleTv.setText(event.getEntity().getAudioName());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        EventBus.getDefault().post(new FrontSizeEvent(seekBar.getProgress()));
        SharePreferenceUtil.setValue(this, "font_size", seekBar.getProgress());

    }

    @Override
    public void onSearchComplete(String content) {
        if (getCurrentPageFragment() != null) {
            getCurrentPageFragment().onSearchComplete(content);
        }
    }

    public class PlayerServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName className, IBinder playerService) {
            setPlayerService((PlayerService.IPlayerService) playerService);
            try {
                serviceAvailable = true;
                //viewPager.setCurrentItem(getPage(getPlayerService().getAudioPath()));
                //Toast.makeText(getPassageActivity(), "PassageID="+getServiceInterface()
                // .getAudioPath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
//                Analytics.reportCaughtException(getPassageActivity(), e);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceAvailable = false;
            setPlayerService(null);
        }


    }


}
