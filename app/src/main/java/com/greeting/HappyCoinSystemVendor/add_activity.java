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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;

public class add_activity extends AppCompatActivity {
    //輸入框==>活動名稱,回饋金額,人數限制,活動說明,活動年,        活動月,         活動日,       開始報名年,      開始報名月,       開始報名日,      報名截止年,         報名截止月,           報名截止日,        簽到截止分,簽到截止時,開放簽到分,     開放簽到時
    EditText actName, reward, people, ActDesc,act_date_year,act_date_month,act_date_day,start_date_year,start_date_month,start_date_day,deadline_date_year,deadline_date_month,deadline_date_day,sign_min,sign_hour,start_sign_min,start_sign_hour;
    ImageView actPic;//活動封面圖放置處
    Button uploadPic, AddAct;//上傳圖片鈕、新增活動鈕
    final int OPEN_PIC = 1021;//開啟頭像時須使用的程式執行序號
    //擷取資料 活動圖檔,活動名稱,活動說明, 開始報名時間,   報名截止時間,     開始簽到時間, 簽到截止時間,活動日期
    String b64 = "", ACTN="", DESC="", start_sign="",act_deadline="", fu02l4="", end_sign="",act_date;
    int RW=0, PP=0;//活動獎勵、人數限制

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_activity);
        //定義區
        actName = findViewById(R.id.actName);
        reward = findViewById(R.id.reward);
        people = findViewById(R.id.people);
        ActDesc = findViewById(R.id.ActDesc);
        actPic = findViewById(R.id.actPic);
        uploadPic = findViewById(R.id.uploadPic);
        AddAct = findViewById(R.id.AddAct);
        start_date_year = findViewById(R.id.start_date_year);
        start_date_month = findViewById(R.id.start_date_month);
        start_date_day = findViewById(R.id.start_date__day);
        deadline_date_year = findViewById(R.id.deadline_date_year);
        deadline_date_month = findViewById(R.id.deadline_date_month);
        deadline_date_day = findViewById(R.id.deadline_date__day);
        act_date_year = findViewById(R.id.act_date_year);
        act_date_month = findViewById(R.id.act_date_month);
        act_date_day = findViewById(R.id.act_date_day);
        start_sign_hour = findViewById(R.id.start_sign_hour);
        start_sign_min = findViewById(R.id.start_sign_min);
        sign_hour = findViewById(R.id.sign_hour);
        sign_min = findViewById(R.id.sign_min);
        //設定區
        uploadPic.setOnClickListener(v -> picOpen());//上傳圖片鈕動作
        AddAct.setOnClickListener(v -> verify());//新增活動鈕動作
    }
    //資料錯誤檢查
    public void verify(){
        String error = "";//錯誤訊息容器
        //檢查是否含有空白或不正確之資料
        ACTN = actName.getText().toString();
        Log.v("test","ACTN = "+ACTN.trim().isEmpty());
        error = ACTN.trim().isEmpty()?error+"活動名稱, ":error;
        DESC = ActDesc.getText().toString();
        error = DESC.trim().isEmpty()?error+"活動說明, ":error;
        RW = Integer.parseInt(reward.getText().toString().trim().isEmpty()?"-1":reward.getText().toString().trim());
        error = RW==-1?error+"每人獎金, ":error;
        PP = Integer.parseInt(people.getText().toString().trim().isEmpty()?"-1":people.getText().toString().trim());
        error = PP==-1?error+"人數限制, ":error;
        error = b64.trim().isEmpty()?error+"活動封面照片, ":error;
        error = error.isEmpty()?error:"請確實填寫以下資料:\n"+error.substring(0,error.length()-3);
        //這是報名開始時間
        try {
            if(Integer.parseInt(start_date_month.getText().toString()) == 2){
                if (!(Integer.parseInt(start_date_year.getText().toString()) % 4 != 0 && Integer.parseInt(start_date_year.getText().toString()) % 100 != 0) && !(Integer.parseInt(start_date_year.getText().toString()) % 400 == 0)){
                    if(Integer.parseInt(start_date_day.getText().toString())>28)
                        error += "今年不是閏年ㄛ<3";
                }
            }
            else if(Integer.parseInt(start_date_month.getText().toString()) == 1||Integer.parseInt(start_date_month.getText().toString()) == 3||Integer.parseInt(start_date_month.getText().toString()) == 5||Integer.parseInt(start_date_month.getText().toString()) == 7||Integer.parseInt(start_date_month.getText().toString()) == 8||Integer.parseInt(start_date_month.getText().toString()) == 10||Integer.parseInt(start_date_month.getText().toString()) == 12)
                if(Integer.parseInt(start_date_day.getText().toString())>31)
                    error += start_date_month.getText().toString()+"月最多只有31天ㄛ";
             else
                if(Integer.parseInt(start_date_day.getText().toString())>30)
                    error += start_date_month.getText().toString()+"月最多只有30天ㄛ";
//這是報名截止時間
            if(Integer.parseInt(deadline_date_month.getText().toString()) == 2){
                if (!(Integer.parseInt(deadline_date_year.getText().toString()) % 4 == 0 && Integer.parseInt(deadline_date_year.getText().toString()) % 100 != 0) && !(Integer.parseInt(deadline_date_year.getText().toString()) % 400 == 0)){
                    if(Integer.parseInt(deadline_date_day.getText().toString())>28)
                        error += "今年不是閏年ㄛ<3";
                }
            }
            else if(Integer.parseInt(deadline_date_month.getText().toString()) == 1||Integer.parseInt(deadline_date_month.getText().toString()) == 3||Integer.parseInt(deadline_date_month.getText().toString()) == 5||Integer.parseInt(deadline_date_month.getText().toString()) == 7||Integer.parseInt(deadline_date_month.getText().toString()) == 8||Integer.parseInt(deadline_date_month.getText().toString()) == 10||Integer.parseInt(deadline_date_month.getText().toString()) == 12)
                if(Integer.parseInt(deadline_date_day.getText().toString())>31)
                    error += deadline_date_month.getText().toString()+"月最多只有31天ㄛ";
                else
                if(Integer.parseInt(deadline_date_day.getText().toString())>30)
                    error += deadline_date_month.getText().toString()+"月最多只有30天ㄛ";
//這是活動日期
            if(Integer.parseInt(act_date_month.getText().toString()) == 2){
                if (!(Integer.parseInt(deadline_date_year.getText().toString()) % 4 == 0 && Integer.parseInt(deadline_date_year.getText().toString()) % 100 != 0) && !(Integer.parseInt(deadline_date_year.getText().toString()) % 400 == 0)){
                    if(Integer.parseInt(act_date_day.getText().toString())>28)
                        error += "今年不是閏年ㄛ<3";
                }
            }
            else if(Integer.parseInt(act_date_month.getText().toString()) == 1||Integer.parseInt(act_date_month.getText().toString()) == 3||Integer.parseInt(act_date_month.getText().toString()) == 5||Integer.parseInt(act_date_month.getText().toString()) == 7||Integer.parseInt(act_date_month.getText().toString()) == 8||Integer.parseInt(act_date_month.getText().toString()) == 10||Integer.parseInt(act_date_month.getText().toString()) == 12)
                if(Integer.parseInt(act_date_day.getText().toString())>31)
                    error += act_date_month.getText().toString()+"月最多只有31天ㄛ";
                else
                if(Integer.parseInt(act_date_day.getText().toString())>30)
                    error += act_date_month.getText().toString()+"月最多只有30天ㄛ";
//簽到開放時間
            if(Integer.parseInt(sign_hour.getText().toString())>23||Integer.parseInt(start_sign_hour.getText().toString())>23)
                error += "一天只有24小時ㄛ";
            if(Integer.parseInt(sign_min.getText().toString())>59||Integer.parseInt(start_sign_min.getText().toString())>59)
                error += "一小時只有60分鐘ㄛ";

            //Log.v("test", "\nEA="+EA+"\nAD="+AD);
//            error = ea.before(ad)?error:error+"\n\n注意:活動日期必須大於報名截止日期";
//            error = as.before(ae)?error:error+"\n\n注意:簽到截止時間必須大於簽到開始時間";
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(error.isEmpty()){
//            Toast.makeText(add_activity.this,"可以上傳",Toast.LENGTH_LONG).show();
//            String b64 = "", ACTN="", DESC="", start_sign="",act_deadline="", fu02l4="", end_sign="";
            Log.v("test","年"+start_date_year.getText().toString());
            Log.v("test","月"+start_date_month.getText().toString());
            Log.v("test","日"+start_date_day.getText().toString());
            start_sign = start_date_year.getText().toString()+"/"+ start_date_month.getText().toString()+"/" +start_date_day.getText().toString()+" 00:00:00";//開始報名
            act_deadline = deadline_date_year.getText().toString()+"/"+ deadline_date_month.getText().toString()+"/" +deadline_date_day.getText().toString()+" 11:59:59";//活動截止報名
            act_date =  act_date_year.getText().toString()+"/"+ act_date_month.getText().toString()+"/" +act_date_day.getText().toString();
            fu02l4 = start_sign_hour.getText().toString()+":"+ start_sign_min.getText().toString();
            end_sign = sign_hour.getText().toString()+":"+ sign_min.getText().toString();//
            ConnectMySql connectMySql = new ConnectMySql();
            connectMySql.execute("");
        }else{
            Toast.makeText(add_activity.this,error,Toast.LENGTH_LONG).show();
        }

    }

    //開啟頭像
    public void picOpen(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"請選擇商品照片"), OPEN_PIC);
    }

    Bitmap dataToConvert;//待轉換活動封面照
    @Override
    //開啟照片後，如果可以將自動轉換大小
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_PIC && RESULT_OK == resultCode){
            Uri imgdata = data.getData();
            actPic.setImageURI(imgdata);
            actPic.setVisibility(View.VISIBLE);
            dataToConvert = ((BitmapDrawable)actPic.getDrawable()).getBitmap();
            int w,h,scale;
            w = dataToConvert.getWidth();
            h = dataToConvert.getHeight();
            if(w>h||w==h&&w/120>1){scale = w/120;}
            else{scale = h/120;}
            w/=scale;
            h/=scale;
            actPic.setImageBitmap(Bitmap.createScaledBitmap(dataToConvert,w,h,false));
            actPic.setVisibility(View.VISIBLE);
            ConvertToBase64 convertToBase64 = new ConvertToBase64();
            convertToBase64.execute("");
        }
    }

    private class ConvertToBase64 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(add_activity.this,"請稍後...",Toast.LENGTH_SHORT).show();
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
    //上傳欲新增的活動
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(add_activity.this,"新增中...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                String result ="";
                CallableStatement cstmt = con.prepareCall("{?= call alter_activity(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, null);
                cstmt.setString(3, acc);
                cstmt.setString(4, ACTN);
                cstmt.setString(5,act_date+" "+fu02l4+":00");
                cstmt.setString(6, act_date+" "+end_sign+":59");
                cstmt.setString(7, start_sign);
                cstmt.setString(8, act_deadline);
                cstmt.setString(9, fu02l4);
                cstmt.setString(10,end_sign);
                cstmt.setInt(11, PP);
                cstmt.setInt(12, RW);
                cstmt.setString(13, b64);
                cstmt.setString(14,DESC );
                cstmt.executeUpdate();
                return cstmt.getString(1);
            }catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(add_activity.this,result,Toast.LENGTH_LONG).show();
            if (result.contains("成功")) {
                onBackPressed();
            }
        }


    }
    //回到首頁
    public void onBackPressed(){
        Intent intent = new Intent(add_activity.this, Home.class);
        startActivity(intent);
        finish();
    }
}
