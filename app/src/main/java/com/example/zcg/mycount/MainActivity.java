package com.example.zcg.mycount;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private EditText txtmoney; //充值金额
    private Button search,recharge,record; //两个按钮
    private TextView banlance; //余额
    Spinner spinner;
    String selected;
    private String databaseFile = "/acount.db";//数据库名
    private SQLiteDatabase acountDatabase;
    private Acount acount;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Cursor cursor;
    private String yumoney;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar(); //获取标题栏
        if (actionbar != null) {
            actionbar.hide();  //隐藏原有的标题栏
        }
        //初始化控件
        txtmoney=(EditText) findViewById(R.id.moneytxt);
        banlance=(TextView)findViewById(R.id.balance);
        spinner=(Spinner)findViewById(R.id.spinner);
        search=(Button)findViewById(R.id.search);
        recharge=(Button)findViewById(R.id.chong); //
        record=(Button)findViewById(R.id.record);
        selected=spinner.getSelectedItem().toString(); //获取当前选中的小车ID
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 1:
                        acount=(Acount)msg.obj;

                        banlance.setText(acount.getMoney());
                        yumoney=acount.getMoney();

                        break;
                    case 2:
                        JSONObject jsonObject=(JSONObject)msg.obj;
                        try {
                            if (jsonObject.getString("result").equals("t"))

                            {

                                Toast.makeText(MainActivity.this,"充值成功！", Toast.LENGTH_SHORT).show();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        JSONObject jsonObject1=(JSONObject)msg.obj;
                        try {


                                banlance.setText(jsonObject1.getString("result"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;


                }}
        };
        new GetInfoThread(handler,selected).start();

        // 初始化数据库
        acountDatabase = SQLiteDatabase.openOrCreateDatabase(
                this.getFilesDir().toString()+databaseFile, null);
        // 初始化数据表信息
        createacountDatabase(acountDatabase);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                new GetInfoThread(handler,adapterView.getItemAtPosition(i).toString()).start();  //启动线程
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected=spinner.getSelectedItem().toString(); //获取当前选中的小车ID
               //显示余额的值
                new GetInfoThread(handler,selected).start();  //启动线程

            }
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected=spinner.getSelectedItem().toString();
                String czmoney=txtmoney.getText().toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String time = simpleDateFormat.format(date);
                int num=0;
                num=Integer.parseInt(txtmoney.getText().toString());
                String insertsql = "insert into rechard values(null, '" + selected +
                        "', " + num+ ", 'user1', '" +
                        time + "')";
                acountDatabase.execSQL(insertsql);
                new GetInfoThread(handler,selected).start();


                new ChongThread(handler,acount,txtmoney.getText().toString()).start();
                new UpdateThread(handler,selected,
                        String.valueOf(Integer.parseInt(banlance.getText().toString())+Integer.parseInt(czmoney))).start();



            }
        });
        //充值记录
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout adialog = (LinearLayout) getLayoutInflater().inflate(R.layout.acountdialog, null);
                TextView tv = new TextView(MainActivity.this);
                tv.setText("账户充值记录");
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(25);
                tv.setPadding(20, 10, 0, 10);//位置左上右下
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setCustomTitle(tv)//设置对话框的标题

                        .setView(adialog)

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                            })
                        .show();
                ListView cz = (ListView) adialog.findViewById(R.id.lv);
                List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
                String sql = "select * from rechard";
                // 获取课程表数据集游标
                cursor = acountDatabase.rawQuery(sql, null);
                for (cursor.moveToFirst();
                     !cursor.isAfterLast(); cursor.moveToNext()) {
                    Map<String,Object>listItem=new HashMap<String,Object>();  //一条记录
                    String carid = cursor.getString(1);
                    int money = cursor.getInt(2);

                    String name = cursor.getString(3);
                    String time = cursor.getString(4);

                    listItem.put("cardid",carid);  //车的id
                    listItem.put("money",money); //存昵称
                    listItem.put("name",name); //存描述
                    listItem.put("time",time); //存描述
                    listItems.add(listItem);  //加入集合中 Map<String,Object>listItem=new HashMap<String,Object>();  //一条记录


                }
                //集合绑定到适配器
                SimpleAdapter simpleAdapter=new SimpleAdapter(MainActivity.this,listItems,
                        R.layout.item_dia ,    //指定一行的界面
                        new String[] {"cardid","money","name","time"},
                        new int[]{R.id.caridtxt,R.id.moneytxt,R.id.ownertxt,R.id.timetxt}
                );
                //绑定到ListView列表上

                cz.setAdapter(simpleAdapter);
            }




        });

    }
    // 初始化数据库
    private void createacountDatabase(SQLiteDatabase acountDatabase){
                // TODO Auto-generated method stub
                // 判断数据表是否已经存在，如不存在则创建数据表

                String sql1 = "SELECT count(*) FROM sqlite_master " +
                        "WHERE type='table' AND name='rechard'";
                Cursor cursor1 = acountDatabase.rawQuery(sql1, null);
                cursor1.moveToFirst();
                // 获取查询的第一个字段来判断表格是否存在
                if (cursor1.getInt(0) < 1) {

                    sql1 = "create table rechard(" +
                            "_id integer primary key autoincrement," +
                            "car_id varchar(20) not null," +
                            "money smallint not null DEFAULT 0," +
                            "name varchar(20) not null," +
                            "time varchar(50) not null)";
                    acountDatabase.execSQL(sql1);

//                    acountDatabase.execSQL("insert into caracount values(null, '1', 0);");
//                    acountDatabase.execSQL("insert into caracount values(null, '2', 0);");
//                    acountDatabase.execSQL("insert into caracount values(null, '3', 0);");
//                    acountDatabase.execSQL("insert into caracount values(null, '4', 0);");

                }
            }
}
