package com.bytedance.androidcamp.network.dou.fragment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bytedance.androidcamp.network.dou.R;
import com.bytedance.androidcamp.network.dou.VideoActivity;
import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.model.GetVideosResponse;
import com.bytedance.androidcamp.network.dou.model.PostVideoResponse;
import com.bytedance.androidcamp.network.dou.model.Video;
import com.bytedance.androidcamp.network.dou.util.ResourceUtils;
import com.bytedance.androidcamp.network.lib.util.ImageHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentR extends Fragment {
    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    public int mwidth ;
    public int mheight ;

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(IMiniDouyinService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indexr,container,false);
        mwidth = this.getResources().getDisplayMetrics().widthPixels;
        mheight = this.getResources().getDisplayMetrics().heightPixels;
        mRv = view.findViewById(R.id.rv);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        initRecyclerView();
        initRefresh();
        refreshList();
        initVideo();
        return view;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }

        public void bind(final Activity activity, final Video video) {

            ImageHelper.displayWebImage(video.getImageUrl(), img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoActivity.launch(activity, video.getVideoUrl());
                }
            });
        }
    }


    private void initRecyclerView() {
        mRv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mRv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new MyViewHolder(
                        LayoutInflater.from(getActivity())
                                .inflate(R.layout.video_item_view, viewGroup, false));
            }
            @Override
            public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
                final Video video = mVideos.get(i);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) viewHolder.img.getLayoutParams();
                float itemWidth = ((mwidth-8*3)/2);
                layoutParams.width = (int) itemWidth;
                float scale = (itemWidth+0f)/ video.getImageWidth();
                layoutParams.height = (int)(video.getImageHeight()*scale);
                viewHolder.img.setLayoutParams(layoutParams);
                viewHolder.bind(getActivity(), video);

            }

            @Override
            public int getItemCount() {
                return mVideos.size();
            }
        });
    }

    public void refreshList() {
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                if(response.body()!=null&&response.isSuccessful()){
                    mVideos = response.body().getVideos();
                    mRv.getAdapter().notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Get Videos Failure", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }


        });
    }
    public void initVideo() {
        Log.i("TAG","refresh");
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response){
                if (response.body() != null && response.body().videos != null) {
                    Log.i("TAG","get");
                    mVideos.clear();
                    mVideos=response.body().videos;
                    mRv.getAdapter().notifyDataSetChanged();
                    Log.i("TAG","get");
                }
            }
            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("TAG","fail");
            }
        });
    }


}
