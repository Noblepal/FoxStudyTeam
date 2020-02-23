package com.trichain.foxstudyteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kobakei.ratethisapp.RateThisApp;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RateThisApp.onStart(this);
        // Custom condition: 3 days and 5 launches
        RateThisApp.Config config = new RateThisApp.Config(0, 4);
        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(this);
//        startService(new Intent(getApplicationContext(), AdService.class));
    }
    public void goToNext(View view){
        Intent intent=new Intent(MainActivity.this, ItemListActivity.class);
        intent.putExtra("category",getViewWordId(view));
        startActivity(intent);
    }
    public String getViewWordId(View v){
        String name5 = null;
        Field[] campos = R.id.class.getFields();
        for(Field f:campos){
            try{
                if(v.getId()==f.getInt(null)){
                    name5 = f.getName();
                    break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        Log.e(TAG, "getViewWordId: "+name5 );
        return name5;
    }
    public void back(View view){
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        Intent intent = new Intent(MainActivity.this, MainService.class);

        startService(intent);
        super.onDestroy();
    }
}
