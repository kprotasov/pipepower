package com.cornford.games.pipepower;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * User: kprotasov
 * Date: 15.03.2017
 * Time: 15:06
 */
public final class SharedPreferencesUtils {

	private SharedPreferencesUtils() {
		throw new UnsupportedOperationException();
	}

	public static void commit(final SharedPreferences.Editor editor) {
		if (Build.VERSION_CODES.GINGERBREAD <= Build.VERSION.SDK_INT) {
			doAPI9StyleCommit(editor);
		} else {
			editor.commit();
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private static void doAPI9StyleCommit(final SharedPreferences.Editor editor) {
		editor.apply();
	}
}