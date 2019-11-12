package br.com.brolam.oha.supervisory.cloudV1;

import android.app.Activity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class OhaAuth {
    private FirebaseAuth firebaseAuth;

    public OhaAuth(){
        this.firebaseAuth = FirebaseAuth.getInstance();

    }

    public void doSignIn(Activity activity, int requestCode){
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                requestCode);
    }

    public void doSingOut(){
        firebaseAuth.signOut();
    }

    public FirebaseUser getUser(){
        return firebaseAuth.getCurrentUser();
    }


}
