package com.pranks.trialsih;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.pranks.trialsih.R;

public class MainActivity extends AppCompatActivity{

    //Declaring EditText
    private EditText editTextEmail;
    private EditText editTextSubject;
    private EditText editTextMessage;

    //Send button
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
