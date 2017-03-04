/**
 * 記事登録画面
 */
package local.hal.st32.android.itarticlecollection45008;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ArticleAddActivity extends AppCompatActivity {

    private String URL = "http://hal.architshin.com/st32/insertItArticle.php";

    StudentInfoClass studentInfo = new StudentInfoClass();

    private String lastName = studentInfo.getLastName();
    private String firstName = studentInfo.getFirstName();
    private String studentID = studentInfo.getStudentid();
    private String seatNO = studentInfo.getSeatno();

    private String title = "";
    private String url = "";
    private String comment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_add);

        //戻る矢印
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("記事登録画面");

        //学生情報を表示
        TextView tvName = (TextView) findViewById(R.id.name);
        tvName.setText(lastName + firstName);

        TextView tvSudentID = (TextView) findViewById(R.id.studenid);
        tvSudentID.setText(studentID);

        TextView tvSeatNO = (TextView) findViewById(R.id.seatno);
        tvSeatNO.setText(seatNO);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;


            case R.id.btnAdd:

                EditText edTitle = (EditText) findViewById(R.id.inputTitle);
                title = edTitle.getText().toString();

                EditText edURL = (EditText) findViewById(R.id.inputURL);
                url = edURL.getText().toString();

                EditText edComment = (EditText) findViewById(R.id.inputComment);
                comment = edComment.getText().toString();

                System.out.println("情報" + title + url + comment);

                RestAccess access = new RestAccess();
                access.execute(URL, lastName, firstName, studentID, seatNO, title, url, comment);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private class RestAccess extends AsyncTask<String, String, String> {
        private static final String DEBUG_TAG = "RestAccess";

        private TextView _tvProcess;

        private boolean _success = false;


        @Override
        public String doInBackground(String... params) {

            String urlStr = params[0];
            String lastname = params[1];
            String firstname = params[2];
            String studentid = params[3];
            String seatno = params[4];
            String title = params[5];
            String url = params[6];
            String comment = params[7];


            String postData = "lastname=" + lastname +
                    "&firstname=" + firstname +
                    "&studentid=" + studentid +
                    "&seatno=" + seatno +
                    "&title=" + title +
                    "&url=" + url +
                    "&comment=" + comment;

            System.out.println("postData : " + postData);
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                publishProgress(getString(R.string.msg_send_before));
                URL urls = new URL(urlStr);
                con = (HttpURLConnection) urls.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();
                int status = con.getResponseCode();

                if (status != 200) {
                    throw new IOException("ステータスコード:" + status);
                }
                publishProgress(getString(R.string.msg_send_after));
                is = con.getInputStream();

                result = is2String(is);
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


        @Override
        public void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        public void onPostExecute(String result) {
            if (_success) {
                String title = "";
                String url = "";
                String comment = "";
                String name = "";
                String studentid = "";
                String seatno = "";
                String status = "";
                String msg = "";
                String timestamp = "";

                onProgressUpdate(getString(R.string.msg_parse_before));

                try {
                    JSONObject rootJSON = new JSONObject(result);
                    title = rootJSON.getString("title");
                    url = rootJSON.getString("url");
                    comment = rootJSON.getString("comment");
                    name = rootJSON.getString("name");
                    studentid = rootJSON.getString("studentid");
                    seatno = rootJSON.getString("seatno");
                    status = rootJSON.getString("status");
                    msg = rootJSON.getString("msg");
                    timestamp = rootJSON.getString("timestamp");

                } catch (JSONException ex) {
                    onProgressUpdate(getString(R.string.msg_err_parse));
                    Log.e(DEBUG_TAG, "JSON解析失敗", ex);
                }

                if (status.equals("0") || status.isEmpty()) {
                    onProgressUpdate(getString(R.string.msg_parse_after));

                    String message = msg + "\n" ;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ArticleAddActivity.this);
                    builder.setTitle(getString(R.string.dlg_title));
                    builder.setMessage(message);
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogButtonClickListener());
                    AlertDialog dialog = builder.create();

                    dialog.show();
                }
                else
                {
                    finish();
                }

            }
        }



    }
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

    private class DialogButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    }
}
