package com.ly.masterviewmodelretrofitrxjava.http.datasource.base;

import com.ly.masterviewmodelretrofitrxjava.http.basic.callback.RequestCallback;
import com.ly.masterviewmodelretrofitrxjava.model.NewsPack;

public interface INewsDataSource {
    void getNews(RequestCallback<NewsPack> callback);
}
