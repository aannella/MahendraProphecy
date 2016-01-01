package company.mahendraprophecy.WEBVIEW_FILES;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.pnikosis.materialishprogress.ProgressWheel;

import company.mahendraprophecy.R;


public class ECONOMIC_CALENDAR extends ActionBarActivity {
    ProgressWheel progressWheel;
    WebView webView;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_calendar);

        ((ImageButton)findViewById(R.id.calendar_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        webView= (WebView) findViewById(R.id.calendar_webview);
        progressWheel= (ProgressWheel) findViewById(R.id.calendar_progress_wheel);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                progressWheel.setVisibility(View.INVISIBLE);
            }
        });

        progressWheel.setVisibility(View.VISIBLE);
        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
        String Url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.mahendraprophecy.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("calendar.php")
                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                .appendQueryParameter("auth_token", sharedpreferences.getString("auth_token", null))
                .appendQueryParameter("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        Url = builder.build().toString();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);
    }

}
