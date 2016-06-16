package com.papa.bible.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.papa.bible.ReaderActivity;
import com.papa.bible.data.db.database.AudioEntity;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.util.Config;

import java.util.List;


public class PlayerService extends Service {
    private static final String INTENT_EXTRA_PASSAGE_ID = "passageId";
    private static final String INTENT_EXTRA_POSITION = "position";
    private static final String LOG_TAG = "PLAYSVC";
    private static final String SERVICE_NAME = "com.papa.bible.service.PlayerService";
    private final Binder binder = new PlayerServiceBinder();
    ReadingsPlayer readingsPlayer;

    public static void requestPlay(ReaderActivity passageActivity, String resoucreId, int
            positionAsThousandth) {
        Intent intent = new Intent(passageActivity, PlayerService.class);
        intent.putExtra(INTENT_EXTRA_PASSAGE_ID, resoucreId);
        intent.putExtra(INTENT_EXTRA_POSITION, positionAsThousandth);
 //       intent.putExtra(ParcelableReadings.PARCEL_NAME, passageActivity.getPassableReadings());
        passageActivity.startService(intent);
    }

    public static void requestStop(Context context) {
        Intent intent = new Intent(ReadingsPlayer.INTENT_STOP);
        context.sendBroadcast(intent);
    }

    public static Boolean isServiceRunning(Context context) {
        Boolean serviceRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager
                .getRunningServices(50);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().equals(SERVICE_NAME)) {
                serviceRunning = true;
                break;
            }
        }
        return serviceRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            createReadingsPlayer(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private ReadingsPlayer createReadingsPlayer(Intent intent) {
        if (readingsPlayer == null) {
            BookEntity bookEntity = (BookEntity) intent.getSerializableExtra
                    (Config.KEY_BOOK);
            //String passageId = intent.getExtras().getString(INTENT_EXTRA_PASSAGE_ID);
            readingsPlayer = new ReadingsPlayer(this, bookEntity);
        }
        return readingsPlayer;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            createReadingsPlayer(intent);
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (readingsPlayer != null) {
            readingsPlayer.doDestroy();
            readingsPlayer = null;
        }
        super.onDestroy();
    }

    public interface IPlayerService {
        void play(String resourceId, int position);

        boolean isPlay();

        void stop();

        void onResume();

        void onPause();

        String getAudioPath();

        int getProgress();

        void setPosition(int progressAsThousandth);

        void next();

        void pre();

        AudioEntity getAudioEntity();
    }

    public class PlayerServiceBinder extends Binder implements IPlayerService {

        @Override
        public void play(String resourceId, int position) {
            readingsPlayer.doPlay(resourceId, position);
        }

        @Override
        public boolean isPlay() {
            return readingsPlayer.isPlay();
        }

        @Override
        public void stop() {
            readingsPlayer.doStop();
        }

        @Override
        public void onResume() {
            readingsPlayer.doResume();
        }

        @Override
        public void onPause() {
            readingsPlayer.doPause();
        }

        @Override
        public String getAudioPath() {
            return readingsPlayer.getAudioPath();
        }

        @Override
        public int getProgress() {
            return readingsPlayer.getProgress();
        }



        @Override
        public void setPosition(int positionAsThousandth) {
            readingsPlayer.setPlayerPosition(positionAsThousandth);
        }

        @Override
        public void next() {
            readingsPlayer.doNext();
        }

        @Override
        public void pre() {
            readingsPlayer.doPre();
        }

        @Override
        public AudioEntity getAudioEntity() {
            return readingsPlayer.getCurrentEntity();
        }
    }
}
