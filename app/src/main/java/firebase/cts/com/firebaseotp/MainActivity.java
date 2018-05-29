package firebase.cts.com.firebaseotp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail = null;
    FirebaseDatabase _database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = _database.getReference("Users");
    private static final String TAG = "MainActivity";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String mVerificationId = null;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.email);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                  mVerificationId = verificationId;
            }
        };
    }

    public void onClick(View view){
        String email = modifyString(mEmail.getText().toString().trim());
        getCurrentUserReference(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "User Exists", Toast.LENGTH_LONG).show();
                    UserSet set = dataSnapshot.getValue(UserSet.class);
                    Log.d("Main",set.phone);

                    sendingOTP(set.phone);
                }else{
                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendingOTP(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            updateLocation(11.1111, 24.11111);
                            /////Location Screen TODO
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                        }
                    }
                });
    }

    private DatabaseReference getCurrentUserReference(String user){

        return _database.getReference("Users/" + user);
    }

    private String modifyString(String user){

        user = user.replace(".", "_dot_");
        user = user.replace("@", "_at_");

        return  user;
    }

    public void updateLocation(double latValue, double longValue){
        String email = modifyString(mEmail.getText().toString().trim());
        Map<String, Object> values = new HashMap<>();
        values.put("lat", latValue);
        values.put("lng", longValue);
        getCurrentUserReference(email).child("location").updateChildren(values);
    }

}
