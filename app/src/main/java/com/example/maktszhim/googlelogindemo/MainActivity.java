package com.example.maktszhim.googlelogindemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout Prof_Section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView FirstName,LastName;
    private ImageView Prof_Pic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private boolean IsLogIn = false;
    private String idToken;
    private String FirstNameString;
    private String LastNameString;
    private String ImageUrl;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "GoogleAcc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Prof_Section = findViewById(R.id.prof_section);
        SignOut = findViewById(R.id.bu_logout);
        SignIn = findViewById(R.id.bu_login);
        FirstName = findViewById(R.id.prof_firstname);
        LastName = findViewById(R.id.prof_lastname);
        Prof_Pic = findViewById(R.id.prof_pic);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        Prof_Section.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.server_client_id)).build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
        getLoginState();
        updateUI(IsLogIn);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bu_login:
                setSignIn();
                break;

            case R.id.bu_logout:
                setSignOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private  void setSignIn(){

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);

    }

    private  void setSignOut(){

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void handleResult(GoogleSignInResult result){

        if(result.isSuccess()){

            GoogleSignInAccount account =result.getSignInAccount();
            if (account.getIdToken() !=null) {
                idToken = account.getIdToken();
            }
            if (account.getGivenName() !=null){
                FirstNameString = account.getGivenName();
                FirstName.setText(FirstNameString);
            }
            if (account.getFamilyName() !=null){
                LastNameString = account.getFamilyName();
                LastName.setText(LastNameString);
            }
            if(account.getPhotoUrl() !=null){
                ImageUrl = account.getPhotoUrl().toString();
                Glide.with(this).load(ImageUrl).into(Prof_Pic);
            }
            updateUI(true);

        }else{
            updateUI(false);
        }

    }

    private void updateUI(boolean isLogin){

        if(isLogin){
            Prof_Section.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
            IsLogIn = true;
        }else{
            Prof_Section.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
            IsLogIn = false;
        }
        saveLoginState(isLogin);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE){

            GoogleSignInResult result =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    private void saveLoginState(boolean isLogin){
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.putBoolean("isLogin", isLogin);
        editor.putString("idToken", idToken);
        editor.putString("FirstName", FirstNameString);
        editor.putString("LastName", LastNameString);
        editor.putString("ImageUrl", ImageUrl);
        editor.apply();

    }

    private void getLoginState(){
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if (sharedpreferences.contains("FirstName")) {
            FirstName.setText(sharedpreferences.getString("FirstName", ""));
        }
        if (sharedpreferences.contains("LastName")) {
            LastName.setText(sharedpreferences.getString("LastName", ""));
        }
        if (sharedpreferences.contains("ImageUrl")) {
            Glide.with(this).load(sharedpreferences.getString("ImageUrl", "")).into(Prof_Pic);
        }
        if (sharedpreferences.contains("isLogin")) {
            IsLogIn = sharedpreferences.getBoolean("isLogin", false);
        }
        if (sharedpreferences.contains("idToken")) {
            idToken = sharedpreferences.getString("idToken", "");
        }

    }
}
