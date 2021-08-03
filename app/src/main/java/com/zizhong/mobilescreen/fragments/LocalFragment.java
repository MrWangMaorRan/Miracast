package com.zizhong.mobilescreen.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkSourceSDK;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zizhong.mobilescreen.bannerss.DislikeDialog;
import com.zizhong.mobilescreen.bannerss.TTAdManagerHolder;
import com.zizhong.mobilescreen.bannerss.TToast;
import com.zizhong.mobilescreen.utils.Loading;
import com.zizhong.mobilescreen.broadcastss.MyBroadcastReceiver;
import com.zizhong.mobilescreen.R;
import com.zizhong.mobilescreen.activitys.MainActivity;
import com.zizhong.mobilescreen.activitys.SettingsActivity;
import com.zizhong.mobilescreen.utils.GlideEngine;
import com.zizhong.mobilescreen.utils.PermissionsUtils;
import com.zizhong.mobilescreen.utils.SharedPreferencesUtil;
import com.zizhong.mobilescreen.utils.ToastUtils;
import com.zizhong.mobilescreen.utils.log.LogUtils;

import java.util.List;

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;


public class LocalFragment extends Fragment {
    public View inflate;
    private ImageView image;
    private ImageView video;
    private String[] permissions;
    private int License = 0;
    private String TGA = "LocalFragment";
    private List<LelinkServiceInfo> lists;
    private List<LocalMedia> localMedia;
    public TextView local_name;
    private boolean play = false;
    private ImageView settingss;
    private Button stoppp;
    private Button renew;
    private Button over;
    private Button volumeJia;
    private Button volumeJian;
    public MainActivity activity;
    public FrameLayout local_banner;
    private TTNativeExpressAd mTTAd;
    private MyBroadcastReceiver mReceiver = new MyBroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            Log.i("传值成功","已接收");
            if("com.amos.demo".equals(action)) {
                String deviceValue = (String) intent.getSerializableExtra("deviceValue");
                if(deviceValue!=null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //接收后的数据在线程更新UI
                            local_name.setText(deviceValue);
                            Log.i("传值成功",deviceValue);
                        }
                    });
                }
            }else if ("duankai".equals(action)){
                String duankai = (String) intent.getSerializableExtra("duankai");
                if(duankai!=null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //接收后的数据在线程更新UI
                            local_name.setText(duankai);
                            Log.i("传值成功",duankai);
                            ll_btt.setVisibility(View.GONE);
                            ll_volime.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    };

    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           if (msg.what==3){
               ll_btt.setVisibility(View.VISIBLE);
               ll_volime.setVisibility(View.VISIBLE);
           }

        }
    };
    private LinearLayout ll_btt;
    private LinearLayout ll_volime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.local_layout, container, false);
        return inflate;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       activity= (MainActivity) getActivity();
       PermissionsUtils.getInstance();
        //权限
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        initView();
        String ok = SharedPreferencesUtil.getSharedPreferences(getActivity()).getString("OK", "");

        if (!ok.equals("123")){

        }else {
            banner_local();
            initLebo();
        }

        initClick();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.amos.demo");
        intentFilter.addAction("duankai");
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(getActivity());
        instance.registerReceiver(mReceiver,intentFilter);

    }

    private void banner_local() {
        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(getContext());

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946444093") //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(600,90) //期望模板广告view的size,单位dp
                .build();

        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //请求失败回调
            @Override
            public void onError(int code, String message) {
                Log.e(TGA, "请求失败"+code+","+message);
            }

            //请求成功回调
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                Log.e(TGA, "请求成功");
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                mTTAd.render();
            }
        });

    }
    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
              //  TToast.show(getContext(), "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
              //  TToast.show(getContext(), "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:");
                TToast.show(getContext(), msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:");
                //返回view的宽高 单位 dp
             //   TToast.show(getContext(), "渲染成功");
                local_banner.removeAllViews();
                local_banner.addView(view);
            }
        });
        //dislike设置
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                //TToast.show(getContext(), "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

                //TToast.show(getContext(), "下载中，点击暂停", Toast.LENGTH_LONG);

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
               // TToast.show(getContext(), "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
               // TToast.show(getContext(), "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
               // TToast.show(getContext(), "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
              //  TToast.show(getContext(), "点击安装", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 设置广告的不喜欢, 注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
     *
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //使用自定义样式
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(activity, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
                    TToast.show(getContext(), "点击 " + filterWord.getName());
                    //用户选择不喜欢原因后，移除广告展示
                    local_banner.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    TToast.show(getContext(), "点击了为什么看到此广告");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int i, String s, boolean b) {
                TToast.show(getContext(), "点击 " + s);
                local_banner.removeAllViews();
                //用户选择不喜欢原因后，移除广告展示
            }

            @Override
            public void onCancel() {
                TToast.show(getContext(), "点击取消 ");
            }


        });
    }

    public void Setname(String name) {
        local_name.setText(name);
    }

    private void initLebo() {
        ILelinkPlayerListener playerListener = new ILelinkPlayerListener() {

            /**
             *  开始加载
             */
            @Override
            public void onLoading() {
                LogUtils.e(TGA, "开始加载");
            }

            /**
             * 播放开始
             */
            @Override
            public void onStart() {
                LogUtils.e(TGA, "播放开始");
                play = true;
                new  Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = handler1.obtainMessage();
                        msg.what=3;
                        handler1.sendMessage(msg);
                        // handler.sendEmptyMessage(1);

                    }
                }).start();

            }

            /**
             * 暂停
             */
            @Override
            public void onPause() {
                LogUtils.e(TGA, "暂停");
            }

            /**
             * 播放完成
             */
            @Override
            public void onCompletion() {
                LogUtils.e(TGA, "播放完成");
            }

            /**
             * 播放结束
             */
            @Override
            public void onStop() {
                LogUtils.e(TGA, "播放结束");
            }

            /**
             * 进度调节：单位为百分比（该接口暂无回调）
             */
            @Override
            public void onSeekComplete(int pPosition) {
                LogUtils.e(TGA, "进度调节：单位为百分比（该接口暂无回调）");
            }


            /**
             * 保留接口
             */
            @Override
            public void onInfo(int i, int i1) {

            }

            @Override
            public void onInfo(int i, String s) {

            }

            /**
             * 错误回调
             */
            @Override
            public void onError(int what, int extra) {
                LogUtils.e(TGA, "错误：" + extra + "");
            }

            /**
             * 音量变化回调（该接口暂无回调）
             */
            @Override
            public void onVolumeChanged(float percent) {
                LogUtils.e(TGA, "音量变化回调" + percent);
            }

            /**
             * 播放进度信息回调
             * @param duration 总长度：单位秒
             * @param position 当前进度：单位秒
             */
            @Override
            public void onPositionUpdate(long duration, long position) {
                LogUtils.e(TGA, "播放进度信息回调：总长度" + duration + " " + "当前进度" + position);
            }
        };

        LelinkSourceSDK.getInstance().setPlayListener(playerListener);
    }

    private void initClick() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lists = LelinkSourceSDK.getInstance().getConnectInfos();
                License = 1;
                if (lists != null) {
                    //这里的this不是上下文，是Activity对象！
                    PermissionsUtils.getInstance().chekPermissions(getActivity(), permissions, permissionsResult);
                } else {
                    ToastUtils.showToast(getActivity(), "请先连接设备");
                    return;
                }
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lists = LelinkSourceSDK.getInstance().getConnectInfos();
                License = 2;
                if (lists != null) {

                    //这里的this不是上下文，是Activity对象！
                    PermissionsUtils.getInstance().chekPermissions(getActivity(), permissions, permissionsResult);
                } else {
                    ToastUtils.showToast(getActivity(), "请先连接设备");
                    return;
                }
            }
        });
        settingss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        stoppp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LelinkSourceSDK.getInstance().pause();
            }
        });
        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    LelinkSourceSDK.getInstance().resume();
            }
        });
        over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LelinkSourceSDK.getInstance().stopPlay();
            }
        });
        volumeJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LelinkSourceSDK.getInstance().addVolume();
            }
        });
        volumeJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LelinkSourceSDK.getInstance().subVolume();
            }
        });
        local_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LelinkSourceSDK.getInstance().startBrowse();
                if (activity.liveFragment1.LelinkServiceInfo!=null){

                    new Loading(getActivity()).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          activity.liveFragment1.mShareDialog.show();
                        }
                    },3000);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            LogUtils.e("回调结果", "不为空");
            //用照片选择器后的返回
            // 图片选择结果回调
            localMedia = PictureSelector.obtainMultipleResult(data);
            LogUtils.e("回调结果", localMedia.size() + "");
            LogUtils.e("回调结果", localMedia.get(0).getPath());

            /*结果回调*/
            if (requestCode == 1) {
                if (lists.size() > 0 && localMedia.size() > 0) {
//                    LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
//                LelinkSourceSDK.getInstance(). startPlayMedia(lelinkPlayerInfo);
                    LelinkSourceSDK.getInstance().startPlayMediaImmed(lists.get(0), localMedia.get(0).getPath(), LelinkSourceSDK.MEDIA_TYPE_IMAGE, true);
                } else {
                    ToastUtils.showToast(getActivity(), "投屏失败，请检查设备连接");
                }


            } else if (requestCode == 2) {
                if (lists.size() > 0 && localMedia.size() > 0) {
//                    LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
//                    LelinkSourceSDK.getInstance(). startPlayMedia(lelinkPlayerInfo);
                    LelinkSourceSDK.getInstance().startPlayMediaImmed(lists.get(0), localMedia.get(0).getPath(), LelinkSourceSDK.MEDIA_TYPE_VIDEO, true);
                } else {
                    ToastUtils.showToast(getActivity(), "投屏失败，请检查设备连接");
                }
            } else {
                ToastUtils.showToast(getActivity(), "请选择正确的打开方式");
            }
        }
    }

    private void initView() {
        image = inflate.findViewById(R.id.image);
        video = inflate.findViewById(R.id.video);
        local_name = inflate.findViewById(R.id.local_name);
        settingss = inflate.findViewById(R.id.settingss);
        stoppp = (Button)inflate. findViewById(R.id.stoppp);
        renew = (Button) inflate. findViewById(R.id.renew);
        over = (Button) inflate. findViewById(R.id.over);
        volumeJia = (Button) inflate. findViewById(R.id.volume_jia);
        volumeJian = (Button)inflate.  findViewById(R.id.volume_jian);
        ll_btt = inflate.findViewById(R.id.ll_btt);
        ll_volime = inflate.findViewById(R.id.ll_volime);
        local_banner = inflate.findViewById(R.id.local_banner);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }

    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
           // Toast.makeText(getContext(), "权限通过，可以正常使用!", Toast.LENGTH_SHORT).show();
            if (License == 1) {
                // 进入相册 以下是例子：用不到的api可以不写
                PictureSelector.create(getActivity())
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        //.theme()//主题样式(不设置为默认样式) 也可参考demo anim_round_rotate/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .imageEngine(GlideEngine.createGlideEngine())
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(true)// 是否可预览视频 true or false
                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(true)// 是否裁剪 true or false
                        .compress(false)// 是否压缩 true or false
                        // .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        .isGif(true)// 是否显示gif图片 true or false
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
                        // .selectionMedia(false)// 是否传入已选图片 List<LocalMedia> list
                        .previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        // .cropCompressQuality()// 裁剪压缩质量 默认90 int
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                        .videoQuality(0)// 视频录制质量 0 or 1 int
//                    .videoMaxSecond()// 显示多少秒以内的视频or音频也可适用 int
//                    .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                        .recordVideoSecond(60)//视频秒数录制 默认60s int
                        .isDragFrame(false)// 是否可拖动裁剪框(固定)
                        .forResult(1);//结果回调onActivityResult code
            } else if (License == 2) {
                // 进入视频 以下是例子：用不到的api可以不写
                PictureSelector.create(getActivity())
                        .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        //.theme()//主题样式(不设置为默认样式) 也可参考demo anim_round_rotate/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .imageEngine(GlideEngine.createGlideEngine())
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(true)// 是否可预览视频 true or false
                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(true)// 是否裁剪 true or false
                        .compress(false)// 是否压缩 true or false
                        // .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        .isGif(true)// 是否显示gif图片 true or false
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
                        // .selectionMedia(false)// 是否传入已选图片 List<LocalMedia> list
                        .previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        // .cropCompressQuality()// 裁剪压缩质量 默认90 int
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                        .videoQuality(0)// 视频录制质量 0 or 1 int
//                    .videoMaxSecond()// 显示多少秒以内的视频or音频也可适用 int
//                    .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                        .recordVideoSecond(60)//视频秒数录制 默认60s int
                        .isDragFrame(false)// 是否可拖动裁剪框(固定)
                        .forResult(2);//结果回调onActivityResult code
            }
        }

        @Override
        public void forbitPermissons() {
//            finish();
            Toast.makeText(getContext(), "权限不通过!", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.e(TGA, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e(TGA, "onResume");


    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.e(TGA, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.e(TGA, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.e(TGA, "onDestroyView");
    }


}
