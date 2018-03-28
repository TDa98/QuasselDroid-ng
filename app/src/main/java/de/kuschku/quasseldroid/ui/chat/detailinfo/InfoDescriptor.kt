package de.kuschku.quasseldroid.ui.chat.detailinfo

import java.io.Serializable

data class InfoDescriptor(
  val type: InfoType,
  val nick: String? = null,
  val channel: String? = null,
  val network: Int
) : Serializable