package com.zizhong.mobilescreen.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zizhong.mobilescreen.R;
import com.zizhong.mobilescreen.activitys.SettingsActivity;


public class DoodlesFragment extends Fragment {
    private View inflate;
    private ImageView start_tou;
    private ImageView doodles_setting;

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initview();
        onClick();
    }

    private void onClick() {
        start_tou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "此功能正在完善中，请尝试其他功能，谢谢!", Toast.LENGTH_SHORT).show();
            }
        });
        doodles_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    inflate=  inflater.inflate(R.layout.doodles_layout, container, false);

        return inflate;
    }

    private void initview() {
        start_tou = inflate.findViewById(R.id.start_tou);
        doodles_setting = inflate.findViewById(R.id.doodles_setting);
    }
}
