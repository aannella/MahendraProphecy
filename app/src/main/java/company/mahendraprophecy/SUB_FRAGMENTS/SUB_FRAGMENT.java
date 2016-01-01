package company.mahendraprophecy.SUB_FRAGMENTS;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;
import company.mahendraprophecy.WEBVIEW_FILES.STORY_VIEW;
import company.mahendraprophecy.WEBVIEW_FILES.SUBSCRIPTION_WEBVIEW;

public class SUB_FRAGMENT extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    JSONArray data;
    String cat_id;
    ListView list;
    String color, title;
    SharedPreferences sharedpreferences;
    boolean loggedin;
    ViewPager Pager;

    public static final SUB_FRAGMENT newInstance(String message, String Color, String Title, ViewPager mainPager) {
        SUB_FRAGMENT f = new SUB_FRAGMENT();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        bdl.putString("color", Color);
        bdl.putString("title", Title);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        cat_id = getArguments().getString(EXTRA_MESSAGE);
        color = getArguments().getString("color");
        title = getArguments().getString("title");
        View v = inflater.inflate(R.layout.sub_list_fragment, container, false);
        list = (ListView) v.findViewById(R.id.list);
        Log.d("process", "View Returned");
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("isLoggedIn", "").matches("")) {
            loggedin = false;
        } else {
            loggedin = true;
        }

        new getListCategories().execute();

    }

    class SUB_FRAGMENT_LIST_ADAPTER extends BaseAdapter {

        private Activity activity;
        private LayoutInflater layoutInflater;

        public SUB_FRAGMENT_LIST_ADAPTER(Activity activity) {
            this.activity = activity;
            layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            //   Toast.makeText(getActivity(),data.length()+" items",Toast.LENGTH_LONG).show();
            return data.length();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ListCell cell;
            {
                view = layoutInflater.inflate(R.layout.row_news_letter, null);
                cell = new ListCell();
                cell.date = (TextView) view.findViewById(R.id.post_date);
                cell.title = (TextView) view.findViewById(R.id.title);
                cell.post_image = (ImageView) view.findViewById(R.id.photo);
                cell.vip = (ImageView) view.findViewById(R.id.vip);
                cell.must = (ImageView) view.findViewById(R.id.must);
                cell.must_vip = (LinearLayout) view.findViewById(R.id.must_vip);
                cell.id = (TextView) view.findViewById(R.id.id);
                cell.cat_id = ((TextView) view.findViewById(R.id.cat_id));
                cell.access = ((TextView) view.findViewById(R.id.access));
            }


            try {
                JSONObject jsonObject = data.getJSONObject(i);

                boolean must = false, vip = false, image = true;

                cell.access.setText(jsonObject.getString("access"));
                if (jsonObject.getString("must_read").equals("1")) {
                    must = true;
                }
                if (jsonObject.getString("vip_locked").equals("1")) {
                    vip = true;
                }

                if (jsonObject.getString("photo").equals("")) {
                    image = false;
                }

                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(0, 0);
                if (!must) {
                    cell.must.setVisibility(View.INVISIBLE);
                    cell.must.setLayoutParams(LLParams);
                }
                if (!vip) {
                    cell.vip.setVisibility(View.INVISIBLE);
                    cell.vip.setLayoutParams(LLParams);
                }

                if (image) {
                    Picasso.with(getActivity())
                            .load(jsonObject.getString("photo"))
                            .resize(85, 85)
                            .into(cell.post_image);
                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                    cell.post_image.setLayoutParams(layoutParams);
                }

                cell.date.setText(jsonObject.getString("published_on"));
                cell.title.setText(jsonObject.getString("title"));
                cell.id.setText(jsonObject.getString("id"));

            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error : " + e, Toast.LENGTH_LONG);
            }

            return view;
        }
    }


    private class ListCell {
        TextView date, title, id, cat_id, access;
        ImageView post_image;
        ImageView must, vip;
        LinearLayout must_vip;
    }

    class getListCategories extends AsyncTask<Void, Void, String> {

        String Url = "", result = "";

        @Override
        protected void onPreExecute() {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("www.mahendraprophecy.com")
                    .appendPath("api")
                    .appendPath("v1")
                    .appendPath("newsletters-list.php")
                    .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                    .appendQueryParameter("cat_id", cat_id);

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
            try {
                JSONObject object = new JSONObject(s);
                data = object.getJSONArray("posts");

                {
                    SUB_FRAGMENT_LIST_ADAPTER adapter = new SUB_FRAGMENT_LIST_ADAPTER(getActivity());
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            try
                            {
                                String value = ((TextView) view.findViewById(R.id.access)).getText().toString();
                                if (value.equals("all"))
                                {
                                    openStory(i);
                                }
                                else if (value.equals("login"))
                                {
                                    if (loggedin)
                                    {
                                        openStory(i);
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        AlertDialog dialog;
                                        builder
                                                .setTitle("Authentication Alert !")
                                                .setMessage("This post is viewable to only logged in users!\nLogin Or Register to view this post. .");
                                        builder.setPositiveButton("LOGIN/REGISTER", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                               startActivity(new Intent(getActivity(),REGISTER_LOGIN.class));
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
                                else
                                {
                                    if(loggedin)
                                    {
                                        DATABASE_OPERATIONS D_OP = new DATABASE_OPERATIONS(getActivity());
                                        Cursor cursor = D_OP.getCatInformation(D_OP, cat_id);
                                        if (cursor.getCount() != 0) {
                                            String name = "", expiry = "";
                                            cursor.moveToFirst();
                                            if (cursor.getString(0).equals("Active")) {
                                                cursor.close();
                                                openStory(i);
                                            } else
                                            {
                                                expiry = cursor.getString(1);
                                                name = cursor.getString(2);
                                                cursor.close();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                AlertDialog dialog;
                                                builder.setTitle("Subscription Expiry Alert !")
                                                        .setMessage("Your subcription to this service has expired, please resubscribe to resume services.!");
                                                builder.setPositiveButton("RENEW NOW !!", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        SharedPreferences preferences = getActivity().getSharedPreferences("meta", getActivity().MODE_PRIVATE);
                                                        String Url;
                                                        Uri.Builder builder = new Uri.Builder();
                                                        builder.scheme("http")
                                                                .authority("www.mahendraprophecy.com")
                                                                .appendPath("api")
                                                                .appendPath("v1")
                                                                .appendPath("checkout.php")
                                                                .appendQueryParameter("p", "services")
                                                                .appendQueryParameter("id", cat_id)
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
                                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                dialog = builder.create();
                                                dialog.show();

                                            }
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            AlertDialog dialog;
                                            builder.setTitle("Subscription Alert !")
                                                    .setMessage("This post is viewable to only subscribed users ,Please subscribe to this service!");
                                            builder.setPositiveButton("SUBCRIBE NOW!", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    SharedPreferences preferences = getActivity().getSharedPreferences("meta", getActivity().MODE_PRIVATE);
                                                    String Url;
                                                    Uri.Builder builder = new Uri.Builder();
                                                    builder.scheme("http")
                                                            .authority("www.mahendraprophecy.com")
                                                            .appendPath("api")
                                                            .appendPath("v1")
                                                            .appendPath("checkout.php")
                                                            .appendQueryParameter("p", "services")
                                                            .appendQueryParameter("id", cat_id)
                                                            .appendQueryParameter("o", "NEW")
                                                            .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                                                            .appendQueryParameter("auth_token", preferences.getString("auth_token", ""))
                                                            .appendQueryParameter("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
                                                    Url = builder.build().toString();
                                                    Intent x = new Intent(getActivity(), SUBSCRIPTION_WEBVIEW.class);
                                                    x.putExtra("url", Url);
                                                    startActivity(x);
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
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        AlertDialog dialog;
                                        builder
                                                .setTitle("Authentication & Subcription Alert !")
                                                .setMessage("This post is viewable to only subscribed users!\nYou seem to have not logged in,Please Login or Register to checkout our services");
                                                 builder.setPositiveButton("LOGIN/REGISTER", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                startActivity(new Intent(getActivity(),REGISTER_LOGIN.class));
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            } catch (JSONException e) {

            }
        }
    }

    void openStory(int i)
    {
        Intent x = new Intent(getActivity(), STORY_VIEW.class);
        try
        {
            x.putExtra("object", data.getJSONObject(i).toString());

        x.putExtra("id",data.getJSONObject(i).getString("id"));
        x.putExtra("cat_id",data.getJSONObject(i).getString("cat_id"));
        x.putExtra("color", color);
        x.putExtra("title", title);
        startActivity(x);
        } catch (JSONException e) {
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


