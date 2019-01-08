package com.example.dcang.myapplication.banner;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.banner.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TestAdapter extends BannerAdapter {
    private Context context;
    private List<String> titleList = new ArrayList<>();
    public TestAdapter(Context context){
        this.context = context;


    }

    public void setTitleList(List<String> titleList){
        this.titleList.clear();
        this.titleList.addAll(titleList);
        notifyDataSetChanged();
    }

    @Override
    protected List<String> getTitles() {
        return titleList;
    }

    @Override
    public View getView(int position,View convertView) {
        TextView img = null;
        if (convertView == null){
            img  = new TextView(context);
        }else {
            img = (TextView) convertView;
        }
        img.setText("数据 " + position);
        return img;
    }

    @Override
    public int getCount() {
        return titleList.size();
    }
}
