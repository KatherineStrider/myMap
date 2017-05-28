package com.example.kate.mymaps;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kate on 28.05.2017.
 */

public class WidgetConfig extends AppCompatActivity {

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent rIntent = null;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("WidgetConfig", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_widget);

        button = (Button) findViewById(R.id.button2);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        rIntent = new Intent();
        rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        setResult(RESULT_CANCELED, rIntent);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v == null) {
                    finish();
                    return;
                }

                setResult(RESULT_OK, rIntent);
                finish();
            }
        };

        button.setOnClickListener(onClickListener);
    }
}
