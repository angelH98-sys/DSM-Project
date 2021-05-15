package sv.edu.udb.dsm_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    private Button btnSignInSend, btnSignInGoogle, btnSignInSignUp;
    private EditText etSignInMail, etSignInPassword;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


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
        db = FirebaseFirestore.getInstance();

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
//                Intent content = new Intent(SignIn.this,Contenido.class);
//                startActivity((content));
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
                            ticketExist(task.getResult().getUser().getUid().toString());
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
                            ticketExist(task.getResult().getUser().getUid().toString());
                            startActivity(new Intent(SignIn.this, Contenido.class));
                        }else{
                             Toast.makeText(SignIn.this, "No existe un usuario registrado con esas credenciales", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void ticketExist(String uid){

        //instancia a autenticacion de Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String loginUser= mAuth.getCurrentUser().getUid();
        Log.i("info","ha pasado por aca: "+loginUser);
        db.collection("tickets")
                .whereEqualTo("id_usuario",loginUser)
                .whereEqualTo("estado","Borrador")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                return;
                            }
                            Log.i("info","ha pasado por aca, esta vacio: "+String.valueOf(task.getResult().size()));

                            Map<String, Object> data = new HashMap<>();
                            data.put("estado", "Borrador");
                            data.put("idusuario", uid);
                            data.put("fechapago",null);
                            data.put("preciototal",0.0);
                            data.put("productos", new ArrayList<>());

                            db.collection("tickets")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(),"Se ha creado el ticket, Puede Seguir comprando",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Hubo un pequeño problema",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

}