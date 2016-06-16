package com.papa.bible;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.papa.bible.data.Configuration;
import com.papa.bible.data.FolioReaderUtils;
import com.papa.bible.data.db.database.BookContentEntity;
import com.papa.bible.events.FrontSizeEvent;
import com.papa.bible.events.IFolioReaderWebViewInterceptLinkClickListener;
import com.papa.bible.util.Config;
import com.papa.bible.util.SharePreferenceUtil;
import com.papa.bible.view.ReaderWebView;
import com.papa.bible.view.VerticalViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReaderFragment extends Fragment implements SearchDialogFragment
        .OnSearchCompleteListener {

    SeekBar seekBar;
    private String path;
    private String baseURL;
    private ReaderWebView webView;
    private VerticalViewPager pager;
//    private ImageButton mPlayImg;

    private BookContentEntity mBookContentEntity;


    public ReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(FrontSizeEvent event) {
        webView.setDefaultFontSize(event.getSize());
    }

    public BookContentEntity getBookContentEntity() {
        return mBookContentEntity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folior_rader_view_pager, container, false);
        mBookContentEntity = (BookContentEntity) getArguments().getSerializable(Configuration
                .BOOK_ENTITY);
        webView = (ReaderWebView) view.findViewById(R.id.reader_webview);
        int size = SharePreferenceUtil.getInt(getActivity(), "font_size");
        if (size == 0)
            size = webView.getDefaultFontSize();
        webView.setDefaultFontSize(size);

        path = getArguments().getString(Configuration.PATH_DECOMPRESSED);
        baseURL = getArguments().getString(Configuration.BASE_URL);

        String data = FolioReaderUtils.getStringFromFile(path, true);

        String pathCSS = "file:///android_asset/style.css";
        String cssTag = String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\">",
                pathCSS);

        String toInject = String.format("\n%s\n</head>", cssTag);

        data = data.replace("</head>", toInject);

        webView.setInterceptLinkListener(new IFolioReaderWebViewInterceptLinkClickListener() {
            @Override
            public boolean interceptURL(String url) {

                if(getActivity() instanceof ReaderActivity){
                    ReaderActivity activity = (ReaderActivity) getActivity();
                    return activity.openUrl(url);
                }

//                List<SpineReference> list = ((BookDecompressed) Configuration.getData
//                        (Configuration.KEY_BOOK)).getBook().getSpine().getSpineReferences();
//                int position = FolioReaderUtils.getPositionResource(list, url);
//
//                if (position != -1) {
//                    pager.setCurrentItem(position, false);
//                    return true;
//                }

                return false;
            }
        });
        webView.loadDataWithBaseURL(baseURL,
                data,
                Configuration.HTML_MIMETYPE,
                Configuration.HTML_ENCODING,
                null);
        initScroll();
        return view;
    }


    private void initScroll() {
        String resourceId = getActivity().getIntent().getStringExtra(Config
                .KEY_RESOURCE_ID);
        if (mBookContentEntity.getResourceId().equals(resourceId)) {
            final int scrollX = getActivity().getIntent().getIntExtra(Config.KEY_SCROLLX, 0);
            final int scrollY = getActivity().getIntent().getIntExtra(Config.KEY_SCROLLY, 0);
            webView.setInitScroll(scrollX, scrollY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setPager(VerticalViewPager pager) {
        this.pager = pager;
    }


    public ReaderWebView getWebView() {
        return webView;
    }

//    @Subscribe
//    public void onEvent(PlayStatusEvent event) {
//        if (mPlayImg != null)
//            mPlayImg.setImageResource(event.getStatus() > 0 ? R
//                    .drawable.ic_play_play : R.drawable
//                    .ic_play_pause);
//
//    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    public int getFontSize() {
        return webView.getDefaultFontSize();
    }

    @Override
    public void onSearchComplete(String content) {
        webView.findAllAsync(content);
    }
}
