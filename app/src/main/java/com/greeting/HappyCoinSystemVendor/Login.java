package com.greeting.HappyCoinSystemVendor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

public class Login extends AppCompatActivity {
    //資料庫連線資料
    public static final String url = "jdbc:mysql://218.161.48.27:3360/happycoin?noAccessToProcedureBodies=true&useUnicode=yes&characterEncoding=UTF-8";
    public static final String user = "currency";
    public static final String pass = "SEclassUmDb@outside";

    //通用變數
    public static String[] RCdata;  //裝載由資料庫回傳之資料
    public static String wcm;       //歡迎訊息
    public static String pfs;       //頭像(Base64)
    public static Bitmap pf;        //頭像(點陣圖)
    public static float pfr;        //頭像角度
    public static String acc;       //帳號
    public static String vendorName;//名字

    //跨區存取變數
    //Alter product(商品異動)
    public static ArrayList<String> PID = new ArrayList<>();                  //商品代碼
    public static ArrayList<String> Pname = new ArrayList<>();                //品名
    public static ArrayList<Integer> Pprice = new ArrayList<>();              //單價
    public static ArrayList<Integer> Pamount = new ArrayList<>();             //庫存量
    public static ArrayList<String> PIMG = new ArrayList<>();                 //商品圖
    public static ArrayList<String> Pproduct_description = new ArrayList<>(); //商品說明
    public static ArrayList<Integer> Psafe_product = new ArrayList<>();       //安全庫存量

    public static int SellId=-1, ReleseQuantity=0;//選取之商品為陣列中之第幾項, 上(下)架數量[下架之數量為負數]

    //Alter event(活動異動)
    public static ArrayList<String> Aid = new ArrayList<>();          //活動代碼
//    public static ArrayList<String> Avendor = new ArrayList<>();      //廠商代碼***檢查是否均已停用
    public static ArrayList<String> Aname = new ArrayList<>();        //活動名稱
    public static ArrayList<String> Actpic = new ArrayList<>();       //活動封面
    public static ArrayList<Date> AactDate = new ArrayList<>();       //活動日期
    public static ArrayList<Date> AactEnd = new ArrayList<>();        //活動結束
    public static ArrayList<Date> Astart_date = new ArrayList<>();    //開放報名
    public static ArrayList<Date> Adeadline_date = new ArrayList<>(); //報名截止
    public static ArrayList<Date> AsignStart = new ArrayList<>();     //開始簽到
    public static ArrayList<Date> AsignEnd = new ArrayList<>();       //簽到截止
    public static ArrayList<Integer> Aamount = new ArrayList<>();     //名額限制
    public static ArrayList<Integer> Areward = new ArrayList<>();     //獎勵金額
    public static ArrayList<Integer> AamountLeft = new ArrayList<>(); //剩餘人數
    public static ArrayList<String> Adesc = new ArrayList<>();        //活動說明
//    public static ArrayList<String> attended = new ArrayList<>();     //已報名活動***檢查是否均已停用
    public static int  EventId=0;//選取之活動為陣列中之第幾項
    public static boolean entryIsRecent = false;//是否由近期活動進入活動異動區域
    //測試用變數
//    public static ArrayList<String> TMP = new ArrayList<>();


    //登入頁面基本元素
    Button login, clear, register;//登入、清除、註冊 按鈕
    TextView ErrMsg;//錯誤訊息區
    EditText myacc, pwd;//帳號、密碼 輸入框
    String account, password;//擷取輸入框之帳號、密碼 專用變數(避免遭造中途竄改)
    public static int rc = 0;//重新繪製次數(避免出現版面異常)
    public static final String ver = "0.0.1";//版本號***此功能需檢查是否可用
    //切換到註冊頁面
    public void swreg() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
    //切換到到主選單
    public void swmenu() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        //定義區
        login = findViewById(R.id.login);//登入
        clear = findViewById(R.id.clear);//清除
        register = findViewById(R.id.register);//切換到註冊頁面
        ErrMsg = findViewById(R.id.ErrMsg);//登入結果
        myacc = findViewById(R.id.acc);//帳號(Email)
        pwd = findViewById(R.id.pwd);//密碼

        //註冊紐動作
        register.setOnClickListener(v -> swreg());

        //登入紐動作
        login.setOnClickListener(v -> {
            account = myacc.getText().toString();//擷取帳號輸入框文字
            password = pwd.getText().toString(); //擷取密碼輸入框文字
//                dtv.setText("call login(@fname, "+account+", "+password+"); select @fname;");
            //檢測空白，出現空牌將會提示錯誤，否則繼續登入步驟
            if (account.trim().isEmpty() || password.trim().isEmpty()) {
                popup(getApplicationContext(),"請輸入帳號密碼以登入!");
            } else {
                SignIn signin = new SignIn();
                signin.execute();
            }
        });

        //清除紐動作
        clear.setOnClickListener(v -> {
            ErrMsg.setText("");
            myacc.setText(null);
            pwd.setText(null);
        });

        //check update ==>this function will be reserved for the next release
        //note: the sql function has not yet added
            /*
             CheckUpdate checkUpdate = new CheckUpdate();
                checkUpdate.execute("");
             */
    }

    //連線至資料庫登入
    private class SignIn extends AsyncTask<Void, Void, String> {
        String res = "";//錯誤信息儲存變數
        @Override
        //登入前請使用者稍待
        protected void onPreExecute() {
            super.onPreExecute();
            popup(getApplicationContext(),"請稍後...");
        }

        //登入
        @Override
        protected String doInBackground(Void... voids) {
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                CallableStatement cstmt = con.prepareCall("{? = call vlogin(?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);//設定輸出變數(參數位置,參數型別)
                cstmt.setString(2, account);
                cstmt.setString(3, password);
                cstmt.setString(4, "N/A");//此為IP***目前開發中
                cstmt.executeUpdate();
                res = cstmt.getString(1);
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
//                Log.v("test", res);
            }
            return res;
        }

        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            //將從資料庫得到的資料分割進陣列
            if (result.contains("zpek,")) {//以"zpek,"作為分割符號，存在時分割進陣列
                RCdata = result.split("zpek,");//頭像旋轉功能尚未開發[DB]***
                //name==>account==>money==>profile
                wcm = RCdata[0] + "您好，目前貴公司\n帳戶餘額為:$" + RCdata[2];//設定歡迎訊息
//                Log.v("test","WCM0= "+wcm);
                pf = ConvertToBitmap(RCdata[3]);//將頭像字串轉換為圖片
//                Log.v("test", wcm);
                acc = account;//將帳號資料保留以供其他頁面使用
                swmenu();//切換到主選單
            } else {//否則秀出錯誤訊息
                Log.v("test","does not contain separator");
                popup(getApplicationContext(), result);
            }
        }
    }

    //通用函式
    //base64轉換為點陣圖
    public static Bitmap ConvertToBitmap(String b64) {
        try {
            byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
            return (BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));//回傳圖片
        } catch (Exception e) {
//            Log.v("test", "error = " + e.toString());
        }
        return null;
    }

    //隱藏鍵盤
    public static void hideKB(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Toast訊息提示的簡化版==>popup(getApplicationContext(),"訊息");
    public static void popup(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    //Log.v簡化版==>lv("訊息");
    public static void lv(String s){
        Log.v("test",s);
    }
}