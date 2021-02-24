package com.ngsown.knowyoursky.di.component;

import com.ngsown.knowyoursky.di.PerActivity;
import com.ngsown.knowyoursky.di.module.ActivityModule;
import com.ngsown.knowyoursky.ui.main.MainActivity;

import dagger.Component;

@PerActivity
@Component(modules = {ActivityModule.class})
public interface ActivityComponent {
    void inject(MainActivity mainActivity);
}
