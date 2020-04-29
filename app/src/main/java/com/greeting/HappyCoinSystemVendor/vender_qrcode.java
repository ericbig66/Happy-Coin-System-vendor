package com.greeting.HappyCoinSystemVendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;


import static com.greeting.HappyCoinSystemVendor.Home.vname;
import static com.greeting.HappyCoinSystemVendor.Login.PID;
import static com.greeting.HappyCoinSystemVendor.Login.Pname;
import static com.greeting.HappyCoinSystemVendor.Login.RCdata;
import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;

public class vender_qrcode extends AppCompatActivity {

    ImageView qrCode;
    Spinner DropDown;
    TextView actName;
    EditText amount;
    Button submit;
    int pos =0;

    public void onBackPressed(){
        Intent intent = new Intent(vender_qrcode.this, Home.class);
        startActivity(intent);
        PID.clear();
        Pname.clear();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_vender_qrcode);

        qrCode = findViewById(R.id.qrCode);
        DropDown = findViewById(R.id.DropDown);
        actName = findViewById(R.id.actName);
        amount = findViewById(R.id.amount);
        submit = findViewById(R.id.submit);



        submit.setOnClickListener(v -> {
            if(amount.getText().toString().trim().isEmpty() || Integer.parseInt(amount.getText().toString())<1){
                Toast.makeText(vender_qrcode.this,"請輸入正確的數量",Toast.LENGTH_SHORT);
            }else{
                GenerateCode(pos);
                closekeybord();
                qrCode.setVisibility(View.VISIBLE);
            }
        });

        DropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actName.setText("掃描我兌換"+Pname.get(position));
                qrCode.setVisibility(View.GONE);
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    public void GenerateCode(int position){
        BarcodeEncoder encoder = new BarcodeEncoder();
        try{
            Bitmap bit = encoder.encodeBitmap((PID.get(position) + "e.4a93," + Integer.parseInt(amount.getText().toString()) + "e.4a93," + RCdata[0])
                    , BarcodeFormat.QR_CODE,1000,1000);
            qrCode.setImageBitmap(bit);
        }catch (WriterException e) {
            e.printStackTrace();
        }
    }


    //建立連接與查詢非同步作業
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//錯誤信息儲存變數

        //開始執行動作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(vender_qrcode.this, "請稍後...", Toast.LENGTH_SHORT).show();
        }

        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select sku, productName from product, vendor  where product.vendor = vendor.VID and name = '"+RCdata[0]+"' and stock>0;");
                while (rs.next()) {
                    PID.add(rs.getString(1));
                    Pname.add(rs.getString(2));
                }
                return Pname.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)

            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute (String result){
            Log.v("test", "heyhey = "+result);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(vender_qrcode.this, android.R.layout.simple_spinner_item, Pname);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            DropDown.setAdapter(adapter);
        }

    }

    //隱藏鍵盤
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
