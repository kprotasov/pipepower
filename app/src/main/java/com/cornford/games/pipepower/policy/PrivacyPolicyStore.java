package com.cornford.games.pipepower.policy;

import android.content.Context;
import android.content.SharedPreferences;

import com.cornford.games.pipepower.data.SharedPreferencesUtils;

/**
 * User: kprotasov
 * Date: 15.03.2017
 * Time: 14:53
 */
public final class PrivacyPolicyStore {

	private static final String PRIVACY_POLICY_FILE = "com.cornford.games.pipepower.PRIVACY_POLICY_FILE";
	private static final String RECORD_IS_ACCEPTED = "com.cornford.games.pipepower.RECORD_IS_ACCEPTED";

	private PrivacyPolicyStore() {
		throw new UnsupportedOperationException();
	}

	public static boolean isAccepted(final Context context) {
		return getPreferences(context).getBoolean(RECORD_IS_ACCEPTED, false);
	}

	public static void setIsAccepted(final Context context, final boolean isAccepted) {
		final SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.putBoolean(RECORD_IS_ACCEPTED, isAccepted);
		SharedPreferencesUtils.commit(editor);
	}

	private static SharedPreferences getPreferences(final Context context) {
		return context.getApplicationContext().getSharedPreferences(PRIVACY_POLICY_FILE, Context.MODE_PRIVATE);
	}

}
