package com.swufe.rateactivity.;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class RateCalcActivity extends AppCompatActivity {

    String TAG = "rateCalc";
    float rate = 0f;
    EditText inp2;
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_calc);
        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("rate",0f);

        Log.i(TAG, "onCreate: title = " + title);
        Log.i(TAG, "onCreate: rate=" + rate);
        ((TextView)findViewById(R.id.title2)).setText(title);
        inp2 = (EditText)findViewById(R.id.inp2);
        inp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView show = (TextView) RateCalcActivity.this.findViewById(R.id.show2);
                if(s.length()>0){
                    float val = Float.parseFloat(s.toString());
                    show.setText(val + "RMB==>" + (100/rate*val));
                }else{
                    show.setText("");
                }

            }
            listItems.remove(position);
listItemAdapter.notifyDataSetChanged();
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
                //删除操作
                //构造对话框进行确认操作
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "onClick: 对话框事件处理");
                        listItems.remove(position);
                        listItemAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("否",null);
                builder.create().show();
                Log.i(TAG, "onItemLongClick: size=" + listItems.size());

                return true;
            }
        });
    }
}
