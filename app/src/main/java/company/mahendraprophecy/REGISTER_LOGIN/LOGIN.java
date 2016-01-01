package company.mahendraprophecy.REGISTER_LOGIN;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

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

import company.mahendraprophecy.MAINSCREEN;
import company.mahendraprophecy.OTHERS.DASHBOARD;
import company.mahendraprophecy.LOCAL_DB_FILES.DATABASE_OPERATIONS;
import company.mahendraprophecy.R;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class LOGIN extends android.support.v4.app.Fragment {

    MaterialEditText email, password;
    Button login;
    LinearLayout forgot_password;
    boolean pass_shown = false;
    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;
    ViewPager pager;


    public LOGIN() {
    }

    ;

    @SuppressLint("ValidFragment")
    public LOGIN(ViewPager pager) {
        this.pager = pager;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        email = (MaterialEditText) getActivity().findViewById(R.id.login_mail);
        password = (MaterialEditText) getActivity().findViewById(R.id.login_password);
        login = (Button) getActivity().findViewById(R.id.login_button);

        ((TextView) getActivity().findViewById(R.id.takeToRegister)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(1);
            }
        });


        ((TextView) getActivity().findViewById(R.id.forgot_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View forgot_password_layout = getActivity().getLayoutInflater().inflate(R.layout.forgot_password_layout, null);

                final DialogPlus dialog = DialogPlus.newDialog(getActivity())
                        .setContentHolder(new ViewHolder(forgot_password_layout))

                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();

                ((TextView) forgot_password_layout.findViewById(R.id.password_reset)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AsyncTask<Void, Void, String>() {
                            String result = ""
                                    ,
                                    url = "https://www.mahendraprophecy.com/api/v1/forgot-password.php?key=1c0d2809e0140c09c6003d098400d477";
                            ProgressDialog progressDialog;

                            @Override
                            protected void onPreExecute() {
                                progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                progressDialog.setMessage("Processing Request.");
                                params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)));
                                params.add(new BasicNameValuePair("email", ((MaterialEditText) forgot_password_layout.findViewById(R.id.forgotpass_mail)).getText().toString()));
                            }

                            @Override
                            protected String doInBackground(Void... voids) {
                                HttpEntity httpEntity = null;

                                try {
                                    HttpClient httpClient = createHttpClient();  // Default HttpClient
                                    HttpPost httpPost = new HttpPost(url);
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
                                    try {
                                        JSONObject object = new JSONObject(s);
                                        if (object.getString("success").equals("true")) {
                                            Toast.makeText(getActivity(), object.getString("content"), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Error Occured While Processing Request!\nTry Again Later.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                progressDialog.dismiss();
                                dialog.dismiss();
                            }
                        }.execute();
                    }
                });
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setError("");
                password.setError("");

                if (email.getText().toString().isEmpty()) {
                    email.setError("Enter Valid Email-Address");
                    return;
                }


                if (password.getText().toString().length() < 6) {
                    password.setError("Password should be minimum 6 characters.");
                    return;
                }

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Logging You In");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new login_user().execute();
                //login karwa yaha pe
            }
        });

        ((ImageButton) getActivity().findViewById(R.id.show_login_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass_shown) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    ((ImageButton) getActivity().findViewById(R.id.show_login_password)).setImageResource(R.drawable.showpass_grey);
                    pass_shown = false;
                } else {
                    password.setTransformationMethod(null);
                    ((ImageButton) getActivity().findViewById(R.id.show_login_password)).setImageResource(R.drawable.showpass_orange);
                    pass_shown = true;
                }
            }
        });


    }

    List<NameValuePair> params;

    class login_user extends AsyncTask<Void, Void, String> {
        String result = "", url = "https://www.mahendraprophecy.com/api/v1/login.php?key=1c0d2809e0140c09c6003d098400d477";

        @Override
        protected void onPreExecute() {
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("device_id", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)));
            params.add(new BasicNameValuePair("email", email.getText().toString()));
            params.add(new BasicNameValuePair("password", password.getText().toString()));
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpEntity httpEntity = null;

            try {
                HttpClient httpClient = createHttpClient();  // Default HttpClient
                HttpPost httpPost = new HttpPost(url);
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
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "An Error Occured, Try Again Later !! ", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject object = new JSONObject(s);

                    if (!object.getString("success").equals("false")) {
                        progressDialog.setMessage("Sucessfully Logged In, Updating Details !!");
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE).edit();
                        editor.putString("auth_token", object.getString("auth_token"));
                        editor.putString("hash_key", object.getString("hash_key"));
                        editor.putString("isLoggedIn", "yes");
                        editor.commit();
                        JSONObject userDetails = object.getJSONObject("user_meta");
                        //((TextView)getActivity().findViewById(R.id.result)).setText(userDetails.getJSONArray("subscriptions").toString());
                        editor = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE).edit();
                        editor.putString("password", password.getText().toString());
                        editor.putString("first_name", userDetails.getString("first_name"));
                        editor.putString("last_name", userDetails.getString("last_name"));
                        editor.putString("full_name", userDetails.getString("full_name"));
                        editor.putString("email", userDetails.getString("email"));
                        editor.putString("address_line1", userDetails.getString("address_line1"));
                        editor.putString("address_line2", userDetails.getString("address_line2"));
                        editor.putString("city", userDetails.getString("city"));
                        editor.putString("state", userDetails.getString("state"));
                        editor.putString("country", userDetails.getString("country"));
                        editor.putString("postal_code", userDetails.getString("postal_code"));
                        editor.putString("mobile", userDetails.getString("mobile"));
                        editor.putString("phone", userDetails.getString("phone"));
                        editor.putString("fax", userDetails.getString("fax"));
                        editor.commit();

                        progressDialog.setMessage("Updating User Subsriptions");
                        DATABASE_OPERATIONS database_operations = new DATABASE_OPERATIONS(getActivity());
                        database_operations.deleteInformation(database_operations.getWritableDatabase());
                        JSONArray subscriptions = userDetails.getJSONArray("subscriptions");
                        for (int i = 0; i < subscriptions.length(); i++) {
                            JSONObject jsonObject = subscriptions.getJSONObject(i);
                            database_operations.putInformation(database_operations, jsonObject.getString("cat_id"), jsonObject.getString("name"), jsonObject.getString("expiry"), jsonObject.getString("status"));
                        }

                        Cursor cursor = database_operations.getInformation(database_operations);
                        cursor.moveToFirst();
                        int count = 0;
                        do {
                            count++;
                        }
                        while (cursor.moveToNext());

                        Log.d("database", String.valueOf(count));

                        startActivity(new Intent(getActivity(), MAINSCREEN.class));
                        //new getMetaData().execute();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),object.getString("errors"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }


    class getMetaData extends AsyncTask<Void, Void, String> {
        String result = "", url = "https://www.mahendraprophecy.com/api/v1/meta.php?key=1c0d2809e0140c09c6003d098400d477";

        @Override
        protected void onPreExecute() {
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
            try {
                //   Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), DASHBOARD.class);
                intent.putExtra("meta", s);
                progressDialog.dismiss();
                startActivity(intent);
            } catch (Exception e) {
                Log.d("Error", "" + e);
                Toast.makeText(getActivity(), "Error : " + e, Toast.LENGTH_LONG).show();
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
