package com.example.zcg.mycount;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetInfoThread extends Thread {
    private String url="http://118.25.27.11:8080/trafic/servlet/SelectAcountAction";
    private Handler handler;
    private String carid;
    Map<String ,String> map=new HashMap<String,String>();
    public GetInfoThread(Handler handler,String carid)  //构造方法
    {this.handler=handler;
    this.carid=carid;

    } //构造方法

    @Override
    public void run() {   //每隔3秒钟获取交通信息
        map.put("carid",carid);

            try {
                HttpUtils.postmap(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                            Message message=Message.obtain();
                            message.what=1;

                            message.obj=new Gson().fromJson(response.body().string(),Acount.class);  //将json字符串转为对象
                            handler.sendMessage(message);


                    }
                },map);

            }catch (Exception e)
            {
                e.printStackTrace();

            }

    }

}
