package com.example.proximityfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthActivity";
    Button bSiguiente;
    private Button bNum;
    private EditText tNum;
    private Button bCod;
    private EditText tCod;
    private FirebaseAuth fireAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bNum = (Button) findViewById(R.id.botonEnviarNumero);
        tNum = (EditText) findViewById(R.id.editTextNumber);
        bCod = (Button) findViewById(R.id.botonEnviarCodigo);
        tCod = (EditText) findViewById(R.id.editTextCode);
        // Start phoneAuth
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.e(TAG, "onVerificationCompleted:" + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.e(TAG, "onCodeSent: success");
                super.onCodeSent(s, forceResendingToken);
            }
        };
        // End phoneAuth
        bNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: Start process");
                verificarNumero(tNum.getText().toString());
                Log.e(TAG, "onClick: End process");
            }
        });
    }

    // Autenticación con nº de telefono
    public void verificarNumero(String numero) {
        fireAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fireAuth)
                .setPhoneNumber(numero)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //Boton siguiente
    public void Siguiente(View view) {
        Intent siguiente = new Intent(this, MapsActivity.class);
        startActivity(siguiente);
    }
}