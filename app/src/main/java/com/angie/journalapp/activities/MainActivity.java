package com.angie.journalapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.angie.journalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private Button signInButton;
    private TextInputLayout emailInputLayout, passwordInputLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        emailInputLayout = findViewById(R.id.input_layout_email);
        passwordInputLayout = findViewById(R.id.input_layout_password);

        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);

        signInButton = findViewById(R.id.sign_in_btn);
        signUpButton = findViewById(R.id.btn_signup);

        signInButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleSignIn();
                }
            }
        );

        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleSignUp();
                    }
                }
        );

        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() != null ){
                    startActivity(new Intent(MainActivity.this, JournalActivity.class));
                }
            }
        };
    }

    private void handleSignUp() {

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "There was a problem Signing up", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleSignIn() {

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Could not sign you in", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onStart(){ //Listens for changes to Auth
        super.onStart();

        firebaseAuth.addAuthStateListener(fireBaseAuthListener);
    }

    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailInputLayout.setError(getString(R.string.err_msg_email));
            requestFocus(emailEditText);
            return false;
        } else {
            emailInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword() {
        if (passwordEditText.getText().toString().trim().isEmpty()) {
            passwordInputLayout.setError(getString(R.string.err_msg_password));
            requestFocus(passwordEditText);
            return false;
        } else {
            passwordInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}
