package com.papa.bible.data;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.AsyncTaskLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLoader extends AsyncTaskLoader<List<File>> {


    private List<File> mData;


    public FileLoader(Context context) {
        super(context);
    }

    @Override
    public List loadInBackground() {
        try {
            return epubList(Environment.getExternalStorageDirectory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        return res;
    }

    @Override
    public void deliverResult(List<File> data) {
        if (isReset()) {
            onReleaseResources(data);
            return;
        }

        List<File> oldData = mData;
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
    public void onCanceled(List<File> data) {
        super.onCanceled(data);

        onReleaseResources(data);
    }

    protected void onReleaseResources(List<File> data) {

        // if (mFileObserver != null) {
        // mFileObserver.stopWatching();
        // mFileObserver = null;
        // }
    }
}