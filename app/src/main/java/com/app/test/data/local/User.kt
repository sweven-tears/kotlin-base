package com.app.test.data.local

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.*
import com.app.test.App
import com.app.test.base.BaseViewModel
import com.app.test.data.network.RetrofitUtils
import com.app.test.data.network.service.ApiService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 实体类
@Entity(tableName = "fun_user")
data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
)

@Entity
data class Game(
    @PrimaryKey val id:Int,
    val name:String,
    val type:String,
    val time:String
)

// DAO接口
@Dao
interface UserDao {
    @Query("SELECT * FROM fun_user")
    suspend fun getUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)
}

// 数据库
@Database(entities = [User::class, Game::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

// 数据仓库
class UserRepository(
    private val api: ApiService,
    private val db: AppDatabase,
) {
    private val userDao = db.userDao()

    suspend fun getUsers(): List<User> {
        val localUsers = userDao.getUsers()
        return if (localUsers.isEmpty()) {
            val remoteUsers = api.getUsers()
            userDao.insertUsers(remoteUsers)
            remoteUsers
        } else {
            localUsers
        }
    }
}

// ViewModel
class UserViewModel(private val repo: UserRepository) : BaseViewModel() {
    private val _users = liveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            try {
                _users.value = repo.getUsers()
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }
}

// 依赖提供
object Injection {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val db = Room.databaseBuilder(
        App.application,
        AppDatabase::class.java, "app-db"
    ).build()

    val apiService: ApiService get() = retrofit.create(ApiService::class.java)
    val userRepository: UserRepository get() = UserRepository(apiService, db)
}

class UserViewModelFactory(private val repo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return UserViewModel(repo) as T
    }
}

// Activity/Fragment中使用
class MainActivity : AppCompatActivity() {
    private val vm: UserViewModel by viewModels { UserViewModelFactory(Injection.userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.users.observe(this) { users ->
            // 更新UI
        }
        vm.loadUsers()
    }
}