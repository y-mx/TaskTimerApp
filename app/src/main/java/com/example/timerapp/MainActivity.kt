package com.example.timerapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {
    private lateinit var timerText: TextView
    private lateinit var startButton: Button
    private lateinit var timer: CountDownTimer
    private var isRunning:Boolean = false;
    private var setTime:Long=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        timerText=findViewById(R.id.textView)
        startButton = findViewById<Button>(R.id.button)
        val timeText: EditText = findViewById<EditText>(R.id.timeinput)
        startButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view:View?){
                if(!isRunning){
                    OnStartTimer(timeText.text.toString().toLong()*60000)
                    setTime = timeText.text.toString().toLong()
                    startButton.text = "end timer"
                    timeText.setText("")
                }else{
                    OnEndTimer()
                    startButton.text = "set timer"
                }
            }
        })
    }

    fun goToDo(view: View){
        val intent = Intent(this, ToDoList::class.java).apply{}
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer"
            val descriptionText = "Timer notification channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("my_channel1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun OnStartTimer(time: Long){
        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "time remaining: " + ((millisUntilFinished / 1000)/60).toString().padStart(2, '0') + ":" + ((millisUntilFinished / 1000)%60).toString().padStart(2, '0')
            }
            override fun onFinish() {
                timerText.text = "done!"
                startButton.text = "set timer"
            }
        }
        isRunning=true
        timer.start()
    }

    fun OnEndTimer(){
        timer.cancel()
        timerText.text = "-"
        isRunning=false;
        val intent = Intent(this, MainActivity::class.java).apply{}
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "my_channel1")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Timer is done")
            .setContentText("Your timer for $setTime minutes is done")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder.build());
    }
}