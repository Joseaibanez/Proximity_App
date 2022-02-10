package com.example.proximityfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
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
    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthCredential token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        bNum = (Button) findViewById(R.id.botonEnviarNumero);
        tNum = (EditText) findViewById(R.id.editTextNumber);
        bCod = (Button) findViewById(R.id.botonEnviarCodigo);
        tCod = (EditText) findViewById(R.id.editTextCode);
        // Inicialización del objeto para verificar
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                Log.e(TAG, "LA VERIFICACIÓN HA FALLADO");

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                verificationID = verificationId;
                resendingToken = token;
            }
        };
        // FIN
        // Evento para enviar nº
        bNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Nº enviado");
                Log.e(TAG, tNum.getText().toString());
                verificarNumero(tNum.getText().toString());
            }
        });
        // Evento para envio de código
        bCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Nº enviado");
                Log.e(TAG, tNum.getText().toString());
                token = PhoneAuthProvider.getCredential(verificationID, String.valueOf(tCod.getText()));
                loginNumTel(token);
            }
        });
    }

    // Verificación del nº de telefono
    public void verificarNumero(String numero) {
        fireAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fireAuth)
                .setPhoneNumber("+34" + numero) // Nº a verificar
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks) // Callbacks en caso de verificarse
                .build();
        FirebaseApp.initializeApp(this);
        fireAuth.setLanguageCode("es"); // Idioma
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    //Fin

    //Boton siguiente
    public void Siguiente(View view) {
        Intent siguiente = new Intent(this, MapsActivity.class);
        startActivity(siguiente);
    }
    //Fin

    // INICIO DE SESIÓN
    private void loginNumTel(PhoneAuthCredential credential) {
        fireAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    // FIN
}