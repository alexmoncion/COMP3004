package com.endgame.endgame;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.endgame.chess.GameFactory;

public class ScenariosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenarios);
        LinearLayout buttonHolder = findViewById(R.id.scenarioButtonHolder);
        Button button;
        for (String scenario : GameFactory.scenarioNames.keySet()) {
            button = new Button(buttonHolder.getContext());
            button.setText(GameFactory.scenarioNames.get(scenario));
            button.setTag(scenario);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE",v.getTag().toString());
                    setResult(2, intent);
                    finish();
                }
            });
            buttonHolder.addView(button);

        }
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        setResult(0);
        super.onBackPressed();  // optional depending on your needs
    }

}
