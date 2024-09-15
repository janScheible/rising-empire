package com.scheible.risingempire.webapp.adapter.frontend;

import java.io.IOException;
import java.util.function.Function;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author sj
 */
@JsonComponent
public class FrontendJacksonComponents {

	private static <T> T createIdFromArray(JsonNode node, Function<String, T> creator) {
		if (node.isArray()) {
			for (JsonNode idText : node) {
				if (idText.isTextual()) {
					return creator.apply(idText.asText());
				}
			}
		}

		throw new IllegalStateException("Can't convert '" + node + "' to a SystemId!");
	}

	public static class SystemIdDeserializer extends JsonDeserializer<SystemId> {

		@Override
		public SystemId deserialize(JsonParser jsonParser, DeserializationContext ctxt)
				throws IOException, JacksonException {
			return createIdFromArray(jsonParser.getCodec().readTree(jsonParser), SystemId::new);
		}

	}

	public static class ColonyIdDeserializer extends JsonDeserializer<ColonyId> {

		@Override
		public ColonyId deserialize(JsonParser jsonParser, DeserializationContext ctxt)
				throws IOException, JacksonException {
			return createIdFromArray(jsonParser.getCodec().readTree(jsonParser), ColonyId::new);
		}

	}

	public static class FleetIdDeserializer extends JsonDeserializer<FleetId> {

		@Override
		public FleetId deserialize(JsonParser jsonParser, DeserializationContext ctxt)
				throws IOException, JacksonException {
			return createIdFromArray(jsonParser.getCodec().readTree(jsonParser), FleetId::new);
		}

	}

	public static class ShipTypeIdDeserializer extends JsonDeserializer<ShipTypeId> {

		@Override
		public ShipTypeId deserialize(JsonParser jsonParser, DeserializationContext ctxt)
				throws IOException, JacksonException {
			return createIdFromArray(jsonParser.getCodec().readTree(jsonParser), ShipTypeId::new);
		}

	}

	public static class TechIdDeserializer extends JsonDeserializer<TechId> {

		@Override
		public TechId deserialize(JsonParser jsonParser, DeserializationContext ctxt)
				throws IOException, JacksonException {
			return createIdFromArray(jsonParser.getCodec().readTree(jsonParser), TechId::new);
		}

	}

}
