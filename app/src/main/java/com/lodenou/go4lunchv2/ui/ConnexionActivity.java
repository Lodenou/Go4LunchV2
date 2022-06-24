package com.lodenou.go4lunchv2.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lodenou.go4lunchv2.databinding.ActivityConnexionBinding;
import com.facebook.FacebookSdk;

import java.util.Arrays;


public class ConnexionActivity extends AppCompatActivity {

    private ActivityConnexionBinding binding;
    private FirebaseAuth mAuth;
    private final String TAG = "GOOGLE_SIGN_IN_TAG";
    private final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnexionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        setGoogleSignIn();
        facebookSignIn();

    }

    @Override
    protected void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        super.onStart();
    }

    public void onClick(View v) {
        if (v == binding.googleButton) {
            Log.d(TAG, "OnClick: begin Google SignIn");
            Intent intent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);

        }
        if (v == binding.loginButtonFb) {

            binding.loginButtonFb.performClick();
        }
    }

    // GOOGLE LOGIN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // GOOGLE
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> mAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = mAccountTask.getResult(ApiException.class);
                FirebaseAuthWithGoogleAccount(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "OnClick: ERROR ACTIVITY RESULT NOT OK");

        }
        // FACEBOOK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void FirebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseauthwithgoogleaccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "OnSuccess: Logged in");
                        // Get logged in user
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        // Get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();
                        Log.d(TAG, "OnSuccess: Email" + email);
                        Log.d(TAG, "OnSuccess: uid" + uid);
                        // Check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            // User is new account created
                            Log.d(TAG, "OnSuccess: Account created...\n" + email);
                            Toast.makeText(ConnexionActivity.this, "Account created...\n" + email, Toast.LENGTH_SHORT).show();
                        } else {
                            // Existing user logged in
                            Log.d(TAG, "OnSuccess: Existing user... \n" + email);
                            Toast.makeText(ConnexionActivity.this, "Existing user...\n" + email, Toast.LENGTH_SHORT).show();
                        }

                        // Start MainActivity
                        startActivity(new Intent(ConnexionActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "OnFailure: Loggin failed" + e.getMessage());
                    }
                });
    }

    private void setGoogleSignIn() {
        // Configure google sign in
        GoogleSignInOptions mGoogleSignInOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("859453280997-nrjn57ulqiao9aer8bckq3hdg2lbtlv0.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOption);

        // Init firebase auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void facebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create();
        binding.loginButtonFb.setPermissions("email", "public_profile");
        binding.loginButtonFb.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(ConnexionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }



    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //Start main activity
                startActivity(new Intent(ConnexionActivity.this, MainActivity.class));
                Toast.makeText(ConnexionActivity.this,
                        "You successfully signed-in ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };


    private void updateUI(FirebaseUser user) {

        if (user != null) {
            Toast.makeText(this, "You Signed In successfully", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "You Didnt signed in", Toast.LENGTH_LONG).show();
        }
    }


}

