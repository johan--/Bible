package com.papa.bible;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBibleTv;
    private TextView mAboutTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBibleTv = (TextView) findViewById(R.id.epub_tv);
        mAboutTv = (TextView) findViewById(R.id.about_tv);
        mBibleTv.setOnClickListener(this);
        mAboutTv.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_tv: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.epub_tv:
                Intent intent = new Intent(this, EpubActivity.class);
                startActivity(intent);
                break;
        }
    }
}
