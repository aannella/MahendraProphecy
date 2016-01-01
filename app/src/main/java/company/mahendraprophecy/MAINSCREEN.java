package company.mahendraprophecy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import io.karim.MaterialTabs;
import company.mahendraprophecy.BOOKS.BOOKS;
import company.mahendraprophecy.OTHERS.DASHBOARD;
import company.mahendraprophecy.OTHERS.SERVICE_DETAILS;
import company.mahendraprophecy.OTHERS.YOUTUBE_LIST;
import company.mahendraprophecy.PROFILE.PROFILE;
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;
import company.mahendraprophecy.WEBVIEW_FILES.ABOUT_US;
import company.mahendraprophecy.WEBVIEW_FILES.DISCLAIMER;
import company.mahendraprophecy.WEBVIEW_FILES.ECONOMIC_CALENDAR;
import company.mahendraprophecy.WEBVIEW_FILES.OFFERS;
import company.mahendraprophecy.WEBVIEW_FILES.PREDICTIONS;
import company.mahendraprophecy.WEBVIEW_FILES.PRIVACY;


public class MAINSCREEN extends ActionBarActivity {

    JSONArray array;
    StaggeredGridView listView;

    TextView text;
    List<NameValuePair> params;
    boolean loggedin;
    SharedPreferences sharedpreferences;
    private Toolbar toolbar;
    private Drawer.Result drawer;
    private MaterialTabs tabs;
    private ViewPager pager;
    private String[] titles = new String[]{"Latest", "Markets", "Messages", "Services"};
    String meta;

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);
        listView = (StaggeredGridView) findViewById(R.id.mainscreen_services);

        // meta = getIntent().getExtras().getString("meta");

        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("isLoggedIn", "").matches("")) {
            loggedin = false;
        } else {
            loggedin = true;
        }

        toolbar = (Toolbar) findViewById(R.id.mainscreen_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        setupDrawer();

        ((LinearLayout) findViewById(R.id.book)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MAINSCREEN.this, BOOKS.class));
            }
        });

        ((LinearLayout) findViewById(R.id.market)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MAINSCREEN.this, MARKET.class));
            }
        });

        ((LinearLayout) findViewById(R.id.message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MAINSCREEN.this, NOTIFICATION.class));
            }
        });

        ((LinearLayout) findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MAINSCREEN.this, CONTACT.class));
            }
        });

        new getAllServices().execute();
    }


    void setupDrawer() {

        if (!loggedin) {
            drawer = new Drawer()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .addDrawerItems
                            (
                                    new PrimaryDrawerItem().withName("Login / Register").withIcon(getResources().getDrawable(R.drawable.login_dashboard)),
                                    new SecondaryDrawerItem().withName("View Services").withIcon(getResources().getDrawable(R.drawable.view_all_services)),
                                    new SecondaryDrawerItem().withName("Newsletters List").withIcon(getResources().getDrawable(R.drawable.read_newsletters_dashboard)),
                                    new SecondaryDrawerItem().withName("Books by Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.books_dashboard)),
                                    new SecondaryDrawerItem().withName("Messages").withIcon(getResources().getDrawable(R.drawable.messages_drawer)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Market Quotes").withIcon(getResources().getDrawable(R.drawable.market_drawer)),
                                    new SecondaryDrawerItem().withName("Economic Calendar").withIcon(getResources().getDrawable(R.drawable.economic_calendar_dashboard)),
                                    new SecondaryDrawerItem().withName("Media Coverage").withIcon(getResources().getDrawable(R.drawable.media_dashboard)),
                                    new SecondaryDrawerItem().withName("Offers").withIcon(getResources().getDrawable(R.drawable.offers_dashboard)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Predictions that Came True").withIcon(getResources().getDrawable(R.drawable.tick_grey)),
                                    new SecondaryDrawerItem().withName("About Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.about_dashboard)),
                                    new SecondaryDrawerItem().withName("Contact Us").withIcon(getResources().getDrawable(R.drawable.contact_drawer)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Disclaimer").withIcon(getResources().getDrawable(R.drawable.disclaimer_dashboard)),
                                    new SecondaryDrawerItem().withName("Privacy").withIcon(getResources().getDrawable(R.drawable.privacy_dashboard))
                            )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                            switch (i) {
                                case 0: {
                                    startActivity(new Intent(MAINSCREEN.this, REGISTER_LOGIN.class));
                                    return;
                                }

                                case 1: {
                                    //  pager.setCurrentItem(3);
                                    return;
                                }

                                case 2: {
                                    new getMetaData().execute();
                                    return;
                                }


                                case 3: {
                                    startActivity(new Intent(MAINSCREEN.this, BOOKS.class));
                                    return;
                                }


                                case 4: {
                                    startActivity(new Intent(MAINSCREEN.this, NOTIFICATION.class));
                                    return;
                                }

                                //5 is divider

                                case 6: {
                                    startActivity(new Intent(MAINSCREEN.this, MARKET.class));
                                    return;
                                }
                                case 7: {
                                    startActivity(new Intent(MAINSCREEN.this, ECONOMIC_CALENDAR.class));
                                    return;
                                }

                                case 8: {
                                    startActivity(new Intent(MAINSCREEN.this, YOUTUBE_LIST.class));
                                    return;
                                }
                                case 9: {
                                    startActivity(new Intent(MAINSCREEN.this, OFFERS.class));
                                    return;
                                }

                                //10 is divider

                                case 11: {
                                    startActivity(new Intent(MAINSCREEN.this, PREDICTIONS.class));
                                    return;
                                }

                                case 12: {
                                    startActivity(new Intent(MAINSCREEN.this, ABOUT_US.class));
                                    return;
                                }


                                case 13: {
                                    startActivity(new Intent(MAINSCREEN.this, CONTACT.class));
                                    return;
                                }
                                //14 is divider

                                case 15: {
                                    startActivity(new Intent(MAINSCREEN.this, DISCLAIMER.class));
                                    return;
                                }

                                case 16: {
                                    startActivity(new Intent(MAINSCREEN.this, PRIVACY.class));
                                    return;
                                }

                            }
                        }
                    })

                    .build();
        } else {
            sharedpreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
            drawer = new Drawer()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .addDrawerItems
                            (
                                    new PrimaryDrawerItem().withName(sharedpreferences.getString("full_name", "")).withIcon(getResources().getDrawable(R.drawable.profile_big)).withDescription(sharedpreferences.getString("email", "")),
                                    new SecondaryDrawerItem().withName("View Services").withIcon(getResources().getDrawable(R.drawable.view_all_services)),
                                    new SecondaryDrawerItem().withName("Newsletters List").withIcon(getResources().getDrawable(R.drawable.read_newsletters_dashboard)),
                                    new SecondaryDrawerItem().withName("Books by Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.books_dashboard)),
                                    new SecondaryDrawerItem().withName("Messages").withIcon(getResources().getDrawable(R.drawable.messages_drawer)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Market Quotes").withIcon(getResources().getDrawable(R.drawable.market_drawer)),
                                    new SecondaryDrawerItem().withName("Economic Calendar").withIcon(getResources().getDrawable(R.drawable.economic_calendar_dashboard)),
                                    new SecondaryDrawerItem().withName("Media Coverage").withIcon(getResources().getDrawable(R.drawable.media_dashboard)),
                                    new SecondaryDrawerItem().withName("Offers").withIcon(getResources().getDrawable(R.drawable.offers_dashboard)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Predictions that Came True").withIcon(getResources().getDrawable(R.drawable.tick_grey)),
                                    new SecondaryDrawerItem().withName("About Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.about_dashboard)),
                                    new SecondaryDrawerItem().withName("Contact Us").withIcon(getResources().getDrawable(R.drawable.contact_drawer)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Disclaimer").withIcon(getResources().getDrawable(R.drawable.disclaimer_dashboard)),
                                    new SecondaryDrawerItem().withName("Privacy").withIcon(getResources().getDrawable(R.drawable.privacy_dashboard)),
                                    new SecondaryDrawerItem().withName("Logout").withIcon(getResources().getDrawable(R.drawable.logout))
                            )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                            switch (i) {

                                case 0: {
                                    startActivity(new Intent(MAINSCREEN.this, PROFILE.class));
                                    return;
                                }
                                case 2: {
                                    new getMetaData().execute();
                                    return;
                                }


                                case 3: {
                                    startActivity(new Intent(MAINSCREEN.this, BOOKS.class));
                                    return;
                                }


                                case 4: {
                                    startActivity(new Intent(MAINSCREEN.this, NOTIFICATION.class));
                                    return;
                                }

                                //5 is divider

                                case 6: {
                                    startActivity(new Intent(MAINSCREEN.this, MARKET.class));
                                    return;
                                }
                                case 7: {
                                    startActivity(new Intent(MAINSCREEN.this, ECONOMIC_CALENDAR.class));
                                    return;
                                }

                                case 8: {
                                    startActivity(new Intent(MAINSCREEN.this, YOUTUBE_LIST.class));
                                    return;
                                }
                                case 9: {
                                    startActivity(new Intent(MAINSCREEN.this, OFFERS.class));
                                    return;
                                }

                                //10 is divider

                                case 11: {
                                    startActivity(new Intent(MAINSCREEN.this, PREDICTIONS.class));
                                    return;
                                }

                                case 12: {
                                    startActivity(new Intent(MAINSCREEN.this, ABOUT_US.class));
                                    return;
                                }


                                case 13: {
                                    startActivity(new Intent(MAINSCREEN.this, CONTACT.class));
                                    return;
                                }
                                //14 is divider

                                case 15: {
                                    startActivity(new Intent(MAINSCREEN.this, DISCLAIMER.class));
                                    return;
                                }

                                case 16: {
                                    startActivity(new Intent(MAINSCREEN.this, PRIVACY.class));
                                    return;
                                }


                                case 17: {
                                    sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
                                    sharedpreferences.edit()
                                            .putString("isLoggedIn", "")
                                            .commit();
                                    //meta
                                    new AsyncTask<Void, Void, String>() {
                                        String result = ""
                                                ,
                                                url = "https://www.mahendraprophecy.com/api/v1/logout.php.php?key=1c0d2809e0140c09c6003d098400d477";
                                        ProgressDialog progressDialog;

                                        @Override
                                        protected void onPreExecute() {
                                            progressDialog = new ProgressDialog(MAINSCREEN.this);
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                            progressDialog.setMessage("Logging Out.");
                                            params = new ArrayList<NameValuePair>();

                                            SharedPreferences preferences = getSharedPreferences("meta", MODE_PRIVATE);
                                            params.add(new BasicNameValuePair("auth_token", preferences.getString("auth_token", "")));
                                            params.add(new BasicNameValuePair("hash_key", preferences.getString("hash_key", "")));
                                        }

                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            HttpEntity httpEntity = null;

                                            try {
                                                HttpClient httpClient = createHttpClient();  // Default HttpClient
                                                HttpPost httpPost = new HttpPost(url);
                                                Log.d("url called ", url);
                                                httpPost.setEntity(new UrlEncodedFormEntity(params));
                                                HttpResponse httpResponse = httpClient.execute(httpPost);
                                                httpEntity = httpResponse.getEntity();
                                                String entityResponse = EntityUtils.toString(httpEntity);
                                                Log.e("Entity Response  : ", entityResponse);
                                                result = entityResponse;
                                            } catch (Exception e) {
                                                result = "Error : " + e;
                                            }

                                            return result;
                                        }

                                        @Override
                                        protected void onPostExecute(String s) {
                                            progressDialog.dismiss();
                                            Intent x = new Intent(MAINSCREEN.this, MAINSCREEN.class);
                                            //x.putExtra("meta",meta);
                                            startActivity(x);
                                        }
                                    }.execute();
                                }
                            }
                        }
                    })
                    .build();
        }
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
            ((ProgressWheel) findViewById(R.id.view_progress_wheel)).setVisibility(View.INVISIBLE);

            if (s.startsWith("E")) {
                Toast.makeText(MAINSCREEN.this, "Error in Network Connection!!", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    array = object.getJSONArray("services");
                    SERVICE_ADAPTER adapter = new SERVICE_ADAPTER(MAINSCREEN.this);
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
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                        try {
                            if (object.getString("type").equals("news")) {
                                Intent x = new Intent(MAINSCREEN.this, SERVICE_SCREEN.class);

                                x.putExtra("id", object.getString("id"));
                                startActivity(x);
                            } else {
                                Intent x = new Intent(MAINSCREEN.this, SERVICE_DETAILS.class);
                                x.putExtra("id", object.getString("id"));
                                startActivity(x);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                //http://www.mahendraprophecy.com/api/v1/checkout.php?&p=services&id={SERVICE_ID_HERE}&o={NEW_OR_RENEW}
                // &key={API_KEY_HERE}
                // &auth_token={AUTH_TOKEN_HERE}
                // &device_id={DEVICE_ID_HERE}

                Picasso.with(MAINSCREEN.this)
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


    ProgressDialog progressDialog;

    class getMetaData extends AsyncTask<Void, Void, String> {
        String result = "", url = "https://www.mahendraprophecy.com/api/v1/meta.php?key=1c0d2809e0140c09c6003d098400d477";

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MAINSCREEN.this);
            progressDialog.setMessage("Please Wait !");
            progressDialog.show();
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

            progressDialog.dismiss();
            try {
                object = new JSONObject(s);
                Intent intent = new Intent(MAINSCREEN.this, DASHBOARD.class);
                intent.putExtra("meta", s);
                startActivity(intent);

            } catch (Exception e) {
                Log.d("Error", "" + e);
                Toast.makeText(getBaseContext(), "Error : " + e, Toast.LENGTH_LONG).show();
            }

        }
    }

    JSONObject object;
}
