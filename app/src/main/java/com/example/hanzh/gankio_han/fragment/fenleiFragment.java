package com.example.hanzh.gankio_han.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hanzh.gankio_han.App;
import com.example.hanzh.gankio_han.PictureActivity;
import com.example.hanzh.gankio_han.R;
import com.example.hanzh.gankio_han.adapter.MyStaggeredViewAdapter;
import com.example.hanzh.gankio_han.model.CategoricalData;
import com.example.hanzh.gankio_han.model.Gank;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class fenleiFragment extends SwipeRefreshFragment implements SwipeRefreshLayout.OnRefreshListener, MyStaggeredViewAdapter.OnItemClickListener, View.OnClickListener {

    private static final int TOPDATA = 0;   //数据顶端
    private static final int BOTTOMDATA = 1;    //数据底端
    private static final int PRELOAD_SIZE = 2;
    boolean mIsFirstTimeTouchBottom = true;
    int mPage = 1;
    int bottomPage = 1;

    private View mView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyStaggeredViewAdapter mStaggeredAdapter;
    private RecyclerView mRecyclerView;
    //private RecyclerView.LayoutManager mLayoutManager;
    private static final int SPAN_COUNT = 2;    //两列
    private CategoricalData meizhiData;
    //分类数据URL:http://gank.avosapps.com/api/data/数据类型/请求个数/第几页
    private static final String staticUrl = "http://gank.avosapps.com/api/data/福利/10/";
    private String url = "";
    private List<Gank> mGankList;

    public fenleiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("进入onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("进入onCreateView");
        url = staticUrl + mPage;
        QueryBuilder query = new QueryBuilder(Gank.class).appendOrderDescBy("publishedAt");

        mGankList = new ArrayList<>();
        ArrayList<Gank> temp = App.sDb.query(query);
        System.out.println("有" + temp.size() + "条数据");
        mGankList.addAll(temp);
        mView = inflater.inflate(R.layout.fragment_fenlei, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.id_swiperefreshlayout);
        //设置瀑布流布局,SPAN_COUNT列
        final StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        mStaggeredAdapter = new MyStaggeredViewAdapter(getActivity());
        mStaggeredAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mStaggeredAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(getScrollToBottomListener(mLayoutManager));
        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFenLeiFromOrm(mGankList);
        getFenLeiData(url,TOPDATA);
        System.out.println("进入onActivityCreated");
    }

    RecyclerView.OnScrollListener getScrollToBottomListener(
            final StaggeredGridLayoutManager layoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                boolean isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1] >= mStaggeredAdapter.getItemCount() - PRELOAD_SIZE;
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
                    if (!mIsFirstTimeTouchBottom) {
                        //到达页面底端
                        Toast.makeText(getActivity(), "到达页面底端", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                        bottomPage += 1;
                        url = staticUrl + bottomPage;
                        getFenLeiData(url, BOTTOMDATA);
                    } else {
                        mIsFirstTimeTouchBottom = false;
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        url = staticUrl + mPage;
        getFenLeiData(url, TOPDATA);
    }

    //MyStaggeredViewAdapter
    @Override
    public void onItemClick(View view, int position, Gank gank) {

        switch (view.getId()) {
            case R.id.iv_meizhi:
                Intent intent = new Intent();
                intent.setClass(getActivity(), PictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Url", gank.getUrl());
                bundle.putString("Desc", gank.getPublishedAt() + " Provided By " + gank.getWho());
                bundle.putString("PicId", gank.getObjectId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.tv_title:
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Toast.makeText(getActivity(), "long click!", Toast.LENGTH_SHORT).show();
    }

    private void saveGanks(List<Gank> ganks) {
        App.sDb.insert(ganks, ConflictAlgorithm.Ignore);
    }

    private void getFenLeiFromOrm(List<Gank> ganks) {
        System.out.println("进入setFenLeiFromOrm");
        for (int i = 0; i < ganks.size(); i++) {
            System.out.println("url : " + ganks.get(i).getUrl());
            System.out.println("updatedAt : " + ganks.get(i).getUpdatedAt());
            mStaggeredAdapter.ganks.add(ganks.get(i));
        }
        mStaggeredAdapter.notifyDataSetChanged();
    }

    private void getFenLeiData(String url, final int DATA) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        switch (DATA) {
            case 0:
                Toast.makeText(getActivity(), "刷新顶端数据", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getActivity(), "刷新底端数据", Toast.LENGTH_SHORT).show();
        }
        //enqueue开启一步线程访问网络
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "没有新数据", Toast.LENGTH_SHORT).show();
                            }
                        });
                        System.out.println("no data error");
                    } else {
                        if (data.getResults().size() == 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "没有新数据", Toast.LENGTH_SHORT).show();
                                }
                            });
                            System.out.println("no data error");
                        } else {
                            meizhiData = data;
                            for (int i = 0; i < data.getResults().size(); i++) {
                                Gank gank = data.getResults().get(i);
                                System.out.println(gank.toString());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 刷新时模拟数据的变化
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                int tempMeizhiCount = meizhiData.getResults().size();
                                                System.out.println("refreshing : " + tempMeizhiCount);
                                                if (tempMeizhiCount == 0) {
                                                    Toast.makeText(getActivity(), "没有数据刷新", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    int newAdd = 0;   //新加入数据数目
                                                    List<Gank> list = new ArrayList<Gank>();
                                                    for (int i = 0; i < tempMeizhiCount; i++) {

                                                        QueryBuilder qb = new QueryBuilder(Gank.class)
                                                                .columns(new String[]{"_id"})
                                                                .where("objectId = ?", new String[]{meizhiData.getResults().get(i).getObjectId()});
                                                        long count = App.sDb.queryCount(qb);
                                                        if (count > 0) {
                                                            System.out.println("已经存在第" + i + "个");
                                                        } else {
                                                            newAdd++;
                                                            switch (DATA) {
                                                                case 0:
                                                                    mStaggeredAdapter.ganks.add(0, meizhiData.getResults().get(i));
                                                                    list.add(0, meizhiData.getResults().get(i));
                                                                    break;
                                                                case 1:
                                                                    mStaggeredAdapter.ganks.add(meizhiData.getResults().get(i));
                                                                    list.add(meizhiData.getResults().get(i));
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                    if (newAdd > 0) {
                                                        saveGanks(list);
                                                        mStaggeredAdapter.notifyDataSetChanged();
                                                        list.clear();
                                                    } else {
                                                        Toast.makeText(getActivity(), "没有新数据加入", Toast.LENGTH_SHORT).show();
                                                    }
                                                    setRefreshing(false);
                                                }
                                            } catch (Exception e) {
                                                System.out.println("没有新数据");
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 1000);
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
