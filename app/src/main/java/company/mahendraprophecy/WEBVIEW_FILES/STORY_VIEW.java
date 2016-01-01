package company.mahendraprophecy.WEBVIEW_FILES;

import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONObject;

import company.mahendraprophecy.R;


public class STORY_VIEW extends ActionBarActivity {


    LinearLayout header;
    TextView title;
    WebView webView;
    JSONObject jsonObject;

    String device_id = "", auth_token = "582846f37273cf8f4b0cc17d67c34c475678b234", hash_key = "88e3c22307d988d6dfc959400d28428312344321", cat_id = "", id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);
        header = (LinearLayout) findViewById(R.id.storyview_header);
        title = (TextView) findViewById(R.id.storyview_title);
        webView = (WebView) findViewById(R.id.storyview);
        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            //JSONObject object = new JSONObject(getIntent().getExtras().getString("object"));
            cat_id = getIntent().getExtras().getString("cat_id");
            id = getIntent().getExtras().getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }


        ((ImageButton)findViewById(R.id.storyview_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        header.setBackgroundColor(Color.parseColor(getIntent().getExtras().getString("color")));
        title.setText(getIntent().getExtras().getString("title"));

        String Url = "";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.mahendraprophecy.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("post.php")
                .appendQueryParameter("device_id", device_id)
                .appendQueryParameter("auth_token", auth_token)
                .appendQueryParameter("hash_key", hash_key)
                .appendQueryParameter("cat_id", cat_id)
                .appendQueryParameter("id", id)
                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

        Url = builder.build().toString();

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                ((ProgressWheel)findViewById(R.id.storyview_progress_wheel)).setVisibility(View.INVISIBLE);
            }
        });
    }


}
