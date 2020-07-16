package com.bytedance.androidcamp.network.dou.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bytedance.androidcamp.network.dou.R;
import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.minterface.args;
import com.bytedance.androidcamp.network.dou.model.GetVideosResponse;
import com.bytedance.androidcamp.network.dou.model.Video;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentL extends Fragment {
    private VideoView videoView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btn_play;
    private ImageButton btnLike,btn_comment;
    private Animation loadHeartAnimation;
    private List<Video> mVideos = new ArrayList<>();
    private GestureDetector mDetector;
    private int currentPos = 0;
    private String url1;

    public static MarqueeTextView marqueeTextView = null;

    private int verticalMinistance = 100;            //水平最小识别距离
    private int minVelocity = 10;            //最小识别速度

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(IMiniDouyinService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private GestureDetector gesture;
    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);

    public FragmentL() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_indexl, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        videoView = view.findViewById(R.id.video_container);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        btn_play = view.findViewById(R.id.btn_play);
        btn_comment = view.findViewById(R.id.btn_comment);
        marqueeTextView = view.findViewById(R.id.marquee_text);
        btnLike =view.findViewById(R.id.btn_like);

        gesture = new GestureDetector(this.getActivity(), new MyOnGestureListener());
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(args.isHeart){
                    args.isHeart=false;
                    btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    loadHeartAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.heart_animation);
                    btnLike.startAnimation(loadHeartAnimation);
                }else{
                    args.isHeart=true;
                    btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
                    loadHeartAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.heart_animation);
                    btnLike.startAnimation(loadHeartAnimation);
                }
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoView.isPlaying()) {
                    videoView.start();
                    marqueeTextView.startScroll();
                    btn_play.setVisibility(View.INVISIBLE);
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.purple, R.color.mediumvioletred, R.color.mediumpurple);
        currentPos = (int) (1 + Math.random() * (30 - 1 + 1));
        initVideo();



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPos = (int) (1 + Math.random() * (30 - 1 + 1));
                initVideo();

            }
        });

        return view;
    }


    private void initVideo() {
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response){
                if (response.body() != null && response.body().videos != null) {
                    mVideos.clear();
                    mVideos=response.body().videos;
                    String url=mVideos.get(currentPos).getVideoUrl();
                    playVideo(url);
                }
            }
            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("TAG","fail");
            }
        });

    }
    private void playVideo(String url2)
    {
        videoView.setMediaController(new MediaController(getActivity()));
        videoView.setVideoURI(Uri.parse(url2));
        videoView.requestFocus();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //隐藏播放按钮
        btn_play.setVisibility(View.INVISIBLE);
        marqueeTextView.startFor0();

        MediaController mc = new MediaController(getActivity());
        mc.setVisibility(View.INVISIBLE);
        videoView.setMediaController(mc);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
    }


    //设置手势识别监听器
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override//此方法必须重写且返回真，否则onFling不起效
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Toast.makeText(getContext(), "fling", Toast.LENGTH_SHORT).show();
            if (e1 != null && e2 != null) {
                if (e1.getY() - e2.getY() > 50) {
//                    Toast.makeText(getContext(), "fling up", Toast.LENGTH_SHORT).show();
                    currentPos = (int) (1 + Math.random() * (30 - 1 + 1));
                    initVideo();
                    return true;
                }
            }

            return false;
        }
    }


}
