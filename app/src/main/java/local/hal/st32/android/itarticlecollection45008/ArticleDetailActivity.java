/**
 * 記事詳細画面
 */
package local.hal.st32.android.itarticlecollection45008;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ArticleDetailActivity extends AppCompatActivity {

    private HashMap<String,String> result = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        //戻る矢印
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("記事詳細画面");

        Intent intent = getIntent();
        result = (HashMap<String,String>) intent.getSerializableExtra("map");

        TextView txName = (TextView) findViewById(R.id.name);
        txName.setText(result.get("last_name") + " " + result.get("first_name"));

        TextView txStudenid = (TextView) findViewById(R.id.studenid);
        txStudenid.setText(result.get("student_id"));

        TextView txSeatno = (TextView) findViewById(R.id.seatno);
        txSeatno.setText(result.get("seat_no"));

        TextView txCreatedAt = (TextView) findViewById(R.id.created_at);
        txCreatedAt.setText(result.get("created_at"));

        TextView txTitle = (TextView) findViewById(R.id.title);
        txTitle.setText(result.get("title"));

        TextView txUrl = (TextView) findViewById(R.id.url);
        txUrl.setText(result.get("url"));

        TextView txComment = (TextView) findViewById(R.id.comment);
        txComment.setText(result.get("comment"));




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;



        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickUrl(View view) {



        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.get("url")));
        startActivity(intent);

    }
}
