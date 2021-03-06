package com.greeting.HappyCoinSystemVendor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static com.greeting.HappyCoinSystemVendor.Login.RCdata;
import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;

/**
 * A simple {@link Fragment} subclass.
 * 此檔案為diary之子檔(子頁籤)
 */
public class SellDiary extends Fragment {
    private ArrayList<String> pname  = new ArrayList<>();   //品名
    private ArrayList<String> pprice  = new ArrayList<>();  //單價
    private ArrayList<String> pamount = new ArrayList<>();  //出售數量
    private ArrayList<String> total = new ArrayList<>();    //交易金額
    private ArrayList<String> selldate = new ArrayList<>(); //出售時間
    TableLayout tradeData;//交易資料顯示處
    Spinner chooser;//活動名稱選擇器(必須宣告，因為共用fragment)
    public SellDiary() {
        // Required empty public constructor
    }
    //建立實體與主檔溝通用
    public static SellDiary newInstance(){
        return new SellDiary();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
//        return textView;

        //定義區
        chooser = view.findViewById(R.id.chooser);
        chooser.setVisibility(View.GONE);
        tradeData = view.findViewById(R.id.tradeData);
        //設定區
        clear();//清除陣列資料避免疊加
          //新增表頭
        pname.add("品名　　");
        pprice.add("單價　　");
        pamount.add("數量　　");
        total.add("總額　　");
        selldate.add("交易日期  &  時間");
          //連接資料庫取得銷售紀錄
        SellDiary.ConnectMySql connectMySql = new SellDiary.ConnectMySql();
        connectMySql.execute("");
        return view;//繪製頁面
    }

        //取得銷售紀錄
        private class ConnectMySql extends AsyncTask<String, Void, String> {
            String res="";//錯誤信息儲存變數

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
//            Toast.makeText(getActivity(),"請稍後...",Toast.LENGTH_SHORT).show();
            }
            //取得交易紀錄
            @Override
            protected String doInBackground(String... strings) {
                try {
                    //連接資料庫
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, user, pass);
                    //建立查詢
                    //String result = "對方帳戶\t交易\t金額\t餘額\n";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("select s.productName, s.price, s.amount, s.date from sell_record s, vendor v where v.VID = s.VID and v.acc ='"+acc+"'");
                    //將查詢結果裝入陣列
                    while(rs.next()){
                        //result += rs.getString("paccount")+"\t"+rs.getString("state")+"\t$"+rs.getString("amount")+"\t$"+rs.getString("moneyLeft")+"\n";
                        pname.add(rs.getString("productName")+"  ");
                        pprice.add("$"+rs.getString("price"));
                        pamount.add(rs.getString("amount")+"  ");
                        total.add("$"+Integer.parseInt(rs.getString("amount"))*Integer.parseInt(rs.getString("price")));
                        selldate.add(rs.getString("date").substring(0,16));
                    }
                    return "0";
                }catch (Exception e){
                    e.printStackTrace();
                    res = e.toString();
                }
                return res;
            }
            //查詢後的結果將回傳於此
            @Override
            protected void onPostExecute(String result) {
                //dt.setText(result);
                renderTable();//繪製表格
            }

            //繪製表格
            private void renderTable(){
                for(int row = 0 ; row < pname.size() ; row++ ){
//                Toast.makeText(Diary.this,"第"+row+"列建構中",Toast.LENGTH_SHORT).show();
                    //新增一列
                    TableRow tr = new TableRow(getActivity());
                    //新增一個TextView
                    TextView t1 = new TextView(getActivity());
                    TextView t2 = new TextView(getActivity());
                    TextView t3 = new TextView(getActivity());
                    TextView t4 = new TextView(getActivity());
                    TextView t5 = new TextView(getActivity());
                    //設定TextView的文字
                    t1.setText(pname.get(row));
                    t2.setText(pprice.get(row));
//                Log.v("test",trade.get(row));
                    t3.setText(pamount.get(row));
                    t4.setText(total.get(row));
                    t5.setText(selldate.get(row));
                    //將TextView放入列
                    tr.addView(t1);
                    tr.addView(t2);
                    tr.addView(t3);
                    tr.addView(t4);
                    tr.addView(t5);
                    //將整列加入預先建立的TableLayout中
                    tradeData.addView(tr,new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
        }

        //清除陣列資料避免商品出售資訊重複疊加
        public void clear(){
            pname.clear();
            pprice.clear();
            pamount.clear();
            total.clear();
            selldate.clear();
        }
}
