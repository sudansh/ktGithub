package com.sudansh.github

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sudansh.github.ui.common.NavigationController
import com.sudansh.github.util.injectActivity

class MainActivity : AppCompatActivity() {
	private val navigationController: NavigationController by injectActivity()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
		if (savedInstanceState == null) {
			navigationController.navigateToSearch()
		}
	}
}
