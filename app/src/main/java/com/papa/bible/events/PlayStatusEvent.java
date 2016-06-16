package com.papa.bible.events;

import com.papa.bible.data.db.database.AudioEntity;

/**
 * Created by Papa on 2016/4/20.
 */
public class PlayStatusEvent {

    /**
     * 0-pause 1-play
     */
    private int mStatus;
    private AudioEntity mEntity;


    public PlayStatusEvent(int status, AudioEntity entity) {
        mStatus = status;
        mEntity = entity;
    }

    public int getStatus() {
        return mStatus;
    }

    public AudioEntity getEntity() {
        return mEntity;
    }

}
