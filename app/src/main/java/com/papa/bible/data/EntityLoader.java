package com.papa.bible.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import de.greenrobot.dao.query.Query;

public class EntityLoader<T> extends AsyncTaskLoader<List> {

    private List<T> mData;
    private Query mQuery;


    public EntityLoader(Context context, Query query) {
        super(context);
        mQuery = query;
    }

    @Override
    public List loadInBackground() {
        try {
            return mQuery.forCurrentThread().list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(List data) {
        if (isReset()) {
            onReleaseResources(data);
            return;
        }

        List<T> oldData = mData;
        mData = data;

        if (isStarted())
            super.deliverResult(data);

        if (oldData != null && oldData != data)
            onReleaseResources(oldData);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null)
            deliverResult(mData);

        // if (mFileObserver == null) {
        // mFileObserver = new FileObserver(mPath, FILE_OBSERVER_MASK) {
        // @Override
        // public void onEvent(int event, String path) {
        // onContentChanged();
        // }
        // };
        // }
        // mFileObserver.startWatching();

        if (takeContentChanged() || mData == null)
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(List data) {
        super.onCanceled(data);

        onReleaseResources(data);
    }

    protected void onReleaseResources(List<T> data) {

        // if (mFileObserver != null) {
        // mFileObserver.stopWatching();
        // mFileObserver = null;
        // }
    }
}