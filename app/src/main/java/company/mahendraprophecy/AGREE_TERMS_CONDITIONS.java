package company.mahendraprophecy;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import me.drakeet.materialdialog.MaterialDialog;
import company.mahendraprophecy.OTHERS.DASHBOARD;


public class AGREE_TERMS_CONDITIONS extends ActionBarActivity {

    static final String DISPLAY_MESSAGE_ACTION = "technovators.mahendraprophecy.DISPLAY_MESSAGE";
    JSONObject object;
    SharedPreferences pref, pref1;
    // Asyntask
    AsyncTask<Void, Void, String> mRegisterTask;
    static final String EXTRA_MESSAGE = "message";
    // Google project id
    static final String SENDER_ID = "276760959039";
    ProgressDialog dialog;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agree_terms_conditions);
        pref = getSharedPreferences("meta-data", MODE_PRIVATE);
        pref1 = getSharedPreferences("first_time", MODE_PRIVATE);

        dialog = new ProgressDialog(AGREE_TERMS_CONDITIONS.this);

        final Context context = AGREE_TERMS_CONDITIONS.this;
        mRegisterTask = new AsyncTask<Void, Void, String>() {
            List<NameValuePair> params;
            String result = ""
                    ,
                    url = "https://www.mahendraprophecy.com/api/v1/register-device.php?key=1c0d2809e0140c09c6003d098400d477";

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Registering Device");
                dialog.setCancelable(false);
                dialog.show();
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)));
                params.add(new BasicNameValuePair("gcm_id", regId));
            }

            @Override
            protected String doInBackground(Void... voids) {
                // Register on our server
                // On server creates a new user
                HttpEntity httpEntity = null;
                try {
                    HttpClient httpClient = createHttpClient();  // Default HttpClient
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    httpEntity = httpResponse.getEntity();
                    String entityResponse = EntityUtils.toString(httpEntity);
                    Log.e("Entity Response  : ", entityResponse + "\nUrl" + url);
                    result = entityResponse;
                } catch (Exception e) {
                    result = "Error : " + e;
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                if (s.startsWith("E")) {
                    Toast.makeText(getBaseContext(), "Error Registering Device", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(getBaseContext(), "Registered Device Sucessfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Error Registering Device\n" + s, Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(AGREE_TERMS_CONDITIONS.this, MAINSCREEN.class));
                        //new getMetaData().execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ((TextView) findViewById(R.id.agree)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = pref1.edit();
                editor.putInt("flag", 1);
                editor.commit();
                GCMRegistrar.checkDevice(AGREE_TERMS_CONDITIONS.this);
                registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));


                // Get GCM registration id
                regId = GCMRegistrar.getRegistrationId(AGREE_TERMS_CONDITIONS.this);
                Log.d("apna_gcm", "Attempt : " + regId);

                // Check if regid already presents
                if (regId.equals(""))
                {
                    dialog.setMessage("Registering Device");
                    dialog.setCancelable(false);
                    dialog.show();
                    Log.d("apna_gcm", "Not Present");
                    // Registration is not present, register now with GCM
                    GCMRegistrar.register(AGREE_TERMS_CONDITIONS.this, SENDER_ID);
                    Log.d("apna_gcm", "Regitered Called : " + regId);

                    int secondsDelayed = 4;
                    new Handler().postDelayed(new Runnable() {
                        public void run()
                        {
                            Log.d("apna_gcm", "Waiting For ID");
                            Log.d("apna_gcm", "Regiteration ID : " + regId);
                            regId = GCMRegistrar.getRegistrationId(AGREE_TERMS_CONDITIONS.this);
                            mRegisterTask.execute();
                            finish();
                        }
                    }, secondsDelayed * 1000);
                }
                else
                {
                    mRegisterTask.execute();
                }


            }
        });

        ((TextView) findViewById(R.id.exit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });


        WebView webView = (WebView) findViewById(R.id.agreement_webview);
        String Url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.mahendraprophecy.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("page.php")
                .appendQueryParameter("q", "terms")
                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

        Url = builder.build().toString();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);
    }

    boolean isLatestVersion(String version) {
        String current_version = pref.getString("version", null);

        if (current_version == null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("version", version);
            return true;
        } else if (current_version.equals(version)) {
            return true;
        }
        return false;
    }

    void updateApp() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog
                .setTitle("Version Control")
                .setCanceledOnTouchOutside(false)
                .setMessage("Hi, Mahendra Prophecy upgrades it's functionality to improve itself and give the best experience to the users.\nCurrent Version of Application is not the latest version. Please Update it to get the Best of Us.")
                .setPositiveButton("Update Now", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    class getMetaData extends AsyncTask<Void, Void, String> {
        String result = "", url = "https://www.mahendraprophecy.com/api/v1/meta.php?key=1c0d2809e0140c09c6003d098400d477";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpEntity httpEntity = null;

            try {
                HttpClient httpClient = createHttpClient();  // Default HttpClient
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);
            } catch (Exception e) {
                result = "Error : " + e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            try {
                object = new JSONObject(s);

                if (isLatestVersion(object.getString("version"))) {
                    Intent intent = new Intent(AGREE_TERMS_CONDITIONS.this, DASHBOARD.class);
                    intent.putExtra("meta", s);
                    startActivity(intent);
                } else {
                    updateApp();
                }
            } catch (Exception e) {
                Log.d("Error", "" + e);
                Toast.makeText(getBaseContext(), "Error : " + e, Toast.LENGTH_LONG).show();
            }

        }


    }


    public HttpClient createHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }


    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            // Showing received message

            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

}

