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
