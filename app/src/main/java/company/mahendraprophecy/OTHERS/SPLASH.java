package company.mahendraprophecy.OTHERS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

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
import org.json.JSONArray;
import org.json.JSONObject;

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

import me.drakeet.materialdialog.MaterialDialog;
import company.mahendraprophecy.AGREE_TERMS_CONDITIONS;
import company.mahendraprophecy.MAINSCREEN;
import company.mahendraprophecy.R;


public class SPLASH extends ActionBarActivity {

    ProgressWheel wheel;
    TextView progressText;
    JSONObject object;
    JSONArray array;
    SharedPreferences pref, pref1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressText = (TextView) findViewById(R.id.progressText);
        pref = getSharedPreferences("meta-data", MODE_PRIVATE);
        pref1 = getSharedPreferences("first_time", MODE_PRIVATE);

        boolean first_time = false;

        if (pref1.getInt("flag",0)==0)
        {
            first_time = true;
        }


        if (first_time)
        {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SPLASH.this,AGREE_TERMS_CONDITIONS.class));
                    finish();
                }
            }, secondsDelayed * 1000);

        }
        else
        {
            int secondsDelayed = 2;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SPLASH.this, MAINSCREEN.class));
                    finish();
                }
            }, secondsDelayed * 1000);



            //
            //new getMetaData().execute();
        }
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

            try
            {
                HttpClient httpClient = createHttpClient();  // Default HttpClient
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);
            } catch (Exception e)
            {
                result = "Error : " + e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            progressText.setText("Checking Version & User Credentials");
            try {
                object = new JSONObject(s);

                if (isLatestVersion(object.getString("version")))
                {
                    Intent intent = new Intent(SPLASH.this, DASHBOARD.class);
                    intent.putExtra("meta", s);
                    startActivity(intent);
                }
                else
                {
                    updateApp();
                }
            } catch (Exception e) {
                Log.d("Error", "" + e);
                Toast.makeText(getBaseContext(), "Error : " + e, Toast.LENGTH_LONG).show();
            }

        }
    }


    public HttpClient createHttpClient()
    {
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
        }
        catch (Exception e)
        {
            return new DefaultHttpClient();
        }
    }


    public class MySSLSocketFactory extends SSLSocketFactory
    {
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
