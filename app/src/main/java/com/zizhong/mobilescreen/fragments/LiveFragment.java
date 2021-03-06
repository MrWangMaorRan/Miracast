package com.zizhong.mobilescreen.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.android.material.tabs.TabLayout;
import com.hpplay.sdk.source.api.IBindSdkListener;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.LelinkSourceSDK;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.zizhong.mobilescreen.bannerss.DislikeDialog;
import com.zizhong.mobilescreen.bannerss.TTAdManagerHolder;
import com.zizhong.mobilescreen.bannerss.TToast;
import com.zizhong.mobilescreen.utils.GlideEngine;
import com.zizhong.mobilescreen.utils.Loading;
import com.zizhong.mobilescreen.R;
import com.zizhong.mobilescreen.activitys.AgreementActivity;
import com.zizhong.mobilescreen.activitys.MainActivity;
import com.zizhong.mobilescreen.activitys.PolicyActivity;
import com.zizhong.mobilescreen.activitys.SettingsActivity;
import com.zizhong.mobilescreen.utils.PermissionsUtils;
import com.zizhong.mobilescreen.utils.SharedPreferencesUtil;
import com.zizhong.mobilescreen.utils.ToastUtils;
import com.zizhong.mobilescreen.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class LiveFragment extends Fragment {

    private IBindSdkListener mBindSdkListener;
    private boolean ok = false;
    private RelativeLayout rlt1;
    private ImageView start_projection;
    public Dialog mShareDialog;
    private String TGA="LiveFragment";
    private Button bt;
    private TextView dianji;
    public ArrayList<LelinkServiceInfo> LelinkServiceInfo;
    private View inflate;
    private TabLayout mTab;
    private ViewPager mVp;
    private ArrayList<Fragment> fm;
    private TextView agreement;
    private TextView cancel;
    private TextView consent;
    private TextView policy;
    private Dialog yinse_dialog;
    public boolean link=false;
    private MyAdapter myAdapter;
    private String name;
    private ImageView img_select;
    private TTNativeExpressAd mTTAd;
    public FrameLayout live_banner;
    private String[] permissions;
    private TTAdNative mTTAdNative_chaping;
    private boolean mIsExpress = true; //????????????????????????
    private boolean mIsLoaded = false; //????????????????????????
    private TTFullScreenVideoAd mttFullVideoAd;
    private boolean mHasShowDownloadActive = false;

    private IBrowseListener browserListener = new IBrowseListener() {

        @Override
        public void onBrowse(int resultCode, List<LelinkServiceInfo> list) {
            LogUtils.e(TGA,"resultCode"+""+list.size()+"");
            if (resultCode == IBrowseListener.BROWSE_ERROR_AUTH) {
                LogUtils.e(TGA,"????????????");
                return;
            }
            if (resultCode == IBrowseListener.BROWSE_STOP) {
                LogUtils.e(TGA,"????????????");
            } else if (resultCode == IBrowseListener.BROWSE_TIMEOUT) {
                LogUtils.e(TGA,"????????????");
            }
            if (resultCode==IBrowseListener.BROWSE_SUCCESS){
                LogUtils.e(TGA,"????????????");
                //ToastUtils.showToast(getContext(),"????????????");
                LelinkServiceInfo.clear();
                LelinkServiceInfo.addAll(list);
               // myAdapter.notifyDataSetChanged();
                handler.sendEmptyMessageDelayed(100,1000);

            }
        }
    };

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==100){
                myAdapter.notifyDataSetChanged();
            }else if (msg.what==1){
                String name1=msg.getData().getString("name");
                dianji.setText(name1);
                linl3.setVisibility(View.VISIBLE);
                rel_2.setVisibility(View.VISIBLE);
                rel_5.setVisibility(View.GONE);
            }else if (msg.what==2){
                tishi.setVisibility(View.VISIBLE);
                mShareDialog.dismiss();
            }

        }
    };
    private com.hpplay.sdk.source.browse.api.LelinkServiceInfo serviceInfo1;
    private static MainActivity activity;
    private ImageView setting;
    private RelativeLayout rel_5;
    private RelativeLayout rel_2;
    private RelativeLayout linl3;
    private Button btn_over;
    private TextView tishi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.live_over_layout, container, false);
        return inflate;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        //??????
        permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//??????????????????????????????????????????????????????
        String ok = SharedPreferencesUtil.getSharedPreferences(getContext()).getString("OK", "");
        initView();
        if (!ok.equals("123")){
            onDialog();
        }else {
          //  ChuShiHua();
            luoji();
            banner();
            start_chaping();
        }
        initDialog();
        initclick();


    }

    private void start_chaping() {
        //step1:?????????sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step3:??????TTAdNative??????,??????????????????????????????
        mTTAdNative_chaping = ttAdManager.createAdNative(getContext());


    }
    @SuppressWarnings("SameParameterValue")
    private void loadAd(String codeId, int orientation) {
        //step4:????????????????????????AdSlot,??????????????????????????????
        AdSlot adSlot;
        if (mIsExpress == true) {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    //????????????????????????????????????????????????????????????,??????dp,?????????????????????????????????????????????0??????
                    .setExpressViewAcceptedSize(500, 500)
                    .build();

        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .build();
        }
        //step5:????????????
        mTTAdNative_chaping.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TGA, "Callback --> onError: " + code + ", " + String.valueOf(message));
                // TToast.show(getApplicationContext(), message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {


                Log.e(TGA, "Callback --> onFullScreenVideoAdLoad");


                mttFullVideoAd = ad;
                Log.e(TGA, mttFullVideoAd + "");
                mIsLoaded = false;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TGA, "Callback --> FullVideoAd show");
                        // TToast.show(getApplicationContext(), "FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TGA, "Callback --> FullVideoAd bar click");
                        // TToast.show(getApplicationContext(), "FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TGA, "Callback --> FullVideoAd close");
                        // TToast.show(getApplicationContext(), "FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.d(TGA, "Callback --> FullVideoAd complete");
                        // TToast.show(getApplicationContext(), "FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TGA, "Callback --> FullVideoAd skipped");
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
                          //  TToast.show(getContext(), "????????????????????????????????????", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                      //  TToast.show(getContext(), "???????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        //TToast.show(getContext(), "?????????????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                       // TToast.show(getContext(), "?????????????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        //TToast.show(getContext(), "???????????????????????????????????????", Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.e(TGA, "Callback --> onFullScreenVideoCached");
                mIsLoaded = true;
                // TToast.show(getApplicationContext(), "FullVideoAd video cached");

            }
        });

    }



    private void banner() {
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
                //TToast.show(getContext(), "???????????????");
            }

            @Override
            public void onAdShow(View view, int type) {
               // TToast.show(getContext(), "????????????");
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
               // TToast.show(getContext(), "????????????");
                live_banner.removeAllViews();
                live_banner.addView(view);
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
               // TToast.show(getContext(), "??????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

                 //   TToast.show(getContext(), "????????????????????????", Toast.LENGTH_LONG);

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
              //  TToast.show(getContext(), "???????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
              //  TToast.show(getContext(), "?????????????????????????????????", Toast.LENGTH_LONG);
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
                    live_banner.removeAllViews();
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
                live_banner.removeAllViews();
                //???????????????????????????????????????????????????
            }


            @Override
            public void onCancel() {
                TToast.show(getContext(), "???????????? ");
            }


        });
    }

    private  void  luoji(){
            //??????log??????????????????false
            UMConfigure.setLogEnabled(true);
            String channel2 = AnalyticsConfig.getChannel(getActivity());
            LogUtils.e("????????????",channel2);
            UMConfigure.init(getActivity(),"61022301864a9558e6d35a6b"
                    ,channel2,UMConfigure.DEVICE_TYPE_PHONE,null);
        // ??????AUTO??????????????????
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        LelinkSourceSDK.getInstance().bindSdk(getContext(), "18388", "52fd374fd644702408ccb459c0c4e857",mBindSdkListener);
        mBindSdkListener = new IBindSdkListener() {
            @Override
            public void onBindCallback(boolean success) {
                LogUtils.e(TGA,"onBindCallback"+success+"");
            }
        };

        LelinkSourceSDK.getInstance().setBrowseResultListener(browserListener);
    }




    public void onDialog(){
        yinse_dialog = new Dialog(getContext(), R.style.dialog_bottom_full);
        yinse_dialog.setCanceledOnTouchOutside(false); //???????????????????????????
        yinse_dialog.setCancelable(false);             //????????? ???true(?????????????????????)
        Window window = yinse_dialog.getWindow();      // ??????dialog?????????
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.share_animation);
        window.getDecorView().setPadding(150, 0, 150, 0);

        View view = View.inflate(getContext(), R.layout.dialog_lay_share_dialog, null); //??????????????????
        agreement = view.findViewById(R.id.agreement);
        cancel = view.findViewById(R.id.cancel);
        consent = view.findViewById(R.id.consent);
        policy = view.findViewById(R.id.policy);
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//??????????????????

        DialogListener();

        yinse_dialog.show();
    }
    private void DialogListener() {
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????????????????
                Intent intent = new Intent(getContext(), AgreementActivity.class);
                startActivity(intent);
            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PolicyActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yinse_dialog.dismiss();
            }
        });
        consent.setOnClickListener(new View.OnClickListener() {

            private String ok="123";

            @Override
            public void onClick(View v) {
                yinse_dialog.dismiss();
                //?????????this?????????????????????Activity?????????
                PermissionsUtils.getInstance().chekPermissions(getActivity(), permissions, permissionsResult);
                SharedPreferencesUtil.getSharedPreferences(getContext()).putString("OK",ok);
                TTAdManagerHolder.init(getContext());
                luoji();
//                banner();
//                start_chaping();

            }
        });
    }


    private void initDialog() {

        mShareDialog = new Dialog(getContext(), R.style.dialog_bottom_full);
        mShareDialog.setCanceledOnTouchOutside(true); //???????????????????????????
        mShareDialog.setCancelable(true);             //????????? ???true
        Window window = mShareDialog.getWindow();     // ??????dialog?????????
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        LelinkServiceInfo = new ArrayList<>();//????????????????????????list
        View view = View.inflate(getContext(), R.layout.selectlayout, null); //??????????????????
        RecyclerView rv = view.findViewById(R.id.rv);
        window.setContentView(view);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, 900);//??????????????????
        //???????????????adapter
        myAdapter = new MyAdapter(this,LelinkServiceInfo);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));//?????????????????????
        rv.setAdapter(myAdapter);//???????????????adapter
        myAdapter.notifyDataSetChanged();//???????????????adaoter
       // new Loading(getContext()).cancel();

//        newInstance(name);
        myAdapter.setOnClickItem(new MyAdapter.OnClickItem() {
            @Override
            public void onClickListenerItem(com.hpplay.sdk.source.browse.api.LelinkServiceInfo position) {
                //????????????
                LelinkSourceSDK.getInstance().stopBrowse();
//                if (position!=null){
//                    ToastUtils.showToast(getContext(),"????????????");
//                }
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                LelinkSourceSDK.getInstance().connect(position);
                IConnectListener connectListener = new IConnectListener() {

                    @Override
                    public void onConnect(LelinkServiceInfo serviceInfo, int connectType) {
                        link=true;
                        Log.i(TGA, "onConnect:" + serviceInfo.getName());
                        serviceInfo1 = serviceInfo;
                        ToastUtils.showToast(getContext(),"?????????????????????:" + serviceInfo.getName());
                        name = serviceInfo.getName();
//                        activity.localFragment.Setname(name);
                        Intent intent = new Intent();
                        intent.setAction("com.amos.demo");
                        intent.putExtra("deviceValue",name);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        new  Thread(new Runnable() {
                           @Override
                           public void run() {
                               Message msg = handler.obtainMessage();
                               msg.what=1;
                               msg.getData().putString("name",name);
                               handler.sendMessage(msg);
                              // handler.sendEmptyMessage(1);

                           }
                       }).start();
                        mShareDialog.dismiss();

                    }

                    @Override
                    public void onDisconnect(LelinkServiceInfo serviceInfo, int what, int extra) {
                        if (serviceInfo == null) {
                            ToastUtils.showToast(getContext(),"??????????????????:null" );
                            LogUtils.e(TGA,"serviceInfo==null");
                            return;
                        }
                        ToastUtils.showToast(getContext(),"???????????????????????????" );
                        LogUtils.e(TGA, "onDisconnect:" + serviceInfo.getName() + " disConnectType:" + what + " extra:" + extra);
                        String text = null;
                        if (what == IConnectListener.WHAT_HARASS_WAITING) {// ??????????????????????????????
                            // ?????????????????????????????????????????????????????????????????????
                            text = serviceInfo.getName() + "??????????????????";
                        } else if (what == IConnectListener.WHAT_DISCONNECT) {
                            switch (extra) {
                                case IConnectListener.EXTRA_HARASS_REJECT:// ??????????????????????????????
                                    text = serviceInfo.getName() + "???????????????";
                                    break;
                                case IConnectListener.EXTRA_HARASS_TIMEOUT:// ??????????????????????????????
                                    text = serviceInfo.getName() + "?????????????????????";
                                    break;
                                case IConnectListener.EXTRA_HARASS_BLACKLIST:// ???????????????????????????????????????
                                    text = serviceInfo.getName() + "???????????????????????????";
                                    break;
                                case IConnectListener.EXTRA_CONNECT_DEVICE_OFFLINE:
                                    text = serviceInfo.getName() + "?????????";
                                    break;
                                default:
                                    text = serviceInfo.getName() + "????????????";
                                    break;
                            }
                        } else if (what == IConnectListener.WHAT_CONNECT_FAILED) {
                            switch (extra) {
                                case IConnectListener.EXTRA_CONNECT_DEVICE_OFFLINE:
                                    text = serviceInfo.getName() + "?????????";
                                    break;
                                default:
                                    text = serviceInfo.getName() + "????????????";
                                    break;
                            }
                        }
                        if (TextUtils.isEmpty(text)) {
                            text = "onDisconnect " + what + "/" + extra;
                            LogUtils.e("??????",text);
                            ToastUtils.showToast(getActivity(),text);
                            new  Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message msg = handler.obtainMessage();
                                    msg.what=2;
                                    msg.getData().putString("XXX",name);
                                    handler.sendMessage(msg);
                                    // handler.sendEmptyMessage(1);

                                }
                            }).start();
                        }

                    }

                };

                LelinkSourceSDK.getInstance().setConnectListener(connectListener);
            }
        });

        mShareDialog.dismiss();
    }


    private void initclick() {
        start_projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LelinkSourceSDK.getInstance().startBrowse();
                String ok = SharedPreferencesUtil.getSharedPreferences(getContext()).getString("OK", "");
                if (ok.equals("123")&& mTTAdNative_chaping!=null){
                    //?????????
                    loadAd("946454406", TTAdConstant.VERTICAL);
                    //?????????????????????????????????????????????
                    if (mttFullVideoAd!=null){
                        mttFullVideoAd.showFullScreenVideoAd(getActivity(), TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
                        mttFullVideoAd = null;
                    }
                }

                if (LelinkServiceInfo!=null){
                    new Loading(getActivity()).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mShareDialog.show();
                        }
                    },3000);
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        btn_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????true????????????????????????false????????????
                boolean disconnect = LelinkSourceSDK.getInstance().disconnect(serviceInfo1);
                if (disconnect==true){
                    ToastUtils.showToast(getActivity(),"??????????????????");
                    linl3.setVisibility(View.GONE);
                    rel_2.setVisibility(View.GONE);
                    rel_5.setVisibility(View.VISIBLE);
                    dianji.setText("??????????????????");
                    Intent intent = new Intent();
                    intent.setAction("duankai");
                    intent.putExtra("duankai","??????????????????");
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    String ok = SharedPreferencesUtil.getSharedPreferences(getContext()).getString("OK", "");
                    if (ok.equals("123")&& mTTAdNative_chaping!=null){
                        //?????????
                        loadAd("946454406", TTAdConstant.VERTICAL);
                        //?????????????????????????????????????????????
                        if (mttFullVideoAd!=null){
                            mttFullVideoAd.showFullScreenVideoAd(getActivity(), TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
                            mttFullVideoAd = null;
                        }
                    }
                }else {
                    ToastUtils.showToast(getActivity(),"??????????????????");
                }
            }
        });
        dianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LelinkSourceSDK.getInstance().startBrowse();
                if (LelinkServiceInfo!=null){

                    new Loading(getActivity()).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mShareDialog.show();
                        }
                    },3000);
                }
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // ???????????????????????????sdk??????????????????SDK????????????????????????
        LelinkSourceSDK.getInstance().unBindSdk();
        //??????true????????????????????????false????????????
        boolean disconnect = LelinkSourceSDK.getInstance().disconnect(serviceInfo1);
        LogUtils.e("??????",disconnect+"");
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }
    private void initView() {
        rlt1 = (RelativeLayout) inflate.findViewById(R.id.rlt1);
        start_projection = (ImageView) inflate.findViewById(R.id.start_projection);
        dianji = inflate.findViewById(R.id.dianji);
        setting = inflate.findViewById(R.id.setting);
        rel_5 = inflate.findViewById(R.id.rel_5);
        rel_2 = inflate.findViewById(R.id.rel_2);
        linl3 = inflate.findViewById(R.id.linl3);
        btn_over = inflate.findViewById(R.id.btn_over);
        tishi = inflate.findViewById(R.id.tishi);
        img_select = inflate.findViewById(R.id.img_select);
        live_banner = inflate.findViewById(R.id.live_banner);
    }

    //?????????????????????????????????
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
           ChuShiHua();
           //TTAdManagerHolder.init(getContext());
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

    public void ChuShiHua(){
        TTAdSdk.init(getContext(),
                new TTAdConfig.Builder()
                        .appId("5199154")
                        .useTextureView(true) //????????????SurfaceView??????????????????,??????SurfaceView??????????????????????????????TextureView
                        .appName("????????????")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)//???????????????
                        .allowShowNotify(true) //????????????sdk?????????????????????,????????????false??????????????????????????????????????????
                        .debug(true) //??????????????????????????????????????????????????????????????????????????????
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //???????????????????????????????????????,????????????????????????????????????apk?????????????????????????????????????????????????????????
                        .supportMultiProcess(false) //????????????????????????true??????
                        .asyncInit(true) //?????????????????????sdk,?????????true????????????SDK??????????????????3450??????????????????~~
                        //.httpStack(new MyOkStack3())//?????????????????????demo????????????okhttp3??????????????????????????????????????????????????????????????????
                        .build());
        //?????????????????????????????????????????????SDK?????????????????????????????????????????????SDK???content
        //if (PROCESS_NAME_XXXX.equals(processName)) {
        //   TTAdSdk.init(context, config);
        //}

    }

}
