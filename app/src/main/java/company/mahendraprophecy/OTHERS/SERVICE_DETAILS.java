package company.mahendraprophecy.OTHERS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONObject;

import company.mahendraprophecy.R;
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;


public class SERVICE_DETAILS extends ActionBarActivity {

    JSONObject jsonObject;
    TextView buy;
    String id;
    WebView webView;
    ProgressWheel progressWheel;
    boolean loggedin;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        id = getIntent().getExtras().getString("id");

        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("isLoggedIn", "").matches("")) {
            loggedin = false;
        } else {
            loggedin = true;
        }
        progressWheel = (ProgressWheel) findViewById(R.id.services_progress_wheel);
        buy = (TextView) findViewById(R.id.buyservice);
        webView = (WebView) findViewById(R.id.service_webview);


        ((ImageButton) findViewById(R.id.backToservices)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.servicesBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressWheel.setVisibility(View.VISIBLE);
                String Url;
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("www.mahendraprophecy.com")
                        .appendPath("api")
                        .appendPath("v1")
                        .appendPath("service-details.php")
                        .appendQueryParameter("id", id)
                        .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

                Url = builder.build().toString();
                webView.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webView.loadUrl(Url);

                YoYo.with(Techniques.Pulse.SlideInUp)
                        .playOn(((LinearLayout) findViewById(R.id.buyLayout)));
            }
        });


        /*
        https://www.mahendraprophecy.com/api/v1/service-details.php?id={service_ID_HERE}&key={KEY_HERE}

        services Buy Now Link:
        http://www.mahendraprophecy.com/api/v1/checkout.php?&p=services&id={service_ID_HERE}&key={API_KEY_HERE}&auth_token={AUTH_TOKEN_HERE}&device_id={DEVICE_ID_HERE}

         */

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                progressWheel.setVisibility(View.INVISIBLE);
            }
        });

        String Url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.mahendraprophecy.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("service-details.php")
                .appendQueryParameter("id", id)
                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

        Url = builder.build().toString();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);


        try {

            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (loggedin) {
                        YoYo.with(Techniques.SlideOutDown)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        SharedPreferences preferences = getSharedPreferences("meta", MODE_PRIVATE);
                                        String Url;
                                        Uri.Builder builder = new Uri.Builder();
                                        builder.scheme("http")
                                                .authority("www.mahendraprophecy.com")
                                                .appendPath("api")
                                                .appendPath("v1")
                                                .appendPath("checkout.php")
                                                .appendQueryParameter("p", "services")
                                                .appendQueryParameter("id", id)
                                                .appendQueryParameter("o", "NEW")
                                                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                                                .appendQueryParameter("auth_token", preferences.getString("auth_token", ""))
                                                .appendQueryParameter("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                                        Url = builder.build().toString();
                                        progressWheel.setVisibility(View.VISIBLE);
                                        webView.loadUrl(Url);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .playOn(((LinearLayout) findViewById(R.id.buyLayout)));

                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SERVICE_DETAILS.this);
                        AlertDialog dialog;
                        builder
                                .setTitle("Authentication Alert !")
                                .setMessage("Please Login Or Register to buy this service !");
                        builder.setPositiveButton("LOGIN/REGISTER", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(SERVICE_DETAILS.this,REGISTER_LOGIN.class));
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error :" + e, Toast.LENGTH_LONG).show();
        }
    }
}