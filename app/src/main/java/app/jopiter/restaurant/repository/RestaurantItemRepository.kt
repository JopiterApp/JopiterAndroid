package app.jopiter.restaurant.repository

import app.jopiter.restaurant.external.JopiterRestaurantClient
import app.jopiter.restaurant.model.RestaurantMenu

class RestaurantItemRepository(private val jopiterClient: JopiterRestaurantClient) {

  fun getRestaurantItems(restaurantId: Int): List<RestaurantMenu> {
    return jopiterClient.fetchItems(restaurantId).getOrNull().orEmpty()
  }
}
