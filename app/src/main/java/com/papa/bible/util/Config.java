package com.papa.bible.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Papa on 2016/4/11.
 */
public class Config {

    public static final String KEY_BOOK = "book";
    public static final String KEY_RESOURCE_ID = "resourceId";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SCROLLX = "scrollX";
    public static final String KEY_SCROLLY = "scrollY";
    public static final String KEY_DATA = "data";

    public static final String FILE_NEW_KING_JAMES_VERSION = "New King james Version";
    public static final String FILE_NUEVA_VERSION_INTERNACIONAL = "Nueva Version Internacional";
    public static final String FILE_NEW_INTERMATIONAL_VERSION = "New Intermational Version";


    public static Fragment getFragmentForPosition(FragmentManager manager,
                                                  int pagerId, int position) {
        String tag = makeFragmentName(pagerId,
                position);
        Fragment fragment = manager.findFragmentByTag(tag);
        return fragment;
    }


    private static String makeFragmentName(int containerViewId, long id) {
        return "android:switcher:" + containerViewId + ":" + id;
    }
}
