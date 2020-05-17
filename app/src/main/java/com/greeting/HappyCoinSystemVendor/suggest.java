package com.greeting.HappyCoinSystemVendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;
import static com.greeting.HappyCoinSystemVendor.Login.ver;

public class suggest extends AppCompatActivity {
    EditText text ;//意見輸入框
    Button send;//送出鈕
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoyt_suggest);
        //定義區
        text = findViewById(R.id.communication);
        send = findViewById(R.id.submit);
        //設定區
        //設定送出按鈕動作
          //呼叫送出
        send.setOnClickListener(v -> {
            Bag bag = new Bag();
            bag.execute();
        });
    }

    //將意見送入資料庫
    private class Bag extends AsyncTask<Void,Void,String> {
        //        String ip = null;
//        int sum = Integer.parseInt(How_much.getText().toString());
//        public String uuid = getUUID(getApplicationContext());
        String Catch;//意見裝載處
        @Override
        //擷取輸入框文字
        protected void onPreExecute() {
            super.onPreExecute();
            Catch = text.getText().toString();//取得意見輸入框文字
        }

        @Override
        //送出意見
        protected String doInBackground(Void... voids) {
            String res = null;
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
//                String result ="";
//                Statement st = con.createStatement();
//                ResultSet rs = st.executeQuery("SELECT replace(substring_index(SUBSTRING_INDEX(USER(), '@', -1),'.',1),'-','.') AS ip;");//抓ip
//                rs.next();
//                ip = rs.getString(1);
                CallableStatement cstmt = con.prepareCall("{? = call add_comment(?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, ver);
                cstmt.setString(3, Catch);
                cstmt.execute();
                res = cstmt.getString(1);
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        //顯示送出結果
        protected void onPostExecute(String result) {
            popup(getApplicationContext(), result);
        }
    }

    @Override
    //返回首頁
    public void onBackPressed() {
        Intent intent = new Intent(suggest.this,Home.class);
        startActivity(intent);
        finish();
    }
}
