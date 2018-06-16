package app.rss;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Custom JSON serializer for RSS channel. By default, Jackson serializes with the items under an "items" tag, when
 * a flat group of items should be added under the channel's root tag.
 */
class RssChannelSerializer extends StdSerializer<RssChannel> {
    RssChannelSerializer() {
        this(null);
    }

    private RssChannelSerializer(Class<RssChannel> t) {
        super(t);
    }

    @Override
    public void serialize(RssChannel channel, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("title", channel.title);
        gen.writeStringField("link", channel.link);
        gen.writeStringField("description", channel.description);

        //according to RSS Spec 2, items should not be under an "items" tag
        for (RssItem item : channel.items) {
            gen.writeObjectField("item", item);
        }
        gen.writeEndObject();
    }
}
