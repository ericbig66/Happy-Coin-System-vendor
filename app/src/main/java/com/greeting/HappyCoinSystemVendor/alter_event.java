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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.HappyCoinSystemVendor.Home.vname;
import static com.greeting.HappyCoinSystemVendor.Login.AactDate;
import static com.greeting.HappyCoinSystemVendor.Login.AactEnd;
import static com.greeting.HappyCoinSystemVendor.Login.Aamount;
import static com.greeting.HappyCoinSystemVendor.Login.AamountLeft;
import static com.greeting.HappyCoinSystemVendor.Login.Actpic;
import static com.greeting.HappyCoinSystemVendor.Login.Adeadline_date;
import static com.greeting.HappyCoinSystemVendor.Login.Adesc;
import static com.greeting.HappyCoinSystemVendor.Login.Aid;
import static com.greeting.HappyCoinSystemVendor.Login.Aname;
import static com.greeting.HappyCoinSystemVendor.Login.Areward;
import static com.greeting.HappyCoinSystemVendor.Login.AsignEnd;
import static com.greeting.HappyCoinSystemVendor.Login.AsignStart;
import static com.greeting.HappyCoinSystemVendor.Login.Astart_date;

import static com.greeting.HappyCoinSystemVendor.Login.EventId;
import static com.greeting.HappyCoinSystemVendor.Login.acc;

import static com.greeting.HappyCoinSystemVendor.Login.hideKB;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;
import static com.greeting.HappyCoinSystemVendor.Login.entryIsRecent;

public class alter_event extends AppCompatActivity {
    int function = 0;//功能選擇器(0 = 取得資料庫資料 1= 修改活動)
    LinearLayout ll;//活動列表顯示區
    String SQL ;//由近期活動進入時會新增的資料庫查詢條件
    public static int cardCounter = 0;//活動數量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_event);
        //定義區
        ll = findViewById(R.id.ll);
        //設定區
        //如果是由近期活動進入此頁
        if(entryIsRecent)
            SQL = " and actDate> now()";//條件限制為未舉辦活動
        else
            SQL = "";
        //從資料庫擷取活動資料
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    //查詢活動資料或修改資料
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            popup(getApplicationContext(),"請稍後...");
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        protected String doInBackground(String... strings) {
            //取得活動資料
            if (function == 0) {
                try {
                    //連接資料庫
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, user, pass);
                    //建立查詢
                    String result = "";
                    Statement st = con.createStatement();
                    ResultSet  rs = st.executeQuery("select a.* from activity a, vendor v where v.vid = a.id  and v.acc= '"+acc+"'"+SQL);
                    while (rs.next()) {
                            Aid.add(rs.getString(1));
//                            Avendor.add(rs.getString(2));
                            Aname.add(rs.getString(3));
                            Actpic.add(rs.getString(4));
                            AactDate.add(rs.getDate(5));
                            AactEnd.add(rs.getDate(6));
                            Astart_date.add(rs.getDate(7));
                            Adeadline_date.add(rs.getDate(8));
                            AsignStart.add(rs.getTime(9));
                            AsignEnd.add(rs.getTime(10));
                            Aamount.add(rs.getInt(11));
                            Areward.add(rs.getInt(12));
                            AamountLeft.add(rs.getInt(13));
                            Adesc.add(rs.getString("actDesc"));
                    }
//                    attended.clear();
                    return Aname.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)
                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.toString();
                }
                return res;
            }
            //修改活動資訊
            else if (function == 1) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, user, pass);
                    //建立查詢
                    String result = "";
                    CallableStatement cstmt = con.prepareCall("{?= call alter_activity(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setString(2, Aid.get(EventId));
                    cstmt.setString(3, acc);
                    cstmt.setString(4, Aname.get(EventId));
                    cstmt.setString(5,""+AactDate.get(EventId));
                    cstmt.setString(6, ""+AactEnd.get(EventId));
                    cstmt.setString(7, ""+Astart_date.get(EventId));
                    cstmt.setString(8, ""+Adeadline_date.get(EventId));
                    cstmt.setString(9, ""+AsignStart.get(EventId));
                    cstmt.setString(10,""+AsignEnd.get(EventId));
                    cstmt.setInt(11, AddAmount);
                    cstmt.setInt(12, Areward.get(EventId));
                    cstmt.setString(13, Actpic.get(EventId));
                    cstmt.setString(14, Adesc.get(EventId));
                    cstmt.executeUpdate();
                    return cstmt.getString(1);
                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.toString();
                }
                return res;
            }
            return res;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute (String result){
            try {
                //取得活動資料
                if (function == 0) {
//                    Log.v("test","hey "+result);
                    cardCounter = Integer.parseInt(result);
                    cardRenderer();
                //修改活動資訊
                } else if (function == 1) {
                    popup(getApplicationContext(),result);
                    if(result.contains("成功")){//修改成功時
                        clear();//清除資料列表
                        recreate();//重新繪製頁面(更新資料)
                    }
                }
                function = -1;
            } catch (Exception e) {
//                Log.v("test", "錯誤: " + e.toString());
            }

        }

    }

    //商品卡產生器
    public void cardRenderer() {
        for (int i = 0; i < Aname.size(); i++) {
//            Log.v("test", "render card " + i);
            add(i);
        }
    }


    //產生商品卡
    public void add(final int ID) {
        //商品卡片
        LinearLayout frame = new LinearLayout(this);
        LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DP(150)
        );


        frame.setPadding(DP(15), DP(15), DP(15), DP(15));
        framep.setMargins(0, 0, 0, DP(20));
        frame.setOrientation(LinearLayout.HORIZONTAL);
        frame.setBackgroundColor(Color.parseColor("#D1FFDE"));
        frame.setLayoutParams(framep);

        //圖片&回饋金額
        LinearLayout picpri = new LinearLayout(this);
        LinearLayout.LayoutParams picprip = new LinearLayout.LayoutParams(DP(120), DP(120));
        picprip.setMargins(0, 0, DP(5), 0);
        picpri.setOrientation(LinearLayout.VERTICAL);
        picpri.setLayoutParams(picprip);

        //活動圖片
        ImageView propic = new ImageView(this);
        LinearLayout.LayoutParams propicp = new LinearLayout.LayoutParams(DP(120), DP(90));
        propic.setImageBitmap(ConvertToBitmap(ID));
        propic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        propic.setLayoutParams(propicp);
        propic.setId(5 * ID);
        propic.setOnClickListener(v -> {
            final int id = ID;
            hideKB(this);
            identifier("D", id,0);
        });

        //獎勵金額
        TextView price = new TextView(this);
        LinearLayout.LayoutParams pricep = new LinearLayout.LayoutParams(DP(120), DP(30));
        price.setText("獎勵: $" + Areward.get(ID));
        price.setTextSize(18f);
        price.setLayoutParams(picprip);

        //活動訊息區
        LinearLayout proinf = new LinearLayout(this);
        LinearLayout.LayoutParams proinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        proinf.setOrientation(LinearLayout.VERTICAL);
        proinf.setLayoutParams(proinfp);

        //活動名稱
        TextView proname = new TextView(this);
        LinearLayout.LayoutParams pronamep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        proname.setText(Aname.get(ID));
        proname.setTextSize(18f);
        proname.setClickable(true);
        proname.setLayoutParams(pronamep);
        proname.setId(5 * ID + 1);

        //報名資訊
        LinearLayout buyinf = new LinearLayout(this);
        LinearLayout.LayoutParams buyinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        buyinf.setOrientation(LinearLayout.HORIZONTAL);
        buyinf.setLayoutParams(buyinfp);

        //剩餘名額
        TextView amount_label = new TextView(this);
        LinearLayout.LayoutParams amount_labelp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount_label.setText("新增名額:");
        amount_label.setTextSize(18f);
        amount_label.setLayoutParams(amount_labelp);

        //新增名額輸入處
        EditText amount_add = new EditText(this);
        LinearLayout.LayoutParams amountp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount_add.setHint("餘額:" + AamountLeft.get(ID));
        amount_add.setTextSize(18f);
        amount_add.setInputType(InputType.TYPE_CLASS_NUMBER);
        amount_add.setLayoutParams(amountp);

        //按鈕箱
        LinearLayout btnbox = new LinearLayout(this);
        LinearLayout.LayoutParams btnboxp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        btnbox.setLayoutParams(btnboxp);


        //修改按鈕
        Button detail = new Button(this);
        LinearLayout.LayoutParams detailp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f
        );
        detailp.setMarginEnd(20);
        detail.setText("修改");
        detail.setBackgroundResource(R.drawable.rounded_button);
        detail.setTextColor(Color.parseColor("#FFFFFF"));
        detail.setTextSize(18f);
        detail.setLayoutParams(detailp);
        detail.setId(5 * ID + 3);
        detail.setOnClickListener(v -> {
            final int id = ID;
            hideKB(this);
            identifier("D", id, 0);
        });

        //新增按鈕
        Button buybtn = new Button(this);
        LinearLayout.LayoutParams buybtnp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f
        );
        buybtn.setText("新增");
        buybtn.setTextSize(18f);
        buybtn.setBackgroundResource(R.drawable.rounded_button);
        buybtn.setTextColor(Color.parseColor("#FFFFFF"));
        buybtn.setLayoutParams(buybtnp);
        buybtn.setId(5 * ID + 4);
        buybtn.setOnClickListener(v -> {
            final int id = ID;
            if(amount_add.getText().toString().trim().isEmpty()){amount_add.setText("0");}
            hideKB(this);
            identifier("B", id, Integer.parseInt(amount_add.getText().toString()));
        });

        //將內容填入frame
        /*
        frame
            propic
            proinf
                proname
                buyinf
                    amount_label
                    amount
                btnbox
                    dteail
                    buybtn
        */
        proinf.addView(proname);
        buyinf.addView(amount_label);
        buyinf.addView(amount_add);
        proinf.addView(buyinf);
        btnbox.addView(detail);
        btnbox.addView(buybtn);
        proinf.addView(btnbox);
        picpri.addView(propic);
        picpri.addView(price);
        frame.addView(picpri);
        frame.addView(proinf);
        ll.addView(frame);
//        Log.v("test", "card" + ID + "rendered");
    }

    public static int DP(float dp) {
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }

    int AddAmount = 0;
    /////////////////////////////////////////////
    public void identifier(String act, int ID, int quantity) {
        EventId = ID;
        if (act.equals("D")) {
//            Log.v("test", "您正在檢視第" + Aname.get(ID) + "的詳細資料");
            Intent intent = new Intent(alter_event.this, event_detail.class);
            startActivity(intent);
        } else if (act.equals("B")) {
//            Log.v("test", "您報名了==>" + Aname.get(ID));
            function = 1;
            if(quantity>0) {
                AddAmount = Aamount.get(EventId)+quantity;
                ConnectMySql connectMySql = new ConnectMySql();
                connectMySql.execute("");
            }else{
                Toast.makeText(alter_event.this,"請至少新增一個名額",Toast.LENGTH_SHORT).show();
            }

        }
    }
    //清空列表以確保活動資訊不會重複疊加
    public void clear() {
        Aid.clear();
        Aname.clear();
        Areward.clear();
        Aamount.clear();
        AamountLeft.clear();
        Adesc.clear();
//        Avendor.clear();
        AactDate.clear();
        AsignStart.clear();
        AactEnd.clear();
        Actpic.clear();
//        attended.clear();
        Adeadline_date.clear();
        AsignEnd.clear();
    }

    public void onBackPressed() {
        entryIsRecent = false ;
        Intent intent = new Intent(alter_event.this, Home.class);
        startActivity(intent);
        clear();
        finish();
    }

    //將base64轉換為點陣圖
    public Bitmap ConvertToBitmap(int ID) {
        try {
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(Actpic.get(ID), Base64.DEFAULT);
            Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            int w = proimg.getWidth();
            int h = proimg.getHeight();
//            Log.v("test", "pic" + ID + " original = " + w + "*" + h);
            //調整圖片大小
            int scale = 1;
            if (w > h && (w / DP(120)) > 1 || h == w && (w / DP(120)) > 1) {
                scale = w / DP(120);
                w = w / scale;
                h = h / scale;
            } else if (h > w && (h / DP(120)) > 1) {
                scale = h / DP(120);
                w = w / scale;
                h = h / scale;
            }
//            Log.v("test", "pic" + ID + " resized = " + w + "*" + h);
            proimg = Bitmap.createScaledBitmap(proimg, w, h, false);
            return proimg;//回傳圖片
        } catch (Exception e) {
//            Log.v("test", "error = " + e.toString());
            return null;
        }
    }
}
