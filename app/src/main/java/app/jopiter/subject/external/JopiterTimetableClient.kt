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
package app.jopiter.subject.external

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.DayOfWeek
import java.time.Duration

private const val DEFAULT_TIMETABLE_URL = "https://timetable-fetcher-dot-jopiter-225101.appspot.com/timetable"
private const val TIMEOUT_SECONDS = 200L
private val JSON_MEDIA_TYPE = "application/json".toMediaType()
private val TIMETABLE_TYPE = object : TypeReference<Map<DayOfWeek, List<TimetableEntry>>>() {}

/**
 * Fetches the student's weekly timetable from JupiterWeb through the timetable-fetcher backend.
 *
 * The backend logs into JupiterWeb on the student's behalf and scrapes their enrolled classes, which
 * can take well over a minute — hence the long [TIMEOUT_SECONDS] timeout (OkHttp's 10s default would
 * never suffice). This is an experimental, best-effort integration: USP can change JupiterWeb at any
 * time and break it.
 *
 * Security: [uspNumber] and [password] are sent only as the JSON body of the HTTPS request to
 * [timetableUrl]; they are never persisted nor logged (failures are surfaced as opaque errors).
 */
open class JopiterTimetableClient(
  private val timetableUrl: String = DEFAULT_TIMETABLE_URL,
  private val objectMapper: ObjectMapper = lenientMapper(),
  private val httpClient: OkHttpClient = longTimeoutClient()
) {

  open fun fetchTimetable(uspNumber: String, password: String): Result<Map<DayOfWeek, List<TimetableEntry>>> =
    runCatching {
      val body = objectMapper.writeValueAsString(mapOf("user" to uspNumber, "pass" to password))
      val request = Request.Builder()
        .url(timetableUrl)
        .post(body.toRequestBody(JSON_MEDIA_TYPE))
        .build()

      httpClient.newCall(request).execute().use { response ->
        check(response.isSuccessful) { "Timetable request failed with status ${response.code}" }
        objectMapper.readValue(response.body.string(), TIMETABLE_TYPE)
      }
    }
}

private fun lenientMapper(): ObjectMapper =
  jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

private fun longTimeoutClient(): OkHttpClient {
  val timeout = Duration.ofSeconds(TIMEOUT_SECONDS)
  return OkHttpClient.Builder()
    .connectTimeout(timeout)
    .readTimeout(timeout)
    .writeTimeout(timeout)
    .callTimeout(timeout)
    .build()
}
