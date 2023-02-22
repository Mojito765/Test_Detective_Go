package com.doit.detective;

import static com.doit.detective.LocationService.myLocationList;
import static com.doit.detective.StatisticsActivity.chopsticks_weight;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.doit.detective.fragment.dialog7_fragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class GoActivity extends AppCompatActivity {

    private static final int MY_FINE_LOCATION_REQUEST = 99;
    private static final int MY_BACKGROUND_LOCATION_REQUEST = 100;

    public static double transportation_weight = 0;
    public static double finalDistance = 0;
    public static double final_carbon_footprint = 0;

    LocationService mLocationService = new LocationService();
    Intent mServiceIntent;

    Button startServiceBtn, stopServiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go);

//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // 取得SharedPreference
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        // 建立一個Key名稱為app_cfp的資料，預設值為0
        getPrefs.getFloat("app_cfp", 0);
        // 建立一個Key名稱為app_mileage的資料，預設值為0
        getPrefs.getFloat("app_mileage", 0);

        btnTransportation();

        startServiceBtn = findViewById(R.id.start_service_btn);
        stopServiceBtn = findViewById(R.id.stop_service_btn);

        // back_to_top start
        Button btnUp = findViewById(R.id.btn_up);
        NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nestedScrollView.fullScroll(View.FOCUS_UP);
//                nestedScrollView.scrollTo(0,0);
                appBarLayout.setExpanded(true);
            }
        });
        // back_to_top end

        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Vibrate
                setVibrate();

                if (ActivityCompat.checkSelfPermission(GoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        if (ActivityCompat.checkSelfPermission(GoActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {


                            AlertDialog alertDialog = new AlertDialog.Builder(GoActivity.this).create();
                            alertDialog.setTitle(Html.fromHtml(getString(R.string.ask_background_location_permission)));
                            alertDialog.setMessage(getString(R.string.background_location_permission_message));

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Keep Only-while-using Access",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            startService();
                                            dialog.dismiss();
                                        }
                                    });

                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Setup All-the-time Access",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestBackgroundLocationPermission();
                                            dialog.dismiss();
                                        }
                                    });

                            alertDialog.show();


                        } else if (ActivityCompat.checkSelfPermission(GoActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            startService();
                        }
                    } else {
                        startService();
                    }

                } else if (ActivityCompat.checkSelfPermission(GoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(GoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                        AlertDialog alertDialog = new AlertDialog.Builder(GoActivity.this).create();
                        alertDialog.setTitle(R.string.ask_location_permission);
                        alertDialog.setMessage(getString(R.string.location_permission_message));

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestFineLocationPermission();
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.show();

                    } else {
                        requestFineLocationPermission();
                    }
                }

            }

        });

        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVibrate();
                stopService();
            }
        });

        Toolbar myChildToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

    }

    //01/24 Transportation
    private void btnTransportation() {
        MaterialButtonToggleGroup materialButtonToggleGroup = findViewById(R.id.toggleButton);

        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    setVibrate();
                    if (checkedId == R.id.trans1) {
                        transportation_weight = 0.06;
                    } else if (checkedId == R.id.trans2) {
                        transportation_weight = 0.046;
                    } else if (checkedId == R.id.trans3) {
                        transportation_weight = 0.173;
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        Toast.makeText(this, Integer.toString(requestCode), Toast.LENGTH_LONG).show();

        if (requestCode == MY_FINE_LOCATION_REQUEST) {

            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestBackgroundLocationPermission();
                    }
                }

            } else {
                Toast.makeText(this, "ACCESS_FINE_LOCATION permission denied", Toast.LENGTH_LONG).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:com.doit.detective")
                    ));
                }
            }

        } else if (requestCode == MY_BACKGROUND_LOCATION_REQUEST) {

            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.background_location_permission_granted, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.background_location_permission_denied, Toast.LENGTH_LONG).show();
            }
        }

    }

    private void setVibrate() {
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(50);
    }

    private void startService() {
        mLocationService = new LocationService();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (!Utility.isMyServiceRunning(mLocationService.getClass(), this)) {
            startService(mServiceIntent);
            setRecordTV();
            Toast.makeText(this, "Start recording", Toast.LENGTH_SHORT).show();
        } else {
            setRecordTV();
            Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopService() {
        mLocationService = new LocationService();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (Utility.isMyServiceRunning(mLocationService.getClass(), this)) {
            stopService(mServiceIntent);
            setNoRecordTV();
            Toast.makeText(this, "Stopping", Toast.LENGTH_SHORT).show();
            saveTravel();
        } else {
            Toast.makeText(this, "Not recording", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                MY_BACKGROUND_LOCATION_REQUEST);
    }

    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, MY_FINE_LOCATION_REQUEST);
    }

    public void saveTravel() {
        try {
            for (int i = 0; i < myLocationList.size(); i += 2) {
                finalDistance += LocationService.GetDistanceFromLatLonInKm(myLocationList.get(i), myLocationList.get(i + 1),
                        myLocationList.get(i + 2), myLocationList.get(i + 3));
            }
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("OutOfBounds");
        }

        //step3 setText
        setResultTV();

        //clear record
        myLocationList.clear();
        finalDistance = 0;
    }

    //step2
    private void setRecordTV() {
        TextView tvStatus = findViewById(R.id.record_status);

//        setText
        tvStatus.setText("Recording");
    }

    private void setNoRecordTV() {
        TextView tvStatus = findViewById(R.id.record_status);

//        setText
        tvStatus.setText("Stopped");
    }

    //step3
    private void setResultTV() {
        TextView tvDistance = findViewById(R.id.carbon_footprint);
        TextView tvTotalDistance = findViewById(R.id.all_move);
        TextView tvTotalFootprint = findViewById(R.id.all_carbon_footprint);
        TextView tvTotalChopsticks = findViewById(R.id.chopsticks);

//        calculate carbon footprint
        final_carbon_footprint = finalDistance * transportation_weight;

//        calculate app total
        // 取得SharedPreference
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        // 取得Key的資料
        float app_cfp = getPrefs.getFloat("app_cfp", 0);
        float app_mileage = getPrefs.getFloat("app_mileage", 0);
        // 取得Editor
        SharedPreferences.Editor editor = getPrefs.edit();
        // 將user_cfp的值設為cfp加總
        float user_cfp = (float) (app_cfp + final_carbon_footprint);
        editor.putFloat("app_cfp", user_cfp);
        // 將user_mileage的值設為mileage加總
        float user_mileage = (float) (app_mileage + finalDistance);
        editor.putFloat("app_mileage", user_mileage);
        // 套用變更，一定要apply才會生效哦
        editor.apply();

//        round off
        double round_final_carbon_footprint = Math.round(final_carbon_footprint * 10.0) / 10.0;
        double round_finalDistance = Math.round(finalDistance * 10.0) / 10.0;
        double myChopsticks = final_carbon_footprint / chopsticks_weight;

//        Double.toString
        String fCF = Double.toString(final_carbon_footprint);
        String fD = Double.toString(finalDistance);
        String tD = Double.toString(round_finalDistance);
        String tF = Double.toString(round_final_carbon_footprint);
        String tC = Integer.toString((int) myChopsticks);

//        setText
        tvTotalDistance.setText(tD);
        tvTotalFootprint.setText(tF);
        tvTotalChopsticks.setText(tC);
        tvDistance.setText(getString(R.string.calculate_result, fD, fCF));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                dialog7_fragment badgeDialog = new dialog7_fragment();
                badgeDialog.show(getSupportFragmentManager(), "dialog7_fragment");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}