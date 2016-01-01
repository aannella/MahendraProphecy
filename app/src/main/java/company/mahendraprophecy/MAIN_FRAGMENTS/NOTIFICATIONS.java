package company.mahendraprophecy.MAIN_FRAGMENTS;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import company.mahendraprophecy.R;
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class NOTIFICATIONS extends android.support.v4.app.Fragment {

    WebView webView;
    boolean loggedin;
    SharedPreferences sharedpreferences;
    LinearLayout loginLayout;
    TextView login_register;
    ProgressWheel progressWheel;


    public NOTIFICATIONS() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mainfragment_notifications, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            /*
           http://www.mahendraprophecy.com/api/v1/messages.php?&key={API_KEY_HERE}&auth_token={AUTH_TOKEN_HERE}&device_id={DEVICE_ID_HERE}

             */


        login_register= (TextView) getActivity().findViewById(R.id.notification_login_register);
        loginLayout = (LinearLayout) getActivity().findViewById(R.id.notification_loginlayout);
        sharedpreferences = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
        progressWheel = (ProgressWheel) getActivity().findViewById(R.id.notification_progress_wheel);
        webView= (WebView) getActivity().findViewById(R.id.notifications_webview);
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
                startActivity(new Intent(getActivity(), REGISTER_LOGIN.class));
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
                    .appendQueryParameter("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID))
                    .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

            Url = builder.build().toString();
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(Url);
        } else {
            loginLayout.setVisibility(View.VISIBLE);
        }

    }
}
