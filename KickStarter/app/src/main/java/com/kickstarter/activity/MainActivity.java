package com.kickstarter.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.transition.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kickstarter.R;
import com.kickstarter.adapter.JSONAdapter;
import com.kickstarter.beans.JSONBeans;
import com.kickstarter.helper.ContextHelper;
import com.kickstarter.helper.HttpHandler;
import com.kickstarter.helper.URLHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    public static ContextHelper ch;

    public static ProgressDialog progressDialog = null;

    public static Dialog dialog_category;
    public static Button dialog_c_button;
    public static Spinner dialog_c_spinner;
    ArrayAdapter<String> category_spinner_adapter;
    ArrayList<String> category_list = new ArrayList<>();

    public static Dialog dialog_publisher;
    public static Button dialog_p_button;
    public static Spinner dialog_p_spinner;
    ArrayAdapter<String> publisher_spinner_adapter;
    ArrayList<String> publisher_list = new ArrayList<>();

    ArrayList<JSONBeans> temp_list = new ArrayList<>();

    public static ArrayList<JSONBeans> arrayList = new ArrayList<>();
    private ListView listView;
    public static JSONAdapter jsonAdapter;

    boolean flag_filter = false;
    ;

    String jsonStr;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            fetchDataFromServer();
            return true;
        }
        if (id == R.id.asc) {
            Collections.sort(arrayList, new Comparator<JSONBeans>() {
                public int compare(JSONBeans o1, JSONBeans o2) {
                    return (int) (o1.getTimestamp() - o2.getTimestamp());
                }
            });
            jsonAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.desc) {
            Collections.sort(arrayList, new Comparator<JSONBeans>() {
                public int compare(JSONBeans o1, JSONBeans o2) {
                    return (int) (o2.getTimestamp() - o1.getTimestamp());
                }
            });
            jsonAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.action_category) {
            dialog_category.show();
            return true;
        }
        if (id == R.id.action_publisher) {
            dialog_publisher.show();
            return true;
        }
        if (id == R.id.action_offline) {
            SharedPreferences preferences = getSharedPreferences("Myfile", MODE_PRIVATE);
            String json = preferences.getString("jsonObj", null);
            if (json == null) {
                Toast.makeText(this, "No Cache present!", Toast.LENGTH_SHORT).show();
            } else {
                fetchFromPreferences(json);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ch = new ContextHelper();
        ch.setContext(this);

        init();
        addListener();

        jsonAdapter = new JSONAdapter(MainActivity.this, R.layout.listitem, arrayList);
        listView.setAdapter(jsonAdapter);

        fetchDataFromServer();
    }

    private void addListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ItemActivity.class);
                i.putExtra("uri", arrayList.get(position).getUri().toString());
                startActivity(i);
            }
        });
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list);

        initDialog();
    }

    private void initDialog() {
        dialog_category = new Dialog(MainActivity.this);
        dialog_category.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_category.setContentView(R.layout.dialog_category);
        dialog_c_spinner = (Spinner) dialog_category.findViewById(R.id.spinner_category);
        category_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, category_list);
        dialog_c_spinner.setAdapter(category_spinner_adapter);
        category_list.add("None");

        dialog_c_button = (Button) dialog_category.findViewById(R.id.category_button);
        dialog_c_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_list.clear();
                if (dialog_c_spinner.getSelectedItem().equals("None")) {
                    Toast.makeText(MainActivity.this, "Nothing Selected!", Toast.LENGTH_SHORT).show();
                } else {
                    flag_filter = true;
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getCategory().equals(dialog_c_spinner.getSelectedItem())) {
                            temp_list.add(arrayList.get(i));
                        }
                    }
                    jsonAdapter = new JSONAdapter(MainActivity.this, R.layout.listitem, temp_list);
                    listView.setAdapter(jsonAdapter);
                    jsonAdapter.notifyDataSetChanged();
                }

            }
        });

        dialog_publisher = new Dialog(MainActivity.this);
        dialog_publisher.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_publisher.setContentView(R.layout.dialog_publisher);
        dialog_p_spinner = (Spinner) dialog_publisher.findViewById(R.id.spinner_publisher);
        publisher_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, publisher_list);
        dialog_p_spinner.setAdapter(publisher_spinner_adapter);
        publisher_list.add("None");

        dialog_p_button = (Button) dialog_publisher.findViewById(R.id.publisher_button);
        dialog_p_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_list.clear();
                if (dialog_p_spinner.getSelectedItem().equals("None")) {
                    Toast.makeText(MainActivity.this, "Nothing Selected!", Toast.LENGTH_SHORT).show();
                } else {
                    flag_filter = true;
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getPublisher().equals(dialog_p_spinner.getSelectedItem())) {
                            temp_list.add(arrayList.get(i));
                        }
                    }
                    jsonAdapter = new JSONAdapter(MainActivity.this, R.layout.listitem, temp_list);
                    listView.setAdapter(jsonAdapter);
                    jsonAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void fetchFromPreferences(String json) {
        arrayList.clear();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        parseJsonData(json);
    }

    private void fetchDataFromServer() {
        arrayList.clear();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
//
//        StringRequest request =
//                new StringRequest(Request.Method.GET, URLHelper.actorURL,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//
//                                progressDialog.cancel();
//                                String res = response;
//                                Log.d("asd", "onResponse: "+res);
//
//                                parseJsonData(response);
//
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                                progressDialog.cancel();
//                                Toast.makeText(MainActivity.this, "Error in network", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(request);

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                jsonStr = HttpHandler.makeServiceCall(URLHelper.actorURL);
                Log.d("123", "fetchDataFromServer: " + jsonStr);
                if (jsonStr != null) {
                    SharedPreferences preferences = getSharedPreferences("Myfile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("jsonObj", jsonStr);
                    editor.commit();
                }

                parseJsonData(jsonStr);
                return null;
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void parseJsonData(String response) {
        try {
            if (response == null) {
                ((Activity) MainActivity.ch.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.ch.getContext(), "Error in network", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);


                int id = jsonObject.getInt("ID");
                String title = jsonObject.getString("TITLE");
                Uri uri = null;
                if (jsonObject.has("URL")) {
                    uri = Uri.parse(jsonObject.getString("URL"));
                }
                String publisher = jsonObject.getString("PUBLISHER");
                String category = jsonObject.getString("CATEGORY");
                Uri hostname = null;
                if (jsonObject.has("HOSTNAME")) {
                    hostname = Uri.parse(jsonObject.getString("HOSTNAME"));
                }
                long timestamp = jsonObject.getLong("TIMESTAMP");


                JSONBeans jsonBean = new JSONBeans();

                jsonBean.setId(id);
                jsonBean.setTitle(title);
                jsonBean.setUri(uri);
                jsonBean.setCategory(category);
                jsonBean.setHostname(hostname);
                jsonBean.setPublisher(publisher);
                jsonBean.setTimestamp(timestamp);

                arrayList.add(jsonBean);
                if (!category_list.contains(category)) {
                    category_list.add(category);
                }
                if (!publisher_list.contains(publisher)) {
                    publisher_list.add(publisher);
                    Log.d("123", "parseJsonData: " + publisher);
                }
            }

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    jsonAdapter = new JSONAdapter(MainActivity.this, R.layout.listitem, arrayList);
                    listView.setAdapter(jsonAdapter);
                    jsonAdapter.notifyDataSetChanged();
                    category_spinner_adapter.notifyDataSetChanged();
                    publisher_spinner_adapter.notifyDataSetChanged();
                    MainActivity.this.findViewById(R.id.sadView).setVisibility(View.GONE);
                    progressDialog.cancel();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
            ((Activity) MainActivity.ch.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.ch.getContext(), "Error in network", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        if (flag_filter == true) {
            fetchDataFromServer();
            flag_filter = false;
        } else {
            super.onBackPressed();
        }
    }

}
