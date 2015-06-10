package com.harmazing.aixiumama.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.harmazing.aixiumama.adapter.StickerSetAdapter;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.utils.GridViewUtility;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.umeng.analytics.MobclickAgent;

public class StickerSetActivity extends BaseActivity {

    GridView gridView;
    StickerSetAdapter stickerSetAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_set);
        gridView = (GridView)findViewById(R.id.gridview);
        if(CuteService.stickersJSONarray.length() > 0) {
            stickerSetAdapter = new StickerSetAdapter(getApplication(), CuteService.stickersJSONarray);
            gridView.setAdapter(stickerSetAdapter);
            GridViewUtility.setGridViewHeightByMySelf(gridView, BitmapUtil.dip2px(getApplicationContext(), 110));
        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sticker_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
