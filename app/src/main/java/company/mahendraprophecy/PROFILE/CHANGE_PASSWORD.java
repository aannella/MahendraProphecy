package company.mahendraprophecy.PROFILE;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class CHANGE_PASSWORD extends android.support.v4.app.Fragment {
    boolean old_pass_shown = false,new_pass_shown=false;
    MaterialEditText oldpassword,newpassword;
    List<NameValuePair> params;
    public CHANGE_PASSWORD() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_profile_change_password, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        oldpassword= (MaterialEditText) getActivity().findViewById(R.id.old_password);

        ((ImageButton) getActivity().findViewById(R.id.show_old_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (old_pass_shown) {
                    oldpassword.setTransformationMethod(new PasswordTransformationMethod());
                    ((ImageButton) getActivity().findViewById(R.id.show_old_password)).setImageResource(R.drawable.showpass_grey);
                    old_pass_shown = false;
                } else {
                    oldpassword.setTransformationMethod(null);
                    ((ImageButton) getActivity().findViewById(R.id.show_old_password)).setImageResource(R.drawable.showpass_orange);
                    old_pass_shown = true;
                }
            }
        });

        newpassword= (MaterialEditText) getActivity().findViewById(R.id.new_password);

        ((ImageButton) getActivity().findViewById(R.id.show_new_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new_pass_shown) {
                    newpassword.setTransformationMethod(new PasswordTransformationMethod());
                    ((ImageButton) getActivity().findViewById(R.id.show_new_password)).setImageResource(R.drawable.showpass_grey);
                    new_pass_shown = false;
                } else {
                    newpassword.setTransformationMethod(null);
                    ((ImageButton) getActivity().findViewById(R.id.show_new_password)).setImageResource(R.drawable.showpass_orange);
                    new_pass_shown = true;
                }
            }
        });

        ((Button)getActivity().findViewById(R.id.changepass_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oldpassword.getText().toString().isEmpty())
                {
                    oldpassword.setError("Enter Current Password");
                    return;
                }

                if(newpassword.getText().toString().isEmpty())
                {
                    newpassword.setError("Enter New Password");
                    return;
                }

                new AsyncTask<Void, Void, String>() {
                    String result = ""
                            ,
                            url = "https://www.mahendraprophecy.com/api/v1/change-password.php?key=1c0d2809e0140c09c6003d098400d477";
                    ProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        progressDialog.setMessage("Processing Request.");
                        params = new ArrayList<NameValuePair>();

                        SharedPreferences preferences=getActivity().getSharedPreferences("meta", getActivity().MODE_PRIVATE);
                        params.add(new BasicNameValuePair("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)));
                        params.add(new BasicNameValuePair("auth_token", preferences.getString("auth_token","")));
                        params.add(new BasicNameValuePair("hash_key", preferences.getString("hash_key","")));
                        params.add(new BasicNameValuePair("password", oldpassword.getText().toString()));
                        params.add(new BasicNameValuePair("new_password", newpassword.getText().toString()));
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
                        if (s.startsWith("E")) {
                            Toast.makeText(getActivity(), "Error Occured While Processing Request!", Toast.LENGTH_SHORT).show();
                        } else {
                            try
                            {
                                JSONObject object=new JSONObject(s);
                                if(object.getString("success").equals("true"))
                                {
                                    Toast.makeText(getActivity(),object.getString("content"),Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(),"Error Occured While Processing Request!\nTry Again Later.",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                        newpassword.setText("");
                        oldpassword.setText("");
                    }
                }.execute();
            }
        });

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
