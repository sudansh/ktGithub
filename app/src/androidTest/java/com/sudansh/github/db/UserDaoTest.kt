package com.sudansh.github.db

import android.support.test.runner.AndroidJUnit4
import com.sudansh.github.util.LiveDataTestUtil.getValue
import com.sudansh.github.util.TestUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest : DbTest() {
    @Test
    fun insertAndLoad() {
        val user = TestUtil.createUser("foo")
        db.userDao().insert(user)

        val loaded = getValue(db.userDao().findByLogin(user.login))
        assertThat(loaded.login, `is`("foo"))

        val replacement = TestUtil.createUser("foo2")
        db.userDao().insert(replacement)

        val loadedReplacement = getValue(db.userDao().findByLogin(replacement.login))
        assertThat(loadedReplacement.login, `is`("foo2"))
    }
}
