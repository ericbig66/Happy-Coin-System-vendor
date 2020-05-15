package com.greeting.HappyCoinSystemVendor;

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

import androidx.appcompat.app.AppCompatActivity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import static com.greeting.HappyCoinSystemVendor.Home.vname;
import static com.greeting.HappyCoinSystemVendor.Login.PID;
import static com.greeting.HappyCoinSystemVendor.Login.PIMG;
import static com.greeting.HappyCoinSystemVendor.Login.Pamount;
import static com.greeting.HappyCoinSystemVendor.Login.Pname;
import static com.greeting.HappyCoinSystemVendor.Login.Pprice;
import static com.greeting.HappyCoinSystemVendor.Login.Pproduct_description;
import static com.greeting.HappyCoinSystemVendor.Login.Psafe_product;
import static com.greeting.HappyCoinSystemVendor.Login.ReleseQuantity;
import static com.greeting.HappyCoinSystemVendor.Login.SellId;
import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.hideKB;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.pf;
import static com.greeting.HappyCoinSystemVendor.Login.popup;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;


public class alter_product extends AppCompatActivity {
    int function = 0;//功能選擇器(0 = 取得資料庫資料 1= 修改商品)
    LinearLayout ll;//商品列表顯示區
    public static int cardCounter = 0;//商品數量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alter_product);
        //定義
        ll = findViewById(R.id.ll);
        //連線置資料庫擷取商品資訊
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");
    }

    //連接置資料庫進行商品資料擷取或修改商品資訊
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            popup(getApplicationContext(),"請稍後...");
        }
        //擷取商品資廖或修改商品資訊
        @Override
        protected String doInBackground(String... strings) {
            //擷取商品資訊
            if(function == 0) {
                try {
                    //連接資料庫
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, user, pass);
                    //建立查詢
                    String result = "";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("select p.* from product p, vendor v where p.vendor = v.VID and acc = '"+acc+"';");
                    while (rs.next()) {
                        Login.PID.add(rs.getString(1));
                        Pname.add(rs.getString(3));
                        Pprice.add(rs.getInt(4));
                        Pamount.add(rs.getInt(5));
                        PIMG.add(rs.getString(7));
                        Psafe_product.add(rs.getInt(6));
                        Pproduct_description.add(rs.getString(8));
                    }
                    return Login.PID.size() + "";//回傳結果給onPostExecute==>取得輸出變數(位置)
                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.toString();
                }
                return res;
            }
            //修改商品資訊
            else if(function == 1){
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, user, pass);
                    //建立查詢
                    String result ="";
                    CallableStatement cstmt = con.prepareCall("{? = call alter_product(?,?,?,?,?,?,?,?)}");
                    cstmt.registerOutParameter(1,Types.VARCHAR);
                    cstmt.setString(2, acc);
                    cstmt.setString(3, Login.PID.get(SellId));
                    cstmt.setString(4, Pname.get(SellId));
                    cstmt.setInt(5, Pprice.get(SellId));
                    cstmt.setInt(6, (Pamount.get(SellId)+ReleseQuantity));
                    cstmt.setInt(7, Psafe_product.get(SellId));
                    cstmt.setString(8, PIMG.get(SellId));
                    cstmt.setString(9, Pproduct_description.get(SellId));
                    cstmt.executeUpdate();
                    return cstmt.getString( 1);
//                    Log.v("test","info updated:\nvname ="+vname+"\npid ="+PID.get(SellId)+"\npname ="+Pname.get(SellId)+"\nprice ="+Pprice.get(SellId)+"\nquantity ="+ReleseQuantity);
                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.toString();
                }
                return res;
            }
            return null;
        }
        //查詢後的結果將回傳於此
        @Override
        protected void onPostExecute(String result) {
            try{
                //取得商品資訊後
                if(function == 0){
                    cardCounter = Integer.parseInt(result);//取得資料陣列大小
                    cardRenderer();//產生商品卡
                }
                //商品修改後
                else if(function == 1){
                    popup(getApplicationContext(),result);//顯示修改結果
                    if(result.contains("成功")){//修改成功時
                        clear();//清除資料列表
                        recreate();//重新繪製頁面(更新資料)
                    }
                }
                function = -1;
            }catch (Exception e){
                Log.v("test","錯誤: "+e.toString());
            }

        }
    }

    //商品卡產生器
    public void cardRenderer(){
        for(int i = 0 ; i < Login.PID.size() ; i++){
            Log.v("test", "render card "+i);
            add(i);
        }
    }


    //產生商品卡
    public void add(final int ID){
        //商品卡片
        LinearLayout frame = new LinearLayout(this);
        LinearLayout.LayoutParams framep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DP(150)
        );


        frame.setPadding(DP(15),DP(15),DP(15),DP(15));
        framep.setMargins(0,0,0,DP(20));
        frame.setOrientation(LinearLayout.HORIZONTAL);
        frame.setBackgroundColor(Color.parseColor("#D1FFDE"));
        frame.setLayoutParams(framep);

        //圖片&價格區
        LinearLayout picpri = new LinearLayout(this);
        LinearLayout.LayoutParams picprip = new LinearLayout.LayoutParams(DP(120),DP(120));
        picprip.setMargins(0,0,DP(5),0);
        picpri.setOrientation(LinearLayout.VERTICAL);
        picpri.setLayoutParams(picprip);

        //數量
        final EditText amount = new EditText(this);
        LinearLayout.LayoutParams amountp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount.setEms(6);
        amount.setHint("庫存:"+ Pamount.get(ID));
        amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        amount.setLayoutParams(amountp);
        amount.setId(5*ID+2);

        //商品圖片
        ImageView propic = new ImageView(this);
        LinearLayout.LayoutParams propicp = new LinearLayout.LayoutParams(DP(120),DP(90));
        //propic.setImageBitmap(Bitmap.createScaledBitmap(ConvertToBitmap(ID), 120, 90, false));
        propic.setImageBitmap(ConvertToBitmap(ID));
        propic.setScaleType(ImageView.ScaleType.CENTER_CROP);

        propic.setLayoutParams(propicp);
        propic.setId(5*ID);
        propic.setOnClickListener(v -> {
            final int id = ID;
            if(amount.getText().toString().trim().isEmpty()){amount.setText("0");}
            final int quantity = Integer.parseInt(amount.getText().toString());
            hideKB(this);
            identifier("D",id,quantity);
        });

        //商品價格
        TextView price = new TextView(this);
        LinearLayout.LayoutParams pricep = new LinearLayout.LayoutParams(DP(120),DP(30));
        price.setText("價格: $"+Pprice.get(ID));
        price.setTextSize(18f);
        price.setLayoutParams(picprip);

        //商品訊息區
        LinearLayout proinf = new LinearLayout(this);
        LinearLayout.LayoutParams proinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1f
        );
        proinf.setOrientation(LinearLayout.VERTICAL);
        proinf.setLayoutParams(proinfp);

        //商品名稱
        TextView proname = new TextView(this);
        LinearLayout.LayoutParams pronamep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        proname.setText(Pname.get(ID));
        proname.setTextSize(18f);
        proname.setClickable(true);
        proname.setLayoutParams(pronamep);
        proname.setId(5*ID+1);

        //購買資訊
        LinearLayout buyinf = new LinearLayout(this);
        LinearLayout.LayoutParams buyinfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        buyinf.setOrientation(LinearLayout.HORIZONTAL);
        buyinf.setLayoutParams(buyinfp);

        //數量:[標籤]
        TextView amount_label = new TextView(this);
        LinearLayout.LayoutParams amount_labelp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        amount_label.setText("數量：");
        amount_label.setTextSize(18f);
        amount_label.setLayoutParams(amount_labelp);

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
                LinearLayout.LayoutParams.WRAP_CONTENT,0.5f
        );
        detailp.setMarginEnd(20);
        detail.setText("修改");
        detail.setTextSize(18f);
        detail.setBackgroundResource(R.drawable.rounded_button);
        detail.setTextColor(Color.parseColor("#FFFFFF"));
        detail.setLayoutParams(detailp);
        detail.setId(5*ID+3);
        detail.setOnClickListener(v -> {
            final int id = ID;
            if(amount.getText().toString().trim().isEmpty()){amount.setText("0");}
            final int quantity = Integer.parseInt(amount.getText().toString());
            hideKB(this);
            identifier("D",id,quantity);
            amount.setText("");
        });

        //上架按鈕
        Button buybtn = new Button(this);
        LinearLayout.LayoutParams buybtnp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,0.5f
        );
        buybtn.setText("上架");
        buybtn.setTextSize(18f);
        buybtn.setBackgroundResource(R.drawable.rounded_button);
        buybtn.setTextColor(Color.parseColor("#FFFFFF"));
        buybtn.setLayoutParams(buybtnp);
        buybtn.setId(5*ID+4);
        buybtn.setOnClickListener(v -> {
            final int id = ID;
            if(amount.getText().toString().trim().isEmpty()){amount.setText("0");}
            final int quantity = Integer.parseInt(amount.getText().toString());
            hideKB(this);
            identifier("R",id,quantity);
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
        buyinf.addView(amount);
        proinf.addView(buyinf);
        btnbox.addView(detail);
        btnbox.addView(buybtn);
        proinf.addView(btnbox);
        picpri.addView(propic);
        picpri.addView(price);
        frame.addView(picpri);
        frame.addView(proinf);
        ll.addView(frame);
        Log.v("test","card"+ID+"rendered");
    }

    //將dp轉換為px
    public static int DP(float dp){
        dp = dp * ((float) Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)dp;
    }
    //功能判斷器
    public void identifier(String act, int ID,int quantity){
        //檢視詳細資料
        if(act.equals("D")){
            //Log.v("test","您正在檢視第"+Pname.get(ID)+"的詳細資料");
            SellId=ID;
            Intent intent = new Intent(alter_product.this,alter_product_detail.class);
            startActivity(intent);
            finish();
        //修改商品資料
        }else if(act.equals("R")){
            //Log.v("test","您上架了"+quantity+"個"+Pname.get(ID));
            function = 1;
            SellId = ID;
            ReleseQuantity = quantity;
            //數量檢查
            if(quantity>0){
                ConnectMySql connectMySql = new ConnectMySql();
                connectMySql.execute("");
            }else{
                function = -1;
                popup(getApplicationContext(), "請至少上架一項商品");
            }
        }
    }

    //清空列表以確保商品資訊不會重複疊加
    public void clear(){
        Login.PID.clear();
        Pname.clear();
        Pprice.clear();
        Pamount.clear();
        PIMG.clear();
    }

    //清空資料陣列並返回首頁
    public void onBackPressed(){
        Log.v("test","excuse me------------------------------------------------");
        Intent intent = new Intent(alter_product.this, Home.class);
        startActivity(intent);
        clear();
        Log.v("test","pf is null (clr)=" + (pf==null));
        finish();
    }

    //將base64轉換為點陣圖
    public Bitmap ConvertToBitmap(int ID){
        try{
//            Log.v("test",PIMG.get(ID));
            byte[] imageBytes = Base64.decode(PIMG.get(ID), Base64.DEFAULT);
            Bitmap proimg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            int w = proimg.getWidth();
            int h = proimg.getHeight();
            Log.v("test","pic"+ID+" original = "+w+"*"+h);
            //比例調整
            int scale = 1;
            if(w>h && (w/120)>1 || h==w && (w/120)>1){
                scale = w/120;
                w = w/scale;
                h = h/scale;
            }else if(h>w && (h/120)>1){
                scale = h/120;
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
}
