package com.example.hanzh.gankio_han;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hanzh.gankio_han.model.Gank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private List<String> titles = new ArrayList<String>(Arrays.asList("清空缓存"));
    private List<String> descs = new ArrayList<String>(Arrays.asList("清空图片,信息等缓存"));
    private ListView setting_list;
    public static final int CLEAR=123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);

        initView();
        initViewAction();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.setting_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        setting_list = (ListView) findViewById(R.id.setting_list);
    }

    private void initViewAction() {
        setting_list.setAdapter(new SettingAdapter());
        setting_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        try {
                            Glide.get(SettingActivity.this).clearMemory();
                            App.sDb.deleteAll(Gank.class);
                            Intent intent = new Intent();
                            intent.putExtra("clear", true);
                            setResult(CLEAR, intent);
                            finish();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(SettingActivity.this, "清空缓存失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    public class SettingAdapter extends BaseAdapter {

        private final class ViewHolder {
            private TextView setting_title;
            private TextView setting_desc;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Object getItem(int position) {
            return titles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder = null;
            if (convertView == null) {
                view = SettingActivity.this.getLayoutInflater().inflate(R.layout.item_setting, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.setting_title = (TextView) view.findViewById(R.id.setting_title);
                viewHolder.setting_desc = (TextView) view.findViewById(R.id.setting_desc);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.setting_title.setText(titles.get(position));
            viewHolder.setting_desc.setText(descs.get(position));

            return view;
        }

    }
}
