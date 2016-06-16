package com.papa.bible.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.papa.bible.data.DataBaseManager;
import com.papa.bible.data.db.dao.AudioEntityDao;
import com.papa.bible.data.db.database.AudioEntity;
import com.papa.bible.data.db.database.BookEntity;
import com.papa.bible.events.PlayStatusEvent;
import com.papa.bible.util.SharePreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


public class ReadingsPlayer implements AudioManager.OnAudioFocusChangeListener, MediaPlayer
        .OnCompletionListener {
    public static final String INTENT_STOP = "stop";
    private static final String LOG_TAG = "PLAYER";
    private final PlayerService playerService;
    BookEntity mBookEntity;
    List<AudioEntity> mBookContentEntities;
    PlayerBroadcastReceiver playerBroadcastReceiver;
    MediaPlayer mediaPlayer;
    private boolean isPause;

    //    private IPlayerNotification playerNotification;
    private String audioPath = "";

    private AudioEntity mCurrentEntity;


    public ReadingsPlayer(PlayerService playerService, BookEntity bookEntity) {
        this.playerService = playerService;
        mBookEntity = bookEntity;
        QueryBuilder<AudioEntity> queryBuilder = DataBaseManager.getInstance(playerService)
                .getDaoSession()
                .getAudioEntityDao().queryBuilder().where(AudioEntityDao.Properties
                        .BookId.eq(mBookEntity.getId()));
        mBookContentEntities = queryBuilder.build().list();
        initPlayer();
//        playerNotification = new PlayerNotificationApi14(playerService, this);
//        playerNotification.show();
        registerPlayerBroadcastReceiver();
    }


    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        initEntity();
    }

    private void initEntity() {
        String cacheAudio = SharePreferenceUtil.getString(playerService, mBookEntity.getPath());
        String path = "";
        int progress = 0;
        if (!TextUtils.isEmpty(cacheAudio)) {
            String[] array = cacheAudio.split(":");
            if (array.length == 2) {
                path = array[0];
                progress = Integer.valueOf(array[1]);
            }
        }
        doPlay(path, progress);
    }


    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String resourceId) {
        this.audioPath = resourceId;
    }

    void doPlay(String audioPath, int positionAsThousandth) {
        if (getAudioFocus()) {
            try {
                AudioEntity entity = getEntity(mBookContentEntities, audioPath);
                if (entity == null)
                    return;
                mCurrentEntity = entity;
                String filePath = entity.getAudioPath();
                setAudioPath(filePath);
                File file = new File(filePath);
                if (file.exists()) {
                    mediaPlayer.reset();// 把各项参数恢复到初始状态
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.setOnPreparedListener(new PreparedListener(positionAsThousandth));
                    mediaPlayer.setOnCompletionListener(this);
                    mediaPlayer.prepare(); // 进行缓冲
                    // 注册一个监听器
                } else {
//                Toast.makeText(playerService, playerService.getString(R.string
//                        .mp3_not_found_goto_settings), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }

    private AudioEntity getEntity(List<AudioEntity> list, String audioPath) {
        if (list != null && !list.isEmpty()) {
            if (TextUtils.isEmpty(audioPath))
                return list.get(0);
            for (int index = 0; index < list.size(); index++) {
                if (audioPath.equals(list.get(index).getAudioPath()))
                    return list.get(index);
            }
        }
        return null;
    }

    private AudioEntity getNextEntity(List<AudioEntity> list, String audioPath) {
        if (list != null) {
            for (int index = 0; index < list.size(); index++) {
                if (audioPath.equals(list.get(index).getAudioPath())) {
                    if (index < (list.size() - 1)) {
                        return list.get(index + 1);
                    }
                }
            }
        }
        return null;
    }

    private AudioEntity getPreEntity(List<AudioEntity> list, String audioPath) {
        if (list != null) {
            for (int index = 0; index < list.size(); index++) {
                if (audioPath.equals(list.get(index).getAudioPath()))
                    if (index > 0) {
                        return list.get(index - 1);
                    }
            }
        }
        return null;
    }


    public boolean isPlay() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }


    public void doStop() {
        abandonAudioFocus();
        setAudioPath("");
//        destroyNotification();
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.prepare();
                EventBus.getDefault().post(new PlayStatusEvent(0, mCurrentEntity));
            }
            // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doNext() {
        AudioEntity entity = getNextEntity(mBookContentEntities, getAudioPath());
        if (entity != null) {
            doPlay(entity.getAudioPath(), 0);
        }
    }

    public void doPre() {
        AudioEntity entity = getPreEntity(mBookContentEntities, getAudioPath());
        if (entity != null) {
            doPlay(entity.getAudioPath(), 0);
        }
    }


    private void destroyNotification() {
        Log.i(LOG_TAG, "destroyNotification");
//        if (playerNotification != null) {
//            playerNotification.destroy();
//            playerNotification = null;
//        }
    }

    public void doPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            EventBus.getDefault().post(new PlayStatusEvent(0, mCurrentEntity));
        }
    }

    public void doResume() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            EventBus.getDefault().post(new PlayStatusEvent(1, mCurrentEntity));
        }
    }

    public void doDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (playerBroadcastReceiver != null) {
            playerService.unregisterReceiver(playerBroadcastReceiver);
            playerBroadcastReceiver = null;
        }
    }

    public void onAudioFocusChange(int focusChange) {
        Log.i(LOG_TAG, "onAudioFocusChange=" + focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                doResume();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                doStop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                doPause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                doPause();
                //mediaPlayer.setVolume(1.0f, 1.0f);
                break;
        }
    }

    private boolean getAudioFocus() {
        AudioManager audioManager = (AudioManager) playerService.getSystemService(Context
                .AUDIO_SERVICE);
        return (audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager
                .AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private void abandonAudioFocus() {
        AudioManager audioManager = (AudioManager) playerService.getSystemService(Context
                .AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
    }


    private void registerPlayerBroadcastReceiver() {
        playerBroadcastReceiver = new PlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_STOP);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        playerService.registerReceiver(playerBroadcastReceiver, intentFilter);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        doNext();
//        advanceOrExit();
    }

    private void advanceOrExit() {
//        for (int i = 0; i < parcelableReadings.passages.size(); i++) {
//            if (getAudioPath() == parcelableReadings.passages.get(i).getAudioPath()) {
//                i++;
//                if (i < parcelableReadings.passages.size()) {
//                    // Play next
//                    setAudioPath(parcelableReadings.passages.get(i).getAudioPath());
////                    doPlay(0);
//                } else {
////                    doStop();
//                }
//                break;
//            }
//        }
    }

    private void doBeep() {
//        beep = true;
//        mediaPlayer.release();
//        mediaPlayer = MediaPlayer.create(playerService, R.raw.beep);
//        mediaPlayer.setOnCompletionListener(this);
//        mediaPlayer.start();
    }

    public int getProgress() {
        int progress = 0;

        try {
            //Split calc to determine source of exceptions
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            progress = (currentPosition * 1000) / duration;
        } catch (Exception e) {
            //               Analytics.reportCaughtException(playerService, e);
        }

        return progress;
    }

    public AudioEntity getCurrentEntity() {
        return mCurrentEntity;
    }

    void setPlayerPosition(int positionAsThousandth) {
        mediaPlayer.seekTo((mediaPlayer.getDuration() * positionAsThousandth) / 1000);

    }

    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start(); // 开始播放
            if (currentTime > 0) { // 如果音乐不是从头播放
                mediaPlayer.seekTo((mediaPlayer.getDuration() * currentTime) / 1000);
//               mediaPlayer.seekTo(currentTime);
            }
            EventBus.getDefault().post(new PlayStatusEvent(1, mCurrentEntity));
//            Intent intent = new Intent();
//            intent.setAction(MUSIC_DURATION);
//            duration = mediaPlayer.getDuration();
//            intent.putExtra("duration", duration);  //通过Intent来传递歌曲的总长度
//            sendBroadcast(intent);
        }
    }

    class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(INTENT_STOP)) {
                doStop();
            } else if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                doStop();
            }
        }
    }
}
