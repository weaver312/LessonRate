package com.weaverhong.lesson.lessonrate;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RateListActivity extends AppCompatActivity{

    MyAdapter mListAdapter;
    RecyclerView mListView;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);

        mListView = findViewById(R.id.ratelist);
        mListView.setLayoutManager(new LinearLayoutManager(this));

        mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Rate> rateList = new ArrayList<>();
                    Document doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/index.html").get();
                    Elements elements = doc.body().select("body > div > div.BOC_main > div.publish > div:nth-child(3) > table > tbody > tr");
                    for (Element element : elements) {
                        String name = element.getAllElements().get(1).text();
                        String value = element.getAllElements().get(6).text();
                        Rate r = new Rate(name, value);
                        rateList.add(r);
                    }
                    RateLab.list = rateList;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                } catch (Exception e) {
                    Log.e("MYLOG", e.toString());
                }
            }
        }).start();

        updateUI();
    }

    private void updateUI() {
        List<Rate> list = RateLab.list;

        if (mListAdapter == null) {
            mListAdapter = new MyAdapter(list);
            mListView.setAdapter(mListAdapter);
        } else {
            mListAdapter.setList(list);
            mListAdapter.notifyDataSetChanged();
        }

        mListAdapter = new MyAdapter(list);
        mListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends RecyclerView.Adapter<RateHolder> {

        private List<Rate> list;

        public void setList(List<Rate> list) {
            this.list = list;
        }

        public MyAdapter(List<Rate> list) { this.list = list; }

        @Override
        public RateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(RateListActivity.this);
            return new RateHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(RateHolder holder, int position) {
            Rate rate = list.get(position);
            holder.bind(rate);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class RateHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView v1;
        private TextView v2;
        private Rate r;

        public RateHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_rate, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            v1 = itemView.findViewById(R.id.rateitemname);
            v2 = itemView.findViewById(R.id.rateitemvalue);
        }

        public void bind(Rate rate) {
            r = rate;
            v1.setText(rate.getName());
            v2.setText("" + rate.getValue());
        }

        @Override
        public void onClick(View v) {
            Log.e("MYLOG short", "nmsl");

            // 启动新视图，或返回，携带点击的汇率
            // return rate value/name or start activity with rate value/name
            Intent intent = NewmainActivity.newInstance(RateListActivity.this,r.getName(),r.getValue());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(RateListActivity.this);
            builder.setTitle("删除");
            builder.setMessage("确定删除吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RateLab.delete(r.getName());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                    return;
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { return; }
            });
            builder.show();
            return true;
        }
    }
}
