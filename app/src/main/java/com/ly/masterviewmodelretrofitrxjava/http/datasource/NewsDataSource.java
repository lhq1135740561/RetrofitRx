package com.ly.masterviewmodelretrofitrxjava.http.datasource;

import com.ly.masterviewmodelretrofitrxjava.http.basic.BaseRemoteDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.http.basic.config.HttpConfig;
import com.ly.masterviewmodelretrofitrxjava.http.datasource.base.INewsDataSource;
import com.ly.masterviewmodelretrofitrxjava.http.service.ApiService;
import com.ly.masterviewmodelretrofitrxjava.model.NewsPack;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.BaseViewModel;

public class NewsDataSource extends BaseRemoteDataSource implements INewsDataSource {
    public NewsDataSource(BaseViewModel baseViewModel) {
        super(baseViewModel);
    }

    @Override
    public void getNews(RequestCallback<NewsPack> callback) {
        execute(getService(ApiService.class, HttpConfig.HTTP_REQUEST_NEWS).getNews(), callback);
    }
}
