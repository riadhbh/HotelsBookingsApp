package com.example.riadh.hotelsbookings.AdminPack;
import android.app.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import android.support.v7.app.*;
import android.view.View;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.riadh.hotelsbookings.CommonPack.BookingsConstant;
import com.example.riadh.hotelsbookings.CommonPack.Bookings_ListAdapter;
import com.example.riadh.hotelsbookings.DBConnection.Server_Host_Constant;
import com.example.riadh.hotelsbookings.DBConnection.MySingleton;
import com.example.riadh.hotelsbookings.R;
import java.util.*;


/**
 * Created by riadh on 8/22/516.
 */



public class Addhr extends Activity {

    ListView MyListV;
    TextView isempty;
    Button addbtn,delbtn,savebtn;
    Spinner cap_sp;
    TextView roomnb,roomcap;
    EditText edit;
    String MyhotelID;
    ArrayList<String> rooms_num;
    ArrayList<String> rooms_cap;
    private ArrayList<HashMap> list;
    ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;
    static String Capacity;
//    float roomprice;
void onload(){
    setContentView(R.layout.addhr);

    MyListV=(ListView)findViewById(R.id.addrerlv);
    isempty=(TextView)findViewById(R.id.addr_empty);

    addbtn= (Button) findViewById(R.id.addr_add);
    delbtn=(Button)findViewById(R.id.addr_del);
    savebtn=(Button)findViewById(R.id.addr_save);
    edit = (EditText) findViewById(R.id.txtItem);

    cap_sp=(Spinner)findViewById(R.id.addr_cap_sp);
    roomnb=(TextView)findViewById(R.id.addhr_txt1);
    roomcap=(TextView)findViewById(R.id.addhr_txt2);
    Capacity="1";
    rooms_num = new ArrayList<String>();
    rooms_cap = new ArrayList<String>();
    list = new ArrayList<HashMap>();
    //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
    build_list_header();
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
        MyhotelID = extras.getString("HOTEL_ID");
    }

    //final Dialog dialog = new Dialog(Addhrer.this);
//        dialog.setContentView(R.layout.getroom_price);
//        dialog.setTitle("Single room price for one night.");
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//
//        final EditText pricetxt=(EditText)dialog.findViewById(R.id.addhrprice);
//        Button submit =(Button)dialog.findViewById(R.id.getpsubmit);
//        Button quit=(Button)dialog.findViewById(R.id.quitadding);
//        roomprice=-1;
//
//        quit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                finish();
//            }
//        });

//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try{
//                    roomprice=Float.parseFloat(pricetxt.getText().toString());
//                } catch (NumberFormatException ex) {
//                    roomprice=0;
//                }
//
//                if(roomprice<=0)
//                    new AlertDialog.Builder(Addhrer.this)
//                            .setTitle("Oops !")
//                            .setIcon(R.drawable.caution)
//                            .setMessage("Sorry, this price is not authorised !")
//                            .setCancelable(false)
//                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            }).create().show();
//                else
//                    dialog.dismiss();
//
//            }
//        });
//
//        dialog.show();
    cap_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3)
        {
            Capacity=cap_sp.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    });


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String num="";
            try {
                num=edit.getText().toString();
            }catch (NullPointerException e){
                Show_empty_room_num_diag();
            }
        if(num.equals(""))
            Show_empty_room_num_diag();
        else{
            rooms_num.add(num);
            rooms_cap.add(Capacity);
            add_list_row(num,Capacity);
            edit.setText("");
            isempty.setVisibility(View.GONE);
            buildMylist(2);
            }
}
    };


    addbtn.setOnClickListener(listener);

    delbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(rooms_num.size()>0){
                int sz=rooms_num.size();
                rooms_num.remove(sz-1);
                rooms_cap.remove(sz-1);
                if(sz==1)
                    isempty.setVisibility(View.VISIBLE);

                list.remove(sz-1);
                buildMylist(2);
            }
        }
    });

    savebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressDialog = ProgressDialog.show(Addhr.this, "",
                    "Loading. Please wait...", true);

            for(int i=0;i<rooms_num.size();i++)
                add_1_room(rooms_num.get(i),rooms_cap.get(i));
            progressDialog.dismiss();
            finish();
        }
    });
}
    private void add_1_room(final String num,final String cap){
        String url= Server_Host_Constant.Host+"/hotelsbookingsapp/addhotelsrooms.php";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Error")){
                    Toast.makeText(getBaseContext(),response, Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(getBaseContext(),"Operation completed successfully", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"Error while establishing connection with server",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("numroom",num);
                params.put("idhotel", MyhotelID);
                params.put("capacity",cap);
                //params.put("price",String.valueOf(roomprice));
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            final Handler start = new Handler();

            start.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onload();

                    }
                }, 300);

        }catch(RuntimeException e){
        e.printStackTrace();
        }
    }


    void build_list_header(){
        roomnb.setText("Room number");
        roomcap.setText("Capacity");
        //2
    }

    private void buildMylist(int nb_cols){
        Bookings_ListAdapter adapter = new Bookings_ListAdapter(this, list,nb_cols);
        MyListV.setAdapter(adapter);
    }

    void add_list_row(String col1,String col2){
        HashMap temp = new HashMap();
        temp.put(BookingsConstant.FIRST_COLUMN,col1);
        temp.put(BookingsConstant.SECOND_COLUMN, col2);
        list.add(temp);
    }

void Show_empty_room_num_diag(){
    new AlertDialog.Builder(Addhr.this)
            .setTitle("Unknown room number")
            .setIcon(R.drawable.caution)
            .setMessage("Please fill the room number field!")
            .setCancelable(false)
            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Whatever...
                }
            }).create().show();
}
}
