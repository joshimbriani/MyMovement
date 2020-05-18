package com.joshimbriani.mymovement.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joshimbriani.mymovement.data.MovementDetailViewModel;

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