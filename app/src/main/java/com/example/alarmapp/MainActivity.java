package com.example.alarmapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    private Calendar calendar;
    private TimePicker timePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.calendar = Calendar.getInstance();
        // 현재 날짜 표시
        displayDate();
        this.timePicker = findViewById(R.id.timePicker);

        findViewById(R.id.btnCalendar).setOnClickListener(mClickListener);
        findViewById(R.id.btnAlarm).setOnClickListener(mClickListener);

        // 텍스트뷰 받아와서
        textView = findViewById(R.id.timeview);
        // 쓰레드를 이용해서 시계표시
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Calendar calendar = Calendar.getInstance(); // 칼렌다 변수
                            int year = calendar.get(Calendar.YEAR); // 년
                            int month = calendar.get(Calendar.MONTH); // 월
                            int day = calendar.get(Calendar.DAY_OF_MONTH); // 일
                            int hour = calendar.get(Calendar.HOUR_OF_DAY); // 시
                            int minute = calendar.get(Calendar.MINUTE); // 분
                            int second = calendar.get(Calendar.SECOND); // 초

                            textView.setText(year + "년 " + (month + 1) + "월 " + day + "일\n" + hour + "시 " + minute + "분" + second + "초 \n");
                        }
                    });
                    try {
                        Thread.sleep(1000); // 1000 ms = 1초
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } // while
            } // run()
        }; // new Thread() { };
        thread.start();

    } //onCreate
    /* 날짜 표시 */
    private void displayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ((TextView) findViewById(R.id.txtDate)).setText(format.format(this.calendar.getTime()));
    }

    /* DatePickerDialog 호출 */
    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // 알람 날짜 설정
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, dayOfMonth);

                // 날짜 표시
                displayDate();
            }
        }, this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH), this.calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }
    /* 알람 등록 */
    private void setAlarm() {
        // 알람 시간 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.calendar.set(Calendar.HOUR_OF_DAY, this.timePicker.getHour());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.calendar.set(Calendar.MINUTE, this.timePicker.getMinute());
        }
        this.calendar.set(Calendar.SECOND, 0);

        // 현재일보다 이전이면 등록 실패
        if (this.calendar.before(Calendar.getInstance())) {
            Toast.makeText(this, "알람시간이 현재시간보다 이전일 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }
        // Receiver 설정
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알람 설정
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, this.calendar.getTimeInMillis(), pendingIntent);

        // Toast 보여주기 (알람 시간 표시)
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Toast.makeText(this, "Alarm : " + format.format(calendar.getTime()), Toast.LENGTH_LONG).show();
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCalendar:
                    // 달력
                    showDatePicker();

                    break;
                case R.id.btnAlarm:
                    // 알람 등록
                    setAlarm();

                    break;
            }
        }
    };

}// MainActivity
