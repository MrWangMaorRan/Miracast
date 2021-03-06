package com.zizhong.mobilescreen.activitys;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.umeng.analytics.MobclickAgent;
import com.zizhong.mobilescreen.BuildConfig;
import com.zizhong.mobilescreen.R;
import com.zizhong.mobilescreen.bannerss.DislikeDialog;
import com.zizhong.mobilescreen.bannerss.TTAdManagerHolder;
import com.zizhong.mobilescreen.bannerss.TToast;
import com.zizhong.mobilescreen.base.BaseActivity;
import com.zizhong.mobilescreen.utils.PermissionsUtils;
import com.zizhong.mobilescreen.utils.SharedPreferencesUtil;

import java.util.List;

import static androidx.camera.core.CameraX.getContext;

public class SettingsActivity extends BaseActivity {

    private LinearLayout ll4;
    private LinearLayout ll5;
    private LinearLayout ll1;
    private LinearLayout ll3;
    private FrameLayout setting_banner;
    private String TGA="SettingsActivity";
    private TTNativeExpressAd mTTAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initView();
        String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");
        PermissionsUtils.getInstance();
        if (!ok.equals("123")){

        }else {
            TTAdManagerHolder.init(this);
            banner_setting();
        }
        initClick();
    }

    private void banner_setting() {
        //??????TTAdNative?????????createAdNative(Context context) context????????????Activity??????
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(SettingsActivity.this);

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
              //  TToast.show(SettingsActivity.this, "???????????????");
            }

            @Override
            public void onAdShow(View view, int type) {
               // TToast.show(SettingsActivity.this, "????????????");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:");
                TToast.show(SettingsActivity.this, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:");
                //??????view????????? ?????? dp
               // TToast.show(SettingsActivity.this, "????????????");
                setting_banner.removeAllViews();
                setting_banner.addView(view);
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
              //  TToast.show(SettingsActivity.this, "??????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

               // TToast.show(SettingsActivity.this, "????????????????????????", Toast.LENGTH_LONG);

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
               // TToast.show(SettingsActivity.this, "???????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
               // TToast.show(SettingsActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                //TToast.show(SettingsActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                //TToast.show(SettingsActivity.this, "????????????", Toast.LENGTH_LONG);
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
            final DislikeDialog dislikeDialog = new DislikeDialog(SettingsActivity.this, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //????????????
                    TToast.show(SettingsActivity.this, "?????? " + filterWord.getName());
                    //???????????????????????????????????????????????????
                    setting_banner.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    TToast.show(SettingsActivity.this, "?????????????????????????????????");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //???????????????????????????dislike????????????
        ad.setDislikeCallback(SettingsActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int i, String s, boolean b) {
                TToast.show(SettingsActivity.this, "?????? " + s);
                setting_banner.removeAllViews();
                //???????????????????????????????????????????????????
            }



            @Override
            public void onCancel() {
                TToast.show(SettingsActivity.this, "???????????? ");
            }



        });
    }

    private void initClick() {
        ll4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PolicyActivity.class);
                startActivity(intent);
            }
        });

        ll5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AgreementActivity.class);
                startActivity(intent);
            }
        });
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRate();
            }
        });
    }

    //?????????????????????
    private void goRate() {
        String market = "market://details?id=" + BuildConfig.APPLICATION_ID;
        Uri uri = Uri.parse(market);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            String url = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    private void initView() {
        ll4 = (LinearLayout) findViewById(R.id.ll4);
        ll5 = (LinearLayout) findViewById(R.id.ll5);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll3 = (LinearLayout) findViewById(R.id.ll3);
        setting_banner = (FrameLayout) findViewById(R.id.setting_banner);
    }
}