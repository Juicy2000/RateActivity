package com.swufe.rateactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);

        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);

        Log.i(TAG, "onCreate: sp dollarRate=" + dollarRate);
        Log.i(TAG, "onCreate: sp euroRate=" + euroRate);
        Log.i(TAG, "onCreate: sp wonRate=" + wonRate);
        //将新设置的汇率写到SP里
        SharedPreferences sharedPreferences1 = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("dollar_rate",dollarRate);
        editor.putFloat("euro_rate",euroRate);
        editor.putFloat("won_rate",wonRate);
        editor.commit();
        Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.show);

        Log.i(TAG, "onClick: ");
        String str = rmb.getText().toString();
        Log.i(TAG, "onClick: get str=" + str);

        float r = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            //用户没有输入内容
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "onClick: r=" + r);
        //计算
        if(btn.getId()==R.id.dollar){
            show.setText(String.valueOf(r*dollarRate));
        }else if(btn.getId()==R.id.euro){
            show.setText(String.valueOf(r*euroRate));
        }else{
            show.setText(String.valueOf(r*wonRate));
        }
    }

    public class RateActivity extends AppCompatActivity implements Runnable{
        private String inputStream2String(InputStream inputStream) throws IOException {
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(inputStream, "gb2312");
            while (true) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        }

        public void run() {
            Log.i(TAG, "run: run()......");
            URL url = null;
            Document doc = Jsoup.parse(html);
            Bundle bundle = new Bundle();
            Document document = null;
            try {
                String url = "http://www.usd-cny.com/bankofchina.htm";
                doc = Jsoup.connect(url).get();
                Log.i(TAG, "run: " + doc.title());
                Element tables = doc.getElementsByTag("table");

                Element table6 = tables.get(5);
                //Log.i(TAG, "run: table6=" + table6);
                //获取TD中的数据
                Element tds = table6.getElementsByTag("td");
                for(int i=0;i<tds.size();i+=8){
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i+5);

                    String str1 = td1.text();
                    String val = td2.text();

                    Log.i(TAG, "run: " + str1 + "==>" + val);

                    float v = 100f / Float.parseFloat(val);
                    if("美元".equals(str1)){
                        bundle.putFloat("dollar-rate", v);
                    }else if("欧元".equals(str1)){
                        bundle.putFloat("euro-rate", v);
                    }else if("韩国元".equals(str1)){
                        bundle.putFloat("won-rate", v);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage(5);
                msg.obj = bundle;
                handler.sendMessage(msg);
                handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.what==5){
                            Bundle bdl = (Bundle) msg.obj;
                            dollarRate = bdl.getFloat("dollar-rate");
                            euroRate = bdl.getFloat("euro-rate");
                            wonRate = bdl.getFloat("won-rate");

                            Log.i(TAG, "handleMessage: dollarRate:" + dollarRate);
                            Log.i(TAG, "handleMessage: euroRate:" + euroRate);
                            Log.i(TAG, "handleMessage: wonRate:" + wonRate);
                            Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                        }
                        super.handleMessage(msg);
                    }
                };
            }
            //保存更新的日期
            SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.putString("update_date",todayStr);
            editor.apply();
            try {
                url = new URL("http://www.usd-cny.com/icbc.htm");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream in = http.getInputStream();

                String html = inputStream2String(in);
                Log.i(TAG, "run: html=" + html);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(int i=1;i<3;i++){
                Log.i(TAG, "run: i=" + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }

//获取Msg对象，用于返回主线程
            Message msg = handler.obtainMessage(5);
//msg.what = 5;
            msg.obj = "Hello from run()";
            handler.sendMessage(msg);
        }
        Handler handler;
        //开启子线程
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    String str = (String) msg.obj;
                    Log.i(TAG, "handleMessage: getMessage msg = " + str);
                    show.setText(str);
                }
                super.handleMessage(msg);
            }
        }
        Thread t = new Thread(this);
        t.start();

    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        },3000);//3000毫秒后执行，即3秒跳转
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            //点击后的事件处理，可填入打开配置汇率页面的代码
        }

        return super.onOptionsItemSelected(item);
    }
    public void openOne(View btn){
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);

        Log.i(TAG, "openOne: dollarRate=" + dollarRate);
        Log.i(TAG, "openOne: euroRate=" + euroRate);
        Log.i(TAG, "openOne: wonRate=" + wonRate);

        //startActivity(config);
        startActivityForResult(config,1);

    }}
