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
package app.jopiter.restaurant.external

import app.jopiter.restaurant.model.Campus
import app.jopiter.restaurant.model.DessertItem
import app.jopiter.restaurant.model.ProteinItem
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.model.RestaurantMenu
import app.jopiter.restaurant.model.VegetarianItem
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.mockserver.MockServerListener
import io.kotest.inspectors.forAtLeast
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import org.mockserver.client.MockServerClient
import org.mockserver.mock.OpenAPIExpectation.openAPIExpectation
import org.mockserver.model.Delay.seconds
import org.mockserver.model.HttpError.error
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.verify.VerificationTimes.exactly
import java.time.LocalDate.now

class JopiterRestaurantClientTest : FunSpec({

  val mockServerListener = listener(MockServerListener())
  val mockServer by lazy { mockServerListener.mockServer!! }

  val target by lazy { JopiterRestaurantClient("http://localhost:${mockServer.port}") }

  context("Restaurants") {
    test("Returns failure when connection is dropped") {
      mockServer.prepareConnectionLost()

      target.fetchRestaurants().shouldBeFailure()
    }

    test("Returns failure when request takes more than 3s") {
      mockServer.prepareTimeout()

      target.fetchRestaurants().shouldBeFailure()
    }

    test("Returns list of restaurants when server is healthy") {
      mockServer.prepareWithJopiterOpenAPI()

      target.fetchRestaurants() shouldBeSuccess listOf(
        Campus(
          "Cidade Universitária", listOf(
            Restaurant(9, "Químicas")
          )
        )
      )
    }

    xtest("Caches restaurants results") {
      mockServer.prepareWithJopiterOpenAPI()

      repeat(5) {
        target.fetchRestaurants().shouldBeSuccess()
      }

      mockServer.verify(request("/api/v1/restaurants"), exactly(1))
    }

    test("Can parse current production response") {
      val client = JopiterRestaurantClient("https://persephone.jopiter.app")
      client.fetchRestaurants().shouldBeSuccess()
    }
  }

  context("Restaurant items") {
    test("Returns failure on connection lost") {
      mockServer.prepareConnectionLost()
      target.fetchItems(1).shouldBeFailure()
    }

    test("Returns failure on timeout") {
      mockServer.prepareTimeout()
      target.fetchItems(1).shouldBeFailure()
    }

    test("Returns list of items when server is healthy") {
      mockServer.prepareSuccessItemsResponse()

      target.fetchItems(1).shouldBeSuccess()
      target.fetchItems(1) shouldBeSuccess listOf(
        RestaurantMenu(
          "string",
          0,
          now(),
          RestaurantMenu.Period.Lunch,
          0,
          ProteinItem("string", ProteinItem.FoodGroup.Ave, ProteinItem.Preparation.AoMolhoGorduroso),
          VegetarianItem("string", VegetarianItem.FoodGroup.CerealMilho, VegetarianItem.Preparation.AoMolhoGorduroso),
          DessertItem("string", DessertItem.FoodGroup.Doce, DessertItem.Preparation.UltraProcessado),
          listOf("string")
        )
      )
    }

    test("Parses any output from production environment") {
      val client = JopiterRestaurantClient("https://persephone.jopiter.app")
      (1..30).toList().forAtLeast(10) {
        client.fetchItems(it).shouldBeSuccess()
      }
    }
  }
})

private fun MockServerClient.prepareConnectionLost() =
  `when`(request()).error(error().withDropConnection(true))

private fun MockServerClient.prepareTimeout() =
  `when`(request()).respond(response().withDelay(seconds(4)))

private fun MockServerClient.prepareWithJopiterOpenAPI() =
  upsert(openAPIExpectation("https://persephone.jopiter.app/api/v1/docs.yaml"))

// FIXME couldn't manage to use OpenAPI specification for this with mockserver
private fun MockServerClient.prepareSuccessItemsResponse() =
  `when`(
    request("/api/v1/restaurants/items")
      .withQueryStringParameter("restaurantId", "1")
      .withQueryStringParameter("date", "${now()}")
  ).respond(
    response().withBody(
      """
    [
      {
        "restaurantId": 0,
        "date": "${now()}",
        "period": "Lunch",
        "calories": 0,
        "mainItem": {
          "item": "string",
          "foodGroup": "Ave",
          "preparation": "AoMolhoGorduroso"
        },
        "vegetarianItem": {
          "item": "string",
          "foodGroup": "CerealMilho",
          "preparation": "AoMolhoGorduroso"
        },
        "dessertItem": {
          "item": "string",
          "foodGroup": "Doce",
          "preparation": "UltraProcessado"
        },
        "mundaneItems": [
          "string"
        ],
        "unparsedMenu": "string",
        "restaurantName": "string"
      }
    ]
  """.trimIndent()
    )
  )
