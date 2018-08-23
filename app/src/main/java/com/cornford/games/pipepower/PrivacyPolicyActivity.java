package com.cornford.games.pipepower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by kprotasov on 14.03.2017.
 */

public class PrivacyPolicyActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        startActivity(intent);
    }

}
