package company.mahendraprophecy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import company.mahendraprophecy.BOOKS.BOOKS;
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;


public class NOTIFICATION extends ActionBarActivity {

    WebView webView;
    boolean loggedin;
    SharedPreferences sharedpreferences;
    LinearLayout loginLayout;
    TextView login_register;
    ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfragment_notifications);


        login_register = (TextView) findViewById(R.id.notification_login_register);
        loginLayout = (LinearLayout) findViewById(R.id.notification_loginlayout);
        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
        progressWheel = (ProgressWheel) findViewById(R.id.notification_progress_wheel);
        webView = (WebView) findViewById(R.id.notifications_webview);


        ((LinearLayout)findViewById(R.id.book)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NOTIFICATION.this, BOOKS.class));
            }
        });

        ((LinearLayout)findViewById(R.id.market)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NOTIFICATION.this,MARKET.class));
            }
        });

        ((LinearLayout)findViewById(R.id.services)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NOTIFICATION.this,MAINSCREEN.class));
            }
        });

        ((LinearLayout)findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NOTIFICATION.this,CONTACT.class));
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


        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NOTIFICATION.this, REGISTER_LOGIN.class));
            }
        });


        if (sharedpreferences.getString("isLoggedIn", "").matches("")) {
            loggedin = false;
        } else {
            loggedin = true;
        }


        if (loggedin) {
            loginLayout.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
            String Url;
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("www.mahendraprophecy.com")
                    .appendPath("api")
                    .appendPath("v1")
                    .appendPath("messages.php")
                    .appendQueryParameter("auth_token", sharedpreferences.getString("auth_token", null))
                    .appendQueryParameter("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                    .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

            Url = builder.build().toString();
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(Url);
        } else {
            loginLayout.setVisibility(View.VISIBLE);
        }

        ((ImageButton) findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}
