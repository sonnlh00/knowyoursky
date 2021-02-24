package com.ngsown.knowyoursky.ui.base;

public interface BasePresenter <T> {
    void setView(T view);
    void initialize();
    void resume();
    void pause();
}
