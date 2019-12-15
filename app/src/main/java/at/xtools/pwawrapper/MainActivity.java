package at.xtools.pwawrapper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import at.xtools.pwawrapper.ui.UIManager;
import at.xtools.pwawrapper.webview.WebViewHelper;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static at.xtools.pwawrapper.Constants.PROFILE_URL;
import static at.xtools.pwawrapper.Constants.PROGRAM_URL;
import static at.xtools.pwawrapper.Constants.SPEAKERS_URL;
import static at.xtools.pwawrapper.Constants.WEBAPP_URL;

public class MainActivity extends AppCompatActivity {
    // Globals
    private UIManager uiManager;
    private WebViewHelper webViewHelper;
    private boolean intentHandled = false;
    private MeowBottomNavigation bottomNavigation;

    public void setCurrentPage_ID(int currentPage_ID) {
        this.currentPage_ID = currentPage_ID;
        bottomNavigation.show(currentPage_ID, true);
    }

    private int currentPage_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup Theme
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Helpers
        uiManager = new UIManager(this);
        webViewHelper = new WebViewHelper(this, uiManager);

        // Setup App
        webViewHelper.setupWebView();
        uiManager.changeRecentAppsIcon();

        bottomNavigation = findViewById(R.id.navigationBar);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_speaker_notes));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_today));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_account_circle));
        bottomNavigation.show(1, false);

        // Set Menu Click Listener
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                int i = model.getId();
                switch (i){
                    case 1:
                        currentPage_ID = 1;
                        webViewHelper.loadIntentUrl(WEBAPP_URL);
                        break;
                    case 2:
                        currentPage_ID = 2;
                        webViewHelper.loadIntentUrl(SPEAKERS_URL);
                        break;
                    case 3:
                        currentPage_ID = 3;
                        webViewHelper.loadIntentUrl(PROGRAM_URL);
                        break;
                    case 4:
                        currentPage_ID = 4;
                        webViewHelper.loadIntentUrl(PROFILE_URL);
                        break;
                }
                return Unit.INSTANCE;
            }
        });

        // Check for Intents
        try {
            Intent i = getIntent();
            String intentAction = i.getAction();
            // Handle URLs opened in Browser
             if (!intentHandled && intentAction != null && intentAction.equals(Intent.ACTION_VIEW)){
                    Uri intentUri = i.getData();
                    String intentText = "";
                    if (intentUri != null){
                        intentText = intentUri.toString();
                    }
                    // Load up the URL specified in the Intent
                    if (!intentText.equals("")) {
                        intentHandled = true;
                        webViewHelper.loadIntentUrl(intentText);
                    }
             } else {
                 // Load up the Web App
                 webViewHelper.loadHome();
             }
        } catch (Exception e) {
            // Load up the Web App
            webViewHelper.loadHome();
        }
    }

    @Override
    protected void onPause() {
        webViewHelper.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (currentPage_ID){
            case 1:
                webViewHelper.onResume(WEBAPP_URL);
                break;
            case 2:
                webViewHelper.onResume(SPEAKERS_URL);
                break;
            case 3:
                webViewHelper.onResume(PROGRAM_URL);
                break;
            case 4:
                webViewHelper.onResume(PROFILE_URL);
                break;
        }
        // retrieve content from cache primarily if not connected
        webViewHelper.forceCacheIfOffline();
    }

    // Handle back-press in browser
    @Override
    public void onBackPressed() {
        if (!webViewHelper.goBack()) {
            super.onBackPressed();
        }
    }
}
