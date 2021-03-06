/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.qualityprofile.ws;

import java.util.List;
import org.sonar.api.server.ws.Response;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.server.qualityprofile.BulkChangeResult;

class BulkChangeWsResponse {

  private BulkChangeWsResponse() {
    // use static methods
  }

  static void writeResponse(BulkChangeResult result, Response response) {
    JsonWriter json = response.newJsonWriter().beginObject();
    json.prop("succeeded", result.countSucceeded());
    json.prop("failed", result.countFailed());
    writeErrors(json, result.getErrors());
    json.endObject().close();
  }

  private static void writeErrors(JsonWriter json, List<String> errorMessages) {
    if (errorMessages.isEmpty()) {
      return;
    }
    json.name("errors").beginArray();
    errorMessages.forEach(message -> {
      json.beginObject();
      json.prop("msg", message);
      json.endObject();
    });
    json.endArray();
  }
}
