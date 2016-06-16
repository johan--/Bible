package com.papa.bible.service;

public interface IPlayerUi {
    void onPassageChange(String sourceId);
    void onEndAll();
    void onPassageEnding(String sourceId);
}
