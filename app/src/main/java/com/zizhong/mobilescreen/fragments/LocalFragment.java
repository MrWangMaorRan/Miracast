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
            Log.i("????????????","?????????");
            if("com.amos.demo".equals(action)) {
                String deviceValue = (String) intent.getSerializableExtra("deviceValue");
                if(deviceValue!=null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //?????????????????????????????????UI
                            local_name.setText(deviceValue);
                            Log.i("????????????",deviceValue);
                        }
                    });
                }
            }else if ("duankai".equals(action)){
                String duankai = (String) intent.getSerializableExtra("duankai");
                if(duankai!=null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //?????????????????????????????????UI
                            local_name.setText(duankai);
                            Log.i("????????????",duankai);
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
        //??????
        permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//??????????????????????????????????????????????????????
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
        //??????TTAdNative?????????createAdNative(Context context) context????????????Activity??????
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(getContext());

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946444093") //?????????id
                .setSupportDeepLink(true)
                .setAdCount(1) //?????????????????????1???3???
                .setExpressViewAcceptedSize(600,90) //??????????????????view???size,??????dp
                .build();

        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //??????????????????
            @Override
            public void onError(int code, String message) {
                Log.e(TGA, "????????????"+code+","+message);
            }

            //??????????????????
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                Log.e(TGA, "????????????");
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
              //  TToast.show(getContext(), "???????????????");
            }

            @Override
            public void onAdShow(View view, int type) {
              //  TToast.show(getContext(), "????????????");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:");
                TToast.show(getContext(), msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:");
                //??????view????????? ?????? dp
             //   TToast.show(getContext(), "????????????");
                local_banner.removeAllViews();
                local_banner.addView(view);
            }
        });
        //dislike??????
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                //TToast.show(getContext(), "??????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

                //TToast.show(getContext(), "????????????????????????", Toast.LENGTH_LONG);

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
               // TToast.show(getContext(), "???????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
               // TToast.show(getContext(), "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
               // TToast.show(getContext(), "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
              //  TToast.show(getContext(), "????????????", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * ????????????????????????, ??????????????????????????????????????????????????????dislike???????????????????????????????????? dislike???????????????dislike?????????
     *
     * @param ad
     * @param customStyle ????????????????????????true:???????????????
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //?????????????????????
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(activity, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //????????????
                    TToast.show(getContext(), "?????? " + filterWord.getName());
                    //???????????????????????????????????????????????????
                    local_banner.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    TToast.show(getContext(), "?????????????????????????????????");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //???????????????????????????dislike????????????
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int i, String s, boolean b) {
                TToast.show(getContext(), "?????? " + s);
                local_banner.removeAllViews();
                //???????????????????????????????????????????????????
            }

            @Override
            public void onCancel() {
                TToast.show(getContext(), "???????????? ");
            }


        });
    }

    public void Setname(String name) {
        local_name.setText(name);
    }

    private void initLebo() {
        ILelinkPlayerListener playerListener = new ILelinkPlayerListener() {

            /**
             *  ????????????
             */
            @Override
            public void onLoading() {
                LogUtils.e(TGA, "????????????");
            }

            /**
             * ????????????
             */
            @Override
            public void onStart() {
                LogUtils.e(TGA, "????????????");
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
             * ??????
             */
            @Override
            public void onPause() {
                LogUtils.e(TGA, "??????");
            }

            /**
             * ????????????
             */
            @Override
            public void onCompletion() {
                LogUtils.e(TGA, "????????????");
            }

            /**
             * ????????????
             */
            @Override
            public void onStop() {
                LogUtils.e(TGA, "????????????");
            }

            /**
             * ????????????????????????????????????????????????????????????
             */
            @Override
            public void onSeekComplete(int pPosition) {
                LogUtils.e(TGA, "????????????????????????????????????????????????????????????");
            }


            /**
             * ????????????
             */
            @Override
            public void onInfo(int i, int i1) {

            }

            @Override
            public void onInfo(int i, String s) {

            }

            /**
             * ????????????
             */
            @Override
            public void onError(int what, int extra) {
                LogUtils.e(TGA, "?????????" + extra + "");
            }

            /**
             * ?????????????????????????????????????????????
             */
            @Override
            public void onVolumeChanged(float percent) {
                LogUtils.e(TGA, "??????????????????" + percent);
            }

            /**
             * ????????????????????????
             * @param duration ?????????????????????
             * @param position ????????????????????????
             */
            @Override
            public void onPositionUpdate(long duration, long position) {
                LogUtils.e(TGA, "????????????????????????????????????" + duration + " " + "????????????" + position);
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
                    //?????????this?????????????????????Activity?????????
                    PermissionsUtils.getInstance().chekPermissions(getActivity(), permissions, permissionsResult);
                } else {
                    ToastUtils.showToast(getActivity(), "??????????????????");
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

                    //?????????this?????????????????????Activity?????????
                    PermissionsUtils.getInstance().chekPermissions(getActivity(), permissions, permissionsResult);
                } else {
                    ToastUtils.showToast(getActivity(), "??????????????????");
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
            LogUtils.e("????????????", "?????????");
            //??????????????????????????????
            // ????????????????????????
            localMedia = PictureSelector.obtainMultipleResult(data);
            LogUtils.e("????????????", localMedia.size() + "");
            LogUtils.e("????????????", localMedia.get(0).getPath());

            /*????????????*/
            if (requestCode == 1) {
                if (lists.size() > 0 && localMedia.size() > 0) {
//                    LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
//                LelinkSourceSDK.getInstance(). startPlayMedia(lelinkPlayerInfo);
                    LelinkSourceSDK.getInstance().startPlayMediaImmed(lists.get(0), localMedia.get(0).getPath(), LelinkSourceSDK.MEDIA_TYPE_IMAGE, true);
                } else {
                    ToastUtils.showToast(getActivity(), "????????????????????????????????????");
                }


            } else if (requestCode == 2) {
                if (lists.size() > 0 && localMedia.size() > 0) {
//                    LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
//                    LelinkSourceSDK.getInstance(). startPlayMedia(lelinkPlayerInfo);
                    LelinkSourceSDK.getInstance().startPlayMediaImmed(lists.get(0), localMedia.get(0).getPath(), LelinkSourceSDK.MEDIA_TYPE_VIDEO, true);
                } else {
                    ToastUtils.showToast(getActivity(), "????????????????????????????????????");
                }
            } else {
                ToastUtils.showToast(getActivity(), "??????????????????????????????");
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

    //?????????????????????????????????
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
           // Toast.makeText(getContext(), "?????????????????????????????????!", Toast.LENGTH_SHORT).show();
            if (License == 1) {
                // ???????????? ??????????????????????????????api????????????
                PictureSelector.create(getActivity())
                        .openGallery(PictureMimeType.ofImage())//??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                        //.theme()//????????????(????????????????????????) ????????????demo anim_round_rotate/styles??? ?????????R.style.picture.white.style
                        .maxSelectNum(1)// ???????????????????????? int
                        .minSelectNum(1)// ?????????????????? int
                        .imageSpanCount(4)// ?????????????????? int
                        .imageEngine(GlideEngine.createGlideEngine())
                        .selectionMode(PictureConfig.MULTIPLE)// ?????? or ?????? PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// ????????????????????? true or false
                        .previewVideo(true)// ????????????????????? true or false
                        .enablePreviewAudio(true) // ????????????????????? true or false
                        .isCamera(true)// ???????????????????????? true or false
                        .imageFormat(PictureMimeType.PNG)// ??????????????????????????????,??????jpeg
                        .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                        .sizeMultiplier(0.5f)// glide ?????????????????? 0~1?????? ????????? .glideOverride()??????
                        .setOutputCameraPath("/CustomPath")// ???????????????????????????,?????????
                        .enableCrop(true)// ???????????? true or false
                        .compress(false)// ???????????? true or false
                        // .glideOverride()// int glide ???????????????????????????????????????????????????????????????????????????????????????
                        //.withAspectRatio()// int ???????????? ???16:9 3:2 3:4 1:1 ????????????
                        .hideBottomControls(true)// ????????????uCrop??????????????????????????? true or false
                        .isGif(true)// ????????????gif?????? true or false
                        .freeStyleCropEnabled(true)// ???????????????????????? true or false
                        .circleDimmedLayer(false)// ?????????????????? true or false
                        .showCropFrame(false)// ?????????????????????????????? ???????????????????????????false   true or false
                        .showCropGrid(false)// ?????????????????????????????? ???????????????????????????false    true or false
                        .openClickSound(false)// ???????????????????????? true or false
                        // .selectionMedia(false)// ???????????????????????? List<LocalMedia> list
                        .previewEggs(false)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????) true or false
                        // .cropCompressQuality()// ?????????????????? ??????90 int
                        .minimumCompressSize(100)// ??????100kb??????????????????
                        .synOrAsy(true)//??????true?????????false ?????? ????????????
                        //.cropWH()// ??????????????????????????????????????????????????????????????? int
                        .rotateEnabled(true) // ??????????????????????????? true or false
                        .scaleEnabled(true)// ????????????????????????????????? true or false
                        .videoQuality(0)// ?????????????????? 0 or 1 int
//                    .videoMaxSecond()// ??????????????????????????????or?????????????????? int
//                    .videoMinSecond(10)// ??????????????????????????????or?????????????????? int
                        .recordVideoSecond(60)//?????????????????? ??????60s int
                        .isDragFrame(false)// ????????????????????????(??????)
                        .forResult(1);//????????????onActivityResult code
            } else if (License == 2) {
                // ???????????? ??????????????????????????????api????????????
                PictureSelector.create(getActivity())
                        .openGallery(PictureMimeType.ofVideo())//??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                        //.theme()//????????????(????????????????????????) ????????????demo anim_round_rotate/styles??? ?????????R.style.picture.white.style
                        .maxSelectNum(1)// ???????????????????????? int
                        .minSelectNum(1)// ?????????????????? int
                        .imageSpanCount(4)// ?????????????????? int
                        .imageEngine(GlideEngine.createGlideEngine())
                        .selectionMode(PictureConfig.MULTIPLE)// ?????? or ?????? PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// ????????????????????? true or false
                        .previewVideo(true)// ????????????????????? true or false
                        .enablePreviewAudio(true) // ????????????????????? true or false
                        .isCamera(true)// ???????????????????????? true or false
                        .imageFormat(PictureMimeType.PNG)// ??????????????????????????????,??????jpeg
                        .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                        .sizeMultiplier(0.5f)// glide ?????????????????? 0~1?????? ????????? .glideOverride()??????
                        .setOutputCameraPath("/CustomPath")// ???????????????????????????,?????????
                        .enableCrop(true)// ???????????? true or false
                        .compress(false)// ???????????? true or false
                        // .glideOverride()// int glide ???????????????????????????????????????????????????????????????????????????????????????
                        //.withAspectRatio()// int ???????????? ???16:9 3:2 3:4 1:1 ????????????
                        .hideBottomControls(true)// ????????????uCrop??????????????????????????? true or false
                        .isGif(true)// ????????????gif?????? true or false
                        .freeStyleCropEnabled(true)// ???????????????????????? true or false
                        .circleDimmedLayer(false)// ?????????????????? true or false
                        .showCropFrame(false)// ?????????????????????????????? ???????????????????????????false   true or false
                        .showCropGrid(false)// ?????????????????????????????? ???????????????????????????false    true or false
                        .openClickSound(false)// ???????????????????????? true or false
                        // .selectionMedia(false)// ???????????????????????? List<LocalMedia> list
                        .previewEggs(false)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????) true or false
                        // .cropCompressQuality()// ?????????????????? ??????90 int
                        .minimumCompressSize(100)// ??????100kb??????????????????
                        .synOrAsy(true)//??????true?????????false ?????? ????????????
                        //.cropWH()// ??????????????????????????????????????????????????????????????? int
                        .rotateEnabled(true) // ??????????????????????????? true or false
                        .scaleEnabled(true)// ????????????????????????????????? true or false
                        .videoQuality(0)// ?????????????????? 0 or 1 int
//                    .videoMaxSecond()// ??????????????????????????????or?????????????????? int
//                    .videoMinSecond(10)// ??????????????????????????????or?????????????????? int
                        .recordVideoSecond(60)//?????????????????? ??????60s int
                        .isDragFrame(false)// ????????????????????????(??????)
                        .forResult(2);//????????????onActivityResult code
            }
        }

        @Override
        public void forbitPermissons() {
//            finish();
            Toast.makeText(getContext(), "???????????????!", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //??????????????????this
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
