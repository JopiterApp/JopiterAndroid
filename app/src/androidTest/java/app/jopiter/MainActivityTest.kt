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
package app.jopiter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun opens_drawer_on_swipe_right() {
    composeTestRule.onRoot().performTouchInput { swipeRight() }
    composeTestRule.assertDrawerIsOpen()
  }

  @Test
  fun closes_drawer_on_swipe_left() {
    composeTestRule.onRoot().performTouchInput { swipeRight() }
    composeTestRule.assertDrawerIsOpen()
    composeTestRule.onRoot().performTouchInput { swipeLeft() }
    composeTestRule.assertDrawerIsClosed()
  }

  @Test
  fun opens_drawer_on_hamburger_touch() {
    composeTestRule.onNodeWithContentDescription("open menu").performClick()
    composeTestRule.assertDrawerIsOpen()
  }

  @Test
  fun contains_app_name_on_top_bar() {
    composeTestRule.topAppBarTitle().assertTextEquals("Jopiter")
  }

  private fun ComposeTestRule.topAppBarTitle() = onNodeWithTag("top_app_bar_title")


  private fun ComposeTestRule.assertDrawerIsOpen() = drawerTitle().assertIsDisplayed()

  private fun ComposeTestRule.assertDrawerIsClosed() = drawerTitle().assertIsNotDisplayed()

  private fun ComposeTestRule.drawerTitle() = onNodeWithTag("drawer_title")
}