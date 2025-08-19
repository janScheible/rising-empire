package com.scheible.risingempire.webapp.adapter.frontend;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameFactory.Savegame;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.army.Army.Annex;
import com.scheible.risingempire.game.impl2.colonization.Colonization.AllocateResources;
import com.scheible.risingempire.game.impl2.colonization.Colonization.Colonize;
import com.scheible.risingempire.game.impl2.colonization.Colonization.SpaceDockShipClass;
import com.scheible.risingempire.game.impl2.colonization.Colonization.TransferColonists;
import com.scheible.risingempire.game.impl2.common.Command;
import com.scheible.risingempire.game.impl2.game.Game2Impl;
import com.scheible.risingempire.game.impl2.game.Savegame2Impl;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployJustLeaving;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployOrbiting;
import com.scheible.risingempire.game.impl2.navy.Navy.RelocateShips;
import com.scheible.risingempire.game.impl2.technology.Technology.AllocateResearch;
import com.scheible.risingempire.game.impl2.technology.Technology.SelectTechnology;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.GameLauncherDto;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.RunningGameDto;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.RunningGameDto.RunningGamePlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.game.GameHolder;
import com.scheible.risingempire.webapp.game.GameManager;
import com.scheible.risingempire.webapp.game.SynchronizedGameProxyFactory.GameInvocationHandler;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import com.scheible.risingempire.webapp.notification.NotificationService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author sj
 */
@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
class GameBrowserController {

	private static List<String> SPACE_WORDS = List.of("Space", "Explore", "Star", "Ship", "Fleet", "Planet", "Colony",
			"Galaxy", "Combat", "Universe", "System", "Technology", "Research", "Trade");

	private final GameHolder gameHolder;

	private final GameManager gameManager;

	private final NotificationService notificationService;

	private final Optional<GitProperties> gitProperties;

	private final Optional<BuildProperties> buildProperties;

	private final ObjectMapper savegameObjectMapper = new ObjectMapper().registerModule(new Jdk8Module())
		.registerModule(createSavegameModule());

	GameBrowserController(GameHolder gameHolder, GameManager gameManager, NotificationService notificationService,
			Optional<GitProperties> gitProperties, Optional<BuildProperties> buildProperties) {
		this.gameHolder = gameHolder;
		this.gameManager = gameManager;
		this.notificationService = notificationService;

		this.gitProperties = gitProperties;
		this.buildProperties = buildProperties;
	}

	@GetMapping("/game-browser")
	ResponseEntity<GameBrowserDto> gameBrowser() {
		String defaultGameId = ThreadLocalRandom.current()
			.ints(0, SPACE_WORDS.size())
			.distinct()
			.limit(3)
			.mapToObj(SPACE_WORDS::get)
			.collect(Collectors.joining());

		return ResponseEntity.ok(new GameBrowserDto(new EntityModel<>(
				new GameLauncherDto(defaultGameId, List.of(PlayerDto.YELLOW, PlayerDto.BLUE, PlayerDto.WHITE)))
			.with(Action.get("start", "games", "{gameId}", "{player}").with("gameId", null).with("player", null))
			.with(Action.jsonPost("load", "game-browser", "games")),
				this.gameHolder.getGameIds()
					.stream()
					.map(gameId -> new EntityModel<>(new RunningGameDto(gameId, toRunningGamePlayers(gameId),
							this.gameHolder.get(gameId).get().round()))
						.with(Action.delete("stop", "game-browser", "games", gameId))
						.with(isGame2(this.gameHolder.get(gameId).get()),
								() -> Action.get("save", "game-browser", "games", gameId)))
					.toList(),
				this.gitProperties.map(GitProperties::getShortCommitId),
				this.buildProperties.map(BuildProperties::getTime)
					.map(ts -> DateTimeFormatter.ISO_DATE_TIME.format(ts.atOffset(ZoneOffset.UTC)))));
	}

	private List<EntityModel<RunningGamePlayerDto>> toRunningGamePlayers(String gameId) {
		List<EntityModel<RunningGamePlayerDto>> result = new ArrayList<>();

		Game game = this.gameHolder.get(gameId).get();

		for (Player player : game.players()) {
			boolean canReceiveNotifications = this.notificationService.hasChannel(gameId, player);
			result.add(
					new EntityModel<>(new RunningGamePlayerDto(PlayerDto.fromPlayer(player), !game.aiControlled(player),
							this.notificationService.getPlayerSession(gameId, player), canReceiveNotifications))
						.with(!game.aiControlled(player) && !canReceiveNotifications,
								() -> Action.delete("kick", "game-browser", "games", gameId,
										player.name().toLowerCase(Locale.ROOT)))
						.with(game.aiControlled(player),
								() -> Action.get("join", "games", gameId, player.name().toLowerCase(Locale.ROOT))));
		}

		return result;
	}

	private boolean isGame2(Game game) {
		if (Proxy.isProxyClass(game.getClass())
				&& Proxy.getInvocationHandler(game) instanceof GameInvocationHandler gameInvocationHandler) {
			return gameInvocationHandler.getGame() instanceof Game2Impl;
		}
		else {
			return false;
		}
	}

	@DeleteMapping(path = "/game-browser/games/{gameId}/{player}")
	ResponseEntity<Object> kickPlayer(@PathVariable String gameId, @PathVariable Player player) {
		this.gameManager.kickPlayer(gameId, player);
		return ResponseEntity.ok(new Object());
	}

	@DeleteMapping(path = "/game-browser/games/{gameId}")
	ResponseEntity<Object> stopGame(@PathVariable String gameId) {
		this.gameManager.stopGame(gameId);
		return ResponseEntity.ok(new Object());
	}

	@GetMapping(path = "/game-browser/games/{gameId}")
	ResponseEntity<String> saveGame(@PathVariable String gameId) throws JsonProcessingException {
		Savegame savegame = this.gameManager.saveGame(gameId);
		// the JSON must be serialized manually to use `this.saveGameObjectMapper`
		return ResponseEntity.ok(this.savegameObjectMapper.writeValueAsString(savegame));
	}

	@PostMapping(path = "/game-browser/games", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<RunningGameDto> loadGame(@RequestBody SavegameBodyDto savegameBody) {
		Savegame savegame;

		try {
			// the JSON must be deserialized manually to use `this.saveGameObjectMapper`
			savegame = this.savegameObjectMapper.readValue(savegameBody.savegame(), Savegame2Impl.class);
		}
		catch (JsonProcessingException ex) {
			return ResponseEntity
				.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
						"Invalid JSON file. Only game 2 savegames can be loaded."))
				.build();
		}

		Game game = GameFactory.get().load(savegame);

		String gameId = savegameBody.gameId();
		this.gameManager.startGame(gameId, Optional.empty(), game, Optional.empty());

		return ResponseEntity.ok(new RunningGameDto(gameId, toRunningGamePlayers(gameId), game.round()));
	}

	private static SimpleModule createSavegameModule() {
		SimpleModule module = new SimpleModule();

		module.addKeySerializer(Round.class, new JsonSerializer<Round>() {
			@Override
			public void serialize(Round round, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeFieldName(Integer.toString(round.quantity()));
			}
		});
		module.addKeyDeserializer(Round.class, new KeyDeserializer() {
			@Override
			public Round deserializeKey(String key, DeserializationContext ctxt) throws IOException {
				return new Round(Integer.parseInt(key));
			}
		});

		module.addKeySerializer(ShipClassId.class, new JsonSerializer<ShipClassId>() {
			@Override
			public void serialize(ShipClassId value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {
				gen.writeFieldName(value.value());
			}
		});
		module.addKeyDeserializer(ShipClassId.class, new KeyDeserializer() {
			@Override
			public ShipClassId deserializeKey(String key, DeserializationContext ctxt) throws IOException {
				return new ShipClassId(key);
			}
		});

		module.setMixInAnnotation(Command.class, CommandMixin.class);
		return module;
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type")
	@JsonSubTypes({ //
			@Type(value = Annex.class, name = "Annex"), //
			@Type(value = Colonize.class, name = "Colonize"), //
			@Type(value = AllocateResources.class, name = "AllocateResources"), //
			@Type(value = SpaceDockShipClass.class, name = "SpaceDockShipClass"), //
			@Type(value = TransferColonists.class, name = "TransferColonists"), //
			@Type(value = RelocateShips.class, name = "RelocateShips"), //
			@Type(value = DeployJustLeaving.class, name = "DeployJustLeaving"), //
			@Type(value = DeployOrbiting.class, name = "DeployOrbiting"), //
			@Type(value = AllocateResearch.class, name = "AllocateResearch"), //
			@Type(value = SelectTechnology.class, name = "SelectTechnology"),

	})
	private abstract class CommandMixin {

	}

	public record SavegameBodyDto(String savegame, PlayerDto player, String gameId) {

	}

}
