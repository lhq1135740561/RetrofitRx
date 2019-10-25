package com.ly.masterviewmodelretrofitrxjava.view.activity;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ly.masterviewmodelretrofitrxjava.R;
import com.ly.masterviewmodelretrofitrxjava.model.NewsPack;
import com.ly.masterviewmodelretrofitrxjava.view.base.BaseActivity;
import com.ly.masterviewmodelretrofitrxjava.view_model.NewsViewModel;
import com.ly.masterviewmodelretrofitrxjava.view_model.base.LViewModelProviders;

public class QueryNewsActivity extends BaseActivity {

    private NewsViewModel mNewsViewModel;
    private TextView mTvNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_news);
        mTvNews = findViewById(R.id.tv_news);
    }

    @Override
    protected ViewModel initViewModel() {
        mNewsViewModel = LViewModelProviders.of(this, NewsViewModel.class);
        mNewsViewModel.getNewsPackMutableLiveData().observe(this, this::handlerNews);
        return mNewsViewModel;
    }

    private void handlerNews(NewsPack newsPack) {
        StringBuilder result = new StringBuilder();
        for (NewsPack.News news : newsPack.getData()) {
            result.append("\n\n").append(new Gson().toJson(news));
        }
        mTvNews.setText(result.toString());
    }

    public void queryNews(View view) {
        mTvNews.setText(null);
        mNewsViewModel.getNews();
    }
}
