package com.papa.bible;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.papa.bible.bean.BookDecompressed;
import com.papa.bible.data.Configuration;
import com.papa.bible.data.FolioReaderUtils;
import com.papa.bible.events.IFolioReaderWebViewInterceptLinkClickListener;
import com.papa.bible.view.ReaderWebView;
import com.papa.bible.view.VerticalViewPager;

import java.util.List;

import nl.siegmann.epublib.domain.SpineReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReaderFragment extends Fragment {

    private VerticalViewPager pager;

    public ReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folior_rader_view_pager, container, false);
        webView = (ReaderWebView) view.findViewById(R.id.reader_webview);

        path = getArguments().getString(Configuration.PATH_DECOMPRESSED);
        baseURL = getArguments().getString(Configuration.BASE_URL);

        String data = FolioReaderUtils.getStringFromFile(path, true);

        String pathCSS = "file:///android_asset/style.css";
        String cssTag = String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\">", pathCSS);

        String toInject = String.format("\n%s\n</head>", cssTag);

        data = data.replace("</head>", toInject);

        webView.setInterceptLinkListener(new IFolioReaderWebViewInterceptLinkClickListener() {
            @Override
            public boolean interceptURL(String url) {

                List<SpineReference> list = ((BookDecompressed) Configuration.getData(Configuration.KEY_BOOK)).getBook().getSpine().getSpineReferences();
                int position = FolioReaderUtils.getPositionResource(list, url);

                if (position != -1) {
                    pager.setCurrentItem(position, false);
                    return true;
                }

                return false;
            }
        });

        webView.loadDataWithBaseURL(baseURL,
                                    data,
                                    Configuration.HTML_MIMETYPE,
                                    Configuration.HTML_ENCODING,
                                    null);

        return view;
    }

    public void setPager(VerticalViewPager pager) {
        this.pager = pager;
    }

    private String path;
    private String baseURL;
    private ReaderWebView webView;
}
