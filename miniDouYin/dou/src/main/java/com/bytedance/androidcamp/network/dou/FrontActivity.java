package com.bytedance.androidcamp.network.dou;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bytedance.androidcamp.network.dou.fragment.FocusFragment;
import com.bytedance.androidcamp.network.dou.fragment.FragmentL;
import com.bytedance.androidcamp.network.dou.fragment.FragmentR;
import com.bytedance.androidcamp.network.dou.fragment.MineFragment;
import com.bytedance.androidcamp.network.dou.fragment.MsgFragment;
import com.bytedance.androidcamp.network.dou.minterface.args;

public class FrontActivity extends AppCompatActivity {
    private final static int REQUEST_CODE = 100;
    private String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private TextView recommend;
    private TextView list;
    private TextView[] tabItem = new TextView[4];
    private ImageView postVideo;
    private LinearLayout tabBar;
    private LinearLayout tabTop;

    private FocusFragment focusFragment = null;
    private FragmentL indexFragmentL = null;
    private FragmentR indexFragmentR = null;
    private MineFragment meFragment = null;
    private MsgFragment msgFragment = null;
    final int focusedColor = Color.parseColor("#ffffff");

    final int textLightColor = Color.parseColor("#66FFFFFF");
    final int textDarkColor = Color.parseColor("#99999a");
    private int currentFocus = 0;
    private int currentTabTop = 0;

    @Override
    protected void onStop() {
        super.onStop();
        if(indexFragmentL.getActivity()!=null){
            VideoView videoView = indexFragmentL.getActivity().findViewById(R.id.video_container);
            videoView.pause();
            Button button = indexFragmentL.getActivity().findViewById(R.id.btn_play);
            button.setVisibility(View.VISIBLE);
        }
    }

    class BtnOnclickListener implements View.OnClickListener{
        private int tabBtnId;
        public BtnOnclickListener(int id){
            tabBtnId = id;
        }
        @Override
        public void onClick(View v) {

            tabItem[currentFocus].setTextColor(textDarkColor);
            tabItem[tabBtnId].setTextColor(focusedColor);

            if(tabBtnId == currentFocus){
            }
            else{
                currentFocus = tabBtnId;
                switch (currentFocus){
                    case 0:
                        tabTop.setVisibility(View.VISIBLE);
                        switch(currentTabTop){
                            case 0:
                                if(indexFragmentL==null) indexFragmentL = new FragmentL();
                                replaceFragment(indexFragmentL);
                                currentTabTop = 0;
                                break;
                            case 1:
                                if(indexFragmentR==null) indexFragmentR = new FragmentR();
                                replaceFragment(indexFragmentR);
                                currentTabTop = 1;
                                break;
                        }
                        break;
                    case 1:
                        tabTop.setVisibility(View.INVISIBLE);
                        if(focusFragment==null) focusFragment = new FocusFragment();
                        replaceFragment(focusFragment);
                        break;
                    case 2:
                        tabTop.setVisibility(View.INVISIBLE);
                        if(msgFragment==null) msgFragment = new MsgFragment();
                        replaceFragment(msgFragment);
                        break;
                    case 3:
                        tabTop.setVisibility(View.INVISIBLE);
                        if(meFragment==null) meFragment = new MineFragment();
                        replaceFragment(meFragment);
                        break;
                }
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frag_layout,fragment);
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);
        initTabs();
        tabItem[currentFocus].setTextColor(focusedColor);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initTabs() {
        tabBar = findViewById(R.id.tabBar);
        tabTop = findViewById(R.id.tabTop);
        recommend = findViewById(R.id.recommend);
        list = findViewById(R.id.list);
        tabItem[0] = findViewById(R.id.tabIndex);
        tabItem[1] = findViewById(R.id.tabFocus);
        tabItem[2] = findViewById(R.id.tabMsg);
        tabItem[3] = findViewById(R.id.tabMe);
        postVideo = findViewById(R.id.postVideo);
        tabTop.bringToFront();
        indexFragmentL = new FragmentL();
        replaceFragment(indexFragmentL);

        for(int i = 0; i < 4; i++){
            tabItem[i].setOnClickListener(new BtnOnclickListener(i));
        }

        postVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (!com.bytedance.androidcamp.network.dou.utils.Utils.isPermissionsReady(FrontActivity.this, permissions)) {
                        com.bytedance.androidcamp.network.dou.utils.Utils.reuqestPermissions(FrontActivity.this, permissions, REQUEST_CODE);
                    }else{
                        if(!args.hasDraft){
                            Intent intent = new Intent(FrontActivity.this,CustomCameraActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(FrontActivity.this).toBundle());

                        }
                        else{
                            Intent intent = new Intent(FrontActivity.this,PostActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(FrontActivity.this).toBundle());
                        }
                    }
                }

        });

        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTabTop == 1){
                    if(indexFragmentL==null) indexFragmentL = new FragmentL();
                    replaceFragment(indexFragmentL);
                    list.setTextColor(textLightColor);
                    list.setTypeface(Typeface.DEFAULT);
                    recommend.setTextColor(focusedColor);
                    recommend.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                    currentTabTop = 0;
                }
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTabTop == 0){
                    if(indexFragmentR==null) indexFragmentR = new FragmentR();
                    replaceFragment(indexFragmentR);
                    recommend.setTextColor(textLightColor);
                    recommend.setTypeface(Typeface.DEFAULT);
                    list.setTextColor(focusedColor);
                    list.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                    currentTabTop = 1;
                }
            }
        });
    }
}


