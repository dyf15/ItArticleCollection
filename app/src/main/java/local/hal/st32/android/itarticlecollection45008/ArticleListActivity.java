/**
 * 記事リスト画面
 */
package local.hal.st32.android.itarticlecollection45008;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.rgb;

public class ArticleListActivity extends AppCompatActivity {

    private String URL = "http://hal.architshin.com/st32/getItArticlesList.php";

    //ListView list;

    private SwipeMenuListView smList;

    List<Map<String, String>> listResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_article_list);


        // list = (ListView) findViewById(R.id.list);
        //list.setOnItemClickListener(new ListItemClickListener());

        smList = (SwipeMenuListView) findViewById(R.id.list);
        smList.setOnItemClickListener(new ListItemClickListener());


        //SwipeMenu の　items追加
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {


                // view url のitem
                SwipeMenuItem viewItem = new SwipeMenuItem(
                        getApplicationContext());
                // 色設定
                viewItem.setBackground(new ColorDrawable(rgb(169,
                        169, 169)));
                
                viewItem.setWidth(130);
                // set item 名
                viewItem.setTitle("View");
                
                viewItem.setTitleSize(18);
                
                viewItem.setTitleColor(Color.WHITE);
                // menu　に　item 追加
                menu.addMenuItem(viewItem);


                // info item
                SwipeMenuItem infoItem = new SwipeMenuItem(
                        getApplicationContext());
                
                infoItem.setBackground(new ColorDrawable(rgb(0, 153,
                        255)));
                
                infoItem.setWidth(130);

                // icon　設定
                infoItem.setIcon(android.R.drawable.ic_menu_info_details);
 
                menu.addMenuItem(infoItem);


            }
        };

        // menu itemを追加
        smList.setMenuCreator(creator);


        //クリックリスナ
        smList.setOnMenuItemClickListener(new MenuItemClickListener());

        //left　swipe方向
        smList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        //非同期
        RestAccess access = new RestAccess();
        access.execute(URL);


    }
    @Override
    public void onResume()
    {
        super.onResume();
        //非同期
        RestAccess access = new RestAccess();
        access.execute(URL);


    }

    public void setLimit(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        EditText edlimitNum = (EditText) findViewById(R.id.limitNum);
        String strLimitNum = edlimitNum.getText().toString();


        if (!strLimitNum.isEmpty()) {
            try {
                int limitNum = Integer.parseInt(strLimitNum);

                String strURL = URL + "?limit=" + limitNum;
                System.out.println("URL : " + strURL);
                RestAccess access = new RestAccess();
                access.execute(strURL);

            } catch (NumberFormatException ex) {

            }
        } else {
            RestAccess access = new RestAccess();
            access.execute(URL);

        }


    }


    //件数指定に入力したものをクリア
    public void setLimitClear(View view) {
        EditText edlimitNum = (EditText) findViewById(R.id.limitNum);
        edlimitNum.getText().clear();

        RestAccess access = new RestAccess();
        access.execute(URL);
        System.out.println("クリアしたURL : " + URL);
        System.out.println("クリアしたlist : " + listResult);
    }


    public class MenuItemClickListener implements SwipeMenuListView.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

            System.out.println("position : " + smList.getItemAtPosition(position));
            HashMap<String, String> map = new HashMap<String, String>();
            map = (HashMap<String, String>) smList.getItemAtPosition(position);

            Intent intent = null;

            switch (index) {
                case 0:
                    // view

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map.get("url")));
                    startActivity(intent);
                    System.out.println("mapURL : " + map.get("url"));

                    break;
                case 1:
                    // info
                    intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                    intent.putExtra("map", map);
                    startActivity(intent);
                    break;
            }
            return false;
        }


    }


    private class RestAccess extends AsyncTask<String, String, String> {
        private static final String DEBUG_TAG = "RestAccess";

        private boolean _success = false;


        //非同期で実行したい処理
        @Override
        public String doInBackground(String... params) {
            String urlStr = params[0];
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";



            try {
                publishProgress(getString(R.string.msg_send_before));
                java.net.URL url = new URL(urlStr);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(urlStr.getBytes());
                os.flush();
                os.close();
                int status = con.getResponseCode();

                if (status != 200) {
                    throw new IOException("ステータスコード:" + status);
                }
                publishProgress(getString(R.string.msg_send_after));
                is = con.getInputStream();

                //Listに変換　resultに入れる
                result = is2String(is);
                // System.out.println("result結果" + result);
                _success = true;


            } catch (MalformedURLException ex) {
                publishProgress(getString(R.string.msg_err_send));
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            } catch (IOException ex) {
                publishProgress(getString(R.string.msg_err_send));
                Log.e(DEBUG_TAG, "通信失敗", ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ex) {
                    publishProgress(getString(R.string.msg_err_parse));
                }
            }
            return result;
        }

        //非同期処理中に実行したい処理
        @Override
        public void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        //非同期処理完了時に実行したい処理
        @Override
        public void onPostExecute(String result) {


            if (_success) {

                String id;
                String title;
                String url;
                String comment;
                String student_id;
                String seat_no;
                String last_name;
                String first_name;
                String created_at;

                String jsonStr = "";
                onProgressUpdate(getString(R.string.msg_parse_before));

                try {

                    //System.out.println("結果" + rootJSON);
                    JSONObject jsonResult = new JSONObject(result);
                    JSONArray rootJSON = jsonResult.getJSONArray("list");
                    //     String list =  object.getJSONObject("list");

                    //   object = rootJSON.getJSONArray("list");
                    listResult = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < rootJSON.length(); i++) {

                        JSONObject object = rootJSON.getJSONObject(i);
                        Map<String, String> map = new HashMap<String, String>();


                        id = object.getString("id");
                        title = object.getString("title");
                        url = object.getString("url");
                        comment = object.getString("comment");

                        student_id = object.getString("student_id");

                        seat_no = object.getString("seat_no");

                        last_name = object.getString("last_name");

                        first_name = object.getString("first_name");

                        created_at = object.getString("created_at");


                        map.put("id", id);
                        map.put("title", title);
                        map.put("url", url);
                        map.put("comment", comment);
                        map.put("student_id", student_id);
                        map.put("seat_no", seat_no);
                        map.put("last_name", last_name);
                        map.put("first_name", first_name);
                        map.put("created_at", created_at);


                       

                        listResult.add(map);


                    }


                    String[] from = {"title", "url"};
                    int[] to = {android.R.id.text1, android.R.id.text2};
                    SimpleAdapter adapter = new SimpleAdapter(ArticleListActivity.this, listResult, android.R.layout.simple_list_item_2, from, to);
                    smList.setAdapter(adapter);


                } catch (JSONException ex) {
                    onProgressUpdate(getString(R.string.msg_err_parse));
                    Log.e(DEBUG_TAG, "JSON解析失敗", ex);
                }

                onProgressUpdate(getString(R.string.msg_parse_after));


            }
        }


    }

    /**
     * InputStreamオブジェクトを文字列に変換するメソッド
     * これをListに変換
     *
     * @param is
     * @return
     * @throws IOException
     */
    private String is2String(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        StringBuffer sb = new StringBuffer();
        char[] b = new char[1024];
        int line;
        while (0 <= (line = reader.read(b))) {
            sb.append(b, 0, line);
        }
        return sb.toString();
    }


    /**
     * 新規ボタンが押された時のイベント処理用メソッド
     *
     * @param view 画面部品
     */
    public void onNewButtonClick(View view) {
        Intent intent = new Intent(ArticleListActivity.this, ArticleAddActivity.class);
        //  intent.putExtra("mode", MODE_INSERT);
        startActivity(intent);

    }


    /**
     * ListViewがタップされた時の処理
     */
    public class ListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            HashMap<String, String> map = new HashMap<String, String>();
            map = (HashMap<String, String>) listResult.get((int) id);
            Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
            intent.putExtra("map", map);
            startActivity(intent);


            System.out.println("id : " + (int) id);

            System.out.println();


        }


    }


}
