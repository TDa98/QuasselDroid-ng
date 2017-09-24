package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object StringListSerializer : Serializer<QStringList?> {
  override fun serialize(buffer: ChainedByteBuffer, data: QStringList?,
                         features: Quassel_Features) {
    IntSerializer.serialize(buffer, data?.size ?: 0, features)
    data?.forEach {
      StringSerializer.UTF16.serialize(buffer, it, features)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): QStringList {
    val size = IntSerializer.deserialize(buffer, features)
    val res = ArrayList<String?>(size)
    for (i in 0 until size) {
      res.add(StringSerializer.UTF16.deserialize(buffer, features))
    }
    return res
  }
}