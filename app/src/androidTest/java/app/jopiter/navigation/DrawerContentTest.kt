/*
* Jopiter App
* Copyright (C) 2022 Leonardo Colman Lopes
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package app.jopiter.navigation

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class DrawerContentTest {

  @get:Rule
  val composeRule = createComposeRule()

  lateinit var pageState: MutableState<Page>
  lateinit var ctx: Context

  @Before
  fun setContent() {
    pageState = mutableStateOf(Page.Home)
    composeRule.setContent {
      DrawerContent(pageState.value) { pageState.value = it }
      ctx = LocalContext.current
    }
  }

  @Test
  fun drawer_title_should_be_app_name() {
    composeRule.drawerTitle().assertTextEquals("Jopiter App")
  }

  @Test
  fun drawer_should_contain_all_pages() {
    Page.values().forEach {
      composeRule.onNodeWithText(it.title(ctx)).assertIsDisplayed()
    }
  }

  @Test
  fun default_selected_page_should_be_home() {
    composeRule.selectedItem().assertTextEquals(Page.Home.title(ctx))
  }

  @Test
  fun touching_any_page_should_set_page_to_it() {
    val randomPage = Page.values().random()

    composeRule.onNodeWithText(randomPage.title(ctx)).performClick()
    assertTrue(pageState.value == randomPage)
  }


  private fun ComposeTestRule.drawerTitle() = onNodeWithTag("drawer_title")

  private fun ComposeTestRule.selectedItem() = onNodeWithTag("selected_item", true)

}
