package com.example.ds_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//import com.example.ds_android_app.databinding.ActivityMainBinding;

import java.io.IOException;

public class TopicActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    //private ActivityMainBinding binding;

    //paradeigma lab
    TextView status;
    EditText sleep;
    private static final int UPDATE_PROGRESS = 1;
    private static final int FINAL_RESULT = 2;

    @SuppressLint("HandlerLeak")
    private final Handler myHandler = new Handler() {
        //h handleMessage pairnei ena mhnyma, to what
        public void handleMessage(Message msg) {
            try{    //analogws to mhnyma kane thn analogh douleia
                int what = msg.what;

                Object obj = msg.obj;
                if (what == UPDATE_PROGRESS){

                    status.setText("Stage 1");


                }else if (what == FINAL_RESULT){
                    status.setText((String)obj);
                }

                Log.d("MY_TAG","Msg.what:"+ msg.what);

            }catch (Exception exp){
                Log.d("MY_TAG",exp.getMessage());
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        Intent myIntent = getIntent();
        String subject = myIntent.getStringExtra(MainActivity.EXTRA_SUBJECT);
        String username = myIntent.getStringExtra(MainActivity.EXTRA_USERNAME);

        Publisher publisher = new Publisher(username);

        Consumer consumer = new Consumer(username);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(subject);

        EditText msg = (EditText) findViewById(R.id.writeMessage);

        ImageButton imgMsg = (ImageButton) findViewById(R.id.get_file);
        imgMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                try {
                    publisher.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                publisher.push(subject, "1", "Image");
                Intent myIntent = new Intent(view.getContext(), TopicActivity.class);
                startActivity(myIntent);
            }

        });

        //TODO: keep list of consumers for each topic
        ImageButton textMsg = (ImageButton) findViewById(R.id.sendMsg);
        textMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String message = msg.getText().toString();

                System.out.println("ok " + message);

                try {
                    publisher.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                publisher.push(subject, "2", message);
                //Intent myIntent = new Intent(view.getContext(), TopicActivity.class);
                //startActivity(myIntent);
            }
        });

        ImageButton refresh = (ImageButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    System.out.println("ok");

                    consumer.pull(subject);
                } catch (IOException e) {
                    e.printStackTrace();
                }



                TextView textView = (TextView) findViewById(R.id.input_message);
                textView.setText(subject);

            }

        });



    }
}