package com.example.zcg.mycount;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChongThread extends Thread {
    private String url="http://118.25.27.11:8080/trafic/servlet/AddChargeAction";
    private Handler handler;
    private Acount acount;
    private String money;
    Map<String ,String> map=new HashMap<String,String>();
    public ChongThread(Handler handler,Acount acount,String money)
    {this.handler=handler;
     this.acount=acount;
     this.money=money;
    } //构造方法

    @Override
    public void run() {   //每隔3秒钟获取交通信息
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        map.put("userid","user1");
        map.put("carid",acount.getCar_id());
        map.put("carnum",acount.getCarnum());
        map.put("money",money);
        map.put("chargtime",time);

            try {
                HttpUtils.postmap(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            Message message=Message.obtain();
                            message.what=2;

                            message.obj=new JSONObject(response.body().string());  //将json字符串转为对象
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },map);

            }catch (Exception e)
            {
                e.printStackTrace();

            }

    }

}
