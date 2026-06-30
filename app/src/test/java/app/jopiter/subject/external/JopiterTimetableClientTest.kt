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

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.mockserver.MockServerListener
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.mockserver.client.MockServerClient
import org.mockserver.matchers.MatchType.ONLY_MATCHING_FIELDS
import org.mockserver.model.HttpError.error
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.JsonBody.json
import org.mockserver.verify.VerificationTimes.exactly
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.WEDNESDAY

private val SAMPLE_TIMETABLE = """
  {
    "MONDAY": [
      {
        "subject": { "code": "MAC0110", "classCode": "2026100", "startTime": "08:00:00", "endTime": "09:40:00" },
        "information": { "name": "Introdução à Computação" }
      }
    ],
    "WEDNESDAY": [
      {
        "subject": { "code": "MAC0110", "classCode": "2026100", "startTime": "10:00:00", "endTime": "11:40:00" },
        "information": { "name": "Introdução à Computação" }
      }
    ]
  }
""".trimIndent()

class JopiterTimetableClientTest : FunSpec({

  val mockServerListener = listener(MockServerListener())
  val mockServer by lazy { mockServerListener.mockServer!! }
  val target by lazy { JopiterTimetableClient("http://localhost:${mockServer.port}/timetable") }

  test("returns failure when the connection is dropped") {
    mockServer.`when`(request()).error(error().withDropConnection(true))

    target.fetchTimetable("12345678", "secret").shouldBeFailure()
  }

  test("parses the timetable returned by the backend") {
    mockServer.respondWith(SAMPLE_TIMETABLE)

    val result = target.fetchTimetable("12345678", "secret")

    result.shouldBeSuccess { timetable ->
      timetable.keys shouldBe setOf(MONDAY, WEDNESDAY)
      timetable.getValue(MONDAY).single().subject.code shouldBe "MAC0110"
      timetable.getValue(MONDAY).single().information.name shouldBe "Introdução à Computação"
      timetable.getValue(WEDNESDAY).single().subject.startTime shouldBe "10:00:00"
    }
  }

  test("sends the credentials only as the POST body of the timetable request") {
    mockServer.respondWith(SAMPLE_TIMETABLE)

    target.fetchTimetable("12345678", "secret").shouldBeSuccess()

    mockServer.verify(
      request()
        .withMethod("POST")
        .withPath("/timetable")
        .withBody(json("""{ "user": "12345678", "pass": "secret" }""", ONLY_MATCHING_FIELDS)),
      exactly(1)
    )
  }
})

private fun MockServerClient.respondWith(body: String) =
  `when`(request().withMethod("POST")).respond(response().withBody(json(body)))
