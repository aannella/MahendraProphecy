package company.mahendraprophecy.BOOKS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONObject;

import company.mahendraprophecy.R;
import company.mahendraprophecy.REGISTER_LOGIN.REGISTER_LOGIN;


public class BOOK_DETAILS extends ActionBarActivity {

    JSONObject jsonObject;
    TextView price, buy;
    String id;
    WebView webView;
    ProgressWheel progressWheel;
    boolean loggedin;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        id = (getIntent().getExtras().getString("id"));
        sharedpreferences = getSharedPreferences("meta", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("isLoggedIn", "").matches("")) {
            loggedin = false;
        } else {
            loggedin = true;
        }
        progressWheel = (ProgressWheel) findViewById(R.id.books_progress_wheel);
        price = (TextView) findViewById(R.id.book_price);
        buy = (TextView) findViewById(R.id.buyBook);
        webView = (WebView) findViewById(R.id.book_webview);


        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ((ImageButton) findViewById(R.id.backToBooks)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BOOK_DETAILS.this, BOOKS.class));
            }
        });

        ((LinearLayout) findViewById(R.id.back_to_book_desccription)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressWheel.setVisibility(View.VISIBLE);
                String Url;
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("www.mahendraprophecy.com")
                        .appendPath("api")
                        .appendPath("v1")
                        .appendPath("book-details.php")
                        .appendQueryParameter("id", id)
                        .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

                Url = builder.build().toString();
                webView.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webView.loadUrl(Url);

                YoYo.with(Techniques.Pulse.SlideInUp)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .playOn(((LinearLayout) findViewById(R.id.buyLayout)));
            }
        });


        /*
        https://www.mahendraprophecy.com/api/v1/book-details.php?id={BOOK_ID_HERE}&key={KEY_HERE}

        Books Buy Now Link:
        http://www.mahendraprophecy.com/api/v1/checkout.php?&p=books&id={BOOK_ID_HERE}&key={API_KEY_HERE}&auth_token={AUTH_TOKEN_HERE}&device_id={DEVICE_ID_HERE}

         */

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                progressWheel.setVisibility(View.INVISIBLE);
            }
        });

        String Url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.mahendraprophecy.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("book-details.php")
                .appendQueryParameter("id", id)
                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477");

        Url = builder.build().toString();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);


        try {
            price.setText("$" + getIntent().getExtras().getString("price"));

            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (loggedin) {
                        YoYo.with(Techniques.SlideOutDown)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        String Url;
                                        Uri.Builder builder = new Uri.Builder();
                                        builder.scheme("http")
                                                .authority("www.mahendraprophecy.com")
                                                .appendPath("api")
                                                .appendPath("v1")
                                                .appendPath("checkout.php")
                                                .appendQueryParameter("p", "books")
                                                .appendQueryParameter("id", id)
                                                .appendQueryParameter("key", "1c0d2809e0140c09c6003d098400d477")
                                                .appendQueryParameter("auth_token", sharedpreferences.getString("auth_token", ""))
                                                .appendQueryParameter("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                                        Url = builder.build().toString();
                                        progressWheel.setVisibility(View.VISIBLE);
                                        webView.loadUrl(Url);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .playOn(((LinearLayout) findViewById(R.id.buyLayout)));

                    } else
                    {
                        /*
                        NiftyDialogBuilder dialogBuilder=new NiftyDialogBuilder(BOOK_DETAILS.this);
                        dialogBuilder
                                .withButton1Text("LOGIN / REGISTER")
                                .setButton1Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(BOOK_DETAILS.this, REGISTER_LOGIN.class));
                                    }
                                })
                        .withDialogColor(Color.parseColor("#FFFFFF"))
                        .withTitleColor(Color.parseColor("#000000"))
                        .withTitle("Authentication Alert !")
                        .withMessage("Please Login Or Register to buy this book !")
                        .withMessageColor(Color.parseColor("#000000"))
                        .isCancelable(true)
                        .withEffect(Effectstype.Fadein)
                        .show();
*/

                        AlertDialog.Builder builder = new AlertDialog.Builder(BOOK_DETAILS.this);
// Add the buttons
                       AlertDialog dialog;

                        builder
                        .setTitle("Authentication Alert !")
                        .setMessage("Please Login Or Register to buy this book !");
                        builder.setPositiveButton("LOGIN/REGISTER", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(BOOK_DETAILS.this, REGISTER_LOGIN.class));
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
// Set other dialog properties


// Create the AlertDialog
                        dialog = builder.create();

                        dialog.show();
                        /*
                        final MaterialDialog mMaterialDialog = new MaterialDialog(BOOK_DETAILS.this);
                        mMaterialDialog
                                .setBackgroundResource(Color.GREEN)
                                .setTitle("Authentication Alert !")
                                .setMessage("Please Login Or Register to buy this book !")
                                .setPositiveButton("LOGIN / REGISTER", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(BOOK_DETAILS.this, REGISTER_LOGIN.class));
                                    }
                                })
                                .setNegativeButton("CANCEL", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                });
                        mMaterialDialog.show();
                        */
                    }
                }
            });
        } catch (Exception e) {
            //Toast.makeText(getBaseContext(), "Error :" + e, Toast.LENGTH_LONG).show();
        }
    }
}