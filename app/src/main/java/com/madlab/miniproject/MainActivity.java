package com.madlab.miniproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    Calendar cal = Calendar.getInstance();
    Button timeButton;
    Button dateButton;
    Button setReminderButton;
    EditText medicineNameET;
    int hour;
    int minute;
    int year;
    int month;
    int day;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeButton = findViewById(R.id.timeButton);
        dateButton = findViewById(R.id.dateButton);
        setReminderButton = findViewById(R.id.setReminderButton);
        medicineNameET = findViewById(R.id.medicineName);
        dbHandler = new DBHandler(MainActivity.this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Medicine Notification Channel", importance);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        }

        public void onTimeButtonClick(View view) {
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;
                    timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }
            };
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), hour, minute);
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, R.style.MyTimePickerStyle,onTimeSetListener, hour, minute, true);
            timePickerDialog.setTitle("Select time");
            timePickerDialog.show();
        }

        public void onDateButtonClick(View view) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                    year = selectedYear;
                    month = selectedMonth;
                    day = selectedDay;
                    cal.set(year, month, day);
                    dateButton.setText(SimpleDateFormat.getDateInstance().format(cal.getTime()));
                }
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.MyDatePickerStyle,onDateSetListener, year, month, day);
            datePickerDialog.setTitle("Select date");
            datePickerDialog.show();
        }

    public void onSetReminderButtonClick(View view) {
        cal.set(year, month, day, hour, minute);

        if(medicineNameET.getText().toString().matches("") || medicineNameET.getText().toString().matches("\\s+")) {
            Toast.makeText(MainActivity.this, "Medicine name cannot be left blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if(dateButton.getText().toString().matches("Select date")) {
            Toast.makeText(MainActivity.this, "Date cannot be left blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if(timeButton.getText().toString().matches("Select time")) {
            Toast.makeText(MainActivity.this, "Time cannot be left blank", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar currtime = Calendar.getInstance();
        if(cal.compareTo(currtime) < 0) {
            Toast.makeText(MainActivity.this, "Reminder cannot be set for a time in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        Random r = new Random();
        int requestCode = Math.abs(r.nextInt());
        int notificationId = Math.abs(r.nextInt());
        Intent notificationIntent = new Intent(MainActivity.this, MyNotificationPublisher.class);
        notificationIntent.putExtra("notificationId", notificationId);
        notificationIntent.putExtra("requestCode", requestCode);
        notificationIntent.putExtra("medicineName", medicineNameET.getText().toString());
        notificationIntent.putExtra("time", String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        notificationIntent.putExtra("date", SimpleDateFormat.getDateInstance().format(cal.getTime()));
        notificationIntent.putExtra("content", "Remember to take " + medicineNameET.getText().toString() + " at " + String.format(Locale.getDefault(), "%02d:%02d", hour, minute) + " on " + SimpleDateFormat.getDateInstance().format(cal.getTime()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, requestCode, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        displayDialog();
        dbHandler.insertReminder(requestCode, medicineNameET.getText().toString(), SimpleDateFormat.getDateInstance().format(cal.getTime()), String.format(Locale.getDefault(), "%02d:%02d", hour, minute), "No");

        medicineNameET.getText().clear();
        dateButton.setText("Select date");
        timeButton.setText("Select time");
    }

    private void displayDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Reminder confirmation");
        alertBuilder.setMessage("Reminder to take " + medicineNameET.getText().toString() + " at " + String.format(Locale.getDefault(), "%02d:%02d", hour, minute) + " on " + SimpleDateFormat.getDateInstance().format(cal.getTime()) + " has successfully been set.");
        alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertBuilder.show();
    }

    public void onCancelReminderButtonClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Reminder");
        builder.setMessage("Enter ID of reminder to be deleted");
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent  = new Intent(MainActivity.this, MyNotificationPublisher.class);
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, Integer.parseInt(input.getText().toString()), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                DBHandler dbhandler = new DBHandler(MainActivity.this);
                if(dbhandler.checkReminder(Integer.parseInt(input.getText().toString()))) {
                    alarmManager.cancel(pendingIntent);
                    dbhandler.deleteReminder(Integer.parseInt(input.getText().toString()));
                    Toast.makeText(MainActivity.this, "Reminder has been cancelled", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Reminder does not exist", Toast.LENGTH_SHORT).show();
                }
                } catch(java.lang.NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Integer value is out of range", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    public void onShowRemindersButtonClick(View view) {
        Intent i = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(i);
    }
}