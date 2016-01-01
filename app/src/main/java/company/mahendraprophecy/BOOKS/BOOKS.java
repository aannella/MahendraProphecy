package company.mahendraprophecy.BOOKS;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
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

import company.mahendraprophecy.CONTACT;
import company.mahendraprophecy.MAINSCREEN;
import company.mahendraprophecy.MARKET;
import company.mahendraprophecy.NOTIFICATION;
import company.mahendraprophecy.OTHERS.DASHBOARD;
import company.mahendraprophecy.R;


public class BOOKS extends ActionBarActivity {

    //http://www.mahendraprophecy.com/api/v1/books.php?key=1c0d2809e0140c09c6003d098400d477
    StaggeredGridView gridView;
    JSONArray array;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        gridView = (StaggeredGridView) findViewById(R.id.books_grid_view);

        ((LinearLayout)findViewById(R.id.services)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BOOKS.this, MAINSCREEN.class));
            }
        });

        ((LinearLayout)findViewById(R.id.market)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BOOKS.this,MARKET.class));
            }
        });

        ((LinearLayout)findViewById(R.id.message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BOOKS.this,NOTIFICATION.class));
            }
        });

        ((LinearLayout)findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BOOKS.this,CONTACT.class));
            }
        });

        new getBookDetails().execute();

        ((ImageButton) findViewById(R.id.books_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // new getMetaData().execute();
                startActivity(new Intent(BOOKS.this, MAINSCREEN.class));
            }
        });
    }


    class booksAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        public booksAdapter() {
            layoutInflater = (LayoutInflater) BOOKS.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return array.length();
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
                view = layoutInflater.inflate(R.layout.book_item, null);
                cell = new ListCell();
                cell.price = (TextView) view.findViewById(R.id.book_price);
                cell.title = (TextView) view.findViewById(R.id.book_title);
                cell.soldout = (TextView) view.findViewById(R.id.sold_out);
                cell.book_image = ((ImageView) view.findViewById(R.id.book_image));
            }

            try {
                JSONObject jsonObject = array.getJSONObject(i);

                Picasso.with(BOOKS.this)
                        .load(jsonObject.getString("photo"))
                        .error(getResources().getDrawable(R.drawable.no_image))
                        .placeholder(getResources().getDrawable(R.drawable.no_image))
                        .into(cell.book_image);

                cell.title.setText(jsonObject.getString("title"));
                cell.price.setText(" $" + jsonObject.getString("price"));

                if (jsonObject.getString("sold_out").matches("1")) {
                    cell.soldout.setVisibility(View.VISIBLE);
                }

                cell.title.setText(jsonObject.getString("title"));


            } catch (Exception e) {
                Log.d("desc", "Error : " + e);
            }

            return view;
        }
    }

    private class ListCell {
        TextView price, soldout, title;
        ImageView book_image;
    }

    class getBookDetails extends AsyncTask<Void, Void, String> {

        String Url, result = "";

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Fetching Books !!");
            progressDialog.show();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("www.mahendraprophecy.com")
                    .appendPath("api")
                    .appendPath("v1")
                    .appendPath("books.php")
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
            progressDialog.dismiss();
            if (result.startsWith("E")) {
                Toast.makeText(BOOKS.this, "Error Occured", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    array = object.getJSONArray("books");
                    booksAdapter adapter = new booksAdapter();
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent x = new Intent(BOOKS.this, BOOK_DETAILS.class);
                            try {
                                x.putExtra("id", array.getJSONObject(i).getString("id"));
                                x.putExtra("price", array.getJSONObject(i).getString("price"));
                                startActivity(x);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(BOOKS.this, "Vahi Parsing Error  : " + e, Toast.LENGTH_LONG).show();
                }
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


    class getMetaData extends AsyncTask<Void, Void, String> {
        String result = "", url = "https://www.mahendraprophecy.com/api/v1/meta.php?key=1c0d2809e0140c09c6003d098400d477";

        @Override
        protected void onPreExecute() {
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
            try
            {
                object = new JSONObject(s);
                Intent intent = new Intent(BOOKS.this, DASHBOARD.class);
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
