package com.joshimbriani.mymovement.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MovementDetailViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private long mMovementId;

    public MovementDetailViewModelFactory(Application application, long id) {
        mApplication = application;
        mMovementId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovementDetailViewModel(mApplication, mMovementId);
    }
}