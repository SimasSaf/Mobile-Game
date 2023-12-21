import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.idlegame.model.GoogleSignInModel
import com.idlegame.viewModel.LoginViewModel

class LoginViewModelFactory(private val googleSignInModel: GoogleSignInModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(googleSignInModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
