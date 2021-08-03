package com.zizhong.mobilescreen.activitys;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.umeng.analytics.MobclickAgent;
import com.zizhong.mobilescreen.R;
import com.zizhong.mobilescreen.adapter.VpAdapter;
import com.zizhong.mobilescreen.base.BaseActivity;
import com.zizhong.mobilescreen.fragments.DoodlesFragment;
import com.zizhong.mobilescreen.fragments.FileFragment;
import com.zizhong.mobilescreen.fragments.LiveFragment;
import com.zizhong.mobilescreen.fragments.LocalFragment;
import com.zizhong.mobilescreen.servicess.ForegroundService;
import com.zizhong.mobilescreen.utils.log.LogUtils;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTab;
    private ViewPager mVp;
    private ArrayList<Fragment> fm;
    public LiveFragment liveFragment1;
    public LocalFragment localFragment;
    private FileFragment fileFragment;
    private DoodlesFragment doodlesFragment;
    private Intent mForegroundService;
    private String TGA="MainActivity_111";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        initServices();
    }

    private void initFragment() {
        fm = new ArrayList<>();
        liveFragment1 = new LiveFragment();
        localFragment = new LocalFragment();
      //  fileFragment = new FileFragment();
       // doodlesFragment = new DoodlesFragment();

        fm.add(liveFragment1);
        fm.add(localFragment);
       // fm.add(fileFragment);
      //  fm.add(doodlesFragment);
        VpAdapter adapter = new VpAdapter(getSupportFragmentManager(), fm);
        mVp.setAdapter(adapter);
        mTab.setupWithViewPager(mVp);
        mVp.setOffscreenPageLimit(4);
        mTab.getTabAt(0).setText("实时投屏").setIcon(R.drawable.time_item);
        mTab.getTabAt(1).setText("本地投屏").setIcon(R.drawable.local_item);
     //   mTab.getTabAt(2).setText("文件投屏").setIcon(R.drawable.file_item);
     //   mTab.getTabAt(3).setText("涂鸦投屏").setIcon(R.drawable.doodles_item);


    }

    private void initView() {
        mTab = (TabLayout) findViewById(R.id.tab);
        mVp = (ViewPager) findViewById(R.id.vp);
        mTab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tab:
                break;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        getSupportFragmentManager().getFragments();
        if (getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, intent);
            }
        }
    }


    //前台服务
    private  void  initServices(){
        // 启动服务
        if (!ForegroundService.serviceIsLive) {
            // Android 8.0使用startForegroundService在前台启动新服务
            mForegroundService = new Intent(this, ForegroundService.class);
            mForegroundService.putExtra("Foreground", "This is a foreground service.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mForegroundService);
            } else {
                startService(mForegroundService);
            }
        } else {
            LogUtils.e(TGA,"前台服务正在运行中");
            //  Toast.makeText(this, "前台服务正在运行中...", Toast.LENGTH_SHORT).show();
        }
    }


    //再按一次退出程序
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                // 停止服务
                mForegroundService = new Intent(this, ForegroundService.class);
                stopService(mForegroundService);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止服务
        mForegroundService = new Intent(this, ForegroundService.class);
        stopService(mForegroundService);
        LogUtils.e(TGA,"销毁");
    }

}
