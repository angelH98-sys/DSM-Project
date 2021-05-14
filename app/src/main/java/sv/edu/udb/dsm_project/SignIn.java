package sv.edu.udb.dsm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn extends AppCompatActivity {

    private Button btnSignInSend, btnSignInGoogle, btnSignInSignUp;
    private EditText etSignInMail, etSignInPassword;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeUI();
    }

    public void initializeUI(){

        btnSignInSend = findViewById(R.id.btnSignInSend);
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        btnSignInSignUp = findViewById(R.id.btnSignInSignUp);
        etSignInMail = findViewById(R.id.etSignInMail);
        etSignInPassword = findViewById(R.id.etSignInPassword);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        btnSignInSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this , SignUp.class));
            }
        });
        btnSignInSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loginUserPassword(); }
        });

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(SignIn.this, gso);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(SignIn.this, "Algo no anda bien, intentalo más tarde", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(SignIn.this, Contenido.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignIn.this, "Algo no anda bien, intentalo más tarde", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginUserPassword(){
        String mail, password;

        mail = etSignInMail.getText().toString();
        password = etSignInPassword.getText().toString();

        if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Debes llenar el formulario para logearte", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(SignIn.this, Contenido.class));
                        }else{
                             Toast.makeText(SignIn.this, "No existe un usuario registrado con esas credenciales", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}