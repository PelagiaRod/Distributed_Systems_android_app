package com.example.ds_project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Random;

public class TopicActivity extends AppCompatActivity {
    //public static final String EXTRA_INPUTMESSAGE = "";

    private Button btn;
    private VideoView videoView;
    private static final String VIDEO_DIRECTORY = "/Images";
    private int GALLERY = 1, CAMERA = 2;

    EditText msg;
    Publisher publisher;
    String subject;
    String username;
    Consumer consumer;
    TextView textView;
    TextView inputmessage;
    // String usermessage;
    // String newmessage;
    Uri selectedImage;

    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        Intent myIntent = getIntent();
        subject = myIntent.getStringExtra(MainActivity.EXTRA_SUBJECT);
        username = myIntent.getStringExtra(MainActivity.EXTRA_USERNAME);
        //usermessage = myIntent.getStringExtra(this.EXTRA_INPUTMESSAGE);

        publisher = new Publisher(username);
        consumer = new Consumer(username);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(subject);

        msg = (EditText) findViewById(R.id.writeMessage);

        inputmessage = (TextView) findViewById(R.id.input_message);
        //inputmessage.setText(usermessage);

        ImageButton imgMsg = (ImageButton) findViewById(R.id.get_file);
        imgMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);




                /*
                showPictureDialog();
                publisher.push(subject, "1", "Image");
                Intent myIntent = new Intent(view.getContext(), TopicActivity.class);
                startActivity(myIntent);

                 */
            }

        });

        ImageButton textMsg = (ImageButton) findViewById(R.id.sendMsg);
        textMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //This is the async task
                PublisherHandle publisherHanle = new PublisherHandle();
                publisherHanle.execute();
            }
        });

        ImageButton refresh = (ImageButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                ConsumerHandle consumerHandle = new ConsumerHandle();
                consumerHandle.execute();

            }
        });
    }

    private class PublisherHandle extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        public PublisherHandle() {
        }

        @Override
        protected String doInBackground(Void... args) {
            String message = msg.getText().toString();

            System.out.println("ok " + message);

            try {
                publisher.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            publisher.push(subject, "2", message);
            return message;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TopicActivity.this,
                    "Please wait...",
                    "Connecting to server...");
        }

        @Override
        protected void onPostExecute(String result) {
            //This runs on the UI thread
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            //newmessage = inputmessage + "\n" +result;
            //Intent myIntent = new Intent(view.getContext(), TopicActivity.class);
            //myIntent.putExtra(EXTRA_INPUTMESSAGE, newmessage);
            //startActivity(myIntent);
            //inputmessage.setText(newmessage);
            //Log.d("MY_TAG","TEST");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //UI thread
            Toast.makeText(TopicActivity.this, "Progress: " + values[0], Toast.LENGTH_LONG).show();
        }
    }


    private class ConsumerHandle extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        public ConsumerHandle() {
        }

        @Override
        protected String doInBackground(Void... args) {
            message = "---> NOT OK";
            try {


                consumer.start(subject);

                // message = "--> OK";
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            try {
                System.out.println("////////////// -- subject " + subject);
                message = consumer.pull(subject);
                System.out.println("//////////////" + message);
            } catch (IOException | InterruptedException e) {
                System.out.println("////////////// -- nope");
                e.printStackTrace();
            }
            return message;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TopicActivity.this,
                    "Please wait...",
                    "Connecting to server...");
        }

        @Override
        protected void onPostExecute(String result) {
            //This runs on the UI thread
            // execution of result of Long time consuming operation
            progressDialog.dismiss();

            //newmessage = inputmessage + "\n" + result;
            //Intent myIntent = new Intent(view.getContext(), TopicActivity.class);
            //myIntent.putExtra(EXTRA_INPUTMESSAGE, newmessage);
            //startActivity(myIntent);
            inputmessage.setText(result);
            Log.d("MY_TAG", "TEST");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //UI thread
            Toast.makeText(TopicActivity.this, "Progress: " + values[0], Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            ImageHandler imageHandler = new ImageHandler();
            imageHandler.execute();
        }
    }

    private class ImageHandler extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        public ImageHandler() {
        }

        @Override
        protected String doInBackground(Void... args) {
            //String message = msg.getText().toString();

            System.out.println("ok " + message);

            try {
                publisher.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            publisher.push(subject, "1", selectedImage.toString());
            return selectedImage.toString();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TopicActivity.this,
                    "Please wait...",
                    "Connecting to server...");
        }

        @Override
        protected void onPostExecute(String result) {
            //This runs on the UI thread
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            //newmessage = inputmessage + "\n" +result;
            //Intent myIntent = new Intent(view.getContext(), TopicActivity.class);
            //myIntent.putExtra(EXTRA_INPUTMESSAGE, newmessage);
            //startActivity(myIntent);
            //inputmessage.setText(newmessage);
            //Log.d("MY_TAG","TEST");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //UI thread
            Toast.makeText(TopicActivity.this, "Progress: " + values[0], Toast.LENGTH_LONG).show();
        }

    }
}
