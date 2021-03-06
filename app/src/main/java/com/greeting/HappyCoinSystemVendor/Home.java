package com.greeting.HappyCoinSystemVendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import static com.greeting.HappyCoinSystemVendor.Login.lv;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.pf;
import static com.greeting.HappyCoinSystemVendor.Login.pfr;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.rc;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;
import static com.greeting.HappyCoinSystemVendor.Login.wcm;
import static com.greeting.HappyCoinSystemVendor.Login.entryIsRecent;

public class Home extends AppCompatActivity {
    TextView wmsg;//歡迎訊息顯示處
    Intent intent;//畫面轉跳事件
    ImageView profile;//頭像圖框
    public static String vname="";//廠商名稱
    int obp = 0; //返回鍵按下次數***功能開發中

    //頁面轉跳選擇器
    public void execute(View v){
        switch (v.getId()){
            case R.id.getcoin://商品兌換(結帳)
                intent = new Intent(Home.this, vender_qrcode.class);
                break;
            case R.id.paycoin://發送紅包
                intent = new Intent(Home.this, vender_send_redbag.class);
                break;
            case R.id.diary://日記簿
                intent = new Intent(Home.this, diary.class);
                break;
            case R.id.AddProd://新增商品
                intent = new Intent(Home.this, newProduct.class);
                break;
            case R.id.AlterProd://商品異動
                intent = new Intent(Home.this,alter_product.class);
                break;
            case R.id.addAct://新增活動
                intent = new Intent(Home.this,add_activity.class);
                break;
            case R.id.AlterEvent://活動異動
                intent = new Intent(Home.this,alter_event.class);
                break;
            case R.id.alter_vendor://會員中心
            case R.id.profile:
                intent = new Intent(Home.this,member_center.class);
                break;
            case R.id.contact://聯絡我們
                intent = new Intent(Home.this,suggest.class);
                break;
            case R.id.recent://近期活動
                entryIsRecent = true;
                intent = new Intent(Home.this,alter_event.class);
                break;
        }
        try {
            lv("before clean");
//            ((BitmapDrawable)profile.getDrawable()).getBitmap().recycle();
            lv("after clean");
            startActivity(intent);//開啟指定頁面
            finish();//結束此頁
        }catch (Exception e){lv(e.toString());}
    }
    public void onBackPressed(){
        obp++;//紀錄返回次數***開發中
        Timer timer = new Timer(true);//配合計數器使用***開發中(無法將返回次數規0)
        lv("obp = "+obp);
        //第二次按下返回鍵時登出
        if(obp>=2){
            wcm ="";
            acc ="";
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
//            pf = null;
            rc = 0;
            finish();
        }
        else{
            popup(getApplicationContext(),"再按一次返回以登出");//第一次案返回鍵時提示
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        //定義區
        wmsg = findViewById(R.id.msg);
        profile = findViewById(R.id.profile);
        Log.v("test","WCM= "+wcm);
        //設定區
        wmsg.setText(wcm);//顯示歡迎訊息
        Log.v("test","i'm still good");
        try {
            lv("profile is null (home)= "+ (pf==null));
            profile.setImageBitmap(pf);//顯示頭像
//            profile.setRotation(pfr);
//            Log.v("test", "profile size = " + pf.getWidth()+"*"+pf.getHeight());
        }catch (Exception e){
//            Log.v("test","profile error = "+e.toString());
        }
        lv("after setting pf");
        //連接資料庫取得帳戶餘額
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    //連接資料庫取得帳戶餘額
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            popup(getApplicationContext(),"請稍後...");
        }
        //餘額查詢
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
            if(rc<=0){recreate();rc++;}//初次開啟將自動重新繪製(避免排版問題)
            wmsg.setText(result);
//            Log.v("test","vname M = "+vname);
        }
    }
}

