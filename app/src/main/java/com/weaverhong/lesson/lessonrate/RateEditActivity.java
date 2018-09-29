package com.weaverhong.lesson.lessonrate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateEditActivity extends AppCompatActivity {

    private TextView mTextView;
    private EditText mEditText;
    private Button mButton;

    private static final String EXTRA_RATE =
            "com.weaverhong.lesson.rate.ratefloat";
    private static final String EXTRA_TYPE =
            "com.weaverhong.lesson.rate.ratetype";

    public static Intent newIntent(Context packageContext, Float rawrate, String rawratetype) {
        Intent intent = new Intent(packageContext, RateEditActivity.class);
        intent.putExtra(EXTRA_RATE, rawrate);
        intent.putExtra(EXTRA_TYPE, rawratetype);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_edit);
        mTextView = (TextView) findViewById(R.id.edit_text);
        mEditText = (EditText) findViewById(R.id.edit_edittext);
        mButton = (Button) findViewById(R.id.edit_button);

        mEditText.setText("" + getIntent().getFloatExtra(EXTRA_RATE, -1));
        mTextView.setText("" + getIntent().getStringExtra(EXTRA_TYPE));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences p = getSharedPreferences("myrate", MODE_PRIVATE);
                    SharedPreferences.Editor ep = p.edit();
                    ep.putFloat(EXTRA_RATE, new Float(mEditText.getText().toString()));
                    ep.commit();
                } catch (Exception e) {
                    Log.e("MYEXCEPTION", e.toString());
                    Toast.makeText(RateEditActivity.this, "fuck", Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
