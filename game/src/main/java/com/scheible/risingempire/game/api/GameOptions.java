package com.scheible.risingempire.game.api;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.GameOptionsBuilder.GalaxySizeStage;
import com.scheible.risingempire.game.api.annotation.Initialized;
import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemNotificationView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * @author sj
 */
@StagedRecordBuilder
public record GameOptions(GalaxySize galaxySize, int playerCount,
		@Initialized @RecordBuilder.Initializer("DEFAULT_TEST_GAME") boolean testGame,
		@Initialized @RecordBuilder.Initializer("DEFAULT_GAME_2") boolean game2,
		@Initialized @RecordBuilder.Initializer("DEFAULT_FAKE_TECH_PROVIDER") Optional<FakeTechProvider> fakeTechProvider,
		@Initialized @RecordBuilder.Initializer("DEFAULT_FAKE_SYSTEM_NOTIFICATION_PROVIDER") Optional<FakeSystemNotificationProvider> fakeSystemNotificationProvider,
		@Initialized @RecordBuilder.Initializer("DEFAULT_FLEET_RANGE_FACTOR") double fleetRangeFactor,
		@Initialized @RecordBuilder.Initializer("DEFAULT_FLEET_SPEED_FACTOR") double fleetSpeedFactor,
		@Initialized @RecordBuilder.Initializer("DEFAULT_SAPCE_COMBAT_OUTCOME") Optional<Outcome> spaceCombatOutcome,
		@Initialized @RecordBuilder.Initializer("DEFAULT_ANNEXATION_SIEGE_ROUNGS") int annexationSiegeRounds) {

	public static final boolean DEFAULT_TEST_GAME = false;

	public static final boolean DEFAULT_GAME_2 = false;

	public static final Optional<FakeTechProvider> DEFAULT_FAKE_TECH_PROVIDER = Optional.empty();

	public static final Optional<FakeSystemNotificationProvider> DEFAULT_FAKE_SYSTEM_NOTIFICATION_PROVIDER = Optional
		.empty();

	public static final double DEFAULT_FLEET_RANGE_FACTOR = 1.0;

	public static final double DEFAULT_FLEET_SPEED_FACTOR = 1.0;

	public static final Optional<Outcome> DEFAULT_SAPCE_COMBAT_OUTCOME = Optional.empty();

	public static final int DEFAULT_ANNEXATION_SIEGE_ROUNGS = 5;

	public GameOptions {
	}

	public static GalaxySizeStage builder() {
		return GameOptionsBuilder.builder();
	}

	public static GameOptionsBuilder testGameBuilder() {
		return builder().galaxySize(GalaxySize.HUGE).playerCount(3).testGame(true);
	}

	/**
	 * Predetermines the outcome of all space combats. If a fleet survives all ship counts
	 * are halved.
	 */
	public Optional<Outcome> spaceCombatOutcome() {
		return this.spaceCombatOutcome;
	}

	public Set<Player> players() {
		return Set.of(Player.BLUE, Player.YELLOW, Player.WHITE);
	}

	@FunctionalInterface
	public interface FakeTechProvider {

		Set<TechGroupView> get(Player player, int round);

	}

	@FunctionalInterface
	public interface FakeSystemNotificationProvider {

		Set<SystemNotificationView> get(Player player, int round);

	}

}
