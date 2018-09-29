package com.weaverhong.lesson.lessonrate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private TextView mText;
    private Button mButtonEuro;
    private Button mButtonDollar;
    private Button mButtonWon;
    private SharedPreferences p = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.input1);
        mText = (TextView) findViewById(R.id.text1);
        mButtonDollar = (Button) findViewById(R.id.button_dollar);
        mButtonEuro = (Button) findViewById(R.id.button_euro);
        mButtonWon = (Button) findViewById(R.id.button_won);

        try {
            p = getSharedPreferences("myrate", MODE_PRIVATE);
        } catch (Exception e) {
            Log.e("MYEXCEPTION", "error get sharedpreference");
            Toast.makeText(MainActivity.this, "ERROR GETTING SHARED PREFERENCE!", Toast.LENGTH_SHORT).show();
        }

        mButtonEuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIllegal()) {
                    Log.e("MYEXCEPTION", "ill input");
                    Toast.makeText(MainActivity.this, "bad input !!",Toast.LENGTH_SHORT).show();
                } else {
                    Float f = new Float(mEditText.getText().toString());
                    Float rate = p.getFloat("euro",-1);
                    Float result = f * rate;
                    new DecimalFormat("#.00").format(result);
                    String r = "" + result;
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
                    Float rate = p.getFloat("dollar",-1);
                    Float result = f * rate;
                    new DecimalFormat("#.00").format(result);
                    String r = "" + result;
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
                    Float rate = p.getFloat("won",-1);
                    Float result = f * rate;
                    new DecimalFormat("#.00").format(result);
                    String r = "" + result;
                    mText.setText(r);
                }
            }
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String type = "";
        switch (item.getItemId()) {
            case R.id.menu_item1:
                type = "euro";
                break;
            case R.id.menu_item2:
                type = "dollar";
                break;
            case R.id.menu_item3:
                type = "won";
                break;
            default:
        }

        Intent intent = RateEditActivity.newIntent(
                MainActivity.this,
                p.getFloat(type,-1),
                type);
        startActivity(intent);

        return true;
    }
}
