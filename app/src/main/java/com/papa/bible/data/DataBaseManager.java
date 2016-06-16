package com.papa.bible.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.papa.bible.BuildConfig;
import com.papa.bible.data.db.dao.DaoMaster;
import com.papa.bible.data.db.dao.DaoSession;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2016/1/27.
 */
public final class DataBaseManager {

    private static final String DB_NAME = "teshehui";
    private static DataBaseManager sInstance;
    private DaoSession mDaoSession;
    private SQLiteDatabase mSQLiteDatabase;

    private DataBaseManager(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mSQLiteDatabase = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(mSQLiteDatabase);
        mDaoSession = daoMaster.newSession();
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }

    public static DataBaseManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DataBaseManager.class) {
                if (sInstance == null) {
                    sInstance = new DataBaseManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return mSQLiteDatabase;
    }
}
