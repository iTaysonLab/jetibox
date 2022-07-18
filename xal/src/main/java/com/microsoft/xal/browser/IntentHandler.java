package com.microsoft.xal.browser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.xal.logging.XalLogger;

public class IntentHandler extends Activity {
    private final XalLogger m_logger = new XalLogger("IntentHandler");

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.m_logger.Important("onCreate() New intent received.");
        this.m_logger.Flush();
        Intent intent = new Intent(this, BrowserLaunchActivity.class);
        intent.setData(getIntent().getData());
        intent.addFlags(603979776);
        startActivity(intent);
        finish();
    }
}