package com.makrusali.smartplug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button state,set_timer,count_timer,mode;
    TextView tvset_timer;
    Animation to_right,to_left,fade,x_to_right,x_to_left,x_fade;
    boolean state_button = false;
    int state_mode = 1;
    long start_timer_time = 0;
    CountDownTimer countDownTimer = null;
    long set_jam;
    long set_minute;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    boolean read = false;
    boolean dapat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        state = findViewById(R.id.state);
        set_timer = findViewById(R.id.button_set);
        count_timer = findViewById(R.id.count_timer);
        tvset_timer = findViewById(R.id.tv_set_timer);
        mode = findViewById(R.id.button_mode);
        to_right = AnimationUtils.loadAnimation(MainActivity.this,R.anim.to_right);
        to_left = AnimationUtils.loadAnimation(MainActivity.this,R.anim.to_left);
        fade = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade);
        x_to_right = AnimationUtils.loadAnimation(MainActivity.this,R.anim.x_to_right);
        x_to_left = AnimationUtils.loadAnimation(MainActivity.this,R.anim.x_to_left);
        x_fade = AnimationUtils.loadAnimation(MainActivity.this,R.anim.x_fade);

        myRef.child("timer_mode").child("state_timer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String snap = String.valueOf(snapshot.getValue());
                if(snap.equals("1")){
                    state_mode = 1;
                }else{
                    state_mode = 0;
                }
                switch (state_mode){
                    case 0://normal mode
                        if(read){
                            set_timer.startAnimation(to_right);
                            count_timer.startAnimation(to_left);
                            tvset_timer.startAnimation(fade);
                        }
                        read = true;
                        mode.setText("Normal Mode");
                        set_timer.setVisibility(View.INVISIBLE);
                        count_timer.setVisibility(View.INVISIBLE);
                        tvset_timer.setVisibility(View.INVISIBLE);
                        state_mode = 1;
                        break;
                    case 1://timer mode
                        mode.setText("Timer Mode");
                        set_timer.startAnimation(x_to_right);
                        count_timer.startAnimation(x_to_left);
                        tvset_timer.startAnimation(x_fade);

                        set_timer.setVisibility(View.VISIBLE);
                        count_timer.setVisibility(View.VISIBLE);
                        tvset_timer.setVisibility(View.VISIBLE);
                        state_mode = 0;
                        break;
                    default:
                        break;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String snap = String.valueOf(snapshot.getValue());
                if(snap.equals("1")){
                    state_button = true;
                }else{
                    state_button = false;
                }
                if(state_button){
                    state.setBackground(getResources().getDrawable(R.drawable.custom_button_on));
                    state.setTextColor(getResources().getColor(R.color.white));
                    state.setText("On");
                }else{
                    state.setBackground(getResources().getDrawable(R.drawable.custom_button_off));
                    state.setTextColor(getResources().getColor(R.color.black));
                    state.setText("Off");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        state.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                state_button = !state_button;
                if(state_mode == 0 && start_timer_time > 0){
                    state_mode = 0;
                    myRef.child("timer_mode").child("state_timer").setValue("0");
                    resetCountdown();
                    reset_timer();
                }
                if(state_button){
                    myRef.child("state").setValue(Integer.valueOf(1));
                    state.setBackground(getResources().getDrawable(R.drawable.custom_button_on));
                    state.setTextColor(getResources().getColor(R.color.white));
                    state.setText("On");
                }else{
                    myRef.child("state").setValue(Integer.valueOf(0));
                    state.setBackground(getResources().getDrawable(R.drawable.custom_button_off));
                    state.setTextColor(getResources().getColor(R.color.black));
                    state.setText("Off");
                }
            }
        });

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCountdown();
                reset_timer();
                switch (state_mode){
                    case 0://normal mode
                        mode.setText("Normal Mode");
                        set_timer.startAnimation(to_right);
                        count_timer.startAnimation(to_left);
                        tvset_timer.startAnimation(fade);

                        set_timer.setVisibility(View.INVISIBLE);
                        count_timer.setVisibility(View.INVISIBLE);
                        tvset_timer.setVisibility(View.INVISIBLE);
                        myRef.child("timer_mode").child("state_timer").setValue(0);
                        state_mode = 1;
                        break;
                    case 1://timer mode
                        mode.setText("Timer Mode");
                        set_timer.startAnimation(x_to_right);
                        count_timer.startAnimation(x_to_left);
                        tvset_timer.startAnimation(x_fade);

                        set_timer.setVisibility(View.VISIBLE);
                        count_timer.setVisibility(View.VISIBLE);
                        tvset_timer.setVisibility(View.VISIBLE);
                        myRef.child("timer_mode").child("state_timer").setValue(1);

                        state_mode = 0;
                        break;
                    default:
                        break;

                }
            }
        });

        mode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(state_mode == 0){
                    if(countDownTimer == null){
                        Toast.makeText(MainActivity.this,"Timer sudah Off",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,"Timer Direset / Off",Toast.LENGTH_SHORT).show();
                    }
                    tvset_timer.setText("00:00");
                    resetCountdown();
                }
                return true;
            }
        });


         myRef.child("timer_mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String snap = snapshot.child("last").getValue().toString();
                if(snap.equals("1") && dapat == false){
                    int state_timer = Integer.valueOf(snapshot.child("state_timer").getValue().toString());
                    if(state_timer > 0){
                        int set_timer = Integer.valueOf(snapshot.child("set_timer").getValue().toString());
                        int time_set_hour = Integer.valueOf(snapshot.child("time_set_hour").getValue().toString());
                        int time_set_minute = Integer.valueOf(snapshot.child("time_set_minute").getValue().toString());
                        int time_set_second = Integer.valueOf(snapshot.child("time_set_second").getValue().toString());

                        Date date = new Date();
                        int now_hour = date.getHours() - time_set_hour ;
                        int now_minute = date.getMinutes() - time_set_minute;
                        int now_second = date.getSeconds() - time_set_second;

                        start_timer_time = (set_timer * 60000) - (( (( now_hour * 60 ) + now_minute ) * 60000 ) + now_second * 1000) ;
                        startCountdown();
                        tvset_timer.setText(String.format(Locale.getDefault(),"%02d:%02d",( set_timer * 3600 ) / 3600,(( set_timer * 3600 ) % 3600 ) / 60));
                    }
                    dapat = true;
                    if(dapat == true){
                        myRef.child("timer_mode").child("last").setValue(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        set_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog setTimer = new TimePickerDialog(
                        MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                resetCountdown();
                                long set_jam;
                                long set_minute;
                                set_jam = i;
                                set_minute = i1;
                                myRef.child("timer_mode").child("set_timer").setValue(Long.valueOf((set_jam*60) + set_minute));
                                valueSet(set_jam,set_minute);
                                tvset_timer.setText(String.format(Locale.getDefault(),"%02d:%02d",set_jam,set_minute));
                                start_timer_time =  ( ( set_jam * 60 ) + set_minute ) * 60000;
                                startCountdown();
                                Date date2 = new Date();
                                int now_hour = date2.getHours();
                                int now_minute = date2.getMinutes();
                                int now_second = date2.getSeconds();
                                myRef.child("timer_mode").child("time_set_hour").setValue(now_hour);
                                myRef.child("timer_mode").child("time_set_minute").setValue(now_minute);
                                myRef.child("timer_mode").child("time_set_second").setValue(now_second);
                            }

                        },0,0,true
                );
                setTimer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                setTimer.show();
            }
        });
    }

    private void valueSet(long set_jam,long set_minute){
        this.set_jam = set_jam;
        this.set_minute = set_minute;
    }

    private void startCountdown(){
        countDownTimer = new CountDownTimer(start_timer_time, 1000) {

            public void onTick(long millisUntilFinished) {
                start_timer_time = millisUntilFinished;
                updateCountdownTimerText();
            }

            public void onFinish() {
                reset_timer();
                resetCountdown();
            }

        }.start();
    }
    private void resetCountdown(){
        start_timer_time = 0;
        myRef.child("timer_mode").child("set_timer").setValue(0);
        myRef.child("timer_mode").child("is_run").setValue(0);

        if(countDownTimer != null){
            countDownTimer.cancel();
            myRef.child("timer_mode").child("time_set_hour").setValue(0);
            myRef.child("timer_mode").child("time_set_minute").setValue(0);
            myRef.child("timer_mode").child("time_set_second").setValue(0);
        }
        countDownTimer = null;
        updateCountdownTimerText();

    }

    private void updateCountdownTimerText(){
        int hours = (int) (start_timer_time / 1000) / 3600;
        int minute = (int)((start_timer_time / 1000) % 3600 ) / 60;
        int second = (int) (start_timer_time / 1000) % 60;
        String timeFormat;
        timeFormat = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minute,second);
        count_timer.setText(timeFormat);
    }
    private  void reset_timer(){
        myRef.child("timer_mode").child("set_timer").setValue(0);
        myRef.child("timer_mode").child("state_timer").setValue(0);
        start_timer_time = 0;
        tvset_timer.setText("00:00");
    }
    @Override
    protected void onStop() {
        super.onStop();
        myRef.child("timer_mode").child("last").setValue(Integer.valueOf(1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.child("timer_mode").child("last").setValue(Integer.valueOf(1));
    }
}