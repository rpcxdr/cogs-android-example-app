package io.cogswell.example;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import io.cogswell.sdk.GambitRequest;
import io.cogswell.sdk.GambitSDKService;
import io.cogswell.sdk.message.GambitRequestMessage;
import io.cogswell.sdk.message.GambitResponseMessage;
import io.cogswell.sdk.notifications.QuickstartPreferences;
import io.cogswell.sdk.notifications.RegistrationIntentService;
import io.cogswell.sdk.push.GambitRequestPush;
import io.cogswell.sdk.push.GambitResponsePush;
import io.cogswell.sdk.request.GambitRequestEvent;
import io.cogswell.sdk.response.GambitResponseEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.cogswell.example.table.CommonProperties;
import io.cogswell.example.table.GambitAttribute;
import io.cogswell.example.table.GambitParameters;

public class PushActivity extends AppCompatActivity  {
    private String accessKey;
    private String secretKey;
    private String namespaceName;
    private String eventName;
    private String platform;
    private String enviornment = "dev";
    private JSONObject attributes;
    private String platform_app_id;
    private boolean wasPushed = false;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String clientSalt = null;
    private String clientSecret = null;

    private String randomUUID = null;
    private String randomUUIDBody = null;
    private ArrayList<GambitAttribute> namespaceAttributs = null;
    private String namespaceBody = null;
    private String eventBody = null;

    private boolean pushServiceStarted = false;

    private Button buttonRegisterPush;
    private Button buttonUnregisterPush;



    private EditText editTextAccessKey;
    private EditText editTextClientSalt;
    private EditText editTextClientSecret;
    private EditText editTextApplicationID;
    private EditText editTextApplication;
    private EditText editTextUUID;
    private EditText editTextAttributes;
    private EditText editTextNamespace;

    private String UDID;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    protected final ExecutorService executor = Executors.newCachedThreadPool();

    private class message extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            LinkedHashMap<String, Object> ciidAttributes = new LinkedHashMap<>();

            ciidAttributes.put("client_id", Integer.parseInt("1"));
            GambitRequestMessage.Builder builder = new GambitRequestMessage.Builder(
                    accessKey, clientSalt, clientSecret
            ).setNamespace(namespaceName)
                    .setAttributes(ciidAttributes)
                    .setUDID(UDID);

            Future<io.cogswell.sdk.GambitResponse> future = null;
            try {
                future = executor.submit(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Log.d("future", String.valueOf(future));
            GambitResponseMessage response;
            try {
                response = (GambitResponseMessage) future.get();

                //Log.d("response", String.valueOf(response));

            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            //Log.d("executed", "executed");
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    private class unRegisterPushService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            GambitRequestPush.Builder builder = new GambitRequestPush.Builder(
                    accessKey, clientSalt, clientSecret
            ).setNamespace(namespaceName)
                    .setAttributes(attributes)
                    .setUDID(UDID)
                    .setEnviornment(enviornment)
                    .setPlatform(platform)
                    .setPlatformAppID(platform_app_id)
                    .setMethodName(GambitRequestPush.unregister);

            Future<io.cogswell.sdk.GambitResponse> future = null;
            try {
                future = executor.submit(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Log.d("future", String.valueOf(future));
            GambitResponsePush response;
            try {
                response = (GambitResponsePush) future.get();

                Log.d("response 2", String.valueOf(response.getMessage()));

            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("test2", "test2");
            //Log.d("executed", "executed");
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    private class registerPushService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            GambitRequestPush.Builder builder = new GambitRequestPush.Builder(
                    accessKey, clientSalt, clientSecret
            ).setNamespace(namespaceName)
                    .setAttributes(attributes)
                    .setUDID(UDID)
                    .setEnviornment(enviornment)
                    .setPlatform(platform)
                    .setPlatformAppID(platform_app_id)
                    .setMethodName(GambitRequestPush.register);

            Future<io.cogswell.sdk.GambitResponse> future = null;
            try {
                future = executor.submit(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Log.d("future", String.valueOf(future));
            GambitResponsePush response;
            try {
                response = (GambitResponsePush) future.get();

                Log.d("response", String.valueOf(response.getRawBody()));

            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("test1", "test1");
            wasPushed = true;
            //Log.d("executed", "executed");
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private void rPushService() {
        if (accessKey == null || accessKey.isEmpty()) {
            return;
        }

        new registerPushService().execute("");
    }
    private void uPushService() {
        if (accessKey == null || accessKey.isEmpty()) {
            return;
        }


        new unRegisterPushService().execute("");
    }
    /*  private class pushService extends AsyncTask<String, Void, String> {

          @Override
          protected String doInBackground(String... params) {

              try {

                  pushServiceStarted = true; //TODO MOVE DOWN

                  LinkedHashMap<String, Object> ciidAttributes = new LinkedHashMap<>();

                  ciidAttributes.put("client_id", Integer.parseInt("1"));

                  GambitSDKService.getInstance().setGambitMessageListener(MainActivity.this);

                  GambitSDKService.getInstance().startPushService(
                          new GambitPushService.Builder(accessKey, clientSalt, clientSecret, activity)
                                  .setNamespace(namespaceName)
                                  .setAttributes(ciidAttributes)
                  );

              } catch (Exception ex) {
                  ex.printStackTrace();
              }

              return null;
          }

          @Override
          protected void onPostExecute(String result) {

              Log.d("executed", "executed");
          }

          @Override
          protected void onPreExecute() {}

          @Override
          protected void onProgressUpdate(Void... values) {}
      }
  */
    private void initPushNotifications() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intentRegistration = new Intent(this, RegistrationIntentService.class);
            startService(intentRegistration);

        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
    private Activity activity;


    private void loadParameters() {
        GambitParameters parameters = CommonProperties.getParameters();

        if (parameters == null) {
            return;
        }
        if (!parameters.accessKey.isEmpty()) {
            editTextAccessKey.setText(parameters.accessKey);
        }
        if(!parameters.clientSalt.isEmpty()) {
            editTextClientSalt.setText(parameters.clientSalt);
        }
        if(!parameters.clientSecret.isEmpty()) {
            editTextClientSecret.setText(parameters.clientSecret);
        }
        if(!parameters.platform_app_id.isEmpty()) {
            editTextApplicationID.setText(parameters.platform_app_id);
        }
        if(!parameters.platform.isEmpty()) {
            editTextApplication.setText(parameters.platform);
        }
        if(!parameters.udid.isEmpty()) {
            editTextUUID.setText(parameters.udid);
        }
        if(!parameters.namespaceName.isEmpty()) {
            editTextNamespace.setText(parameters.namespaceName);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initPushNotifications();
        activity = this;

        editTextAccessKey = (EditText) findViewById(R.id.editTextAccessKey);
        editTextClientSalt = (EditText) findViewById(R.id.editTextClientSalt);
        editTextClientSecret = (EditText) findViewById(R.id.editTextClientSecret);
        editTextApplicationID = (EditText) findViewById(R.id.editTextApplicationID);
        //editTextApplication = (EditText) findViewById(R.id.editTextApplication);
        editTextUUID = (EditText) findViewById(R.id.editTextUUID);
        editTextNamespace = (EditText) findViewById(R.id.editTextNamespace);
        editTextAttributes = (EditText) findViewById(R.id.editTextAttributes);
        //new CleintSecretCall().execute("");
        buttonRegisterPush = (Button) findViewById(R.id.buttonRegisterPush);
        buttonUnregisterPush = (Button) findViewById(R.id.buttonUnregisterPush);
        RelativeLayout toolbar_execute = (RelativeLayout) findViewById(R.id.toolbar_execute);
        RelativeLayout toolbar_start = (RelativeLayout) findViewById(R.id.toolbar_start);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (sharedPreferences.getString("accessKey", null) != null) {
            editTextAccessKey.setText(sharedPreferences.getString("accessKey", null));
        }
        if (sharedPreferences.getString("clientSalt", null) != null) {
            editTextClientSalt.setText(sharedPreferences.getString("clientSalt", null));
        }
        if (sharedPreferences.getString("clientSecret", null) != null) {
            editTextClientSecret.setText(sharedPreferences.getString("clientSecret", null));
        }
//        if (sharedPreferences.getString("platform_app_id", null) != null) {
//            editTextApplicationID.setText(String.valueOf(sharedPreferences.getString("platform_app_id", null)));
//        }
        if (sharedPreferences.getString("namespaceName", null) != null) {
            editTextNamespace.setText(sharedPreferences.getString("namespaceName", null));
        }
//        if (sharedPreferences.getString("platform", null) != null) {
//            editTextApplication.setText(sharedPreferences.getString("platform", null));
//        }
        if (sharedPreferences.getString("UDID", null) != null) {
            editTextUUID.setText(sharedPreferences.getString("UDID", null));
        }
        if (sharedPreferences.getString("attributes", null) != null) {
            editTextAttributes.setText(sharedPreferences.getString("attributes", null));
        }
        loadParameters();
        /*
        editTextAccessKey.setText(accessKey);
        editTextClientSalt.setText(clientSalt);
        editTextAccessKey.setText(clientSecret);
        editTextApplicationID.setText(platform_app_id);
        editTextApplication.setText(platform);*/

        toolbar_execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accessKey = editTextAccessKey.getText().toString();
                ///clientSalt = editTextClientSalt.getText().toString();
                //clientSecret = editTextClientSecret.getText().toString();
                //platform = editTextApplication.getText().toString();
                //UDID = editTextUUID.getText().toString();
                //platform_app_id = editTextApplicationID.getText().toString();



            }
        });
        toolbar_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(PushActivity.this,StartActivity.class);
                PushActivity.this.startActivity(mainIntent);
                PushActivity.this.finish();
            }
        });
        buttonRegisterPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wasPushed) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    if (sharedPreferences.getString("accessKey", null) != null) {
                        accessKey = sharedPreferences.getString("accessKey", null);
                    }
                    if (sharedPreferences.getString("clientSalt", null) != null) {
                        clientSalt = sharedPreferences.getString("clientSalt", null);
                    }
                    if (sharedPreferences.getString("clientSecret", null) != null) {
                        clientSecret = sharedPreferences.getString("clientSecret", null);
                    }
                    if (sharedPreferences.getString("platform_app_id", null) != null) {
                        platform_app_id = sharedPreferences.getString("platform_app_id", null);
                    }
                    if (sharedPreferences.getString("platform", null) != null) {
                        platform = sharedPreferences.getString("platform", null);
                    }
                    if (sharedPreferences.getString("UDID", null) != null) {
                        UDID = sharedPreferences.getString("UDID", null);
                    }
                    if (sharedPreferences.getString("namespaceName", null) != null) {
                        namespaceName = sharedPreferences.getString("namespaceName", null);
                    }
                    if (sharedPreferences.getString("attributes", null) != null) {
                        try {
                            attributes = new JSONObject(sharedPreferences.getString("attributes", null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    uPushService();

                }
                accessKey = editTextAccessKey.getText().toString();
                clientSalt = editTextClientSalt.getText().toString();
                clientSecret = editTextClientSecret.getText().toString();
                platform_app_id = editTextApplicationID.getText().toString();
                UDID = editTextUUID.getText().toString();
                platform = "android";//editTextApplication.getText().toString();
                namespaceName = editTextNamespace.getText().toString();

                try {
                    attributes = new JSONObject(editTextAttributes.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                sharedPreferences.edit().putString("accessKey", accessKey).apply();
                sharedPreferences.edit().putString("clientSalt", clientSalt).apply();
                sharedPreferences.edit().putString("clientSecret", clientSecret).apply();
                sharedPreferences.edit().putString("attributes", attributes.toString()).apply();
                sharedPreferences.edit().putString("platform", platform).apply();
                sharedPreferences.edit().putString("UDID", UDID).apply();
                sharedPreferences.edit().putString("platform_app_id", platform_app_id).apply();
                sharedPreferences.edit().putString("namespaceName", namespaceName).apply();

                Handler handlerTimer = new Handler();
                handlerTimer.postDelayed(new Runnable() {
                    public void run() {
                        rPushService();
                    }
                }, 500);
            }
        });

        buttonUnregisterPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessKey = editTextAccessKey.getText().toString();
                clientSalt = editTextClientSalt.getText().toString();
                clientSecret = editTextClientSecret.getText().toString();
                platform_app_id = editTextApplicationID.getText().toString();
                UDID = editTextUUID.getText().toString();
                platform = editTextApplication.getText().toString();
                namespaceName = editTextNamespace.getText().toString();
                try {
                    attributes = new JSONObject(editTextAttributes.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                sharedPreferences.edit().putString("accessKey", accessKey).apply();
                sharedPreferences.edit().putString("clientSalt", clientSalt).apply();
                sharedPreferences.edit().putString("clientSecret", clientSecret).apply();
                sharedPreferences.edit().putString("attributes", attributes.toString()).apply();
                sharedPreferences.edit().putString("platform", platform).apply();
                sharedPreferences.edit().putString("UDID", UDID).apply();
                sharedPreferences.edit().putString("platform_app_id", platform_app_id).apply();
                sharedPreferences.edit().putString("namespaceName", namespaceName).apply();

                Handler handlerTimer = new Handler();
                handlerTimer.postDelayed(new Runnable() {
                    public void run() {
                        uPushService();
                    }
                }, 500);
            }
        });

        /*buttonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new message().execute("");
            }
        });*/

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                String token = sharedPreferences.getString("token", "");
                if (sentToken) {
                    UDID = token;
                    //editTextUUID.setText(String.valueOf(UDID));
                    Log.d("variant1", "variant1");
                } else {
                    Log.d("variant2", "variant2");
                }
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
