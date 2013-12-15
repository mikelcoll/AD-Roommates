package com.roommates.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

public class Login extends Activity implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final String TAG = "GPlusLogin";

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPlusClient = new PlusClient.Builder(this, this, this).setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").build();
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("google-plus", "onClick");

                if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
                    if (mConnectionResult == null) {
                        mConnectionProgressDialog.show();
                        Log.d("google-plus", "cas 1");

                    } else {
                        try {
                            Log.d("google-plus", "cas 2");

                            mConnectionResult.startResolutionForResult(Login.this, REQUEST_CODE_RESOLVE_ERR);
                        } catch (IntentSender.SendIntentException e) {
                            // Try connecting again.
                            Log.d("google-plus", "cas 2 catch");

                            mConnectionResult = null;
                            mPlusClient.connect();
                        }
                    }
                }
            }
        });
    }






    @Override
    public void onConnected(Bundle connectionHint) {
        mConnectionProgressDialog.dismiss();
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Log.d("google-plus", "disconnected");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("google-plus", "connection failed");

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }
    }

}
