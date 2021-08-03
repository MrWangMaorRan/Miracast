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
    private boolean mIsExpress = true; //是否请求模板广告
    private boolean mIsLoaded = false; //视频是否加载完成
    private TTFullScreenVideoAd mttFullVideoAd;
    private boolean mHasShowDownloadActive = false;

    private IBrowseListener browserListener = new IBrowseListener() {

        @Override
        public void onBrowse(int resultCode, List<LelinkServiceInfo> list) {
            LogUtils.e(TGA,"resultCode"+""+list.size()+"");
            if (resultCode == IBrowseListener.BROWSE_ERROR_AUTH) {
                LogUtils.e(TGA,"授权失败");
                return;
            }
            if (resultCode == IBrowseListener.BROWSE_STOP) {
                LogUtils.e(TGA,"搜索停止");
            } else if (resultCode == IBrowseListener.BROWSE_TIMEOUT) {
                LogUtils.e(TGA,"搜索超时");
            }
            if (resultCode==IBrowseListener.BROWSE_SUCCESS){
                LogUtils.e(TGA,"搜索成功");
                //ToastUtils.showToast(getContext(),"搜索成功");
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
        //权限
        permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
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
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative_chaping = ttAdManager.createAdNative(getContext());


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
                          //  TToast.show(getContext(), "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                      //  TToast.show(getContext(), "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        //TToast.show(getContext(), "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                       // TToast.show(getContext(), "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        //TToast.show(getContext(), "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
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
                //TToast.show(getContext(), "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
               // TToast.show(getContext(), "广告展示");
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
               // TToast.show(getContext(), "渲染成功");
                live_banner.removeAllViews();
                live_banner.addView(view);
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
               // TToast.show(getContext(), "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

                 //   TToast.show(getContext(), "下载中，点击暂停", Toast.LENGTH_LONG);

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
              //  TToast.show(getContext(), "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
              //  TToast.show(getContext(), "下载失败，点击重新下载", Toast.LENGTH_LONG);
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
                    live_banner.removeAllViews();
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
                live_banner.removeAllViews();
                //用户选择不喜欢原因后，移除广告展示
            }


            @Override
            public void onCancel() {
                TToast.show(getContext(), "点击取消 ");
            }


        });
    }

    private  void  luoji(){
            //设置log开关，默认为false
            UMConfigure.setLogEnabled(true);
            String channel2 = AnalyticsConfig.getChannel(getActivity());
            LogUtils.e("渠道名字",channel2);
            UMConfigure.init(getActivity(),"61022301864a9558e6d35a6b"
                    ,channel2,UMConfigure.DEVICE_TYPE_PHONE,null);
        // 选用AUTO页面采集模式
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
        yinse_dialog.setCanceledOnTouchOutside(false); //手指触碰到外界取消
        yinse_dialog.setCancelable(false);             //可取消 为true(屏幕返回键监听)
        Window window = yinse_dialog.getWindow();      // 得到dialog的窗体
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.share_animation);
        window.getDecorView().setPadding(150, 0, 150, 0);

        View view = View.inflate(getContext(), R.layout.dialog_lay_share_dialog, null); //获取布局视图
        agreement = view.findViewById(R.id.agreement);
        cancel = view.findViewById(R.id.cancel);
        consent = view.findViewById(R.id.consent);
        policy = view.findViewById(R.id.policy);
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏

        DialogListener();

        yinse_dialog.show();
    }
    private void DialogListener() {
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建意图，跳转
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
                //这里的this不是上下文，是Activity对象！
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
        mShareDialog.setCanceledOnTouchOutside(true); //手指触碰到外界取消
        mShareDialog.setCancelable(true);             //可取消 为true
        Window window = mShareDialog.getWindow();     // 得到dialog的窗体
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        LelinkServiceInfo = new ArrayList<>();//创建数组存储数据list
        View view = View.inflate(getContext(), R.layout.selectlayout, null); //获取布局视图
        RecyclerView rv = view.findViewById(R.id.rv);
        window.setContentView(view);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, 900);//设置横向全屏
        //创建适配器adapter
        myAdapter = new MyAdapter(this,LelinkServiceInfo);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));//设置布局管理器
        rv.setAdapter(myAdapter);//设置适配器adapter
        myAdapter.notifyDataSetChanged();//刷新适配器adaoter
       // new Loading(getContext()).cancel();

//        newInstance(name);
        myAdapter.setOnClickItem(new MyAdapter.OnClickItem() {
            @Override
            public void onClickListenerItem(com.hpplay.sdk.source.browse.api.LelinkServiceInfo position) {
                //停止搜索
                LelinkSourceSDK.getInstance().stopBrowse();
//                if (position!=null){
//                    ToastUtils.showToast(getContext(),"搜索关闭");
//                }
                //在一投一的过程中该接口不是必须调用的，开发者在调用投屏业务传入设备信息即可
                LelinkSourceSDK.getInstance().connect(position);
                IConnectListener connectListener = new IConnectListener() {

                    @Override
                    public void onConnect(LelinkServiceInfo serviceInfo, int connectType) {
                        link=true;
                        Log.i(TGA, "onConnect:" + serviceInfo.getName());
                        serviceInfo1 = serviceInfo;
                        ToastUtils.showToast(getContext(),"连接设备名字为:" + serviceInfo.getName());
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
                            ToastUtils.showToast(getContext(),"连接设备返回:null" );
                            LogUtils.e(TGA,"serviceInfo==null");
                            return;
                        }
                        ToastUtils.showToast(getContext(),"错误，请看连接日志" );
                        LogUtils.e(TGA, "onDisconnect:" + serviceInfo.getName() + " disConnectType:" + what + " extra:" + extra);
                        String text = null;
                        if (what == IConnectListener.WHAT_HARASS_WAITING) {// 防骚扰，等待用户确认
                            // 乐播投屏防骚扰等待消息，请开发者务必处理该消息
                            text = serviceInfo.getName() + "等待用户确认";
                        } else if (what == IConnectListener.WHAT_DISCONNECT) {
                            switch (extra) {
                                case IConnectListener.EXTRA_HARASS_REJECT:// 防骚扰，用户拒绝投屏
                                    text = serviceInfo.getName() + "连接被拒绝";
                                    break;
                                case IConnectListener.EXTRA_HARASS_TIMEOUT:// 防骚扰，用户响应超时
                                    text = serviceInfo.getName() + "防骚扰响应超时";
                                    break;
                                case IConnectListener.EXTRA_HARASS_BLACKLIST:// 防骚扰，该用户被加入黑名单
                                    text = serviceInfo.getName() + "已被加入投屏黑名单";
                                    break;
                                case IConnectListener.EXTRA_CONNECT_DEVICE_OFFLINE:
                                    text = serviceInfo.getName() + "不在线";
                                    break;
                                default:
                                    text = serviceInfo.getName() + "连接断开";
                                    break;
                            }
                        } else if (what == IConnectListener.WHAT_CONNECT_FAILED) {
                            switch (extra) {
                                case IConnectListener.EXTRA_CONNECT_DEVICE_OFFLINE:
                                    text = serviceInfo.getName() + "不在线";
                                    break;
                                default:
                                    text = serviceInfo.getName() + "连接失败";
                                    break;
                            }
                        }
                        if (TextUtils.isEmpty(text)) {
                            text = "onDisconnect " + what + "/" + extra;
                            LogUtils.e("失败",text);
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
                    //穿山甲
                    loadAd("946454406", TTAdConstant.VERTICAL);
                    //展示广告，并传入广告展示的场景
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
                //返回true表示断开连接成功false表示失败
                boolean disconnect = LelinkSourceSDK.getInstance().disconnect(serviceInfo1);
                if (disconnect==true){
                    ToastUtils.showToast(getActivity(),"断开连接成功");
                    linl3.setVisibility(View.GONE);
                    rel_2.setVisibility(View.GONE);
                    rel_5.setVisibility(View.VISIBLE);
                    dianji.setText("选择连接设备");
                    Intent intent = new Intent();
                    intent.setAction("duankai");
                    intent.putExtra("duankai","选择连接设备");
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    String ok = SharedPreferencesUtil.getSharedPreferences(getContext()).getString("OK", "");
                    if (ok.equals("123")&& mTTAdNative_chaping!=null){
                        //穿山甲
                        loadAd("946454406", TTAdConstant.VERTICAL);
                        //展示广告，并传入广告展示的场景
                        if (mttFullVideoAd!=null){
                            mttFullVideoAd.showFullScreenVideoAd(getActivity(), TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
                            mttFullVideoAd = null;
                        }
                    }
                }else {
                    ToastUtils.showToast(getActivity(),"断开连接失败");
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
        // 如果不需要使用投屏sdk时，需要注销SDK，以节省资源开销
        LelinkSourceSDK.getInstance().unBindSdk();
        //返回true表示断开连接成功false表示失败
        boolean disconnect = LelinkSourceSDK.getInstance().disconnect(serviceInfo1);
        LogUtils.e("销毁",disconnect+"");
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

    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
           ChuShiHua();
           //TTAdManagerHolder.init(getContext());
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

    public void ChuShiHua(){
        TTAdSdk.init(getContext(),
                new TTAdConfig.Builder()
                        .appId("5199154")
                        .useTextureView(true) //默认使用SurfaceView播放视频广告,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName("手机投屏")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)//落地页主题
                        .allowShowNotify(true) //是否允许sdk展示通知栏提示,若设置为false则会导致通知栏不显示下载进度
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                        .supportMultiProcess(false) //是否支持多进程，true支持
                        .asyncInit(true) //是否异步初始化sdk,设置为true可以减少SDK初始化耗时。3450版本开始废弃~~
                        //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                        .build());
        //如果明确某个进程不会使用到广告SDK，可以只针对特定进程初始化广告SDK的content
        //if (PROCESS_NAME_XXXX.equals(processName)) {
        //   TTAdSdk.init(context, config);
        //}

    }

}
