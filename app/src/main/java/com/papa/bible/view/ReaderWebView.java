package com.papa.bible.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.papa.bible.events.IFolioReaderWebViewInterceptLinkClickListener;


/**
 * Created by rodrigo.almeida on 28/04/15.
 */
public class ReaderWebView extends WebView {
    private IFolioReaderWebViewInterceptLinkClickListener interceptLinkListener;

    public ReaderWebView(Context context) {
        super(context);
        init();
    }

    public ReaderWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReaderWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.getSettings().setJavaScriptEnabled(true);
        this.setWebViewClient(new WebViewClient() {
            /**
             * Return false to load content inside webView other wise true;
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean intercept = false;

                if (interceptLinkListener != null)
                    intercept = interceptLinkListener.interceptURL(url);

                return intercept;
            }
        });
    }


    @Override
    public int getContentHeight() {
        return this.computeVerticalScrollRange() - this.getHeight();
    }

    public void setInterceptLinkListener(IFolioReaderWebViewInterceptLinkClickListener interceptLinkListener) {
        this.interceptLinkListener = interceptLinkListener;
    }
}
