package company.mahendraprophecy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.pnikosis.materialishprogress.ProgressWheel;

import company.mahendraprophecy.BOOKS.BOOKS;


public class CONTACT extends ActionBarActivity
{


    ProgressWheel progressWheel;
    WebView webView;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        progressWheel = (ProgressWheel) findViewById(R.id.contact_progress_wheel);
        webView= (WebView) findViewById(R.id.contact_webview);

        ((LinearLayout)findViewById(R.id.book)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CONTACT.this, BOOKS.class));
            }
        });

        ((LinearLayout)findViewById(R.id.market)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CONTACT.this,MARKET.class));
            }
        });

        ((LinearLayout)findViewById(R.id.message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CONTACT.this,NOTIFICATION.class));
            }
        });

        ((LinearLayout)findViewById(R.id.services)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CONTACT.this,MAINSCREEN.class));
            }
        });

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
        /*
        http://www.mahendraprophecy.com/api/v1/markets.php?key={API_KEY_HERE}&auth_token={AUTH_TOKEN_HERE}&device_id={DEVICE_ID_HERE}
         */
        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
        String Url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.mahendraprophecy.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("contact.php")
                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

        Url = builder.build().toString();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);

        ((ImageButton)findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}


