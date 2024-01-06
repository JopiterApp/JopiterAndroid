package app.jopiter.navigation

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import app.jopiter.R.string.app_name
import app.jopiter.R.string.open_menu_content_description

@Composable
fun JopiterTopBar(onNavigationClick: () -> Unit) {
    val title = stringResource(app_name)
    val iconDescription = stringResource(open_menu_content_description)

    TopAppBar(
        title = { Text(title, Modifier.testTag("top_app_bar_title")) },
        navigationIcon = { Icon(Icons.Default.Menu, iconDescription, Modifier.clickable { onNavigationClick() }) },
        modifier = Modifier.testTag("top_app_bar")
    )
}