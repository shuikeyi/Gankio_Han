package com.example.hanzh.gankio_han;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.daimajia.numberprogressbar.NumberProgressBar;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private WebView videoWebView;
    private NumberProgressBar mProgressbar;
    private String mUrl;
    private String mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mUrl = getIntent().getStringExtra("Url");
        mDesc = getIntent().getStringExtra("Desc");
        System.out.println("mUrl : "+mUrl+" ; mDesc : "+mDesc);
        initView();
        initViewAction();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.video_toolbar);
        fab = (FloatingActionButton) findViewById(R.id.video_fab);
        videoWebView=(WebView)findViewById(R.id.videoWebView);
        mProgressbar=(NumberProgressBar)findViewById(R.id.video_number_progress_bar);
    }

    private void initViewAction(){
        toolbar.setTitle(mDesc);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(this);
        if(!mUrl.equals("")){
            WebSettings settings = videoWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setDatabaseEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setSaveFormData(false);
            settings.setLoadWithOverviewMode(true);
            settings.setAppCacheEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setSupportZoom(true);
            settings.setUseWideViewPort(true);
            videoWebView.setWebChromeClient(new ChromeClient());
            videoWebView.setWebViewClient(new LoveClient());
            videoWebView.loadUrl(mUrl);
        }
    }

    private class ChromeClient extends WebChromeClient {

        @Override public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressbar.setProgress(newProgress);
            if (newProgress == 100) { mProgressbar.setVisibility(View.GONE); }
            else { mProgressbar.setVisibility(View.VISIBLE); }
        }


        @Override public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

    private class LoveClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null) view.loadUrl(url);
            return true;
        }
    }

    private void refresh() {
        videoWebView.reload();
    }
    private void stopLoading() {
        videoWebView.stopLoading();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_fab:
                VideoActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            videoWebView.getClass().getMethod("onResume").invoke(videoWebView, (Object[]) null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            videoWebView.getClass().getMethod("onPause").invoke(videoWebView, (Object[]) null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (videoWebView != null) videoWebView.destroy();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.video_action_reload:
                refresh();
                return true;
            case R.id.video_action_share:
                showShare(mUrl,mDesc);
                return true;
            case R.id.video_action_stop:
                stopLoading();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showShare(String url,String title) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(title);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("测试GankIO Design By Han");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(this);
    }

}
