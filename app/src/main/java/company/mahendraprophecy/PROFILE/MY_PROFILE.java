package company.mahendraprophecy.PROFILE;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
public class MY_PROFILE extends android.support.v4.app.Fragment {

    MaterialEditText firstName,lastName,email,phone,city;
    AutoCompleteTextView countrySpinner;
    ImageButton editButton;
    Button updateDetails;
    SharedPreferences sharedpreferences;
    List countries;
    String[] countryCodes;
    List<NameValuePair> params;
    boolean isActive=false;
    String selected_country,code;
    public MY_PROFILE()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_profile_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firstName= (MaterialEditText) getActivity().findViewById(R.id.profile_fname);
        lastName= (MaterialEditText) getActivity().findViewById(R.id.profile_lname);
        email= (MaterialEditText) getActivity().findViewById(R.id.profile_email);
        phone= (MaterialEditText) getActivity().findViewById(R.id.profile_mobile);
        city= (MaterialEditText) getActivity().findViewById(R.id.profile_city);
        editButton= (ImageButton) getActivity().findViewById(R.id.edit_profile);
        updateDetails= (Button) getActivity().findViewById(R.id.profile_update_button);
        countrySpinner= (AutoCompleteTextView) getActivity().findViewById(R.id.profile_country_spinner);
        countryCodes=getResources().getStringArray(R.array.country_codes);
        countries= Arrays.asList(getResources().getStringArray(R.array.country_names));
        final ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,countries);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selected_country=adapter.getItem(position).toString();
                code=countryCodes[countries.indexOf(selected_country)];
            }
        });

        inActivateAll();
        setDetails();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isActive)
                {
                    setDetails();
                    inActivateAll();
                    isActive=!isActive;
                    editButton.requestFocus();
                }
                 else
                {
                    ActivateAll();
                    firstName.requestFocus();
                    isActive=!isActive;
                }
            }
        });

        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstName.getText().toString().isEmpty())
                {
                    firstName.setError("First Name Cannot Be Empty");
                    return;
                }

                if(lastName.getText().toString().isEmpty())
                {
                    lastName.setError("Last Name Cannot Be Empty");
                    return;
                }

                if(email.getText().toString().isEmpty())
                {
                    email.setError("Email Cannot Be Empty");
                    return;
                }
                if(phone.getText().toString().isEmpty())
                {
                    phone.setError("Phone Cannot Be Empty");
                    return;
                }

                if(city.getText().toString().isEmpty())
                {
                    city.setError("City Cannot Be Empty");
                    return;
                }


                new AsyncTask<Void, Void, String>()
                {

                    String result = ""
                            ,
                            url = "https://www.mahendraprophecy.com/api/v1/update-profile.php?key=1c0d2809e0140c09c6003d098400d477";
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
                        params.add(new BasicNameValuePair("first_name", firstName.getText().toString()));
                        params.add(new BasicNameValuePair("last_name", lastName.getText().toString()));
                        params.add(new BasicNameValuePair("email", email.getText().toString()));
                        params.add(new BasicNameValuePair("address_line1",""));
                        params.add(new BasicNameValuePair("address_line2",""));
                        params.add(new BasicNameValuePair("city",city.getText().toString()));
                        params.add(new BasicNameValuePair("state",""));
                        params.add(new BasicNameValuePair("country",code));
                        params.add(new BasicNameValuePair("postal_code",""));
                        params.add(new BasicNameValuePair("mobile",phone.getText().toString()));
                        params.add(new BasicNameValuePair("fax",""));

                        /*
                        device_id
                        auth_token
                        hash_key
                        first_name
                        last_name
                        email
                        address_line1
                        address_line2
                        city
                        state
                        country
                        postal_code
                        mobile
                        phone
                        fax
                         */


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
                            Toast.makeText(getActivity(), "Error Occured While Processing Request!\n"+s, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(),object.getString("content"),Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        inActivateAll();
                        setDetails();
                        progressDialog.dismiss();
                    }
                }.execute();
            }
        });
    }

    void inActivateAll()
    {
        firstName.setFocusableInTouchMode(false);
        lastName.setFocusableInTouchMode(false);
        email.setFocusableInTouchMode(false);
        phone.setFocusableInTouchMode(false);
        city.setFocusableInTouchMode(false);
        firstName.setCursorVisible(false);
        lastName.setCursorVisible(false);
        email.setCursorVisible(false);
        phone.setCursorVisible(false);
        city.setCursorVisible(false);
        countrySpinner.setEnabled(false);
        updateDetails.setVisibility(View.INVISIBLE);
        editButton.setImageResource(R.drawable.edit_profile_grey);
    }


    void setDetails()
    {

        sharedpreferences=getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        firstName.setText(sharedpreferences.getString("first_name",""));
        lastName.setText(sharedpreferences.getString("last_name", ""));
        email.setText(sharedpreferences.getString("email", ""));
        phone.setText(sharedpreferences.getString("mobile", ""));
        city.setText(sharedpreferences.getString("city", ""));
        countryCodes=getResources().getStringArray(R.array.country_codes);
        countries= Arrays.asList(getResources().getStringArray(R.array.country_names));
        for(int i=0;i<countryCodes.length;i++)
        {
            if(countryCodes[i].equals(sharedpreferences.getString("country", "")))
            {
                countrySpinner.setText(countries.get(i).toString());
                break;
            }
        }
    }

    void ActivateAll()
    {
        firstName.setFocusableInTouchMode(true);
        lastName.setFocusableInTouchMode(true);
        email.setFocusableInTouchMode(true);
        phone.setFocusableInTouchMode(true);
        city.setFocusableInTouchMode(true);
        firstName.setCursorVisible(true);
        lastName.setCursorVisible(true);
        email.setCursorVisible(true);
        phone.setCursorVisible(true);
        city.setCursorVisible(true);
        countrySpinner.setEnabled(true);
        updateDetails.setVisibility(View.VISIBLE);
        editButton.setImageResource(R.drawable.edit_profile_orange);
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
