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
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.HappyCoinSystemVendor.Home.vname;
import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.pf;
import static com.greeting.HappyCoinSystemVendor.Login.pfs;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;


public class member_center extends AppCompatActivity {
    static final int OPEN_PIC = 1021;

    EditText name, account, em, pwd, chkpwd, pwdhint, phone, opwd, address, website;
    Button pic, reg, clr, rotate;
    CircularImageView profile;
    int function = 0;
    String data = "";
    //裝載轉換出的EditText中的文字
    String NAME=null, EM=null, PH=null, PWD = null, PWDHINT=null, CHKPWD=null, b64=null, OPWD=null, ADD=null, WEB=null;
    Bitmap dataToConvert;
    //清除所有填寫的資料(會被重新填寫按鈕呼叫或註冊成功時會被呼叫)
    public void clear(){
        name.setText("");
        em.setText("");
        pwd.setText("");
        pwdhint.setText("");
        chkpwd.setText("");
        address.setText("");
        website.setText("");
        NAME=null;
        EM=null;
        PWD =null;
        PWDHINT=null;
        OPWD =null;
        ADD=null;
        WEB=null;
        opwd.setText("");

    }

    //隱藏鍵盤
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    //檢查填寫資料正確性(按下註冊鈕後呼叫)
    public void verify(){
        boolean haveError = false;
        String err ="";
//        err = NAME.trim().isEmpty()?err+="公司名稱,":err;
        if (PWD.trim().isEmpty() && CHKPWD.trim().isEmpty()&& !OPWD.trim().isEmpty()) {
            PWD = OPWD;
            CHKPWD = OPWD;
        }else if(OPWD.trim().isEmpty()){err+="目前密碼,";}
        else{
            err = PWD.trim().isEmpty() ? err += "新密碼," : err;
            err = CHKPWD.trim().isEmpty() ? err += "確認新密碼," : err;
        }
//        err = PWDHINT.trim().isEmpty()?err+="密碼提示":err;
//        err = EM.trim().isEmpty()?err+="E-mail,":err;
//        err = PH.trim().isEmpty()?err+="公司電話號碼":err;
//        err = ADD.trim().isEmpty()?err+="公司地址":err;
//        err = WEB.trim().isEmpty()?err+="公司網站":err;
//        err = b64.trim().isEmpty()?err+="上傳頭像,":err;
        err = err.isEmpty()?err:err.substring(0, err.length() - 1);
        if(!err.isEmpty()){err+=" 為必填項目\n請確認是否已填寫!";}
        haveError = !err.isEmpty();
        if(haveError){
            Toast.makeText(member_center.this, err, Toast.LENGTH_LONG).show();}
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
        if(haveError && !err.trim().isEmpty()){Toast.makeText(member_center.this, err, Toast.LENGTH_LONG).show();}
        if(!haveError){
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }
        NAME = NAME.trim().length() > 0 ?NAME:null;
        EM = EM.trim().length() > 0 ?EM:null;
        PH = PH.trim().length() > 0 ?PH:null;
        PWD = PWD.trim().length() > 0 ?PWD:null;
        PWDHINT = PWDHINT.trim().length() > 0 ?PWDHINT:null;
        CHKPWD = CHKPWD.trim().length() > 0 ?CHKPWD:null;
        ADD = ADD.trim().length() > 0 ?ADD:null;
        WEB = WEB.trim().length() > 0 ?WEB:null;
        OPWD = OPWD.trim().length() > 0 ?OPWD:null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_member_center);

        name = findViewById(R.id.name);
        account = findViewById(R.id.account);
        account.setText(acc);
        em = findViewById(R.id.em);
        pwd = findViewById(R.id.pwd);
        pwdhint = findViewById(R.id.pwdhint);
        chkpwd = findViewById(R.id.chkpwd);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        website = findViewById(R.id.website);
        pic = findViewById(R.id.pic);
        reg = findViewById(R.id.reg);
        clr = findViewById(R.id.clr);

        opwd = findViewById(R.id.opwd);

        profile = findViewById(R.id.profile);

//        rotate = findViewById(R.id.rotate);

//        rotate.setOnClickListener(v -> rotate());

        pic.setOnClickListener(v -> picOpen());



        reg.setOnClickListener(v -> {
            closekeybord();
            NAME = name.getText().toString();
            EM = em.getText().toString();
            PH = phone.getText().toString();
            PWD = pwd.getText().toString();
            PWDHINT = pwdhint.getText().toString();
            CHKPWD = chkpwd.getText().toString();
            ADD = address.getText().toString();
            WEB = website.getText().toString();
            OPWD = opwd.getText().toString();



            verify();
        });

        clr.setOnClickListener(v -> onBackPressed());

        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

//    Float degree = 0f;
//    public void rotate(){
//        degree=(degree+90f)>=(360f)?0f:degree+90f;
//        profile.setRotation(degree);
//    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(member_center.this,"註冊中...",Toast.LENGTH_SHORT).show();
            Log.v("test","function = "+function);
        }

        @Override
        protected String doInBackground(String... strings) {
            try{


                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement st = con.createStatement();
                String result ="";
                if(function == 0){
//                    ResultSet rs = st.executeQuery("select phone, profileRotate from vendor where mail = '"+ acc+"'");
//                    while (rs.next()){
//                        result += rs.getString(1)+","+rs.getString(2);
//                    }
                    return result;
                }else{
                    CallableStatement cstmt = con.prepareCall("{? = call vlogin(?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);//設定輸出變數(參數位置,參數型別)
                    cstmt.setString(2, acc);
                    cstmt.setString(3, OPWD);
                    cstmt.setString(4, "N/A");
                    cstmt.executeUpdate();
//                    Log.v("test","yee"+cstmt.getString(1)+"Acc = "+acc+"PWD = "+PWD);
                    if (!cstmt.getString(1).contains("錯誤")){
                        cstmt = con.prepareCall("{?=call alter_vendor(?,?,?,?,?,?,?,?,?)}");
                        cstmt.registerOutParameter(1, Types.VARCHAR);
                        cstmt.setString(2, acc);
                        cstmt.setString(3, NAME);
                        cstmt.setString(4, PH);
                        cstmt.setString(5, ADD);
                        cstmt.setString(6, EM);
                        cstmt.setString(7, PWD);
                        cstmt.setString(8, PWDHINT);
                        cstmt.setString(9, WEB);
                        cstmt.setString(10,b64);

                        cstmt.executeUpdate();
                        return cstmt.getString(1);
                    }else{
                        return "您目前的密碼有誤\n如您忘記密碼，請聯絡客服人員";
                    }



                }


            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();

            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("test","function = "+function);
            Log.v("test", "error = "+result);
            if(result.equals("更新成功")){
                Toast.makeText(member_center.this, result, Toast.LENGTH_SHORT).show();
                pf = dataToConvert;
//                onBackPressed();
            }else if(function == 0){
//                data = result;
//                Log.v("test","data = "+result);
                autoFill();
                function =1;
            }else{
                Toast.makeText(member_center.this, result, Toast.LENGTH_SHORT).show();
                Log.v("test","SQLexception = "+result);
            }


        }


    }
    //********************************************************************************************
    //開啟頭像
    public void picOpen(){
        ((BitmapDrawable) profile.getDrawable()).getBitmap().recycle();//一定要做否則會當機
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
//            rotate.setVisibility(View.GONE);
            dataToConvert = ((BitmapDrawable)profile.getDrawable()).getBitmap();
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }
    //將圖片編碼為base64

    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.v("test","apple pen");
            super.onPreExecute();
            Toast.makeText(member_center.this,"請稍後...",Toast.LENGTH_SHORT).show();
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
            if(dataToConvert != null){
                pf = dataToConvert;
            }
        }
    }

    public void autoFill(){
//        String tmp[] = data.split(",");
//        name.setText(vname);
//        em.setText(acc);
//        phone.setText(tmp[0]);
        b64 = pfs;
        profile.setImageBitmap(pf);
//        if(degree.toString().trim().isEmpty()){degree = 0f;}
//        degree = Float.parseFloat(tmp[1]) - 90f;
//        rotate();


    }

    public void onBackPressed(){
        Intent intent = new Intent(member_center.this, Home.class);
        startActivity(intent);
        clear();
        finish();
    }
    }

