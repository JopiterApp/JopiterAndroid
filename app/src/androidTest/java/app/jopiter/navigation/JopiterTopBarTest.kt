/*
* Jopiter App
* Copyright (C) 2026 Leonardo Colman Lopes
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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class JopiterTopBarTest {

  @get:Rule
  val composeRule = createComposeRule()

  private var clickCount = 0

  @Before
  fun setContent() {
    composeRule.setContent {
      JopiterTopBar { clickCount++ }
    }
  }


  @Test
  fun should_include_app_name_in_top_bar() {
    composeRule.barTitle().assertIsDisplayed().assertTextEquals("Jopiter")
  }

  @Test
  fun should_run_on_click_when_clicking_navigation_button() {
    composeRule.barIcon().performClick()
    assertEquals(1, clickCount)
    composeRule.barIcon().performClick()
    assertEquals(2, clickCount)
  }

  private fun ComposeTestRule.barTitle() = onNodeWithTag("top_app_bar_title")
  private fun ComposeTestRule.barIcon() = onNodeWithTag("top_app_bar_icon")
}
