package io.cogswell.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import io.cogswell.example.notifications.QuickstartPreferences;
import io.cogswell.example.notifications.RegistrationIntentService;
import io.cogswell.sdk.push.GambitRequestPush;
import io.cogswell.sdk.push.GambitResponsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private String attributesJSONAsString;
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

    private class unRegisterPushService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Future<io.cogswell.sdk.GambitResponse> future = null;
            try {
                // This will throw an exception if the json is invalid.
                JSONObject attributes = new JSONObject(attributesJSONAsString);

                GambitRequestPush.Builder builder = new GambitRequestPush.Builder(
                        accessKey, clientSalt, clientSecret
                ).setNamespace(namespaceName)
                        .setAttributes(attributes)
                        .setUDID(UDID)
                        .setEnviornment(enviornment)
                        .setPlatform(platform)
                        .setPlatformAppID(platform_app_id)
                        .setMethodName(GambitRequestPush.unregister);

                future = executor.submit(builder.build());

                //Log.d("future", String.valueOf(future));
                GambitResponsePush response;
                try {
                    response = (GambitResponsePush) future.get();

                    Log.d("response 2", String.valueOf(response.getMessage()));
                    final String message = response.getRawBody();
                    Log.d("response", String.valueOf(response.getRawBody()));
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (!message.equals("") && message != null && !message.isEmpty()) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Please Note!")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }
                    });

                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                final String messageFinal;
                if (e instanceof JSONException) {
                    messageFinal = "The JSON syntax for Attributes as JSON is invalid.";
                } else {
                    messageFinal = "Please confirm your keys, ids, and namespace are correct.";
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(activity)
                                .setTitle("Invalid un-subscription data:")
                                .setMessage(messageFinal)
                                .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
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

            Future<io.cogswell.sdk.GambitResponse> future = null;
            try {
                // This will throw an exception if the json is invalid.
                JSONObject attributes = new JSONObject(attributesJSONAsString);

                GambitRequestPush.Builder builder = new GambitRequestPush.Builder(
                        accessKey, clientSalt, clientSecret
                ).setNamespace(namespaceName)
                        .setAttributes(attributes)
                        .setUDID(UDID)
                        .setEnviornment(enviornment)
                        .setPlatform(platform)
                        .setPlatformAppID(platform_app_id)
                        .setMethodName(GambitRequestPush.register);

                future = executor.submit(builder.build());

                //Log.d("future", String.valueOf(future));
                GambitResponsePush response;
                try {
                    response = (GambitResponsePush) future.get();
                    final String message = response.getRawBody();
                    Log.d("response", String.valueOf(response.getMessage()));
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (!message.equals("") && message != null && !message.isEmpty()) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Please Note!")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }
                    });

                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                final String messageFinal;
                if (e instanceof JSONException) {
                    messageFinal = "The JSON syntax for Attributes as JSON is invalid.";
                } else {
                    messageFinal = "Please confirm your keys, ids, and namespace are correct.";
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(activity)
                                .setTitle("Invalid un-subscription data:")
                                .setMessage(messageFinal)
                                .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
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
        // We've loaded from the parameters once - after this, rely on the saved properties.
        CommonProperties.setParameters(null);
        saveFields();
    }
    public boolean validateFields() {

        accessKey = editTextAccessKey.getText().toString();
        clientSalt = editTextClientSalt.getText().toString();
        clientSecret = editTextClientSecret.getText().toString();
        platform = "android";//editTextApplication.getText().toString();
        platform_app_id = editTextApplicationID.getText().toString();
        UDID = editTextUUID.getText().toString();
        namespaceName = editTextNamespace.getText().toString();

        String message = null;
        if (accessKey == null || accessKey.equals("") || accessKey.isEmpty() || accessKey.length() < 10) {
            message = "Access Key is Missing or It's not Correct!";
        }
        if (clientSalt == null || clientSalt.equals("") || clientSalt.isEmpty() || clientSalt.length() < 10) {
            message = "Client Salt is Missing or It's not Correct!";
        }
        if (clientSecret == null || clientSecret.equals("") || clientSecret.isEmpty() || clientSecret.length() < 10) {
            message = "Client Secret is Missing or It's not Correct!";
        }
        if (platform == null || platform.equals("") || platform.isEmpty()) {
            message = "Application is Missing!";
        }
        if (platform_app_id == null || platform_app_id.equals("") || platform_app_id.isEmpty()) {
            message = "Application ID is Missing!";
        }
        if (UDID == null || UDID.equals("") || UDID.isEmpty()) {
            message = "UUID is Missing!";
        }
        try {
            // Change the field to use the converted JSON so the user clearly sees what the server is seeing.
            attributesJSONAsString = editTextAttributes.getText().toString();
            attributesJSONAsString = new JSONObject(attributesJSONAsString).toString();
            editTextAttributes.setText(attributesJSONAsString);
        } catch (Exception e) {
            e.printStackTrace();
            message = "Attributes as JSON is not valid JSON!";
        }

        if (message != null) {
            final String messageFinal = message;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(activity)
                            .setTitle("Please Note!")
                            .setMessage(messageFinal)
                            .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // We need to implement this method for the cancel button to appear.
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            return false;
        }

        return true;
    }

    public void saveFields() {
        accessKey = editTextAccessKey.getText().toString();
        clientSalt = editTextClientSalt.getText().toString();
        clientSecret = editTextClientSecret.getText().toString();
        platform = "android";//editTextApplication.getText().toString();
        platform_app_id = editTextApplicationID.getText().toString();
        UDID = editTextUUID.getText().toString();
        namespaceName = editTextNamespace.getText().toString();
        attributesJSONAsString = editTextAttributes.getText().toString();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPreferences.edit().putString("accessKey", accessKey).apply();
        sharedPreferences.edit().putString("clientSalt", clientSalt).apply();
        sharedPreferences.edit().putString("clientSecret", clientSecret).apply();
        sharedPreferences.edit().putString("platform", platform).apply();
        sharedPreferences.edit().putString("platform_app_id", platform_app_id).apply();
        sharedPreferences.edit().putString("UDID", UDID).apply();
        sharedPreferences.edit().putString("namespaceName", namespaceName).apply();
        sharedPreferences.edit().putString("attributes", attributesJSONAsString).apply();
    }
    public void fillData() {
        accessKey = editTextAccessKey.getText().toString();
        clientSalt = editTextClientSalt.getText().toString();
        clientSecret = editTextClientSecret.getText().toString();
        platform_app_id = editTextApplicationID.getText().toString();
        UDID = editTextUUID.getText().toString();
        platform = "android";//editTextApplication.getText().toString();
        namespaceName = editTextNamespace.getText().toString();
        attributesJSONAsString = editTextAttributes.getText().toString();

        if(validateFields() == false) {
            return;
        }
        saveFields();
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
        if (sharedPreferences.getString("platform_app_id", null) != null) {
           editTextApplicationID.setText(String.valueOf(sharedPreferences.getString("platform_app_id", null)));
        }
        if (sharedPreferences.getString("namespaceName", null) != null) {
            editTextNamespace.setText(sharedPreferences.getString("namespaceName", null));
        }
        // The platform is always android.
        // if (sharedPreferences.getString("platform", null) != null) {
        //     editTextApplication.setText(sharedPreferences.getString("platform", null));
        // }
        // The UDID is automatically populated.
        //if (sharedPreferences.getString("UDID", null) != null) {
        //    editTextUUID.setText(sharedPreferences.getString("UDID", null));
        //}
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
                fillData();

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
                        attributesJSONAsString = sharedPreferences.getString("attributes", null);
                    }
                    if(validateFields() == false) {
                        return;
                    }
                    uPushService();

                }

                if(validateFields() == false) {
                    return;
                }
                saveFields();

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

                if(validateFields() == false) {
                    return;
                }

                saveFields();

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
                    editTextUUID.setText(String.valueOf(UDID));
                    Log.d("UDID", UDID);
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
        fillData();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
