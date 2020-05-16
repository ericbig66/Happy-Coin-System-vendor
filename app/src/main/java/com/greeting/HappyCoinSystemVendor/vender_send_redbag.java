package com.greeting.HappyCoinSystemVendor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.HappyCoinSystemVendor.Login.Aid;
import static com.greeting.HappyCoinSystemVendor.Login.Aname;
import static com.greeting.HappyCoinSystemVendor.Login.Areward;
import static com.greeting.HappyCoinSystemVendor.Login.EventId;
import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.hideKB;
import static com.greeting.HappyCoinSystemVendor.Login.lv;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;


public class vender_send_redbag extends AppCompatActivity {
    Button pay;//確認按鈕
    EditText amount;//金額(輸入)
    ImageView qrCode;//QRcode顯示區
    Spinner DropDown;//補簽到/發紅包
    LinearLayout customInput;//金額輸入框
    TextView actName;//活動名稱顯示
    int total;//金額(擷取)

    //清除陣列資料(以免重複疊加)然後返回首頁
    public void onBackPressed(){
        Aid.clear();
        Aname.clear();
        Intent intent = new Intent(vender_send_redbag.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vender_send_redbag);
        lv(EventId+"");
        //定義區
        pay = findViewById(R.id.pay);
        amount = findViewById(R.id.amount);
        qrCode = findViewById(R.id.qrCode);
        DropDown = findViewById(R.id.DropDown);
        customInput = findViewById(R.id.customInput);
        actName = findViewById(R.id.actName);
        //設定區
          //如偵測到輸入的金額更改時將自動隱藏目前已顯示的QRcode
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qrCode.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
          //偵測下拉式選單選取項目
        DropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //若選擇"紅包/人工補簽"
                if(position == Aname.size()-1){
                    function=2;//功能切換為發送紅包
                    customInput.setVisibility(View.VISIBLE);//顯示金額輸入框
                    qrCode.setVisibility(View.GONE);//隱藏先前QRcode
                }
                else{
                    function=1;//功能切換為簽到
                    total=Areward.get(EventId);//設定獎勵金額(自動取得)
                    customInput.setVisibility(View.GONE);//隱藏金額輸入框
//                    Log.v("test",acc+"fu02l," + Aid.get(position));
                    try{
                        EventId=position;//目前選取的活動位於陣列第幾項(供查詢使用)
                        //簽到資料查詢
                        ConnectMySql connectMySql = new ConnectMySql();
                        connectMySql.execute("");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                actName.setText(Aname.get(position)+"簽到請掃描我");//***文字語病
                qrCode.setVisibility(View.VISIBLE);//顯示簽到QRcode
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //初始化下拉式清單(擷取活動資料)
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    public void getCode(View v) {
        hideKB(this);
        //發紅包時檢查金額是否正確
        total=amount.getText().toString().trim().length()>0?Integer.parseInt(amount.getText().toString()):0;
        Log.v("test","total.length= "+amount.getText().toString().trim().length());
//        Log.v("test", acc+"cj/1l," +amount .getText().toString());
        if(amount.getText().toString().trim().isEmpty() || Integer.parseInt(amount.getText().toString().trim())<1||total<1){
            Toast.makeText(vender_send_redbag.this,"請輸入紅包金額",Toast.LENGTH_LONG).show();
        }
        else{
            //金額正確，發紅包
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }


    }

//    int counter = 0 ;//***Debug 使用
    int function=0;//資料庫功能選擇器
    //查詢活動資料、顯示活動簽到QRcode、送紅包QRcode產生
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//錯誤信息儲存變數

        //開始執行動作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            popup(getApplicationContext(),"請稍後...");
            lv("fn before = "+function);
        }

        //查詢與修改資料
        @Override
        protected String doInBackground(String... strings) {
                try {
                    //連接資料庫
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, user, pass);
                    //建立查詢
                    String result = "";
                    Statement st = con.createStatement();
                    //擷取活動資料
                    if(function==0) {
                        ResultSet rs = st.executeQuery("select id, actName,reward from activity, vendor where HostId = vendor.vid and acc = '" + acc + "' and Date(actDate) = CURDATE()");
                Log.v("test","select id, actName,reward from activity, vendor where HostId = vendor.vid and acc = '"+ acc+"' and actDate = curDate()");
                        while (rs.next()) {
//                            counter++;
                            Aid.add(rs.getString(1));
//                            Log.v("test","hihi = "+Aid.size()+"");
                            Aname.add(rs.getString(2));
//                            Log.v("test","hihi1 = "+Aname.size()+"");
                            Areward.add(rs.getInt(3));
//                            Log.v("test","hihi2 = "+Areward.size()+"");
//                            lv("reward["+(counter-1)+""+"]= "+rs.getInt(3));
                        }
                        int actAmount = Aid.size();
                        Aname.add("紅包/人工補簽");
                        return Aname.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)
                    }
                    //送紅包
                    else if(function>0){
                        lv("送紅包");
                        CallableStatement cstmt = con.prepareCall("{? = call redenvelope_manager(?,?,?,?,?,?,?)}");
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setString(2,acc);
                        cstmt.setString(3,"V");
                        cstmt.setString(4,null);
                        cstmt.setString(5,null);
                        if(function==1) {
                            cstmt.setInt(6, Areward.get(EventId));
                            cstmt.setString(7, Aname.get(EventId) + "補簽到紅包");
                        }
                        else {
                            cstmt.setInt(6, total);
                            cstmt.setString(7, "廠商紅包");
                        }
                        cstmt.setString(8,null);
                        cstmt.execute();
                        res = cstmt.getString(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.toString();
                }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute (String result){
            lv("error when generate code: "+ result);
//            Log.v("test",counter+"");
            //擷取到活動資料後
            if(function==0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(vender_send_redbag.this, android.R.layout.simple_spinner_item, Aname);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                DropDown.setAdapter(adapter);
                function++;
            }
            //成功生成紅包條碼後
            else {
                lv("fn= "+function);
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bit = null;
                try {
                    bit = encoder.encodeBitmap(acc+"cj/61l,"+result+"cj/61l,"+total+"cj/61l,"+"n/a" +"cj/61l,"+"V", BarcodeFormat.QR_CODE,1000,1000);
                    qrCode.setVisibility(View.VISIBLE);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                qrCode.setImageBitmap(bit);//輸出紅包發放條碼
            }
        }
    }
}

