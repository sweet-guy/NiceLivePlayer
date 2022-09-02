package cn.zhiup.niceliveplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class MainActivity extends AppCompatActivity {
    StandardGSYVideoPlayer videoPlayer;
    OrientationUtils orientationUtils;
    String source1 = "https://res.exexm.com/cw_145225549855002";
    String LiveUrl="**************";//服务器地址

    private GSYVideoOptionBuilder gsyVideoOptionBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoPlayer =  (StandardGSYVideoPlayer)findViewById(R.id.LivePlayer);
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, videoPlayer);
        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder()
                .setIsTouchWiget(true)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setSeekRatio(1)
                .setUrl(LiveUrl)
                .setCacheWithPlay(true)
                .setVideoTitle("测试直播")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(videoPlayer.isRotateWithSystem());
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                });
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                videoPlayer.startWindowFullscreen(MainActivity.this, true, true);
            }
        });
        gsyVideoOptionBuilder.build(videoPlayer);
        videoPlayer.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoPlayer.startPlayLogic();
            }
        }, 1000);
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
///       不需要回归竖屏
//        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            videoPlayer.getFullscreenButton().performClick();
//            return;
//        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}