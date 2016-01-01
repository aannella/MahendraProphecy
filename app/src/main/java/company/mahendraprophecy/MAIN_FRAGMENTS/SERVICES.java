package company.mahendraprophecy.MAIN_FRAGMENTS;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

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
import company.mahendraprophecy.OTHERS.SERVICE_DETAILS;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SERVICES extends android.support.v4.app.Fragment {

    ListView listView;
    Cursor cursor;
    JSONArray array;
    DATABASE_OPERATIONS D_OP;
    boolean loggedin = false;

    public SERVICES() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mainfragment_services, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getActivity().findViewById(R.id.servies_list);
        D_OP = new DATABASE_OPERATIONS(getActivity());
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("isLoggedIn", "").matches("")) {
            loggedin = false;
        } else {
            loggedin = true;
        }
        new getAllServices().execute();
    }


    class getAllServices extends AsyncTask<Void, Void, String> {
        String Url = "", result = "";

        @Override
        protected void onPreExecute() {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("www.mahendraprophecy.com")
                    .appendPath("api")
                    .appendPath("v1")
                    .appendPath("services.php")
                    .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

            Url = builder.build().toString();
            Log.d("url", Url);
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpEntity httpEntity = null;

            try {
                HttpClient httpClient = createHttpClient();  // Default HttpClient
                HttpGet httpGet = new HttpGet(Url);
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
            ((ProgressWheel) getActivity().findViewById(R.id.serviceslistview_progress_wheel)).setVisibility(View.INVISIBLE);

            if (s.startsWith("E")) {
                Toast.makeText(getActivity(), "Error in Network Connection!!", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    array = object.getJSONArray("services");
                    SERVICE_ADAPTER adapter = new SERVICE_ADAPTER(getActivity());
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SERVICE_ADAPTER extends BaseAdapter {
        private Activity activity;
        private LayoutInflater layoutInflater;

        public SERVICE_ADAPTER(Activity activity) {
            this.activity = activity;
            layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return array.length();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ListCell cell;
            {
                view = layoutInflater.inflate(R.layout.service_item, null);
                cell = new ListCell();
                cell.id = (TextView) view.findViewById(R.id.service_id);
                cell.description = (TextView) view.findViewById(R.id.service_description);
                cell.title = (TextView) view.findViewById(R.id.service_name);
                cell.image = (ImageView) view.findViewById(R.id.item_image);
                cell.subscribe = (Button) view.findViewById(R.id.subscribe);
                cell.re_subscribe = (Button) view.findViewById(R.id.resubscribe);
            }

            try {
                final JSONObject object = array.getJSONObject(i);
                cell.id.setText(object.getString("id"));
                cell.title.setText(object.getString("title"));
                cell.description.setText(object.getString("excerpt"));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent x = new Intent(getActivity(), SERVICE_DETAILS.class);
                        try {
                            x.putExtra("id", object.getString("id"));
                            startActivity(x);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                //http://www.mahendraprophecy.com/api/v1/checkout.php?&p=services&id={SERVICE_ID_HERE}&o={NEW_OR_RENEW}
                // &key={API_KEY_HERE}
                // &auth_token={AUTH_TOKEN_HERE}
                // &device_id={DEVICE_ID_HERE}

                Picasso.with(getActivity())
                        .load(object.getString("photo"))
                        .resize(85, 85)
                        .into(cell.image);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return view;
        }

        private class ListCell {
            TextView title, description, id;
            ImageView image;
            Button subscribe, re_subscribe;
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
