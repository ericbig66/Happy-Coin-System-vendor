package com.greeting.HappyCoinSystemVendor;

import androidx.appcompat.app.AppCompatActivity;

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

    //sql connections settings
    public static final String url = "jdbc:mysql://218.161.48.27:3360/happycoin?noAccessToProcedureBodies=true";
    public static final String user = "currency";
    public static final String pass = "SEclassUmDb@outside";

    //shared variable
    public static String[] RCdata;//data received from database
    public static String wcm;//welcome message
    public static String pfs;//profile String
    public static Bitmap pf;//profile picture
    public static float pfr;//profile rotation
    public static String acc;//vendor account
    public static String vendorName;
    //寄放區

    //Alter product
    public static ArrayList<String> PID = new ArrayList<>();
    public static ArrayList<String> Pname = new ArrayList<>();
    public static ArrayList<Integer> Pprice = new ArrayList<>();
    public static ArrayList<Integer> Pamount = new ArrayList<>();
    public static ArrayList<String> PIMG = new ArrayList<>();
    public static ArrayList<String> Pproduct_description = new ArrayList<>();
    public static ArrayList<Integer> Psafe_product = new ArrayList<>();

    public static int SellId=-1, ReleseQuantity=0;

    //Alter event
    public static ArrayList<String> Aid = new ArrayList<>();  //event
    public static ArrayList<String> Avendor = new ArrayList<>();
    public static ArrayList<String> Aname = new ArrayList<>();
    public static ArrayList<String> Actpic = new ArrayList<>();
    public static ArrayList<Date> AactDate = new ArrayList<>();
    public static ArrayList<Date> AactEnd = new ArrayList<>();
    public static ArrayList<Date> Astart_date = new ArrayList<>();
    public static ArrayList<Date> Adeadline_date = new ArrayList<>();
    public static ArrayList<Date> AsignStart = new ArrayList<>();
    public static ArrayList<Date> AsignEnd = new ArrayList<>();
    public static ArrayList<Integer> Aamount = new ArrayList<>();
    public static ArrayList<Integer> Areward = new ArrayList<>();
    public static ArrayList<Integer> AamountLeft = new ArrayList<>();
    public static ArrayList<String> Adesc = new ArrayList<>(); //資料庫無
    public static ArrayList<String> attended = new ArrayList<>();
    public static int  EventId=-1;
    public static boolean entryIsRecent = false;

    //測試用變數
    public static ArrayList<String> TMP = new ArrayList<>();
    //

    //basic element
    Button login, clear, register;
    TextView ErrMsg;
    EditText myacc, pwd;
    String account, password, data;
    public static int rc = 0;

    public static final String ver = "0.0.1";

    public void swreg() {  //切換註冊頁面
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void swmenu() {   //切換到主選單
        Intent intent = new Intent(this, Home.class);
//        wcm = data;
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        //set basic element
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
            account = myacc.getText().toString();
            password = pwd.getText().toString();
//                dtv.setText("call login(@fname, "+account+", "+password+"); select @fname;");
            if (account.trim().isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(Login.this, "請輸入帳號密碼以登入!", Toast.LENGTH_SHORT).show();
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

    //public function
    //base64 byte array to bitmap converter
    public static Bitmap ConvertToBitmap(String b64) {
        try {
            byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
            return (BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        } catch (Exception e) {
//            Log.v("test", "error = " + e.toString());
        }
        return null;
    }


    //private function
    //隱藏鍵盤
    public void closekeybord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //toast popup
    public static void popup(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    //建立連接與查詢非同步作業
    private class SignIn extends AsyncTask<Void, Void, String> {
        String res = "";//錯誤信息儲存變數

        //開始執行動作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Login.this, "請稍後...", Toast.LENGTH_SHORT).show();
        }

        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(Void... voids) {
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
//
                Connection con = DriverManager.getConnection(url, user, pass);
                //建立查詢
                String result = "";


                CallableStatement cstmt = con.prepareCall("{? = call vlogin(?,?,?)}");
                cstmt.registerOutParameter(1, Types.VARCHAR);//設定輸出變數(參數位置,參數型別)
                cstmt.setString(2, account);
                cstmt.setString(3, password);
                cstmt.setString(4, "N/A");
                cstmt.executeUpdate();
                res = cstmt.getString(1);
//                pfs = cstmt.getString(5);
//                pfr = cstmt.getFloat(6);
//                ConvertToBitmap();
                return res;
            } catch (Exception e) {
//                res = result;
                e.printStackTrace();
                res = e.toString();
//                Log.v("test", res);
            }
            return res;
        }

        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            if (result.contains("zpek,")) {
                RCdata = result.split("zpek,");//profile rotate isn't available on both app and database side
                //name==>account==>money==>profile
                wcm = RCdata[0] + "您好，目前貴公司\n帳戶餘額為:$" + RCdata[2];
                Log.v("test","WCM0= "+wcm);
                pf = ConvertToBitmap(RCdata[3]);
//                Log.v("test", wcm);
//                data = result;
                acc = account;
                Toast.makeText(Login.this, "請稍後...", Toast.LENGTH_SHORT).show();
                swmenu();
            } else {
                Log.v("test","does not contain separator");
                popup(getApplicationContext(), result);
            }
//            if (result.equals("遊客您好!\n目前您尚有$null")) {
//                result = "遊客您好!\n如出售物品請先註冊帳號\n謝謝您的合作!";
//                ErrMsg.setText(result);//設定結果顯示
//            } else {
//
//            }
        }
    }
}