package screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.chatapp.ui.theme.LCViewModel

@Composable
fun StatusScreen(navController: NavController,vm: LCViewModel) {

    BottomNavigationMenu(selectedItem = BottomNavigationItem.STATUSLIST, navController = navController)
}