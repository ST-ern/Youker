package com.yechaoa.materialdesign.activity;

import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yechaoa.materialdesign.R;
import com.yechaoa.materialdesign.model.dao.Constant;
import com.yechaoa.materialdesign.utils.AnalysisUtils;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyBookDescriptionActivity extends ToolbarActivity implements View.OnClickListener {

    @BindView(R.id.til_book_description)
    TextInputLayout til_description;
    @BindView(R.id.btn_modify_book_description)
    Button btn_modify_description;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String userName;
    private String title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_book_description;
    }

    @Override
    protected void setToolbar() {
        mToolbar.setTitle("Modify Description");
    }

    @Override
    protected void initView(){
        userName = AnalysisUtils.readLoginUserName(this);

        // 从Intent获得book_title
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("bookTitle");
        if(title==null){ title = "No found."; }

        btn_modify_description.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_modify_book_description:
                Toast.makeText(ModifyBookDescriptionActivity.this, "Description", Toast.LENGTH_SHORT).show();
                submit();
                break;
            default:
                break;
        }
    }

    private void submit() {
        final String description = til_description.getEditText().getText().toString().trim();

        //Todo:将userName, title, description传到后端

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("cardbag_id", title);
                paramsMap.put("card_describe", description);
                paramsMap.put("user_name", userName);

                Gson gson = new Gson();
                String data = gson.toJson(paramsMap);


                RequestBody formBody;
                formBody = RequestBody.create(JSON, data);
                Request request = new Request.Builder().url(Constant.UPDATEBAG).post(formBody).build();

                try (Response response = okHttpClient.newCall(request).execute()) {
                    Looper.prepare();
                    final String answer = response.body().string();
                    Boolean t;
                    if (answer.equals("1")) {
                        t = true;
                    } else {
                        t = false;
                    }
                    final Boolean add_success = t;
                    //同名卡包添加失败的显示

                    ModifyBookDescriptionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (add_success) {
                                Toast.makeText(ModifyBookDescriptionActivity.this, "Description Ok", Toast.LENGTH_SHORT).show();
                                ModifyBookDescriptionActivity.this.finish();
                            } else {
                                Toast.makeText(ModifyBookDescriptionActivity.this, "Modify False.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



        ModifyBookDescriptionActivity.this.finish();
    }
}
