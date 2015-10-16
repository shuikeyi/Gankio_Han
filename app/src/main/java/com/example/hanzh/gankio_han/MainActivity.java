package com.example.hanzh.gankio_han;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ops.appunion.sdk.AppUnionSDK;
import com.example.hanzh.gankio_han.Utils.BrightnessUtils;
import com.example.hanzh.gankio_han.Utils.SPUtils;
import com.example.hanzh.gankio_han.Utils.SnackbarUtil;
import com.example.hanzh.gankio_han.adapter.MyViewPagerAdapter;
import com.example.hanzh.gankio_han.fragment.fenleiFragment;
import com.example.hanzh.gankio_han.fragment.OthersFragment;
import com.example.hanzh.gankio_han.model.UserInfo;
import com.example.hanzh.gankio_han.receiver.ConnectionChangeReceiver;
import com.example.hanzh.gankio_han.view.CircleImageView;
import com.example.hanzh.gankio_han.widget.CustomDialog;
import com.example.hanzh.gankio_han.widget.CustomDialogPay;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        View.OnClickListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,
        ConnectionChangeReceiver.GetNet {

    public static final String FILE_NAME = "GankIO_Han_SP";
    public static final int CLEAR=123;
    //private FloatingActionButton fab;
    private CoordinatorLayout container_main;
    private String fenleiText = "", meiriText = "";
    private NavigationView id_navigationview;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    // TabLayout中的tab标题
    private String[] mTitles;
    // 填充到ViewPager中的Fragment
    private List<Fragment> mFragments;
    // ViewPager的数据适配器
    private MyViewPagerAdapter mViewPagerAdapter;
    //RapidFloatingActionButton
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private View head_layout;
    private static Boolean isExit = false;    //判断是否第一次点击退出

    private int modeNum = 0;
    private CircleImageView headicon;
    private TextView authorname;
    private ConnectionChangeReceiver netReceiver;
    private SharedPreferences sp;

    private String username,usericon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUnionSDK.getInstance(this).initSdk();    //广告模块初始化
        setContentView(R.layout.activity_main);
        initView();
        initData();
        configViews();
    }

    private void initView() {
        container_main=(CoordinatorLayout)findViewById(R.id.container_main);
        head_layout = (LinearLayout) findViewById(R.id.head_layout);
        headicon=(CircleImageView)findViewById(R.id.id_header_face);
        authorname=(TextView)findViewById(R.id.id_header_authorname);
//        authornote=(TextView)findViewById(R.id.id_header_note);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        //fab = (FloatingActionButton) findViewById(R.id.fenleiFAB);
        id_navigationview = (NavigationView) findViewById(R.id.id_navigationview);
        mTabLayout = (TabLayout) findViewById(R.id.id_tablayout);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        rfaButton = (RapidFloatingActionButton) findViewById(R.id.activity_main_rfab);
        rfaLayout = (RapidFloatingActionLayout) findViewById(R.id.activity_main_rfal);
        netReceiver = new ConnectionChangeReceiver(MainActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netReceiver, filter);
        netReceiver.setNetListener(this);
        sp=getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(myListener);
    }

    private void initData() {
        mTitles = new String[]{"妹纸", "IOS", "安卓", "瞎推荐", "拓展资源"};
        //初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();

        fenleiFragment fenleifragment = new fenleiFragment();
        mFragments.add(0, fenleifragment);

        for (int i = 1; i < 5; i++) {
            Bundle mBundle = new Bundle();
            mBundle.putInt("flag", i);
            OthersFragment othersfragment = new OthersFragment();
            othersfragment.setArguments(mBundle);
            mFragments.add(i, othersfragment);
        }

        usericon=(String)SPUtils.get(MainActivity.this, "usericon", "");
        username=(String)SPUtils.get(MainActivity.this, "username", "");
    }

    private void configViews() {

        modeNum = (int) SPUtils.get(MainActivity.this, "mode", 0);
        mToolbar.setTitle("GankIO " + mTitles[0]);
        if (modeNum == 0) { //当前模式为日间模式
            mToolbar.setBackgroundColor(Color.parseColor("#3F56B5"));
            mTabLayout.setBackgroundColor(Color.parseColor("#3F56B5"));
            head_layout.setBackgroundColor(Color.parseColor("#3F56B5"));
            id_navigationview.setBackgroundColor(Color.parseColor("#FAFAD2"));
            BrightnessUtils.setSystemBrightness(MainActivity.this, 200);
        } else {    //夜间模式
            mToolbar.setBackgroundColor(Color.parseColor("#2A2A2A"));
            mTabLayout.setBackgroundColor(Color.parseColor("#2A2A2A"));
            head_layout.setBackgroundColor(Color.parseColor("#2A2A2A"));
            id_navigationview.setBackgroundColor(Color.parseColor("#383838"));
            BrightnessUtils.setSystemBrightness(MainActivity.this,120);
        }
        // 设置显示Toolbar
        setSupportActionBar(mToolbar);

        // 设置Drawerlayout开关指示器，即Toolbar最左边的那个icon
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        //设置FloatingActionButton
        //fab.setOnClickListener(this);

        // 自己写的方法，设置NavigationView中menu的item被选中后要执行的操作
        onNavgationViewMenuItemSelected(id_navigationview);

        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        // 设置ViewPager最大缓存的页面个数
        mViewPager.setOffscreenPageLimit(5);
        // 给ViewPager添加页面动态监听器（为了让Toolbar中的Title可以变化相应的Tab的标题）
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        // 将TabLayout和ViewPager进行关联，让两者联动起来
        mTabLayout.setupWithViewPager(mViewPager);
        // 设置Tablayout的Tab显示ViewPager的适配器中的getPageTitle函数获取到的标题
        mTabLayout.setTabsFromPagerAdapter(mViewPagerAdapter);

        setRfa();
        headicon.setOnClickListener(this);
        if(!username.equals("")){
            authorname.setText(username);
        }else{
            authorname.setText("请点击头像登录");
        }
        if(!usericon.equals("")){
            try {
                headicon.setImageBitmap(compressImageFromFile(usericon));
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "读取图像文件出错", Toast.LENGTH_SHORT).show();
                headicon.setImageDrawable(getResources().getDrawable(R.drawable.gitman));
            }
        }
        else {
            headicon.setImageDrawable(getResources().getDrawable(R.drawable.gitman));
        }
    }

    // 图片压缩
    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }

    //监听SharedPreferences改变
    private SharedPreferences.OnSharedPreferenceChangeListener myListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("username")){
                username=sharedPreferences.getString(key,"");
                if(!username.equals("")) {
                    authorname.setText(sharedPreferences.getString(key, ""));
                }else {
                    authorname.setText("请点击头像登录");
                }
            }
            if(key.equals("usericon")){
                usericon=sharedPreferences.getString(key,"");
                if(!usericon.equals("")){
                    try {
                        headicon.setImageBitmap(compressImageFromFile(usericon));
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "读取图像文件出错", Toast.LENGTH_SHORT).show();
                        headicon.setImageDrawable(getResources().getDrawable(R.drawable.gitman));
                    }
                }else {
                    headicon.setImageDrawable(getResources().getDrawable(R.drawable.gitman));
                }
            }
        }
    };

    @Override
    public boolean isNetConnect(boolean isConnect) {
        if(isConnect==true) {
            Toast.makeText(MainActivity.this, "网络已连接", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "网络断开,请检查网络", Toast.LENGTH_SHORT).show();
        }
        return isConnect;
    }

    private void setRfa() {

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(MainActivity.this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("每日资讯")
                        .setResId(R.mipmap.ic_github)
                        .setIconNormalColor(0xffd84315)
                        .setIconPressedColor(0xffbf360c)
                        .setLabelColor(Color.WHITE)
                        .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaad84315, ABTextUtil.dip2px(MainActivity.this, 4)))
                        .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("模式选择")
                        .setResId(R.mipmap.mode)
//                        .setDrawable(getResources().getDrawable(R.mipmap.ico_test_c))
                        .setIconNormalColor(0xff4e342e)
                        .setIconPressedColor(0xff3e2723)
                        .setLabelColor(Color.WHITE)
                        .setLabelSizeSp(14)
                        .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(MainActivity.this, 4)))
                        .setWrapper(1)
        );
        rfaContent.setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(MainActivity.this, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(MainActivity.this, 5));

        rfabHelper = new RapidFloatingActionHelper(MainActivity.this, rfaLayout, rfaButton, rfaContent).build();
    }

    /**
     * 设置NavigationView中menu的item被选中后要执行的操作
     * 处理侧边栏的menu
     *
     * @param mNav
     */
    private void onNavgationViewMenuItemSelected(NavigationView mNav) {
        mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                String msgString = "";

                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_home:
                        SnackbarUtil.show(container_main,"你正在看的不就是首页吗 ╮(╯▽╰)╭",1);
                        break;
                    case R.id.nav_menu_feedback:
                        msgString = (String) menuItem.getTitle();
                        startSendEmailIntent();
                        break;
                    case R.id.nav_menu_ad:
                        AppUnionSDK.getInstance(MainActivity.this).showAppList();
                        break;
                    case R.id.nav_menu_about:
                        showAlertDialog();
                        break;
                    case R.id.nav_menu_setting:
                        msgString = (String) menuItem.getTitle();
                        //SnackbarUtil.show(container_main,"你觉得还有什么好设置的 ╮(╯▽╰)╭可以反馈给我哦",1);
                        Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                }

                // Menu item点击后选中，并关闭Drawerlayout
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case CLEAR:
                Bundle b=data.getExtras(); //data为B中回传的Intent
                boolean clear=b.getBoolean("clear");//str即为回传的值
                if(clear){
                    Toast.makeText(MainActivity.this, "清空缓存成功", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void showAlertDialog() {

        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(getString(R.string.i_want_to_talk));
        builder.setTitle("关于");
        builder.setPositiveButton(getString(R.string.pay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                CustomDialogPay.Builder builderPay = new CustomDialogPay.Builder(MainActivity.this);
                builderPay.setTitle("支付宝扫码打赏");
                builderPay.setPositiveButton(getString(R.string.ok_dialog), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //设置你的操作事项
                    }
                });
                builderPay.create().show();
            }
        });

        builder.setNegativeButton("虽然不打赏，但是我觉得你做的不错",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void startSendEmailIntent(){
        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:1217343302@qq.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "GankIO_Han反馈");
        data.putExtra(Intent.EXTRA_TEXT, "反馈：");
        startActivity(data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;   //取消退出
                }
            }, 2000);    //等待2秒钟
        } else {
            finish();
        }
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
        switch (position) {
            case 0:
                Intent intent = new Intent(MainActivity.this, TodayActivity.class);
                startActivity(intent);
                break;
            case 1:
                ModeChange();
                break;
            default:
                break;
        }
    }

    private void ModeChange() {
        if (modeNum == 0) { //当前是日间模式
            modeNum = 1;
            mToolbar.setBackgroundColor(Color.parseColor("#2A2A2A"));
            mTabLayout.setBackgroundColor(Color.parseColor("#2A2A2A"));
            head_layout.setBackgroundColor(Color.parseColor("#2A2A2A"));
            id_navigationview.setBackgroundColor(Color.parseColor("#383838"));
            BrightnessUtils.setSystemBrightness(MainActivity.this, 120);
        } else {
            modeNum = 0;
            mToolbar.setBackgroundColor(Color.parseColor("#3F56B5"));
            mTabLayout.setBackgroundColor(Color.parseColor("#3F56B5"));
            head_layout.setBackgroundColor(Color.parseColor("#3F56B5"));
            id_navigationview.setBackgroundColor(Color.parseColor("#FAFAD2"));
            BrightnessUtils.setSystemBrightness(MainActivity.this, 200);
        }
        SPUtils.put(MainActivity.this, "mode", modeNum);
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
        switch (position) {
            case 0:
                Intent intent = new Intent(MainActivity.this, TodayActivity.class);
                startActivity(intent);
                break;
            case 1:
                ModeChange();
                break;
            default:
                break;
        }
    }

    public void showToastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_topic) {
            String url =
                    getString(R.string.url_gank_io) + String.format("%s/%s/%s", Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            intent.putExtra("Url", url);
            intent.putExtra("Desc", "今日话题");
            startActivity(intent);
            return true;
        }

        if(id==R.id.action_git){
            openGitHubTrending();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openGitHubTrending() {
        String url = getString(R.string.url_github_trending);
        String title = getString(R.string.action_github_trending);
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("Url", url);
        intent.putExtra("Desc", title);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_header_face:
                showRegister();
                break;
            default:
                break;
        }
    }

    private void showRegister() {
        ThirdPartyLogin tpl = new ThirdPartyLogin();
        tpl.setOnLoginListener(new OnLoginListener() {
            public boolean onSignin(String platform, HashMap<String, Object> res) {
                // 在这个方法填写尝试的代码，返回true表示还不能登录，需要注册
                // 此处全部给回需要注册
                return true;
            }

            public boolean onSignUp(UserInfo info) {
                // 填写处理注册信息的代码，返回true表示数据合法，注册页面可以关闭
                return true;
            }
        });
        tpl.show(this);
    }

    @Override
    protected void onDestroy() {
        AppUnionSDK.getInstance(this).quitSdk();
        unregisterReceiver(netReceiver);
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mToolbar.setTitle("GankIO " + mTitles[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
