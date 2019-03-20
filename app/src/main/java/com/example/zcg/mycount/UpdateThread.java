package com.example.zcg.mycount;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateThread extends Thread {
    private String url="http://118.25.27.11:8080/trafic/servlet/UpdateAcountAction";
    private Handler handler;
    private String carid,money;
    Map<String ,String> map=new HashMap<String,String>();
    public UpdateThread(Handler handler, String carid,String money)
    {this.handler=handler;
     this.carid=carid;
     this.money=money;
    } //构造方法

    @Override
    public void run() {   //每隔3秒钟获取交通信息

        map.put("carid",carid);

        map.put("money",money);


            try {
                HttpUtils.postmap(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            Message message=Message.obtain();
                            message.what=3;

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
