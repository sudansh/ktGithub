package com.sudansh.github.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sudansh.github.vo.User

/**
 * Interface for database access for User related operations.
 */
@Dao
interface UserDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(user: User)

	@Query("SELECT * FROM user WHERE login = :login")
	fun findByLogin(login: String): LiveData<User>
}
