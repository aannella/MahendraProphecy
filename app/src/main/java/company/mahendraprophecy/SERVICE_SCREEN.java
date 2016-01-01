package company.mahendraprophecy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import company.mahendraprophecy.OTHERS.SERVICE_DETAILS;
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;
import company.mahendraprophecy.WEBVIEW_FILES.STORY_VIEW;

public class SERVICE_SCREEN extends ActionBarActivity
{
    TextView date;
    TextView details;
    TextView exceprt;
    TextView heading;
    String id;
    boolean loggedin;
    TextView login;
    TextView news;
    String newsid;
    ImageView photo;
    SharedPreferences sharedpreferences;
    TextView subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_screen);
        photo= (ImageView) findViewById(R.id.photo);
        login= (TextView) findViewById(R.id.login_text);
        news= (TextView) findViewById(R.id.news);
        date= (TextView) findViewById(R.id.date);
        details= (TextView) findViewById(R.id.details_button);
        exceprt= (TextView) findViewById(R.id.excerpt);
        heading= (TextView) findViewById(R.id.heading);
        subscribe= (TextView) findViewById(R.id.subscribe);
        id=getIntent().getExtras().getString("id");

        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);

        ((ImageButton)findViewById(R.id.back)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(sharedpreferences.getString("isLoggedIn", "").matches(""))
        {
            loggedin = false;
        }
        else
        {
            loggedin = true;
        }

        if(loggedin)
        {
            login.setVisibility(View.GONE);
        }
        else
        {
            login.setVisibility(View.VISIBLE);
        }


        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SERVICE_SCREEN.this,REGISTER_LOGIN.class));
            }
        });
        new AsyncTask<Void, Void, String>() {

            String Url,result="";

            @Override
            protected void onPreExecute() {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("www.mahendraprophecy.com")
                        .appendPath("api")
                        .appendPath("v1")
                        .appendPath("service-news.php")
                        .appendQueryParameter("id", id)
                        .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                        .appendQueryParameter("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                        .appendQueryParameter("auth_token", sharedpreferences.getString("auth_token", null));
                Url = builder.build().toString();
            }

            @Override
            protected String doInBackground(Void... voids) {
                HttpEntity httpEntity = null;

                try {
                    HttpClient httpClient = createHttpClient();  // Default HttpClient
                    HttpGet httpGet = new HttpGet(Url);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    httpEntity = httpResponse.getEntity();
                    Log.d("url",Url);
                    result = EntityUtils.toString(httpEntity);
                } catch (Exception e) {
                    result = "Error : " + e;
                }

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                if(!s.startsWith("E"))
                {
                    try
                    {
                        final JSONObject object=new JSONObject(s).getJSONObject("service");
                        newsid=object.getString("news_id");
                        heading.setText(object.getString("heading"));
                        date.setText(object.getString("date"));
                        news.setText(object.getString("news"));
                        exceprt.setText(object.getString("excerpt"));

                        Picasso.with(SERVICE_SCREEN.this)
                                .load(object.getString("photo"))
                                .into(photo);

                        details.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openDetails();
                            }
                        });

                        subscribe.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openDetails();
                            }
                        });

                        ((LinearLayout)findViewById(R.id.read)).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent x = new Intent(SERVICE_SCREEN.this, STORY_VIEW.class);
                                try
                                {
                                    x.putExtra("id",newsid);
                                    x.putExtra("cat_id",id);
                                    x.putExtra("color", object.getString("bgcolor"));
                                    x.putExtra("title", object.getString("title"));
                                    startActivity(x);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }


    void openDetails()
    {
        Intent x = new Intent(SERVICE_SCREEN.this, SERVICE_DETAILS.class);
        try
        {
            x.putExtra("id", id);
            startActivity(x);
        } catch (Exception e) {
            e.printStackTrace();
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

}