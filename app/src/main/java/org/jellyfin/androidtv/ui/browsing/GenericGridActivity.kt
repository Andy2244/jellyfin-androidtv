package org.jellyfin.androidtv.ui.browsing

import android.os.Bundle
import org.jellyfin.androidtv.ui.shared.BaseActivity

class GenericGridActivity : BaseActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		supportFragmentManager
			.beginTransaction()
			.replace(android.R.id.content, BrowseGridFragment())
			.commit()
	}
}
