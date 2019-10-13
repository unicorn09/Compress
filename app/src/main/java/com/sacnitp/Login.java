package com.sacnitp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    Button googleSignUp;String name2;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        googleSignUp = findViewById(R.id.btn_login);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        DatabaseReference daa=FirebaseDatabase.getInstance().getReference();
        Log.i("harsh",daa.toString());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        progressDialog.dismiss();
                        Log.e("connection failed",connectionResult.getErrorMessage());
                        Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                } )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Getting accounts...");
                progressDialog.show();
                signIn();
            }
        });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            progressDialog.setMessage("Signing you in...");
              GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                mGoogleApiClient.clearDefaultAccountAndReconnect();

            }
            else {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), " Connection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Login Page", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Something went wrong\n" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Successful signin",
                                    Toast.LENGTH_SHORT).show();

                            progressDialog.setMessage("Checking Email...");
                            checkingUserExist(auth.getCurrentUser().getUid());
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    private void checkingUserExist(String UID) {
        Log.e("payraj",UID);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");

        db.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    secondActivity();

                }
                else{
                    String mailid = "";
                    String name = "";
                    String photo = "";
                    try{
                        mailid = auth.getCurrentUser().getEmail();
                        name = auth.getCurrentUser().getDisplayName().toUpperCase();
                        photo = auth.getCurrentUser().getPhotoUrl().toString();
                    }
                    catch (Exception e){
                        Log.e("Getting Started",e.getMessage());
                    }
                    progressDialog.setMessage("Creating New User...");
                    createNewUser(mailid,name,photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void secondActivity() {
        Intent i=new Intent(getApplicationContext(), Home.class);
        i.putExtra("Name2",name2);
        startActivity(i);
        finish();
    }

    private void nextActivity() {
        Intent i=new Intent(getApplicationContext(), Home.class);
        i.putExtra("Name2",name2);
        startActivity(i);
        finish();
    }
    public void createNewUser( String mailid, String name, String photo) {
        String key = auth.getCurrentUser().getUid();
        Log.e("payraj",key);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        db.child("Email").setValue(mailid);
        db.child("Username").setValue(name);
        db.child("UID").setValue(key);


        db.child("imgurl").setValue(photo);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    secondActivity();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                nextActivity();
            }
        });
    }
    @Override
    public void onStart() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e("USERUID----",auth.getCurrentUser().getUid());
                    if(user.isEmailVerified()) {
                        Log.e("Verify dialog true",user.isEmailVerified()+"" );
                        secondActivity();
                        finish();
                    }
                }
            }
        };
        auth.addAuthStateListener(authStateListener);
        super.onStart();
    }
    @Override
    public void onResume() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(user.isEmailVerified()) {

                        finish();

                    }
                    else {
                        Dialog mdialog = new Dialog(getApplicationContext());
                        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    }
                }
            }
        };
        auth.addAuthStateListener(authStateListener);
        super.onResume();
    }
    @Override
    public void onStop() {
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }
}

