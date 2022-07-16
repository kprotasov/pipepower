package com.cornford.games.pipepower.policy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cornford.games.pipepower.MainActivity;
import com.cornford.games.pipepower.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by kprotasov on 14.03.2017.
 */

public class PrivacyPolicyActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (PrivacyPolicyStore.isAccepted(this)) {
            showMainActivity();
            finish();
        } else {
            setContentView(R.layout.privacy_policy_layout);

            final Button cancelButton = (Button) findViewById(R.id.close_privacy_policy_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PrivacyPolicyStore.setIsAccepted(PrivacyPolicyActivity.this, false);
                    finish();
                }
            });
            final Button acceptButton = (Button) findViewById(R.id.accept_privacy_policy_button);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PrivacyPolicyStore.setIsAccepted(PrivacyPolicyActivity.this, true);
                    showMainActivity();
                    finish();
                }
            });
        }
    }

    private void showMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        //final Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

}
