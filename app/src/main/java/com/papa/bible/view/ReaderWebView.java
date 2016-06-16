package com.papa.bible.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.papa.bible.events.IFolioReaderWebViewInterceptLinkClickListener;



public class ReaderWebView extends WebView {
    private IFolioReaderWebViewInterceptLinkClickListener interceptLinkListener;

    private int mScrollX;
    private int mScrollY;

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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mScrollX > 0 || mScrollY > 0) {
                    scrollTo(mScrollX, mScrollY);
                }
                //findAllAsync("meeting");
            }

        });
    }

    public void setInitScroll(int scrollX, int scrollY) {
        mScrollX = scrollX;
        mScrollY = scrollY;
    }

    public int getDefaultFontSize() {
        return getSettings().getDefaultFontSize();
    }

    public void setDefaultFontSize(int size) {
        getSettings().setDefaultFontSize(size);
    }

    @Override
    public int getContentHeight() {
        return this.computeVerticalScrollRange() - this.getHeight();
    }

    public void setInterceptLinkListener(IFolioReaderWebViewInterceptLinkClickListener
                                                 interceptLinkListener) {
        this.interceptLinkListener = interceptLinkListener;
    }
}
