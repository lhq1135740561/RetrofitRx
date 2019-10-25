package com.ly.masterviewmodelretrofitrxjava.view_model;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.http.datasource.NewsDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.repo.NewsRepo;
import com.ly.masterviewmodelretrofitrxjava.model.NewsPack;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

public class NewsViewModel extends BaseViewModel {
    private MutableLiveData<NewsPack> mNewsPackMutableLiveData = new MutableLiveData<>();

    private NewsRepo mNewsRepo = new NewsRepo(new NewsDataSource(this));

    public void getNews() {
        mNewsRepo.getNews().observe(mLifecycleOwner, newsPack ->
                mNewsPackMutableLiveData.setValue(newsPack));
    }

    public MutableLiveData<NewsPack> getNewsPackMutableLiveData() {
        return mNewsPackMutableLiveData;
    }
}
