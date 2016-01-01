package company.mahendraprophecy.OTHERS;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import company.mahendraprophecy.MAIN_FRAGMENTS.LATEST_NEWSLETTERS;
import company.mahendraprophecy.MAIN_FRAGMENTS.MARKET_QUOTES;
import company.mahendraprophecy.MAIN_FRAGMENTS.NOTIFICATIONS;
import company.mahendraprophecy.MAIN_FRAGMENTS.SERVICES;
import company.mahendraprophecy.PROFILE.PROFILE;
import company.mahendraprophecy.R;
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;
import company.mahendraprophecy.WEBVIEW_FILES.ABOUT_US;
import company.mahendraprophecy.WEBVIEW_FILES.BEST_NINE;
import company.mahendraprophecy.WEBVIEW_FILES.DISCLAIMER;
import company.mahendraprophecy.WEBVIEW_FILES.ECONOMIC_CALENDAR;
import company.mahendraprophecy.WEBVIEW_FILES.OFFERS;
import company.mahendraprophecy.WEBVIEW_FILES.PREDICTIONS;
import company.mahendraprophecy.WEBVIEW_FILES.PRIVACY;


public class DASHBOARD extends ActionBarActivity {

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

    /*
    @Override
    public void onBackPressed()
    {

        if (drawer.isDrawerOpen())
        {
            drawer.closeDrawer();
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }
    */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        meta = getIntent().getExtras().getString("meta");

        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("isLoggedIn", "").matches("")) {
            loggedin = false;
        } else {
            loggedin = true;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //setupDrawer();
        setupTabs();
    }

    void setupDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!loggedin) {
            drawer = new Drawer()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .addDrawerItems
                    (
                            new PrimaryDrawerItem().withName("Login / Register").withIcon(getResources().getDrawable(R.drawable.login_dashboard)),
                            new SecondaryDrawerItem().withName("Read Latest Newsletters").withIcon(getResources().getDrawable(R.drawable.read_newsletters_dashboard)),
                            new SecondaryDrawerItem().withName("Market Quotes").withIcon(getResources().getDrawable(R.drawable.statistics_grey)),
                            new SecondaryDrawerItem().withName("Economic Calendar").withIcon(getResources().getDrawable(R.drawable.economic_calendar_dashboard)),
                            new SecondaryDrawerItem().withName("Offers").withIcon(getResources().getDrawable(R.drawable.offers_dashboard)),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName("View all Services").withIcon(getResources().getDrawable(R.drawable.view_all_services)),
                            new SecondaryDrawerItem().withName("Books by Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.books_dashboard)),
                            new SecondaryDrawerItem().withName("Media Coverage").withIcon(getResources().getDrawable(R.drawable.media_dashboard)),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName("Predictions that Came True").withIcon(getResources().getDrawable(R.drawable.tick_grey)),
                            new SecondaryDrawerItem().withName("Best 9 Predictions").withIcon(getResources().getDrawable(R.drawable.best_dashboard)),
                            new SecondaryDrawerItem().withName("About Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.about_dashboard)),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName("Disclaimer").withIcon(getResources().getDrawable(R.drawable.disclaimer_dashboard)),
                            new SecondaryDrawerItem().withName("Privacy").withIcon(getResources().getDrawable(R.drawable.privacy_dashboard))
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                            switch (i) {
                                case 0: {
                                    startActivity(new Intent(DASHBOARD.this, REGISTER_LOGIN.class));
                                    return;
                                }
                                case 1: {
                                    pager.setCurrentItem(0);
                                    return;
                                }
                                case 2: {
                                    pager.setCurrentItem(1);
                                    return;
                                }
                                case 3: {
                                    startActivity(new Intent(DASHBOARD.this,ECONOMIC_CALENDAR.class));
                                    return;
                                }
                                case 4: {
                                    startActivity(new Intent(DASHBOARD.this,OFFERS.class));
                                    return;
                                }

                                //5 is divider

                                case 6: {
                                    pager.setCurrentItem(3);
                                    return;
                                }

                                case 7: {
                                    startActivity(new Intent(DASHBOARD.this, BOOKS.class));
                                    return;
                                }
                                case 8: {
                                    startActivity(new Intent(DASHBOARD.this, YOUTUBE_LIST.class));
                                    return;
                                }


                                //9 is divider

                                case 10: {
                                    startActivity(new Intent(DASHBOARD.this, PREDICTIONS.class));
                                    return;
                                }
                                case 11: {
                                    startActivity(new Intent(DASHBOARD.this, BEST_NINE.class));
                                    return;
                                }

                                case 12: {
                                    startActivity(new Intent(DASHBOARD.this, ABOUT_US.class));
                                    return;
                                }

                                //13 is divider

                                case 14: {
                                    startActivity(new Intent(DASHBOARD.this, DISCLAIMER.class));
                                    return;
                                }

                                case 15: {
                                    startActivity(new Intent(DASHBOARD.this, PRIVACY.class));
                                    return;
                                }

                            }
                        }
                    })

                    .build();
        }
        else
        {
            sharedpreferences=getSharedPreferences("user_details", Context.MODE_PRIVATE);
            drawer = new Drawer()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .addDrawerItems
                            (
                                    new PrimaryDrawerItem().withName(sharedpreferences.getString("full_name","")).withIcon(getResources().getDrawable(R.drawable.profile_big)).withDescription(sharedpreferences.getString("email","")),
                                    new SecondaryDrawerItem().withName("Read Latest Newsletters").withIcon(getResources().getDrawable(R.drawable.read_newsletters_dashboard)),
                                    new SecondaryDrawerItem().withName("Market Quotes").withIcon(getResources().getDrawable(R.drawable.statistics_grey)),
                                    new SecondaryDrawerItem().withName("Economic Calendar").withIcon(getResources().getDrawable(R.drawable.economic_calendar_dashboard)),
                                    new SecondaryDrawerItem().withName("Offers").withIcon(getResources().getDrawable(R.drawable.offers_dashboard)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("View all Services").withIcon(getResources().getDrawable(R.drawable.view_all_services)),
                                    new SecondaryDrawerItem().withName("Books by Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.books_dashboard)),
                                    new SecondaryDrawerItem().withName("Media Coverage").withIcon(getResources().getDrawable(R.drawable.media_dashboard)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Predictions that Came True").withIcon(getResources().getDrawable(R.drawable.tick_grey)),
                                    new SecondaryDrawerItem().withName("Best 9 Predictions").withIcon(getResources().getDrawable(R.drawable.best_dashboard)),
                                    new SecondaryDrawerItem().withName("About Mahendra Sharma").withIcon(getResources().getDrawable(R.drawable.about_dashboard)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Logout").withIcon(getResources().getDrawable(R.drawable.logout)),
                                    new DividerDrawerItem(),
                                    new SecondaryDrawerItem().withName("Disclaimer").withIcon(getResources().getDrawable(R.drawable.disclaimer_dashboard)),
                                    new SecondaryDrawerItem().withName("Privacy").withIcon(getResources().getDrawable(R.drawable.privacy_dashboard))
                            )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                            switch (i) {

                                case 0: {
                                    startActivity(new Intent(DASHBOARD.this, PROFILE.class));
                                    return;
                                }
                                case 1: {
                                    pager.setCurrentItem(0);
                                    return;
                                }
                                case 2: {
                                    pager.setCurrentItem(1);
                                    return;
                                }
                                case 3: {
                                    startActivity(new Intent(DASHBOARD.this,ECONOMIC_CALENDAR.class));
                                    return;
                                }
                                case 4: {
                                    startActivity(new Intent(DASHBOARD.this,OFFERS.class));
                                    return;
                                }

                                //5 is divider

                                case 6: {
                                    pager.setCurrentItem(3);
                                    return;
                                }

                                case 7: {
                                    startActivity(new Intent(DASHBOARD.this, BOOKS.class));
                                    return;
                                }
                                case 8: {
                                    startActivity(new Intent(DASHBOARD.this, YOUTUBE_LIST.class));
                                    return;
                                }


                                //9 is divider

                                case 10: {
                                    startActivity(new Intent(DASHBOARD.this, PREDICTIONS.class));
                                    return;
                                }
                                case 11: {
                                    startActivity(new Intent(DASHBOARD.this, BEST_NINE.class));
                                    return;
                                }

                                case 12: {
                                    startActivity(new Intent(DASHBOARD.this, ABOUT_US.class));
                                    return;
                                }

                                //13 is divider

                                case 14:
                                {
                                    sharedpreferences=getSharedPreferences("meta", Context.MODE_PRIVATE);
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
                                            progressDialog = new ProgressDialog(DASHBOARD.this);
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                            progressDialog.setMessage("Logging Out.");
                                            params = new ArrayList<NameValuePair>();

                                            SharedPreferences preferences=getSharedPreferences("meta", MODE_PRIVATE);
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
                                        protected void onPostExecute(String s)
                                        {
                                            progressDialog.dismiss();
                                            Intent x=new Intent(DASHBOARD.this, DASHBOARD.class);
                                            x.putExtra("meta",meta);
                                            startActivity(x);
                                        }
                                    }.execute();
                                }

                                //15 is divider

                                case 16: {
                                    startActivity(new Intent(DASHBOARD.this, DISCLAIMER.class));
                                    return;
                                }

                                case 17:
                                {
                                    startActivity(new Intent(DASHBOARD.this, PRIVACY.class));
                                    return;
                                }
                            }
                        }
                    })
                    .build();
        }

    }


    void setupTabs() {
        tabs = (MaterialTabs) findViewById(R.id.mainTabs);
        pager = (ViewPager) findViewById(R.id.mainPager);
        MainScreenAdapter adapter = new MainScreenAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }

    class MainScreenAdapter extends FragmentPagerAdapter implements MaterialTabs.CustomTabProvider {

        public MainScreenAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Fragment();

            switch (position) {
                case 0:
                    return new LATEST_NEWSLETTERS(meta, pager);

                case 1:
                    return new MARKET_QUOTES();

                case 2:
                    return new NOTIFICATIONS();

                case 3:
                    return new SERVICES();

                default:
                    return fragment;
            }

        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return 1;
            //return titles.length;
        }

        @Override
        public View getCustomTabView(ViewGroup viewGroup, int i) {
            LinearLayout LL = new LinearLayout(DASHBOARD.this);
            LL.setOrientation(LinearLayout.VERTICAL);
            LL.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            LL.setPadding(2, 20, 2, 20);
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LL.setLayoutParams(LLParams);

            ImageView icon = new ImageView(DASHBOARD.this);
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


            text = new TextView(DASHBOARD.this);
            text.setTextColor(Color.parseColor("#FFFFFF"));
            text.setGravity(Gravity.CENTER);
            text.setPadding(2, 6, 2, 2);
            text.setTextSize(12);
            LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            text.setLayoutParams(LLParams);

            LL.addView(icon);
            LL.addView(text);
            switch (i) {

                // private String[] titles = new String[]{"Latest News", "Market Quotes", "Messages", "Services"};
                case 0: {
                    text.setText(titles[i]);
                    icon.setImageResource(R.drawable.news_tabs);
                    break;
                }

                case 1: {
                    text.setText(titles[i]);
                    icon.setImageResource(R.drawable.market_tabs);
                    break;
                }
                case 2: {
                    text.setText(titles[i]);
                    icon.setImageResource(R.drawable.messages_tabs);
                    break;
                }
                case 3: {
                    text.setText(titles[i]);
                    icon.setImageResource(R.drawable.services_tabs);
                    break;
                }
            }

            icon.getLayoutParams().height = 50;
            icon.getLayoutParams().width = 50;
            icon.requestLayout();
            return LL;
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
