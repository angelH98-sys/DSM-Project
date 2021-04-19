package sv.edu.udb.dsm_project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.StringValue;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import static java.util.Map.entry;

public class SignUp extends AppCompatActivity {

    private EditText etName, etAddress, etDui, etPhone, etMail, etPassword, etConfirmationPassword;
    private TextView tvName, tvAddress, tvDui, tvPhone, tvMail, tvPassword, tvConfirmationPassword;
    private CheckBox cbPasswordLength, cbPasswordUpper, cbPasswordLower, cbPasswordSymbols, cbPasswordNumbers;
    private Button send;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeUI();
    }

    private void createAuth(){

        String mail, password;
        mail = etMail.getText().toString();
        password = etPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        uid = authResult.getUser().getUid();
                        createUser();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Ocurrio un problema al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void createUser(){

        db.collection("usuarios").add(Map.ofEntries(
                entry("nombre", etName.getText().toString()),
                entry("telefono", etPhone.getText().toString()),
                entry("dui", etDui.getText().toString()),
                entry("direccion", etAddress.getText().toString()),
                entry("correo", etMail.getText().toString()),
                entry("uid", uid),
                entry("estado", "Habilitado"),
                entry("tipo", "Cliente")
        )).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Algo salió mal en el registro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ResourceType")
    private boolean validateForm(){

        if(TextUtils.isEmpty(etName.getText().toString())){

            Toast.makeText(getApplicationContext(), "Ingresar nombre", Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(etAddress.getText().toString())){

            Toast.makeText(getApplicationContext(), "Ingresar dirección", Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(etDui.getText().toString())){

            Toast.makeText(getApplicationContext(), "Ingresar DUI", Toast.LENGTH_LONG).show();
            return false;
        }else if(!etDui.getText().toString().matches("^[0-9]{8}-[0-9]{1}$")){

            Toast.makeText(getApplicationContext(), "Formáto de DUI erróneo", Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(etPhone.getText().toString())){

            Toast.makeText(getApplicationContext(), "Ingresar teléfono", Toast.LENGTH_LONG).show();
            return false;
        }else if(!etPhone.getText().toString().matches("^(7|6)[0-9]{3}-[0-9]{4}$")){

            Toast.makeText(getApplicationContext(), "Formáto de teléfono erróneo", Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(etMail.getText().toString())){

            Toast.makeText(getApplicationContext(), "Ingresar correo", Toast.LENGTH_LONG).show();
            return false;
        }else if(!etMail.getText().toString().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){

            Toast.makeText(getApplicationContext(), "Formáto de correo erróneo", Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(etPassword.getText().toString())){

            Toast.makeText(getApplicationContext(), "Ingresar contraseña", Toast.LENGTH_LONG).show();
            return false;
        }else if(!etPassword.getText().toString().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?])[A-Za-z\\d@$!%*?&]{8,}$")){

            Toast.makeText(getApplicationContext(), "Formáto de contraseña erróneo", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!etPassword.getText().toString().equals(etConfirmationPassword.getText().toString())){

            Toast.makeText(getApplicationContext(), "Contraseñas no coinciden", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void initializeUI(){

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.etSignUpName);
        etDui = findViewById(R.id.etSignUpDUI);
        etPhone = findViewById(R.id.etSignUpPhone);
        etAddress = findViewById(R.id.etSignUpAddress);
        etMail = findViewById(R.id.etSignUpMail);
        etPassword = findViewById(R.id.etSignUpPassword);
        etConfirmationPassword = findViewById(R.id.etSignUpConfirmationPassword);
        tvName = findViewById(R.id.tvSignupName);
        tvDui = findViewById(R.id.tvSignUpDUI);
        tvPhone = findViewById(R.id.tvSignUpPhone);
        tvAddress = findViewById(R.id.tvSignUpAddress);
        tvMail = findViewById(R.id.tvSignUpMail);
        tvPassword = findViewById(R.id.tvSignUpPassword);
        tvConfirmationPassword = findViewById(R.id.tvSignUpConfirmationPassword);
        progressBar = findViewById(R.id.signUpProgressBar);
        cbPasswordLength = findViewById(R.id.cbSignUpPasswordLength);
        cbPasswordUpper = findViewById(R.id.cbSignUpPasswordUpper);
        cbPasswordLower = findViewById(R.id.cbSignUpPasswordLower);
        cbPasswordSymbols = findViewById(R.id.cbSignUpPasswordSymbols);
        cbPasswordNumbers = findViewById(R.id.cbSignUpPasswordNumbers);
        send = findViewById(R.id.btnSignUpSend);

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v){
                send.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                if(validateForm()){
                    createAuth();
                }else{
                    send.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            int beforeLength;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeLength = s.toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int after = s.toString().length();
                String phone;
                if(beforeLength < after && after == 4){
                    phone = s.toString() + "-";
                    etPhone.setText(phone);
                    etPhone.setSelection(5);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDui.addTextChangedListener(new TextWatcher() {
            int beforeLength;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeLength = s.toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int after = s.toString().length();
                String dui;
                if(beforeLength < after && after == 8){
                    dui = s.toString() + "-";
                    etDui.setText(dui);
                    etDui.setSelection(9);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pass = s.toString();
                if(pass.matches("[\\s\\S]{1,}[A-Z]{1,}[\\s\\S]{1,}|[A-Z]{1,}[\\s\\S]{1,}|[\\s\\S]{1,}[A-Z]{1,}|[A-Z]{1,}")){

                    cbPasswordUpper.setChecked(true);
                }else{

                    cbPasswordUpper.setChecked(false);
                }

                if(pass.matches("[\\s\\S]{1,}[a-z]{1,}[\\s\\S]{1,}|[a-z]{1,}[\\s\\S]{1,}|[\\s\\S]{1,}[a-z]{1,}|[a-z]{1,}")){

                    cbPasswordLower.setChecked(true);
                }else{

                    cbPasswordLower.setChecked(false);
                }

                if(pass.matches("[\\s\\S]{1,}[0-9]{1,}[\\s\\S]{1,}|[0-9]{1,}[\\s\\S]{1,}|[\\s\\S]{1,}[0-9]{1,}|[0-9]{1,}")){

                    cbPasswordNumbers.setChecked(true);
                }else{

                    cbPasswordNumbers.setChecked(false);
                }

                if(pass.matches("[\\s\\S]{1,}[@$!%*?]{1,}[\\s\\S]{1,}|[@$!%*?]{1,}[\\s\\S]{1,}|[\\s\\S]{1,}[@$!%*?]{1,}|[@$!%*?]{1,}")){

                    cbPasswordSymbols.setChecked(true);
                }else{

                    cbPasswordSymbols.setChecked(false);
                }

                if(pass.length() >= 8) {

                    cbPasswordLength.setChecked(true);
                }else{

                    cbPasswordLength.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}