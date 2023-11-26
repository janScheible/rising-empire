package com.scheible.risingempire.game.api;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 * @author sj
 */
public class GameOptions {

	private final GalaxySize galaxySize;

	private final int playerCount;

	private final boolean testGameScenario;

	private Optional<FakeTechProvider> fakeTechProvider = Optional.empty();

	private Optional<FakeSystemNotificationProvider> fakeSystemNotificationProvider = Optional.empty();

	private double fleetRangeFactor = 1.0;

	private double fleetSpeedFactor = 1.0;

	private Optional<Outcome> spaceCombatOutcome = Optional.empty();

	private int annexationSiegeRounds = 5;

	public GameOptions(GalaxySize galaxySize, int playerCount) {
		this(galaxySize, playerCount, false);
	}

	private GameOptions(GalaxySize galaxySize, int playerCount, boolean testGameScenario) {
		this.galaxySize = galaxySize;
		this.playerCount = playerCount;
		this.testGameScenario = testGameScenario;
	}

	public static GameOptions forTestGameScenario() {
		return new GameOptions(GalaxySize.HUGE, 3, true);
	}

	public GameOptions fakeTechProvider(FakeTechProvider fakeTechProvider) {
		this.fakeTechProvider = Optional.of(fakeTechProvider);
		return this;
	}

	public GameOptions fakeSystemNotificationProvider(FakeSystemNotificationProvider fakeSystemNotificationProvider) {
		this.fakeSystemNotificationProvider = Optional.of(fakeSystemNotificationProvider);
		return this;
	}

	public GameOptions fleetRangeFactor(double shipRangeFactor) {
		this.fleetRangeFactor = shipRangeFactor;
		return this;
	}

	public GameOptions fleetSpeedFactor(double shipSpeedFactor) {
		this.fleetSpeedFactor = shipSpeedFactor;
		return this;
	}

	public GameOptions spaceCombatWinner(Outcome outcome) {
		this.spaceCombatOutcome = Optional.of(outcome);
		return this;
	}

	public GameOptions annexationSiegeRounds(int annexationSiegeRounds) {
		this.annexationSiegeRounds = annexationSiegeRounds;
		return this;
	}

	public GalaxySize getGalaxySize() {
		return this.galaxySize;
	}

	public int getPlayerCount() {
		return this.playerCount;
	}

	public Optional<FakeTechProvider> getFakeTechProvider() {
		return this.fakeTechProvider;
	}

	public Optional<FakeSystemNotificationProvider> getFakeNotificationProvider() {
		return this.fakeSystemNotificationProvider;
	}

	public double getFleetRangeFactor() {
		return this.fleetRangeFactor;
	}

	public double getFleetSpeedFactor() {
		return this.fleetSpeedFactor;
	}

	public Optional<Outcome> getSpaceCombatOutcome() {
		return this.spaceCombatOutcome;
	}

	public boolean isTestGameScenario() {
		return this.testGameScenario;
	}

	public int getAnnexationSiegeRounds() {
		return this.annexationSiegeRounds;
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
