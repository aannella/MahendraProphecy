package company.mahendraprophecy.OTHERS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import company.mahendraprophecy.R;


public class YOUTUBE_LIST extends ActionBarActivity {


    ListView youtubeList;
    List<String> ids;
    JSONArray array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_list);
        youtubeList = (ListView) findViewById(R.id.youtube_listview);

        ((ImageView)findViewById(R.id.vidoes_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        youtubeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

        new AsyncTask<Void, Void, String>() {
            String result = "",url = "https://www.mahendraprophecy.com/api/v1/videos.php?key=1c0d2809e0140c09c6003d098400d477";

            @Override
            protected void onPreExecute() {

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
                List<String> ids = new ArrayList<String>();
                try {
                    JSONObject object = new JSONObject(s);
                    array = object.getJSONArray("videos");

                    youtubeListAdapter youtubeListAdapter = new youtubeListAdapter(YOUTUBE_LIST.this);
                    youtubeList.setAdapter(youtubeListAdapter);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "e" + "\n" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    class ListCell {
        ImageView image;
        TextView title, code;
        ImageButton play;
    }

    class youtubeListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater layoutInflater;

        public youtubeListAdapter(Activity activity) {
            this.activity = activity;
            layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                view = layoutInflater.inflate(R.layout.youtube_layout, null);
                cell = new ListCell();
                cell.code = (TextView) view.findViewById(R.id.code);
                cell.title = (TextView) view.findViewById(R.id.videoTitle);
                cell.image = (ImageView) view.findViewById(R.id.videoImage);
                cell.play= (ImageButton) view.findViewById(R.id.play);
            }

            try {
                final JSONObject object = array.getJSONObject(i);
                cell.code.setText(object.getString("youtube_code"));
                cell.title.setText(object.getString("title"));

                Picasso.with(YOUTUBE_LIST.this)
                        .load(object.getString("photo"))
                        .into(cell.image);

                cell.play.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Toast.makeText(getBaseContext(),"Play Now",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(YOUTUBE_LIST.this, YouTubePlayerActivity.class);
                        // Youtube video ID (Required, You can use YouTubeUrlParser to parse Video Id from url)
                        try {
                            intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, object.getString("youtube_code"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Youtube player style (DEFAULT as default)
                        intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.CHROMELESS);
                        // Screen Orientation Setting (AUTO for default)
                        // AUTO, AUTO_START_WITH_LANDSCAPE, ONLY_LANDSCAPE, ONLY_PORTRAIT
                        intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO_START_WITH_LANDSCAPE);
                        // Show audio interface when user adjust volume (true for default)
                        intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);
                        // If the video is not playable, use Youtube app or Internet Browser to play it
                        // (true for default)
                        intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            return view;
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
