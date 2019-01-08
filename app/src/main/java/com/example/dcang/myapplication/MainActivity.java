package com.example.dcang.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.banner.BannerView;
import com.banner.BannerViewPager;
import com.banner.IndicatorView;
import com.example.dcang.myapplication.banner.TestAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    @IFindById(R.id.text_1)
    TextView textView;
    @IFindById(R.id.banner_view)
    BannerView viewPager;
    @IFindById(R.id.circleIndicator)
    LinearLayout circleIndicator;

    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtil.inject(this);
        textView.setText("IOC成功了");

        adapter = new TestAdapter(this);
        viewPager.setAdapter(adapter);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> titleList = new ArrayList<>();
                titleList.add("asjfjsojfsojf");
                titleList.add("wwwwwwwwwww");
                titleList.add("gggggggggggggggggggggggggggggggggggggggggggggggg");
                titleList.add("wwwwsaaaaaaaaaaaagasdgasssfsdfasf");
                titleList.add("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                adapter.setTitleList(titleList);
            }
        });
    }
    @OnClick(R.id.text_1)
    public void testButtonClick(View v) {
//        Intent i = new Intent(this,TestAcitivity.class);
//       startActivity(i);


    }

    private void addIndicatorView(int size){
        for (int i = 0; i < size; i++){
            IndicatorView view = new IndicatorView(this);
            circleIndicator.addView(view);
            LinearLayout.LayoutParams params = new
                    LinearLayout.LayoutParams(30,30);
            params.leftMargin = params.rightMargin = 5;
            view.setLayoutParams(params);
        }
    }
}
