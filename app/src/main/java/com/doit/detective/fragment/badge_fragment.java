package com.doit.detective.fragment;

import static com.doit.detective.go_activity.total_travel_carbon_footprint;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.doit.detective.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class badge_fragment extends Fragment {
    private View v;

    public static boolean badge1_unlock = false;
    public static boolean badge2_unlock = false;
    public static boolean badge3_unlock = false;
    public static boolean badge4_unlock = false;
    public static boolean badge5_unlock = false;

    public badge_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.badge_fragment, container, false);
        configureImageButton();

        //badge1
        if (total_travel_carbon_footprint >= 0) {
            badge1_unlock = true;
        }

        //badge2
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        // get current time
        Date date = new Date(System.currentTimeMillis());
        if (simpleDateFormat.format(date).equals("00:00")) {
            badge2_unlock = true;
        }

        //badge4
        if (total_travel_carbon_footprint >= 100) {
            badge4_unlock = true;
        }

        //badge5
        if (getBatteryLevel() < 20) {
            badge5_unlock = true;
        }

        return v;
    }

    private int getBatteryLevel() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireContext().registerReceiver(null, iFilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }

    private void configureImageButton() {
        ImageButton btn1 = v.findViewById(R.id.badge_btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1_fragment badgeDialog = new dialog1_fragment();
                badgeDialog.show(getChildFragmentManager(), "dialog1_fragment");
            }
        });

        ImageButton btn2 = v.findViewById(R.id.badge_btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2_fragment badgeDialog = new dialog2_fragment();
                badgeDialog.show(getChildFragmentManager(), "dialog2_fragment");
            }
        });

        ImageButton btn3 = v.findViewById(R.id.badge_btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3_fragment badgeDialog = new dialog3_fragment();
                badgeDialog.show(getChildFragmentManager(), "dialog3_fragment");
            }
        });

        ImageButton btn4 = v.findViewById(R.id.badge_btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog4_fragment badgeDialog = new dialog4_fragment();
                badgeDialog.show(getChildFragmentManager(), "dialog4_fragment");
            }
        });

        ImageButton btn5 = v.findViewById(R.id.badge_btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog5_fragment badgeDialog = new dialog5_fragment();
                badgeDialog.show(getChildFragmentManager(), "dialog5_fragment");
            }
        });

        ImageButton btn6 = v.findViewById(R.id.badge_btn6);
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Stay tuned", Toast.LENGTH_LONG).show();
            }
        });
    }
}