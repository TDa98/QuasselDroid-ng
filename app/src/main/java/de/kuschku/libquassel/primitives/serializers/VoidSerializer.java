package de.kuschku.libquassel.primitives.serializers;


import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class VoidSerializer implements PrimitiveSerializer<Void> {
    @Override
    public void serialize(final ByteChannel channel, final Void data) throws IOException {

    }

    @Nullable
    @Override
    public Void deserialize(final ByteBuffer buffer) throws IOException {
        return null;
    }
}
