package com.greeting.HappyCoinSystemVendor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

import static com.greeting.HappyCoinSystemVendor.Login.hideKB;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;

public class Register extends AppCompatActivity {

    static final int OPEN_PIC = 1021;
    //輸入框==>公司名稱,帳號, 密碼, 確認密碼,密碼提示, e-mail,電話,地址,    往站
    EditText name, account, pwd, chkpwd, pwdhint, em, phone, address, website;
    // 變更頭像, 註冊, 切至登入,清除,旋轉頭像==>旋轉尚未開發完成
    Button pic, reg, login, clr, rotate;
    CircularImageView profile;//頭像顯示處

    //裝載轉換出的EditText中的文字
    //     公司名稱, 帳號,        e-mail,密碼,     確認密碼,   密碼提示,    電話,   地址,   網站,    頭像base64
    String NAME="", ACCOUNT="", EM="", PWD = "", CHKPWD="", PWDHINT="", PH="", ADD="", WEB="", b64="";
    Bitmap dataToConvert;//裝載帶轉換的頭像

    //清除功能
    public void clear(){
        name.setText("");
        account.setText("");
        pwd.setText("");
        chkpwd.setText("");
        pwdhint.setText("");
        em.setText("");
        phone.setText("");
        address.setText("");
        website.setText("");
        NAME="";
        ACCOUNT="";
        EM="";
        PWD = "";
        CHKPWD="";
        PWDHINT="";
        PH="";
        ADD="";
        WEB="";
        b64="";
        profile.setVisibility(View.GONE);//隱藏頭像區域
//        rotate.setVisibility(View.GONE);
    }
    //切換回登入模式(被該按鈕呼叫)
    public void swlogin(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    //檢查填寫資料正確性(按下註冊鈕後呼叫)
    public void verify(){
        boolean haveError = false;
        String err ="";
        err = NAME.trim().isEmpty()?err+="公司名稱,":err;
        err = ACCOUNT.trim().isEmpty()?err+="帳號,":err;
        err = PWD.trim().isEmpty()?err+="密碼,":err;
        err = CHKPWD.trim().isEmpty()?err+="確認密碼,":err;
        err = EM.trim().isEmpty()?err+="E-mail,":err;
        err = PH.trim().isEmpty()?err+="公司電話號碼":err;
        err = ADD.trim().isEmpty()?err+="公司地址":err;
        err = b64.trim().isEmpty()?err+="上傳頭像,":err;
        err = err.isEmpty()?err:err.substring(0, err.length() - 1);
        if(!err.isEmpty()){err+=" 為必填項目\n請確認是否已填寫!";}
        haveError = !err.isEmpty();
        if(haveError){
            Toast.makeText(Register.this, err, Toast.LENGTH_LONG).show();}
        err = "";
        if(!PWD.trim().isEmpty() && !CHKPWD.trim().isEmpty() && !PWD.equals(CHKPWD)){
            err += "您輸入的密碼前後不一致，請重新輸入\n";
            chkpwd.setText("");
            CHKPWD = "";
            haveError = true;
        }

        if ( !EM.trim().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(EM).matches() ) {
            err += "請輸入正確的電子郵件地址";
            em.setText("");
            EM = "";
            haveError = true;
        }
        if(haveError && !err.trim().isEmpty()){Toast.makeText(Register.this, err, Toast.LENGTH_LONG).show();}
        if(!haveError){
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        //定義區
        name = findViewById(R.id.name);
        account = findViewById(R.id.account);
        em = findViewById(R.id.em);
        pwd = findViewById(R.id.pwd);
        chkpwd = findViewById(R.id.chkpwd);
        pwdhint = findViewById(R.id.pwdhint);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        website = findViewById(R.id.website);
        pic = findViewById(R.id.pic);
        reg = findViewById(R.id.reg);
        login = findViewById(R.id.login);
        clr = findViewById(R.id.clr);
        profile = findViewById(R.id.profile);
        rotate = findViewById(R.id.rotate);
        //設定區
//        rotate.setOnClickListener(v -> rotate());
        pic.setOnClickListener(v -> picOpen());//開啟頭像動作
        login.setOnClickListener(v -> swlogin());//切換至登入頁面動作
        reg.setOnClickListener(v -> {//註冊按鈕動作
            hideKB(this);
            //擷取輸入框資料
            NAME = name.getText().toString();
            EM = em.getText().toString();
            PH = phone.getText().toString();
            PWD = pwd.getText().toString();
            CHKPWD = chkpwd.getText().toString();
            ACCOUNT = account.getText().toString();
            ADD = address.getText().toString();
            PWDHINT = pwdhint.getText().toString();
            WEB = website.getText().toString();
            verify();//驗證資料
        });
        clr.setOnClickListener(v -> clear());//清除按鈕動作
    }
    Float degree = 0f;//頭像角度
    //旋轉頭像
    public void rotate(){
        degree=(degree+90f)>=(360f)?0f:degree+90f;
        profile.setRotation(degree);
    }

    //連線置資料庫註冊
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//裝載回傳訊息

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            popup(getApplicationContext(),"註冊中...");
        }
        //註冊
        @Override
        protected String doInBackground(String... strings) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                String result ="";
                CallableStatement cstmt = con.prepareCall("{? = call vregister(?,?,?,?,?,?,?,?,?)}");
                cstmt.registerOutParameter(1,Types.VARCHAR);
                cstmt.setString(2, NAME);//brand
                cstmt.setString(3, PH);//phone
                cstmt.setString(4, ADD);//address+
                cstmt.setString(5, EM);//email
                cstmt.setString(6, ACCOUNT);//account+
                cstmt.setString(7, PWD);//code
                cstmt.setString(8, PWDHINT);//code hint+
                cstmt.setString(9, WEB);//website+
                cstmt.setString(10, b64);//profile picture+
                cstmt.executeUpdate();
                return cstmt.getString(1);
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        //執行結果
        protected void onPostExecute(String result) {
            result = result.contains("failure")?"請檢查您的網路連線\n然後重新註冊":result;
            popup(getApplicationContext(),result);
//            Log.v("test", "error = "+result);
            if(result.equals("註冊成功!")){//註冊成功將自動返回登入頁面
                clear();
                swlogin();
                finish();
            }
        }
    }
    //********************************************************************************************
    //開啟頭像
    public void picOpen(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"請選擇您的頭像"), OPEN_PIC);
    }
    //取得圖片路徑
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(Register.this, "hi!",Toast.LENGTH_SHORT).show();
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){
            Uri imgdata = data.getData();
            profile.setImageURI(imgdata);
            profile.setVisibility(View.VISIBLE);
//            rotate.setVisibility(View.VISIBLE);
            dataToConvert = ((BitmapDrawable)profile.getDrawable()).getBitmap();
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    //將圖片編碼為base64
    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Register.this,"請稍後...",Toast.LENGTH_SHORT).show();
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
            b64 = s;
        }
    }
}
