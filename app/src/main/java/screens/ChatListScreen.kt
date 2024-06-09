package screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chatapp.ui.theme.LCViewModel

@Composable
fun ChatListScreen(navController: NavController, vm : LCViewModel) {
    BottomNavigationMenu(selectedItem = BottomNavigationItem.CHATLIST, navController = navController)
}
