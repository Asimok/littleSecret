package com.qq1962976634.littlesecret.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.qq1962976634.littlesecret.R;
import com.qq1962976634.littlesecret.tools.MyNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private OkHttpClient okhttpClient;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        Button bt1=root.findViewById(R.id.search);
        bt1.setOnClickListener(this);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(getActivity(),"点击",Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest( "1", "1", "1", "1");
            }
        }).start();
    }

    private void sendRequest( String mima1, String Name1, String PhoneNumber1, String Email1) {
        Map map = new HashMap();

        map.put("mima", mima1);
        map.put("Name", Name1);
        map.put("PhoneNumber", PhoneNumber1);
        map.put("Email", Email1);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://192.168.1.10:8080/littleSecret/RegistServlet")
                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity(). runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "连接服务器失败！", Toast.LENGTH_SHORT).show();

                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                Log.d("aa",res);
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String status = jsonObj.getString("status");

                    showRequestResult(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void showRequestResult(final String status) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
                if (status.equals("0")) {
                    MyNotification notify = new MyNotification(getActivity());
                    notify.MyNotification("智能会议室", "注册成功", R.drawable.ic_menu_camera, "regist", "注册", 10, "注册");
                    Toast.makeText(getActivity(), "注册成功！", Toast.LENGTH_SHORT).show();


                } else if (status.equals("-1")) {
                    Toast.makeText(getActivity(), "信息不存在，注册失败！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-4")) {
                    Toast.makeText(getActivity(), "该员工号已注册！请重新输入！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-3")) {
                    Toast.makeText(getActivity(), "您不是该公司员工！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-2")) {
                    Toast.makeText(getActivity(), "账户名非法！请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}