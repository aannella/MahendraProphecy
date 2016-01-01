package company.mahendraprophecy.PROFILE;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import company.mahendraprophecy.LOCAL_DB_FILES.DATABASE_OPERATIONS;
import company.mahendraprophecy.R;
import company.mahendraprophecy.WEBVIEW_FILES.SUBSCRIPTION_WEBVIEW;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MY_SUBSCRIPTIONS extends android.support.v4.app.Fragment {

    RelativeLayout nosubscriptions;
    ListView listView;
    String[] servicesName, status, dateOfExpiry,id;

    public MY_SUBSCRIPTIONS() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_profile_subscriptions, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getActivity().findViewById(R.id.my_subscriptions);
        nosubscriptions = (RelativeLayout) getActivity().findViewById(R.id.no_subscriptions);

        new AsyncTask<Void, Void, String>() {

            String result = ""
                    ,
                    url = "";
            ProgressDialog progressDialog;

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
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching your subscriptions");
                progressDialog.setCancelable(false);
                progressDialog.show();

                SharedPreferences preferences = getActivity().getSharedPreferences("meta", getActivity().MODE_PRIVATE);
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("www.mahendraprophecy.com")
                        .appendPath("api")
                        .appendPath("v1")
                        .appendPath("mysubscriptions.php")
                        .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                        .appendQueryParameter("auth_token", preferences.getString("auth_token", ""))
                        .appendQueryParameter("hash_key", preferences.getString("hash_key", ""));
                url = builder.build().toString();
                Log.d("url", url);
            }

            @Override
            protected void onPostExecute(String s) {
                JSONObject object = null;
                try {
                    object = new JSONObject(s);
                    DATABASE_OPERATIONS database_operations = new DATABASE_OPERATIONS(getActivity());
                    database_operations.deleteInformation(database_operations.getWritableDatabase());
                    JSONArray subscriptions = object.getJSONArray("subscriptions");
                    for (int i = 0; i < subscriptions.length(); i++) {
                        JSONObject jsonObject = subscriptions.getJSONObject(i);
                        database_operations.putInformation(database_operations, jsonObject.getString("cat_id"), jsonObject.getString("name"), jsonObject.getString("expiry"), jsonObject.getString("status"));
                    }

                    DATABASE_OPERATIONS D_OP = new DATABASE_OPERATIONS(getActivity());
                    Cursor cursor = D_OP.getInformation(D_OP);
                    if (cursor.getCount() == 0) {
                        nosubscriptions.setVisibility(View.VISIBLE);
                    } else {
                        nosubscriptions.setVisibility(View.INVISIBLE);
                        servicesName = new String[cursor.getCount()];
                        status = new String[cursor.getCount()];
                        dateOfExpiry = new String[cursor.getCount()];
                        id = new String[cursor.getCount()];
                        cursor.moveToFirst();
                        int count = 0;
                        do {
                            id[count] = cursor.getString(0);
                            servicesName[count] = cursor.getString(1);
                            dateOfExpiry[count] = cursor.getString(2);
                            status[count] = cursor.getString(3);

                            count++;
                        } while (cursor.moveToNext());
                        cursor.close();
                        MY_SUBSCRIPTIONS_ADAPTER adapter = new MY_SUBSCRIPTIONS_ADAPTER(getActivity());
                        listView.setAdapter(adapter);
                    }

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }.execute();
    }


    class MY_SUBSCRIPTIONS_ADAPTER extends BaseAdapter {
        private Activity activity;
        private LayoutInflater layoutInflater;

        public MY_SUBSCRIPTIONS_ADAPTER(Activity activity) {
            this.activity = activity;
            layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return servicesName.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ListCell cell;
            {
                view = layoutInflater.inflate(R.layout.subcription_item, null);
                cell = new ListCell();
                cell.expiry = (TextView) view.findViewById(R.id.subscription_expiry);
                cell.status = (TextView) view.findViewById(R.id.subscription_status);
                cell.title = (TextView) view.findViewById(R.id.subscription_name);
                cell.renew= (TextView) view.findViewById(R.id.renew_subscription);
            }

            if (status[i].equals("Active")) {
                cell.status.setTextColor(Color.parseColor("#00BFA5"));
                cell.expiry.setTextColor(Color.parseColor("#00BFA5"));
                cell.renew.setBackgroundColor(Color.parseColor("#00BFA5"));
            }

            cell.renew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("meta", getActivity().MODE_PRIVATE);
                    String Url;
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http")
                            .authority("www.mahendraprophecy.com")
                            .appendPath("api")
                            .appendPath("v1")
                            .appendPath("checkout.php")
                            .appendQueryParameter("p", "services")
                            .appendQueryParameter("id", id[i])
                            .appendQueryParameter("o", "RENEW")
                            .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                            .appendQueryParameter("auth_token", preferences.getString("auth_token", ""))
                            .appendQueryParameter("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
                    Url = builder.build().toString();
                    Intent x = new Intent(getActivity(), SUBSCRIPTION_WEBVIEW.class);
                    x.putExtra("url", Url);
                    startActivity(x);
                }
            });



            cell.title.setText(servicesName[i]);
            cell.expiry.setText(dateOfExpiry[i]);
            cell.status.setText(status[i]);


            try {
            } catch (Exception e) {
                e.printStackTrace();
            }

            return view;
        }

        private class ListCell {
            TextView title, expiry, status,renew;
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
