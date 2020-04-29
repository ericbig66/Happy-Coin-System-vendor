package com.greeting.HappyCoinSystemVendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.pf;
import static com.greeting.HappyCoinSystemVendor.Login.pfr;
import static com.greeting.HappyCoinSystemVendor.Login.rc;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;
import static com.greeting.HappyCoinSystemVendor.Login.wcm;
import static com.greeting.HappyCoinSystemVendor.Login.entryIsRecent;

public class Home extends AppCompatActivity {
    TextView wmsg;
    Intent intent;
    ImageView profile;
    public static String vname="";
    int obp = 0; //times of on back pressed



    public void execute(View v){
        switch (v.getId()){
            case R.id.getcoin:
                intent = new Intent(Home.this, vender_qrcode.class);
                break;
//            case R.id.paycoin:
//                intent = new Intent(Home.this, vender_send_redbag.class);
//                break;
//            case R.id.diary:
////                intent = new Intent(Home.this, Diary.class);
//                intent = new Intent(Home.this,NewDiary.class);
//                break;
            case R.id.AddProd:
                intent = new Intent(Home.this, newProduct.class);
                break;
            case R.id.AlterProd:
                intent = new Intent(Home.this,alter_product.class);
                break;
            case R.id.addAct:
                intent = new Intent(Home.this,add_activity.class);
                break;
            case R.id.AlterEvent:
                intent = new Intent(Home.this,alter_event.class);
                break;
            case R.id.alter_vendor:
                intent = new Intent(Home.this,member_center.class);
                break;
            case R.id.contact:
                intent = new Intent(Home.this,suggest.class);
                break;
            case R.id.recent:
                entryIsRecent = true;
                intent = new Intent(Home.this,alter_event.class);
                break;
        }
        startActivity(intent);
        finish();
    }
    public void onBackPressed(){
        obp++;
        Timer timer = new Timer(true);


        if(obp>=2){
            wcm ="";
            acc ="";
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            pf = null;
            rc = 0;
            finish();
        }
        else{
            Toast.makeText(Home.this,"再按一次返回以登出",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
//        Intent intent = getIntent();
//        String msg = intent.getStringExtra("msg");
//        Log.v("test", "profile size = " + pf.getWidth()+"*"+pf.getHeight());
        String msg = wcm;
        setContentView(R.layout.layout_home);
        wmsg = findViewById(R.id.msg);
//        wmsg.setText(msg);
        Log.v("test","WCM= "+wcm);
        wmsg.setText(wcm);
        profile = findViewById(R.id.profile);
        try {
            profile.setImageBitmap(pf);
//            profile.setRotation(pfr);
//            Log.v("test", "profile size = " + pf.getWidth()+"*"+pf.getHeight());
        }catch (Exception e){
//            Log.v("test","profile error = "+e.toString());
        }

        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(Home.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            try{
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select name, money from vendor where acc = '"+acc+"'");

                while(rs.next()){
                    vname = rs.getString(1);
                    result += rs.getString(1)+"您好，目前貴公司\n帳戶餘額為:$"+rs.getInt(2);
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
                return res;
            }

        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            if(rc<=0){recreate();rc++;}
            wmsg.setText(result);
//            Log.v("test","vname M = "+vname);
        }
    }
    }

