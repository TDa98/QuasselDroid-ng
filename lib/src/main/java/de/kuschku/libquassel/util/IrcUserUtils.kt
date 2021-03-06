/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.util

import java.util.*

object IrcUserUtils {
  fun senderColor(nick: String): Int {
    return 0xf and CRCUtils.qChecksum(
      nick.trimEnd('_').toLowerCase(Locale.US).toByteArray(Charsets.ISO_8859_1)
    )
  }

  fun nick(hostmask: String): String {
    return hostmask.substring(
      0,
      hostmask.lastIndex('!', hostmask.lastIndex('@')) ?: hostmask.length
    )
  }

  fun user(hostmask: String): String {
    return hostmask.substring(
      (hostmask.lastIndex('!', hostmask.lastIndex('@')) ?: -1) + 1,
      hostmask.lastIndex('@') ?: hostmask.length
    )
  }

  fun host(hostmask: String): String {
    return hostmask.substring(
      (hostmask.lastIndex('@') ?: -1) + 1
    )
  }

  fun mask(hostmask: String): String {
    return hostmask.substring(
      (hostmask.lastIndex('!', hostmask.lastIndex('@')) ?: -1) + 1
    )
  }

  private fun String.firstIndex(char: Char,
                                startIndex: Int? = null,
                                ignoreCase: Boolean = false): Int? {
    val lastIndex = indexOf(char, startIndex ?: 0, ignoreCase)
    return if (lastIndex < 0)
      null
    else
      lastIndex
  }

  private fun String.lastIndex(char: Char,
                               startIndex: Int? = null,
                               ignoreCase: Boolean = false): Int? =
    lastIndexOf(char, startIndex ?: lastIndex, ignoreCase).let { lastIndex ->
      if (lastIndex < 0)
        null
      else
        lastIndex
    }
}
