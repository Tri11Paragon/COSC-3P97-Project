package com.mouseboy.finalproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mouseboy.finalproject.server.ServerApi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        findViewById(R.id.button).setOnClickListener(e ->
//                ServerApi.root(this,
//                        ((TextView)findViewById(R.id.textView))::setText,
//                        err -> ((TextView)findViewById(R.id.textView)).setText("Something went wrong: "+err.getMessage())));

        TextView textView = findViewById(R.id.textView);
        findViewById(R.id.button).setOnClickListener(e ->
                ServerApi.meow(this,
                        "username thing",
                        textView::setText,
                        err -> textView.setText(err.getMessage())
                )
        );
    }
}