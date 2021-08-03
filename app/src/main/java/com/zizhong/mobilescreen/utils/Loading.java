package com.zizhong.mobilescreen.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zizhong.mobilescreen.R;

import androidx.annotation.NonNull;

public  class Loading extends Dialog {
    private ImageView imageView;
    private Button button;
    private Handler handler;
    private Runnable runnable;
    /**
     *
     */
    public Loading(@NonNull Context context) {
        super(context);
        setContentView(R.layout.londing);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,900);
        window.setWindowAnimations(R.style.share_animation);
        /**
         * 5.0以上默认有个.9图片作为背景，导致不能铺满横向屏幕
         */
        window.setBackgroundDrawableResource(android.R.color.white);
        /*
            设置旋转动画
         */
        Animation circle_anim = AnimationUtils.loadAnimation(context, R.anim.anim_round_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        imageView=findViewById(R.id.imagelong);
        imageView.startAnimation(circle_anim);


        //这里Handler的postDelayed方法，等待10000毫秒在执行run方法。
        //在Activity中我们经常需要使用Handler方法更新UI或者执行一些耗时事件，
        //并且Handler中post方法既可以执行耗时事件也可以做一些UI更新的事情，比较好用，推荐使用
        handler=new Handler();
        runnable=new Runnable(){
            public void run(){
                //等待10000毫秒后销毁此页面，并提示
                Loading.this.dismiss();
                Toast.makeText(getContext(), "搜索完成", Toast.LENGTH_LONG).show();
            }
        };
        handler.postDelayed(runnable,3000);
    }


}
