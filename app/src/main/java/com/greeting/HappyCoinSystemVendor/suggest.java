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
    EditText text ;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoyt_suggest);
        text = findViewById(R.id.communication);
        send = findViewById(R.id.submit);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bag bag = new Bag();
                bag.execute();
            }
        });
    }

    private class Bag extends AsyncTask<Void,Void,String> {
        //        String ip = null;
//        int sum = Integer.parseInt(How_much.getText().toString());
//        public String uuid = getUUID(getApplicationContext());
        String Catch;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Catch = text.getText().toString();
        }

        @Override
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
        protected void onPostExecute(String result) {
            popup(getApplicationContext(), result);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(suggest.this,Home.class);
        startActivity(intent);
        finish();
    }
}
