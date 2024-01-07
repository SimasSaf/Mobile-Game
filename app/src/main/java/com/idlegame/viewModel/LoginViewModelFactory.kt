import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.idlegame.model.LoginModel
import com.idlegame.viewModel.LoginViewModel

class LoginViewModelFactory(private val loginModel: LoginModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
