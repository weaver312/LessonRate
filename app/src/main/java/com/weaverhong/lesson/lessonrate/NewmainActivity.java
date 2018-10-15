package com.weaverhong.lesson.lessonrate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class NewmainActivity extends AppCompatActivity {

    TextView mTextView;
    EditText mEditText;

    public static Intent newInstance(Context context, String name, String value) {
        Intent intent = new Intent(context, NewmainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("value", value);
        return intent;
    }

    @Override
    protected void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_newmain);
        mTextView = findViewById(R.id.newtext);
        mEditText = findViewById(R.id.newedittext);

        final String name = getIntent().getStringExtra("name");
        String value = getIntent().getStringExtra("value");
        final Float ratevalue = new Float(value);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEditText.getText()!=null) {
                    mTextView.setText(((mEditText.getText().toString().equals(""))?
                            0:mEditText.getText().toString()) +
                            name + " = " + (mEditText.getText().toString().equals("")?"0":""+(new Float(mEditText.getText().toString())*ratevalue)/100) + "人民币");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
