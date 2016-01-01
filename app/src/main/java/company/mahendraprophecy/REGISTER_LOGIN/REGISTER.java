package company.mahendraprophecy.REGISTER_LOGIN;


import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Arrays;
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
public class REGISTER extends android.support.v4.app.Fragment
{

    AutoCompleteTextView country_spinner;
    String[] codes;
    List countries;
    MaterialEditText fname,lname,number,city,password,email;
    boolean pass_shown=false;
    String selected_country;
    int selected_index=-1;

    ProgressDialog progressDialog;

    public REGISTER()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.register, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        codes=getResources().getStringArray(R.array.country_codes);

        country_spinner= (AutoCompleteTextView) getActivity().findViewById(R.id.register_country);
        fname= (MaterialEditText) getActivity().findViewById(R.id.register_fname);
        lname= (MaterialEditText) getActivity().findViewById(R.id.register_lname);
        number= (MaterialEditText) getActivity().findViewById(R.id.register_mobile);
        password= (MaterialEditText) getActivity().findViewById(R.id.register_password);
        email= (MaterialEditText) getActivity().findViewById(R.id.register_email);
        city= (MaterialEditText) getActivity().findViewById(R.id.register_city);

        countries= Arrays.asList(getResources().getStringArray(R.array.country_names));
        //getviews
        country_spinner= (AutoCompleteTextView) getActivity().findViewById(R.id.register_country);
        final ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,countries);
        country_spinner.setAdapter(adapter);


        country_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selected_country=adapter.getItem(position).toString();
                Toast.makeText(getActivity(),selected_country+"\n"+codes[countries.indexOf(selected_country)],Toast.LENGTH_SHORT).show();
            }
        });



        ((ImageButton)getActivity().findViewById(R.id.show_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass_shown) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    pass_shown = false;
                    ((ImageButton) getActivity().findViewById(R.id.show_password)).setImageResource(R.drawable.showpass_grey);
                } else {
                    password.setTransformationMethod(null);
                    pass_shown = true;
                    ((ImageButton) getActivity().findViewById(R.id.show_password)).setImageResource(R.drawable.showpass_orange);
                }
            }
        });

        ((Button)getActivity().findViewById(R.id.register_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname.setError("");
                lname.setError("");
                password.setError("");
                number.setError("");
                email.setError("");
                city.setError("");

                if (fname.getText().toString().isEmpty()) {
                    fname.setError("Enter First Name");
                    return;
                }

                if (lname.getText().toString().isEmpty()) {
                    lname.setError("Enter Last Name");
                    return;
                }

                if (email.getText().toString().isEmpty()) {
                    email.setError("Enter Email Address");
                    return;
                }

                if (password.getText().toString().length() < 6) {
                    password.setError("password 6 characters minimum");
                    return;
                }

                if (number.getText().toString().isEmpty()) {
                    number.setError("Enter Phone Number");
                    return;
                }

                if (city.getText().toString().isEmpty()) {
                    city.setError("Enter City");
                    return;
                }

                progressDialog=new ProgressDialog(getActivity());
                progressDialog.setMessage("Registering User");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new registerUser().execute();


            }
        });
    }



     List<NameValuePair> params;
    class registerUser extends AsyncTask<Void, Void, String>
    {
        String result = "", url = "https://www.mahendraprophecy.com/api/v1/register.php?key=1c0d2809e0140c09c6003d098400d477";

        @Override
        protected void onPreExecute()
        {
            params = new ArrayList<NameValuePair>();

            /*
            device_id
            first_name
            last_name
            email
            password
            city
            country
            mobile
            phone
             */

            params.add(new BasicNameValuePair("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)));
            params.add(new BasicNameValuePair("first_name", fname.getText().toString()));
            params.add(new BasicNameValuePair("last_name", lname.getText().toString()));
            params.add(new BasicNameValuePair("email", email.getText().toString()));
            params.add(new BasicNameValuePair("password", password.getText().toString()));
            params.add(new BasicNameValuePair("city", city.getText().toString()));
            params.add(new BasicNameValuePair("country", codes[countries.indexOf(selected_country)]));
            params.add(new BasicNameValuePair("mobile", number.getText().toString()));

        }

        @Override
        protected String doInBackground(Void... voids)
        {

            HttpEntity httpEntity = null;

            try
            {
                HttpClient httpClient = createHttpClient();  // Default HttpClient
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();
                String entityResponse = EntityUtils.toString(httpEntity);
                Log.e("Entity Response  : ", entityResponse);
                return entityResponse;
            }
            catch (Exception e)
            {
                result = "Error : " + e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s)
        {
            progressDialog.dismiss();
            try
            {
                Toast.makeText(getActivity(),new JSONObject(s).getJSONArray("content").getString(0),Toast.LENGTH_LONG).show();
            } catch (JSONException e)
            {
                e.printStackTrace();
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
}
