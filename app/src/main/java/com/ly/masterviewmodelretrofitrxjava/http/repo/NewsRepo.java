package com.ly.masterviewmodelretrofitrxjava.http.repo;

import android.arch.lifecycle.MutableLiveData;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRepo;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.INewsDataSource;
import com.ly.masterviewmodelretrofitrxjava.model.NewsPack;

public class NewsRepo extends BaseRepo<INewsDataSource> {
    public NewsRepo(INewsDataSource remoteDataSource) {
        super(remoteDataSource);
    }

    public MutableLiveData<NewsPack> getNews() {
        MutableLiveData<NewsPack> newsPackMutableLiveData = new MutableLiveData<>();
        mRemoteDataSource.getNews(newsPackMutableLiveData::setValue);
        return newsPackMutableLiveData;
    }
}
