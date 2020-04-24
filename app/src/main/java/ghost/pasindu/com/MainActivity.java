package ghost.pasindu.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;


    long startTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);

            loadAd();

            timerHandler.postDelayed(this, 120000);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                "Loading. Please wait...", true);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("https://katha.onlinehelpstudio.info");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println(url);
                loadAd();
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }
        });


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6889711673587860/2689708022"); //live
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        //test ca-app-pub-6889711673587860~3476423505
        //live ca-app-pub-6889711673587860/8252988831

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                System.out.println("********Ad Loaded********");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                timerHandler.postDelayed(timerRunnable, 0);
            }

            @Override
            public void onAdLoaded() {

            }
        });

    }

    private void loadAd() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void loadGoogleSheetOnWebView() {
        String googleSendFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLSdw-Y6PiPcsbhUL_G4qFDCUm1CJZ0wv1TpKnh-uKHSm2bUcwA";
        String currentUrl = mWebView.getUrl();
        System.out.println("CURRENT URL :::: " + currentUrl);
        if (currentUrl != null && !currentUrl.contains(googleSendFormUrl)) {
            mWebView.loadUrl("https://goo.gl/forms/wEgMWh1HfES9kmj43");
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    System.out.println(url);
                    loadAd();


                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rate_us) {
            String packageName = this.getPackageName();
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
            }

            //getApplicationContext().getPackageName()))
        } else if (id == R.id.action_exit) {
            showExitDialog();
        } else if (id == R.id.action_contact_us) {
            loadGoogleSheetOnWebView();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("කතා අහුර")
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
