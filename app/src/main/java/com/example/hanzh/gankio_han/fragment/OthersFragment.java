package com.example.hanzh.gankio_han.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hanzh.gankio_han.App;
import com.example.hanzh.gankio_han.R;
import com.example.hanzh.gankio_han.adapter.GankListAdapter;
import com.example.hanzh.gankio_han.adapter.MyStaggeredViewAdapter;
import com.example.hanzh.gankio_han.adapter.OthersRecyclerViewAdapter;
import com.example.hanzh.gankio_han.model.CategoricalData;
import com.example.hanzh.gankio_han.model.DailyData;
import com.example.hanzh.gankio_han.model.Gank;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OthersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final int IOS = 1;
    private static final int ANDROID = 2;
    private static final int TUIJIAN = 3;
    private static final int ZIYUAN = 4;
    private RecyclerView mRecyclerView;
    private OthersRecyclerViewAdapter mAdapter;
    private View mView;
    private int flag;
    private CategoricalData meizhiData;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = (int) getArguments().get("flag");
        switch (flag) {
            case IOS:
                url = "http://gank.avosapps.com/api/data/iOS/40/1";
                break;
            case ANDROID:
                url = "http://gank.avosapps.com/api/data/Android/40/1";
                break;
            case TUIJIAN:
                url = "http://gank.avosapps.com/api/data/瞎推荐/40/1";
                break;
            case ZIYUAN:
                url = "http://gank.avosapps.com/api/data/拓展资源/40/1";
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_others, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView(mView);
        initViewAction();
    }

    private void initView(View view) {
        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.otherSRL);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_gank);
    }

    private void initViewAction() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new OthersRecyclerViewAdapter(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getData(url);
    }

    @Override
    public void onRefresh() {

        // 刷新时模拟数据的变化
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                getData(url);
                System.out.println("url : "+url);
            }
        }, 1000);
    }

    private void getData(String url) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        //enqueue开启一步线程访问网络
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("client onFailure");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    System.out.println("body:" + body);
                    meizhiData = null;
                    CategoricalData data = new Gson().fromJson(body, CategoricalData.class);
                    if (data.isError() == true) {
                        Toast.makeText(getActivity(), "没有新数据", Toast.LENGTH_SHORT).show();
                        System.out.println("no data error");
                    } else {
                        int count = data.getResults().size();
                        if (count <= 0) {
                            Toast.makeText(getActivity(), "没有新数据", Toast.LENGTH_SHORT).show();
                            System.out.println("no data error");
                        } else {
                            meizhiData = data;
                            for (int i = 0; i < count; i++) {
                                Gank gank = meizhiData.getResults().get(i);
                                System.out.println(gank.toString());
                                getActivity()
                                        .runOnUiThread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // 刷新时模拟数据的变化
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                mAdapter.mGankList.clear();
                                                                mAdapter.mGankList.addAll(meizhiData.getResults());
                                                                mAdapter.notifyDataSetChanged();
                                                            }
                                                        }, 1000);
                                                    }
                                                }
                                        );
                            }
                        }
                    }
                }
            }
        });
    }
}
