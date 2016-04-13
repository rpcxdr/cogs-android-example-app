package io.cogswell.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.cogswell.example.table.CommonProperties;
import io.cogswell.example.table.GambitParameters;

public class SplashActivity extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2500;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        Uri data = getIntent().getData();
        if (data != null) {
            String scheme = data.getScheme();
            String host = data.getHost();
            String params = data.getQuery();

            Map<String, String> map = getQueryMap(params);
            Set<String> keys = map.keySet();
            GambitParameters parameters = new GambitParameters();
            for (String key : keys)
            {
                //Log.d("key", key);
                //Log.d("value", map.get(key));
                if (key.equals("access_key")){
                    parameters.accessKey = map.get(key);
                } else if (key.equals("secret_key")) {
                    parameters.secretKey = map.get(key);
                } else if (key.equals("namespace")) {
                    parameters.namespaceName = map.get(key);
                } else if (key.equals("event")) {
                    parameters.eventName = map.get(key);
                } else if (key.equals("platform")) {
                    parameters.platform = map.get(key);
                } else if (key.equals("enviornment")) {
                    parameters.enviornment = map.get(key);
                } else if (key.equals("platform_app_id")) {
                    parameters.platform_app_id = map.get(key);
                } else if (key.equals("campaign_id")) {
                    parameters.campaign_id = Integer.parseInt(map.get(key));
                } else if (key.equals("client_salt")) {
                    parameters.clientSalt = map.get(key);
                } else if (key.equals("client_secret")) {
                    parameters.clientSecret = map.get(key);
                } else if (key.equals("debug_directive")) {
                    parameters.debug_directive = map.get(key);
                } else if (key.equals("udid")) {
                    parameters.udid = map.get(key);
                }
            }
            CommonProperties.setParameters(parameters);

        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this,StartActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }
}
