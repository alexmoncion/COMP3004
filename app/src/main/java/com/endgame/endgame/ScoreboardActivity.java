package com.endgame.endgame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class ScoreboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

//        LinearLayout linearLayout = findViewById(R.id.ScoreBoard);

        // some wizardry to get the data passed in from mainactivity
        ArrayList<String> rawDat;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                rawDat = null;
            } else {
                rawDat = extras.getStringArrayList("ScoreboardData");
            }
        } else {
            rawDat = (ArrayList<String>) savedInstanceState.getSerializable("ScoreboardData");
        }

        // append a new text view for each piece of data onto scoreboard
//        String[] parsedData = rawDat.split("\\r?\\n");
        int i = 0;
        if (rawDat != null) {

            Iterator<String> iterator = rawDat.iterator();
            LinearLayout linearLayout = findViewById(R.id.ScoreBoard);
            while (iterator.hasNext()) {
                LinearLayout linearLayout2 = new LinearLayout(getApplicationContext());

                for(int col = 0; col < 3; col++) {
                    String data = iterator.next();
                    TextView textView = new TextView(this);
                    textView.setText(data);
                    if (i % 2 == 0) {
                        textView.setBackgroundColor(Color.parseColor("#c39d7f"));
                        textView.setTextColor(Color.parseColor("#FFFFFF"));
                    } else {
                        textView.setBackgroundColor(Color.parseColor("#e9d9be"));
                        textView.setTextColor(Color.parseColor("#000000"));
                    }
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(18);

                    linearLayout2.addView(textView);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

                }
                linearLayout.addView(linearLayout2);

                i++;
            }
        }

    }
}
