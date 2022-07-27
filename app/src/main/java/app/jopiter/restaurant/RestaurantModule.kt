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
package app.jopiter.restaurant

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import app.jopiter.restaurant.external.JopiterRestaurantClient
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import org.koin.dsl.module

private const val RemoteAddress = "https://v2.backend.jopiter.app"

private val Context.preferredRestaurantDatastore by preferencesDataStore("preferred_restaurant")

val restaurantModule = module {

  single { JopiterRestaurantClient(RemoteAddress) }

  single { PreferredRestaurantRepository(get<Context>().preferredRestaurantDatastore) }

}
