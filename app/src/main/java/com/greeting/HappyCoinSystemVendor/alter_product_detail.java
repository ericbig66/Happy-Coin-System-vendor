package com.greeting.HappyCoinSystemVendor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.greeting.HappyCoinSystemVendor.Home.vname;
import static com.greeting.HappyCoinSystemVendor.Login.PID;
import static com.greeting.HappyCoinSystemVendor.Login.PIMG;
import static com.greeting.HappyCoinSystemVendor.Login.Pamount;
import static com.greeting.HappyCoinSystemVendor.Login.Pname;
import static com.greeting.HappyCoinSystemVendor.Login.Pprice;
import static com.greeting.HappyCoinSystemVendor.Login.Pproduct_description;
import static com.greeting.HappyCoinSystemVendor.Login.Psafe_product;
import static com.greeting.HappyCoinSystemVendor.Login.RCdata;
import static com.greeting.HappyCoinSystemVendor.Login.ReleseQuantity;
import static com.greeting.HappyCoinSystemVendor.Login.SellId;
import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.pf;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.popupL;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;
public class  alter_product_detail extends AppCompatActivity {
    int OPEN_PIC = 1021;//開啟頭像時須使用的程式執行序號
    String b64 = PIMG.get(SellId);//取得商品圖之base64碼
    //系統時間及格式設定
    Date curDate = new Date(System.currentTimeMillis()) ;//取得系統時間
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");//格式化日期顯示方式
    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");//格式化時間顯示方式
    //格式化出可直接使用的年月日變數
    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    SimpleDateFormat month = new SimpleDateFormat("mm");
    SimpleDateFormat day = new SimpleDateFormat("dd");
    String yyyy = year.format(curDate);
    String mm = month.format(curDate);
    String dd = day.format(curDate);

    //將base64轉換為點陣圖
    public Bitmap ConvertToBitmap(int ID){
        try{
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(PIMG.get(ID), Base64.DEFAULT);
            Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            int w = proimg.getWidth();
            int h = proimg.getHeight();
            Log.v("test","pic"+ID+" original = "+w+"*"+h);
            //調整圖片大小
            int scale = 1;
            if(w>h && (w/360)>1 || h==w && (w/360)>1){
                scale = w/360;
                w = w/scale;
                h = h/scale;
            }else if(h>w && (h/360)>1){
                scale = h/360;
                w = w/scale;
                h = h/scale;
            }
            Log.v("test","pic"+ID+" resized = "+w+"*"+h);
            proimg = Bitmap.createScaledBitmap(proimg, w, h, false);
            return proimg;//回傳圖片
        }catch (Exception e){
            Log.v("test","error = "+e.toString());
            return null;
        }
    }
    //輸入框 上(下)架數量,品名,     單價,            商品代碼,      安全庫存,        商品介紹,           廠商名稱,    庫存量
    EditText Qt, EdtProductName, edtProductPrice, edtProductID, edtProductSafe, edtProductDetail, edtVdrName, edtProductAmount;
    RadioButton RadioShelves, RadioTakeOff;//上架按鈕、下架按鈕
    Button alterPic;//修改圖片鈕
    ImageView merPic;//商品圖放置處

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_product_detail);
        Log.v("test","pf is null (0)=" + (pf==null));
        //定義區
        merPic = findViewById(R.id.merPic);
        edtVdrName = findViewById(R.id.edtVdrName);
        edtProductAmount = findViewById(R.id.edtProductAmount);
        EdtProductName = findViewById(R.id.EdtProductName);
        edtProductID = findViewById(R.id.edtProductID);
        edtProductSafe = findViewById(R.id.edtProductSafe);
        edtProductDetail = findViewById(R.id.edtProductDetail);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        RadioShelves=findViewById(R.id.RadioShelves);
        RadioTakeOff=findViewById(R.id.RadioTakeOff);
        alterPic = findViewById(R.id.alterPic);
        Button btnChangeConfirm=findViewById(R.id.btnChangeConfirm);
        Qt = findViewById(R.id.Qt);
        //設定區
        merPic.setImageBitmap(ConvertToBitmap(SellId));                 //設定商品圖片
        EdtProductName.setText(Pname.get(SellId));                      //設定品名
        edtVdrName.setText(RCdata[0]);                                  //廠商名稱
        edtProductID.setText(PID.get(SellId));                          //產品代碼
        edtProductAmount.setText(Pamount.get(SellId)+"");               //庫存量
        edtProductPrice.setText(Pprice.get(SellId)+"");                 //單價
        edtProductSafe.setText(Psafe_product.get(SellId)+"");           //安全庫存
        edtProductDetail.setText(Pproduct_description.get(SellId)+"");  //商品說明
        Qt.setText(ReleseQuantity+"");                                  //設定上(下)架數量[預設為0]
        alterPic.setOnClickListener(v ->picOpen());//點擊修改圖片時的動作
        btnChangeConfirm.setOnClickListener(v -> verifier());//點擊確認時的動作
    }
    ////清空列表以確保商品資訊不會重複疊加
    public void clear(){
        PID.clear();
        Pname.clear();
        Pprice.clear();
        Pamount.clear();
        PIMG.clear();
    }

    @Override
    //清除陣列並返回首頁
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(alter_product_detail.this,alter_product.class);
        startActivity(intent);
        clear();
        Log.v("test","pf is null (clr)=" + (pf==null));
        finish();
    }

    //更換商品圖
    public void picOpen(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"請選擇商品照片"), OPEN_PIC);
    }
    //設定上新的商品圖
    Bitmap dataToConvert;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){
            Uri imgdata = data.getData();
            merPic.setImageURI(imgdata);
            merPic.setVisibility(View.VISIBLE);
            dataToConvert = ((BitmapDrawable)merPic.getDrawable()).getBitmap();
            int w = dataToConvert.getWidth();
            int h = dataToConvert.getHeight();
            //設定圖片大小
            int scale = 1;
            if(w>h && (w/360)>1 || h==w && (w/360)>1){
                scale = w/360;
                w = w/scale;
                h = h/scale;
            }else if(h>w && (h/360)>1){
                scale = h/360;
                w = w/scale;
                h = h/scale;
            }
            merPic.setImageBitmap(Bitmap.createScaledBitmap(dataToConvert, w, h, false));//設定商品新圖片
            //自動將圖片轉換出base64(供資料庫儲存使用)
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }
    //將點陣圖轉換為base64
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
            b64 = s;//將base64填入變數替換舊圖片
        }
    }

    //更新的商品資訊
    String error = "", pid, pname, pdetail;//錯誤訊息, 商品代碼, 品名, 商品說明
    int quantity = 0, price, psafe;//商品上(下)架數量、單價、安全庫存
    //新資訊錯誤檢查
    public void verifier(){
        error = EdtProductName.getText().toString().trim().isEmpty()?error+"商品名稱, ":error;
        error = edtProductPrice.getText().toString().trim().isEmpty()?error+"商品價格, ":error;
        error = edtProductSafe.getText().toString().trim().isEmpty()?error+"安全庫存, ":error;
        error = edtProductDetail.getText().toString().trim().isEmpty()?error+"商品說明, ":error;
        if(Qt.getText().toString().trim().isEmpty()){Qt.setText("0");}
        else if(RadioShelves.isChecked()){quantity = Integer.parseInt(Qt.getText().toString());}
        else if(RadioTakeOff.isChecked()){quantity = Integer.parseInt(Qt.getText().toString())*-1;}
        //有錯報錯，沒錯繼續將資料填入變數
        if(!error.trim().isEmpty()){
            error = "請確實填寫"+error.substring(0,error.length()-2);
            popupL(getApplicationContext(),error);
            error="";
        }else{
            pid= edtProductID.getText().toString();
            price = Integer.parseInt(edtProductPrice.getText().toString().trim());
            pname = EdtProductName.getText().toString();
            psafe = Integer.parseInt(edtProductSafe.getText().toString().trim());
            pdetail = edtProductDetail.getText().toString();
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }
    }

    //更新商品資訊置資料庫
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
//            Toast.makeText(AlterProductDetail.this,"請稍後...",Toast.LENGTH_SHORT).show();
        }
        //更新商品資訊
        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result ="";
                CallableStatement cstmt = con.prepareCall("{? = call alter_product(?,?,?,?,?,?,?,?)}");
                cstmt.registerOutParameter(1,Types.VARCHAR);
                cstmt.setString(2, acc);
                cstmt.setString(3, Login.PID.get(SellId));
                cstmt.setString(4, pname);
                cstmt.setInt(5, price);
                cstmt.setInt(6, (Pamount.get(SellId)+quantity));
                cstmt.setInt(7, psafe);
                cstmt.setString(8, b64);
                cstmt.setString(9, pdetail);
                cstmt.executeUpdate();
                Log.v("test","info updated:\nvname ="+vname+"\npid ="+pid+"\npname ="+pname+"\nprice ="+price+"\nquantity ="+quantity);
                return cstmt.getString(1);
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
//            Log.v("test","hello?");
            try{
//                Log.v("test","excuse me");
                popup(getApplicationContext(),result);//顯示修改結果
                if(result.contains("成功")){//若成功將自動返回上頁
                    onBackPressed();
                }
            }catch (Exception e){
                Log.v("test","錯誤: "+e.toString());
            }
        }
    }
}
