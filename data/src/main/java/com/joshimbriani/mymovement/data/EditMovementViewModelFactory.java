package com.joshimbriani.mymovement.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joshimbriani.mymovement.data.EditMovementViewModel;

public class EditMovementViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private long mMovementId;

    public EditMovementViewModelFactory(Application application, long id) {
        mApplication = application;
        mMovementId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditMovementViewModel(mApplication, mMovementId);
    }
}
