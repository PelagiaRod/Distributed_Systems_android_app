package com.example.ds_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class First_Activity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "com.example.ds_project.EXTRA_SUBJECT";
    public static final String EXTRA_USERNAME = "com.example.ds_project.EXTRA_USERNAME";

    private ArrayList<String> data = new ArrayList<String>();
    Node node = new Node();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ListView lv = (ListView) findViewById(R.id.listview);

        Bundle extras = getIntent().getExtras();
        String theuser = extras.getString("NEEDED_USERNAME");

        generateListContent();

        lv.setAdapter(new First_Activity.MyListAdaper(this, R.layout.list_item, data));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subject = data.get(position);

                //String username = "Publisher";

                Intent myIntent = new Intent(view.getContext(), TopicActivity.class);
                myIntent.putExtra(EXTRA_SUBJECT, subject);
                myIntent.putExtra(EXTRA_USERNAME, theuser);
                startActivity(myIntent);
                //TODO: change page and keep subject -> ok

                //TODO: 2nd page run consumer with parameter topic, show topic, messages from consumer

                //publisher text input file input kalei push function prepei na pairnei parametro to message


                //Toast.makeText(MainActivity.this, "List item was clicked at " + position + data.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateListContent() {

        node.loadTopics();

        for(int i = 0; i < node.topics.size(); i++) {
            data.add(node.topics.get(i).getChannelName());
        }
    }
    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            First_Activity.ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                First_Activity.ViewHolder viewHolder = new First_Activity.ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (First_Activity.ViewHolder) convertView.getTag();
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
    }
}

/*package com.example.ds_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class First_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }
}*/