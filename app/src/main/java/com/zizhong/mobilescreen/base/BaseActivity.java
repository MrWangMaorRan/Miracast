package com.zizhong.mobilescreen.base;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.luck.picture.lib.tools.ScreenUtils;
import com.zizhong.mobilescreen.activitys.SettingsActivity;
import com.zizhong.mobilescreen.apps.BaseApplication;
import com.zizhong.mobilescreen.bannerss.TTAdManagerHolder;
import com.zizhong.mobilescreen.bannerss.TToast;
import com.zizhong.mobilescreen.utils.SharedPreferencesUtil;

import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;



public abstract class BaseActivity extends AppCompatActivity {

    protected Context context;
    private TTFullScreenVideoAd mttFullVideoAd;
    private boolean mIsLoaded = false; //视频是否加载完成
    private String TAG="BaseActivity";
    private TTAdNative mTTAdNative_chaping;
    private ProgressDialog loadingDialog = null;  //初始化等待动画
    public static boolean ALLOWED_FLAG = false; //已经授权
    private int mFinalCount;
    private boolean mHasShowDownloadActive = false;
    private boolean mIsExpress = true; //是否请求模板广告
    private String mHorizontalCodeId;
    private String mVerticalCodeId;
    public static boolean isActive; //全局变量
    private String currentPosId;
    private boolean time= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");

        if (!ok.equals("123")){

        }else {
            TTAdManagerHolder.init(this);
            TTAdManagerHolder.get().requestPermissionIfNecessary(this);
            //step1:初始化sdk
            TTAdManager ttAdManager = TTAdManagerHolder.get();
            //step3:创建TTAdNative对象,用于调用广告请求接口
            mTTAdNative_chaping = ttAdManager.createAdNative(this);
            //  loadAd("946274412", TTAdConstant.VERTICAL);
            time =true;
        }

    }
    @SuppressWarnings("SameParameterValue")
     private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot;
        if (mIsExpress == true) {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,全屏视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(500, 500)
                    .build();

        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .build();
        }
        //step5:请求广告
        mTTAdNative_chaping.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                // TToast.show(getApplicationContext(), message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                Log.e(TAG, "Callback --> onFullScreenVideoAdLoad");


                mttFullVideoAd = ad;
                Log.e(TAG, mttFullVideoAd + "");
                mIsLoaded = false;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "Callback --> FullVideoAd show");
                        // TToast.show(getApplicationContext(), "FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> FullVideoAd bar click");
                        // TToast.show(getApplicationContext(), "FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> FullVideoAd close");
                        // TToast.show(getApplicationContext(), "FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> FullVideoAd complete");
                        // TToast.show(getApplicationContext(), "FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TAG, "Callback --> FullVideoAd skipped");
                        // TToast.show(getApplicationContext(), "FullVideoAd skipped");

                    }

                });


                ad.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                           // TToast.show(getApplicationContext(), "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                       // TToast.show(getApplicationContext(), "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                       // TToast.show(getApplicationContext(), "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                       // TToast.show(getApplicationContext(), "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                      //  TToast.show(getApplicationContext(), "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.e(TAG, "Callback --> onFullScreenVideoCached");
                mIsLoaded = true;
                // TToast.show(getApplicationContext(), "FullVideoAd video cached");

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mttFullVideoAd != null) {
            mttFullVideoAd = null;
        }
    }

    int screen=0;
    int stopss=0;
    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.topActivity = this;
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
//            screen = SharedPreferencesUtil.getSharedPreferences(this).getInt("screen", screen);
//            screen++;
          //  SharedPreferencesUtil.getSharedPreferences(this).putInt("screen",screen);
            Log.i("ACTIVITY", "程序从后台唤醒");
            if (mttFullVideoAd != null) {
                //穿山甲广告
                //step6:在获取到广告后展示
                //该方法直接展示广告
                //mttFullVideoAd.showFullScreenVideoAd(FullScreenVideoActivity.this);
                //展示广告，并传入广告展示的场景
                mttFullVideoAd.showFullScreenVideoAd(this, TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
                mttFullVideoAd = null;


//            } else {
//                TToast.show(this, "请先加载广告");
            }

        }

    }

    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
            Log.i("ACTIVITY", "程序进入后台");
          //  stopss = SharedPreferencesUtil.getSharedPreferences(this).getInt("stopss", stopss);
            //stopss++;
            String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");
            if (ok.equals("123")&& time==true) {
              //  if (stopss%2==0){
                    //穿山甲
                     loadAd("946454406", TTAdConstant.VERTICAL);

                    SharedPreferencesUtil.getSharedPreferences(this).remove("stopss");
              //  }


            }
        }
        super.onStop();
    }
    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}
