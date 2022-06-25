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
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
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
    private String selectedPath;
    String message;

    private static final int SELECT_VIDEO = 3;

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
                /*
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
*/
                //ImageHandler imageHandler = new ImageHandler();
                //imageHandler.execute();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
                //startActivityForResult(intent, 1);

                System.out.println("ok " + message);

                //UploadVideo uploadVideo = new UploadVideo();
                //uploadVideo.execute();


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
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(TopicActivity.this, "Progress: " + values[0], Toast.LENGTH_LONG).show();
        }
    }


    private class ConsumerHandle extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        public ConsumerHandle() {
        }

        @Override
        protected String doInBackground(Void... args) {
            try {
                consumer.start(subject);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            try {
                message = consumer.pull(subject);
            } catch (IOException | InterruptedException e) {
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
        System.out.println("IN ACTIVITY RESULT");
        if (resultCode == RESULT_OK) {
        //if (resultCode == 1) {
          //  if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedImageUri = data.getData();
                //selectedPath = getPath(selectedImageUri);
            try {
                selectedPath =PathUtil.getPath(this, selectedImageUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            UploadVideo uploadVideo = new UploadVideo();
                uploadVideo.execute();
          //  }
        }
        /*
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            ImageHandler imageHandler = new ImageHandler();
            imageHandler.execute();
        }*/
    }

    private class UploadVideo extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        public UploadVideo() {
        }

        @Override
        protected String doInBackground(Void... args) {

            try {
                publisher.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            publisher.push(subject, "1", selectedPath);
            return selectedPath;
        }

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(TopicActivity.this,
                    "Please wait...",
                    "Connecting to server...");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
           // inputmessage.setText(selectedPath);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(TopicActivity.this, "Progress: " + values[0], Toast.LENGTH_LONG).show();
        }

    }


    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        System.out.println("------>Document_id: "+document_id);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

/*
    private class ImageHandler extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        public ImageHandler() {
        }

        @Override
        protected String doInBackground(Void... args) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            //intent.setAction(Intent.ACTION_GET_CONTENT);
            //startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
            startActivityForResult(intent, 1);
            System.out.println("ok " + message);

            return selectedPath;
        }

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(TopicActivity.this,
                    "Please wait...",
                    "Connecting to server...");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            //inputmessage.setText(selectedPath);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(TopicActivity.this, "Progress: " + values[0], Toast.LENGTH_LONG).show();
        }

    }*/

}
