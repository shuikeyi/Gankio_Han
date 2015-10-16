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
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class WebActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar webToolbar;
    private FloatingActionButton fab,web_fab_share;
    private WebView mWebView;
    private NumberProgressBar mProgressbar;
    private TextView web_toolbar_title;
    private String mUrl;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mUrl = getIntent().getStringExtra("Url");
        mTitle = getIntent().getStringExtra("Desc");

        initView();
        initViewAction();
    }

    private void initView(){
        webToolbar = (Toolbar) findViewById(R.id.web_toolbar);
        fab = (FloatingActionButton) findViewById(R.id.web_fab);
        web_fab_share=(FloatingActionButton)findViewById(R.id.web_fab_share);
        mWebView=(WebView)findViewById(R.id.webView);
        mProgressbar=(NumberProgressBar)findViewById(R.id.number_progress_bar);
        web_toolbar_title=(TextView)findViewById(R.id.web_toolbar_title);
    }

    private void initViewAction(){
        web_toolbar_title.setText(mTitle);
        setSupportActionBar(webToolbar);
        fab.setOnClickListener(this);
        web_fab_share.setOnClickListener(this);

//        WebSettings settings = mWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setAppCacheEnabled(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        settings.setSupportZoom(true);
//        mWebView.setWebChromeClient(new ChromeClient());
//        mWebView.setWebViewClient(new LoveClient());
//
//        mWebView.loadUrl(mUrl);

//        WebSettings settings = mWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setAppCacheEnabled(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        settings.setSupportZoom(true);
//        settings.setPluginState(WebSettings.PluginState.ON);
//        settings.setUseWideViewPort(true);
//        mWebView.setWebChromeClient(new ChromeClient());
//        mWebView.setWebViewClient(new LoveClient());
//        mWebView.loadUrl(mUrl);

        WebSettings settings = mWebView.getSettings();
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
        mWebView.setWebChromeClient(new ChromeClient());
        mWebView.setWebViewClient(new LoveClient());
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.web_fab:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
                else {
                    WebActivity.this.finish();
                }
                break;
            case R.id.web_fab_share:
                showShare(mUrl,mTitle);
                break;
            default:
                break;
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
        mWebView.reload();
    }

    private void stopLoading() {
        mWebView.stopLoading();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) mWebView.destroy();
    }


    @Override protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
            try {
                mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }


    @Override protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            try {
                mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.web_action_reload:
                refresh();
                return true;
            case R.id.web_action_stop:
                stopLoading();
                return true;
            case R.id.web_action_openurl:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(mUrl);
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                else {
                    Toast.makeText(WebActivity.this, "打开网站失败", Toast.LENGTH_SHORT).show();
                }
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
