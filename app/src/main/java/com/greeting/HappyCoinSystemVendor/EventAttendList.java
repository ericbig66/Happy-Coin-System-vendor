package com.greeting.HappyCoinSystemVendor;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static com.greeting.HappyCoinSystemVendor.Login.acc;
import static com.greeting.HappyCoinSystemVendor.Login.pass;
import static com.greeting.HappyCoinSystemVendor.Login.url;
import static com.greeting.HappyCoinSystemVendor.Login.user;

/**
 * A simple {@link Fragment} subclass.
 * 此檔案為diary之子檔(子頁籤)
 */
public class EventAttendList extends Fragment {
    private ArrayList<String> Aname = new ArrayList<>();//活動名稱
    private ArrayList<String> Mail = new ArrayList<>(); //客戶帳號***目前客戶尚不知道其UUID
    private ArrayList<String> Name = new ArrayList<>(); //客戶姓名
    private ArrayList<String> Sign = new ArrayList<>(); //簽到時間
    private ArrayList<String> catagory = new ArrayList<>();//活動選擇器
    Spinner chooser;//活動名稱選擇器
    TableLayout tradeData;//交易資料顯示處
    public EventAttendList(){
        // Required empty public constructor
    }
    //建立實體與主檔溝通用
    public static EventAttendList newInstance() {
        return new EventAttendList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        View view = inflater.inflate(R.layout.fragment_diary,container, false);
        //定義區
        tradeData = view.findViewById(R.id.tradeData);
        chooser = view.findViewById(R.id.chooser);
        chooser.setVisibility(View.VISIBLE);
        //設定區
        clear();//清除陣列資料避免疊加
          //新增表頭
        Aname.add("活動名稱　");
        Mail.add("客戶帳號　");
        Name.add("客戶姓名　");
        Sign.add("簽到時間");
          //連接資料庫取得報名資料
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute("");

          //取得所選取的活動
        chooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderTable(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;//繪製頁面
    }

    //取得報名資料
    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res="";//錯誤信息儲存變數
        //開始執行動作
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        //查詢執行動作(不可使用與UI相關的指令)
        @Override
        //取得資料
        protected String doInBackground(String... strings) {
            try {
                //連接資料庫
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT a.actName, CONCAT(SUBSTR(c.acc,1,3),SUBSTR(c.acc,-3)) AS account, c.name, af.signTime from application_form af join activity a ON af.activityID = a.id join client c ON af.clientID = c.ID join vendor v on a.HostId = v.VID\n" +
                        "where v.acc = '"+acc+"';");
                //將查詢結果裝入陣列
                while(rs.next()){
//                    String star = "";
//                    for(int i = 0 ; i<rs.getInt("EMR") ; i++){star+="*";}
                    Aname.add(rs.getString(1)+"　");
                    Mail.add(rs.getString(2)+"　");
//                    star="";
//                    for(int i = 0 ; i<rs.getInt("F_name") ; i++){star+="*";}
                    Name.add(rs.getString(3)+"　");
                    Sign.add(rs.getString(4));
                }
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery("select distinct actName from activity a, vendor v where v.vid = a.HostId and v.acc = '"+acc+"'");
                while (rs2.next()){
                    catagory.add(rs2.getString(1));
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
            Log.v("test","onpost execute: "+result);
            //將取得的活動類別加入選單
            ArrayAdapter<String> actName= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, catagory);
            actName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            chooser.setAdapter(actName);
//            Log.v("test","YOUR RESULT ="+result);
//            renderTable();
        }
    }

    //繪製表格
    private void renderTable(int position){
        tradeData.removeAllViews();
        for(int row = 0 ; row < Aname.size() ; row++ ){
            Log.v("test",Aname.get(row)+" = "+catagory.get(position) +"==>"+ Aname.get(row).equals(catagory.get(position)));
            if (!Aname.get(row).equals(catagory.get(position)+"　") && row!=0){continue;}
//                Toast.makeText(Diary.this,"第"+row+"列建構中",Toast.LENGTH_SHORT).show();
            //新增一列
            TableRow tr = new TableRow(getActivity());
            //新增一個TextView
            TextView t1 = new TextView(getActivity());
            TextView t2 = new TextView(getActivity());
            TextView t3 = new TextView(getActivity());
            TextView t4 = new TextView(getActivity());
            //設定TextView的文字
            t1.setText(Aname.get(row));
            t2.setText(Mail.get(row));
//                Log.v("test",trade.get(row));
            t3.setText(Name.get(row));
            t4.setText(Sign.get(row));
            //將TextView放入列
            tr.addView(t1);
            tr.addView(t2);
            tr.addView(t3);
            tr.addView(t4);
            //將整列加入預先建立的TableLayout中
            tradeData.addView(tr,new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
    //清除陣列資料避免活動報名資訊重複疊加
    public void clear(){
        Aname.clear();
        Mail.clear();
        Name.clear();
        Sign.clear();
        catagory.clear();
    }
}

