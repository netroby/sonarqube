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
package org.sonar.server.platform.monitoring;

import java.util.Map;
import org.sonar.api.server.ServerSide;
import org.sonar.server.platform.ws.InfoAction;

/**
 * Any component that is involved in the information returned by the web service api/system/info
 */
@ServerSide
public interface Monitor {
  /**
   * Name of section in System Info page
   */
  String name();

  /**
   * Type of attribute values must be supported by {@link org.sonar.api.utils.text.JsonWriter#valueObject(Object)}
   * because of JSON export by {@link InfoAction}.
   */
  Map<String, Object> attributes();
}
