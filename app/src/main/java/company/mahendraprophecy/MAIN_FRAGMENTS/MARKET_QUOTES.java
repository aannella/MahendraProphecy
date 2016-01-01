package company.mahendraprophecy.MAIN_FRAGMENTS;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pnikosis.materialishprogress.ProgressWheel;

import company.mahendraprophecy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MARKET_QUOTES extends android.support.v4.app.Fragment {


    ProgressWheel progressWheel;
    WebView webView;
    SharedPreferences sharedpreferences;

    public MARKET_QUOTES() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mainfragment_market_quotes, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressWheel = (ProgressWheel) getActivity().findViewById(R.id.marketquotes_progress_wheel);
        webView= (WebView) getActivity().findViewById(R.id.marketquotes_webview);

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
        sharedpreferences = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
        String Url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.mahendraprophecy.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("markets.php")
                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                .appendQueryParameter("auth_token", sharedpreferences.getString("auth_token", null))
                .appendQueryParameter("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

        Url = builder.build().toString();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);

    }
}
