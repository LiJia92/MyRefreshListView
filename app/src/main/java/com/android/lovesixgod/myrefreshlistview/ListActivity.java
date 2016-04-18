package com.android.lovesixgod.myrefreshlistview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jaceli on 2016-04-18.
 */
public class ListActivity extends AppCompatActivity implements RefreshListView.OnRefreshListener {

    private RefreshListView listView;
    private List<String> data;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (RefreshListView) findViewById(R.id.refresh_list);
        String[] items = new String[]{"hello world", "hello world", "hello world", "hello world",
                "hello world", "hello world", "hello world", "hello world", "hello world",
                "hello world", "hello world", "hello world", "hello world", "hello world",};
        this.data = new ArrayList<>(Arrays.asList(items));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.data);
        listView.setAdapter(adapter);
        listView.setRefreshListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListActivity.this, data.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    data.add(0, "new data");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            listView.setSelection(0);

                            listView.setOnRefreshComplete();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
