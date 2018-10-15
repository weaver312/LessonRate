package com.weaverhong.lesson.lessonrate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Runnable{

    public static final String KEY_MONEY = "KEY_MONEY";

    private EditText mEditText;
    private TextView mText;
    private Button mButtonEuro;
    private Button mButtonDollar;
    private Button mButtonWon;
    private SharedPreferences p = null;
    private Handler mHandler;

    public static Intent newInstance(Context context, String name, String value) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("value", value);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.input1);
        mText = (TextView) findViewById(R.id.text1);
        mButtonDollar = (Button) findViewById(R.id.button_dollar);
        mButtonEuro = (Button) findViewById(R.id.button_euro);
        mButtonWon = (Button) findViewById(R.id.button_won);

        if (savedInstanceState != null) {
            mText.setText(""+savedInstanceState.getString(KEY_MONEY, "-1"));
        }

        try {
            p = getSharedPreferences("myrate", MODE_PRIVATE);
        } catch (Exception e) {
            Log.e("MYEXCEPTION", "error get sharedpreference");
            Toast.makeText(MainActivity.this, "ERROR GETTING SHARED PREFERENCE!", Toast.LENGTH_SHORT).show();
        }

        // 每日第一次去网上更新数据
        int lastday = p.getInt("REFRESHDATE",19700101);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        int currentday = new Integer(simpleDateFormat.format(date));
        if (lastday != currentday) {
            refreshdata();
            Toast.makeText(this,"UPDATE DATA AUTOMATICLY !!", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor ep = p.edit();
            ep.putInt("REFRESHDATE",currentday);
            Toast.makeText(this,"REFRESHDATE: " + currentday, Toast.LENGTH_LONG).show();
            ep.commit();
        } else {
            Toast.makeText(this,"HAVE UPDATED TODAY, WILL NOT UPDATE NOW !", Toast.LENGTH_LONG).show();
        }

        mButtonEuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIllegal()) {
                    Log.e("MYEXCEPTION", "ill input");
                    Toast.makeText(MainActivity.this, "bad input !!",Toast.LENGTH_SHORT).show();
                } else {
                    Float f = new Float(mEditText.getText().toString());
                    Float rate = p.getFloat("EUR",-1);
                    Float result = f * rate;
                    new DecimalFormat("#.00").format(result);
                    String r = new DecimalFormat("#.00").format(result);
                    mText.setText(r);
                }
            }
        });

        mButtonDollar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIllegal()) {
                    Log.e("MYEXCEPTION", "ill input");
                    Toast.makeText(MainActivity.this, "bad input !!",Toast.LENGTH_SHORT).show();
                } else {
                    Float f = new Float(mEditText.getText().toString());
                    Float rate = p.getFloat("USD",-1);
                    Float result = f * rate;
                    String r = new DecimalFormat("#.00").format(result);
                    mText.setText(r);
                }
            }
        });

        mButtonWon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIllegal()) {
                    Log.e("MYEXCEPTION", "ill input");
                    Toast.makeText(MainActivity.this, "bad input !!",Toast.LENGTH_SHORT).show();
                } else {
                    Float f = new Float(mEditText.getText().toString());
                    Float rate = p.getFloat("KRW",-1);
                    Float result = f * rate;
                    new DecimalFormat("#.00").format(result);
                    String r = new DecimalFormat("#.00").format(result);
                    mText.setText(r);
                }
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

    }

    private boolean checkIllegal() {
        try {
            Float f = new Float(mEditText.getText().toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(KEY_MONEY, (mText.getText().toString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String type = "";
        switch (item.getItemId()) {
            case R.id.menu_item1:
                type = "EUR";
                break;
            case R.id.menu_item2:
                type = "USD";
                break;
            case R.id.menu_item3:
                type = "KRW";
                break;
            case R.id.menu_item4:
                refreshdata();
                return true;
        }

        Intent intent = RateEditActivity.newIntent(
                MainActivity.this,
                p.getFloat(type,-1),
                type);
        startActivity(intent);

        return true;
    }

    private void refreshdata() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String[] targetrates = {"https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=CNY&to_currency=USD&apikey=5ZJSE6C84I3D161L",
                                        "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=CNY&to_currency=KRW&apikey=5ZJSE6C84I3D161L",
                                        "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=CNY&to_currency=EUR&apikey=5ZJSE6C84I3D161L"};
                    for (String s : targetrates) {
                        URL u = new URL(s);
                        HttpURLConnection http = (HttpURLConnection) u.openConnection();
                        InputStream in = http.getInputStream();
                        final String response = fromInputStreamtoString(in);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject j = new JSONObject(response).getJSONObject("Realtime Currency Exchange Rate");
//                                    Log.e("MYEXCEPTION",response);
//                                    Log.e("MYEXCEPTION",j.toString());
//                                    Toast.makeText(MainActivity.this,j.getString("3. To_Currency Code"),Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(MainActivity.this,j.getString("5. Exchange Rate"),Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor ep = p.edit();
                                    ep.putFloat(j.getString("3. To_Currency Code"),
                                                new Float(j.getString("5. Exchange Rate")));
                                    ep.commit();
                                } catch (Exception e) {
                                    Log.e("MYEXCEPTION",e.toString());
                                }
                            }
                        });
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"refresh complete!!",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("MYEXCEPTION",e.toString());
                }
            }
        }).start();
    }

    private static String fromInputStreamtoString(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }
    @Override
    public void run() {

    }
}
