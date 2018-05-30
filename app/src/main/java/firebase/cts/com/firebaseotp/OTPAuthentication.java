package firebase.cts.com.firebaseotp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by NagarajuBhusani on 30/05/18.
 */

public class OTPAuthentication extends AppCompatActivity{

    private EditText mOtp = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp);

        mOtp = findViewById(R.id.otp);
    }

    public void verifyOTP(View view){

    }
}
