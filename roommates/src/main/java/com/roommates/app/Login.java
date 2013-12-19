package com.roommates.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.android.gms.plus.GooglePlusUtil;
import com.google.android.gms.plus.PlusClient;


public class Login extends Activity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final String TAG = "GPlusLogin";


    // A magic number we will use to know that our sign-in error
    // resolution activity has completed.
    private static final int OUR_REQUEST_CODE = 49404;

    private ProgressDialog mConnectionProgressDialog;

    private PlusClient mPlusClient;

    // A flag to stop multiple dialogues appearing for the user.
    private boolean mResolveOnFail;
    // We can store the connection result from a failed connect()
    // attempt in order to make the application feel a bit more
    // responsive for the user.
    private ConnectionResult mConnectionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int errorCode = GooglePlusUtil.checkGooglePlusApp(this);

        if (errorCode != GooglePlusUtil.SUCCESS) {
            GooglePlusUtil.getErrorDialog(errorCode, this, 0).show();
        }

        mPlusClient = new PlusClient.Builder(this, this, this)
                .setVisibleActivities("http://schemas.google.com/BuyActivity")
                .build();
        // We use mResolveOnFail as a flag to say whether we should trigger
        // the resolution of a connectionFailed ConnectionResult.
        mResolveOnFail = false;

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "Start");
        // Every time we start we want to try to connect. If it
        // succeeds we'll get an onConnected() callback. If it
        // fails we'll get onConnectionFailed(), with a result!
        mPlusClient.connect();
    }


    @Override
    public void onClick(View view) {
        Log.d("google-plus", "onClick");
        switch (view.getId()) {
            case R.id.sign_in_button:
                Log.v(TAG, "Tapped sign in");
                if (!mPlusClient.isConnected()) {
                    // Show the dialog as we are now signing in.
                    mConnectionProgressDialog.show();
                    // Make sure that we will start the resolution (e.g. fire the
                    // intent and pop up a dialog for the user) for any errors
                    // that come in.
                    mResolveOnFail = true;
                    // We should always have a connection result ready to resolve,
                    // so we can start that process.
                    if (mConnectionResult != null) {
                        startResolution();
                    }
                    else {
                        // If we don't have one though, we can start connect in
                        // order to retrieve one.
                        mPlusClient.connect();
                    }
                }
                break;
        }
    };



    @Override
    public void onConnected(Bundle connectionHint) {
        mConnectionProgressDialog.dismiss();
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), FlatSettings.class);
        startActivity(intent);
    }


    @Override
    public void onDisconnected() {
        Log.d("google-plus", "disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v(TAG, "ConnectionFailed");
        // Most of the time, the connection will fail with a
        // user resolvable result. We can store that in our
        // mConnectionResult property ready for to be used
        // when the user clicks the sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mResolveOnFail) {
                // This is a local helper function that starts
                // the resolution of the problem, which may be
                // showing the user an account chooser or similar.
                startResolution();
            }
        }
    }


    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnenctionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a
            // result from the activity we're about to start.
            mResolveOnFail = false;
            // If we can resolve the error, then call start resolution
            // and pass it an integer tag we can use to track. This means
            // that when we get the onActivityResult callback we'll know
            // its from being started here.
            mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new
            // ConnectionResult.
            mPlusClient.connect();
        }
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
