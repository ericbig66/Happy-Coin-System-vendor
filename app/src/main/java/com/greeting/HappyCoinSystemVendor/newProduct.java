package com.greeting.HappyCoinSystemVendor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import static com.greeting.HappyCoinSystemVendor.Home.vname;
import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.popupL;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;

public class newProduct extends AppCompatActivity {
    //產品代碼、品名、單價、庫存、安全庫存、產品說明 輸入框
    EditText pid, pname, Pprice, stock,safe_stock,product_description;
    ImageView propic;//商品圖區
    Button loadpic, submit;//上傳圖片、送出按鈕
    final int OPEN_PIC = 1021;//開啟頭像時須使用的程式執行序號
    //擷取輸入框==>產品代號、品名、圖片base64碼、產品說明
    String PID = "", PNAME = "", b64 = "",PRODUCT_DESCRIPTION ="";
    //擷取輸入框==>單價、庫存、安全庫存
    int PPRICE = 0, STOCK = 0, SAFE_STOCK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_product);
        Log.v("test", "vname = " + vname);
        //定義區
        pid = findViewById(R.id.pid);
        pname = findViewById(R.id.pname);
        Pprice = findViewById(R.id.Pprice);
        stock = findViewById(R.id.stock);
        safe_stock = findViewById(R.id.safe_stock);
        product_description = findViewById(R.id.product_description);
        propic = findViewById(R.id.propic);
        loadpic = findViewById(R.id.loadpic);
//        rotate = findViewById(R.id.rotate);
        submit = findViewById(R.id.submit);
        //設定區
        loadpic.setOnClickListener(v -> picOpen());//上傳圖片
//        rotate.setOnClickListener(v -> rotate());
        submit.setOnClickListener(v -> verify());//驗證輸入資料
    }

    //開啟頭像
    public void picOpen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "請選擇商品照片"), OPEN_PIC);
    }

    Bitmap dataToConvert;//待轉換為base64的圖片

    @Override
    //接收到圖片
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        //圖片正確載入
        if (requestCode == OPEN_PIC && RESULT_OK == resultCode) {
            Uri imgdata = data.getData();
            propic.setImageURI(imgdata);//設定頭像
            propic.setVisibility(View.VISIBLE);
//            rotate.setVisibility(View.VISIBLE);
            dataToConvert = ((BitmapDrawable) propic.getDrawable()).getBitmap();//擷取圖片供轉換
//            rotate.setVisibility(View.VISIBLE);
            propic.setVisibility(View.VISIBLE);
            //將圖片轉換成base64格式以儲存置資料庫
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }
    //將圖片轉換成base64格式
    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            popup(getApplicationContext(),"請稍後...");
        }

        @Override
        protected String doInBackground(String... strings) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dataToConvert.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return imageString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            b64 = s;//將轉換後的資料放入專用變數內
        }
    }
    //檢查輸入的資料是否符合規範
    public void verify() {
        //擷取資料並去除空字串
        PID = pid.getText().toString().trim();
        PNAME = pname.getText().toString().trim();
        PRODUCT_DESCRIPTION = product_description.getText().toString().trim();
        PPRICE = Integer.parseInt((Pprice.getText().toString().trim()).isEmpty()?"-1":(Pprice.getText().toString().trim()));
        STOCK = Integer.parseInt((stock.getText().toString().trim()).isEmpty()?"-1":(stock.getText().toString().trim()));
        SAFE_STOCK = Integer.parseInt((safe_stock.getText().toString().trim()).isEmpty()?"-1":(safe_stock.getText().toString().trim()));

        //錯誤訊息疊加
        String error = "";//錯誤訊息
        error = PID.isEmpty() ? error + "商品編號, " : error;
        error = PNAME.isEmpty() ? error + "商品名稱, " : error;
        error = PRODUCT_DESCRIPTION.isEmpty() ? error +"產品說明" :error;
        error = PPRICE < 1 ? error + "商品價格, " : error;
        error = STOCK < 1 ? error + "庫存量, " : error;
        error = SAFE_STOCK < 1 ? error + "安渠庫存量, " : error;
        error = b64.isEmpty() ? error + "商品照片, " : error;
        error = error.isEmpty() ? error : "請確認以下資料是否正確填寫:" + error.substring(0, error.length() - 3);
        if (error.isEmpty()) {
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        } else {
            popupL(getApplicationContext(), error);
        }
    }
    //新增商品置資料庫
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//裝載結果用
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            popup(getApplicationContext(),"新增中...");
        }
        //新增商品
        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                String result = "";
                CallableStatement cstmt = con.prepareCall("{? = call alter_product(?,?,?,?,?,?,?,?)}");
                cstmt.registerOutParameter(1,Types.VARCHAR);
                cstmt.setString(2, acc);
                cstmt.setString(3, PID);
                cstmt.setString(4, PNAME);
                cstmt.setInt(5, PPRICE);
                cstmt.setInt(6, STOCK);
                cstmt.setInt(7, SAFE_STOCK);
                cstmt.setString(8, b64);
                cstmt.setString(9, PRODUCT_DESCRIPTION);
                cstmt.executeUpdate();
                return cstmt.getString( 1);
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //新增後
        @Override
        protected void onPostExecute(String result) {
            popupL(getApplicationContext(),result);//顯示結果
            if (result.contains("成功")) {//若成功則自動返回首頁
                onBackPressed();
            }
        }


    }
    //返回首頁
    public void onBackPressed() {
        Intent intent = new Intent(newProduct.this, Home.class);
        startActivity(intent);
        finish();
    }

}
