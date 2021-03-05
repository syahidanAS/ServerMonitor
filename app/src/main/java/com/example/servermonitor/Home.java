package com.example.servermonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.util.ArrayList;

public class Home extends AppCompatActivity{
    private LineChart cpuChart;
    private TextView cpuTemprature,cpuUsage,memoryUsage;
    private static final String TAG = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Switch fan_switch = findViewById(R.id.fanSwitch);
        cpuChart = findViewById(R.id.cpuChart);
        cpuTemprature = findViewById(R.id.cpuTemp);
        cpuUsage = findViewById(R.id.cpuUsage);
        memoryUsage = findViewById(R.id.ramUsage);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification",
                    "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        DatabaseReference kost = FirebaseDatabase.getInstance().getReference("Kost");
        DatabaseReference NAS = kost.child("NAS");
        DatabaseReference cpu_temp = NAS.child("cpu_temp");
        DatabaseReference cpu_usage = NAS.child("cpu_usage");
        DatabaseReference memory_usage = NAS.child("memory_usage");
        DatabaseReference relay = NAS.child("relay");

        relay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long kondisi = (Long) dataSnapshot.getValue();

                if (kondisi == 1){
                    fan_switch.setChecked(true);
                }else{
                    fan_switch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
        cpu_temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double suhu = (Double) dataSnapshot.getValue();
                String suhu_cpu = String.valueOf(suhu);

                cpuTemprature.setText(suhu_cpu);

                if (suhu > 39){
                    showNotification();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
        cpu_usage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double cpuUsageNilai = (Double) dataSnapshot.getValue();
                String cpu_usage = String.valueOf(cpuUsageNilai);
                cpuUsage.setText(cpu_usage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
        memory_usage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double memUsg = (Double) dataSnapshot.getValue();
                String mem_usg = String.valueOf(memUsg);
                memoryUsage.setText(mem_usg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

        fan_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference Kost = database.getReference("Kost");
                    DatabaseReference NAS = Kost.child("NAS");
                    DatabaseReference relay = NAS.child("relay");
                    relay.setValue(1);
                }else{
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference Kost = database.getReference("Kost");
                    DatabaseReference NAS = Kost.child("NAS");
                    DatabaseReference relay = NAS.child("relay");
                    relay.setValue(0);
                }
            }
        });

        cpuChart.setDragEnabled(true);
        cpuChart.setScaleEnabled(false);

        ArrayList<Entry> yValue = new ArrayList<>();

        yValue.add(new Entry(0,60f));
        yValue.add(new Entry(1,50f));
        yValue.add(new Entry(2,1000f));
        yValue.add(new Entry(3,50f));
        yValue.add(new Entry(4,20f));
        yValue.add(new Entry(5,100f));
        yValue.add(new Entry(6,300f));

        LineDataSet set1 = new LineDataSet(yValue,"Data Set 1");

        set1.setFillAlpha(110);
        ArrayList<ILineDataSet>dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        cpuChart.setData(data);

    }
    public void showNotification(){
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(Home.this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, "My Notification");
        builder.setContentTitle("Critical Server Temprature");
        builder.setContentText("Please turn on the cooling system");
        builder.setSmallIcon(R.drawable.ic_alert_foreground);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Home.this);
        managerCompat.notify(1,builder.build());
    }

}