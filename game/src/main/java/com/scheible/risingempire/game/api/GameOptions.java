package com.scheible.risingempire.game.api;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.universe.Player;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 *
 * @author sj
 */
public class GameOptions {

	@FunctionalInterface
	public interface FakeTechProvider {

		Set<TechGroupView> get(Player player, int round);
	}

	@FunctionalInterface
	public interface FakeSystemNotificationProvider {

		Set<SystemNotificationView> get(Player player, int round);
	}

	private final GalaxySize galaxySize;
	private final int playerCount;
	private final boolean testGameScenario;

	@Nullable
	private FakeTechProvider fakeTechProvider;
	@Nullable
	private FakeSystemNotificationProvider fakeSystemNotificationProvider;

	private double fleetRangeFactor = 1.0;
	private double fleetSpeedFactor = 1.0;
	private Outcome spaceCombatOutcome = null;

	private int annexationSiegeRounds = 5;

	public GameOptions(final GalaxySize galaxySize, final int playerCount) {
		this(galaxySize, playerCount, false);
	}

	private GameOptions(final GalaxySize galaxySize, final int playerCount, final boolean testGameScenario) {
		this.galaxySize = galaxySize;
		this.playerCount = playerCount;
		this.testGameScenario = testGameScenario;
	}

	public static GameOptions forTestGameScenario() {
		return new GameOptions(GalaxySize.HUGE, 3, true);
	}

	public GameOptions fakeTechProvider(final FakeTechProvider fakeTechProvider) {
		this.fakeTechProvider = fakeTechProvider;
		return this;
	}

	public GameOptions fakeSystemNotificationProvider(
			final FakeSystemNotificationProvider fakeSystemNotificationProvider) {
		this.fakeSystemNotificationProvider = fakeSystemNotificationProvider;
		return this;
	}

	public GameOptions fleetRangeFactor(final double shipRangeFactor) {
		this.fleetRangeFactor = shipRangeFactor;
		return this;
	}

	public GameOptions fleetSpeedFactor(final double shipSpeedFactor) {
		this.fleetSpeedFactor = shipSpeedFactor;
		return this;
	}

	public GameOptions spaceCombatWinner(final Outcome outcome) {
		this.spaceCombatOutcome = outcome;
		return this;
	}

	public GameOptions annexationSiegeRounds(final int annexationSiegeRounds) {
		this.annexationSiegeRounds = annexationSiegeRounds;
		return this;
	}

	public GalaxySize getGalaxySize() {
		return galaxySize;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public Optional<FakeTechProvider> getFakeTechProvider() {
		return Optional.ofNullable(fakeTechProvider);
	}

	public Optional<FakeSystemNotificationProvider> getFakeNotificationProvider() {
		return Optional.ofNullable(fakeSystemNotificationProvider);
	}

	public double getFleetRangeFactor() {
		return fleetRangeFactor;
	}

	public double getFleetSpeedFactor() {
		return fleetSpeedFactor;
	}

	public Outcome getSpaceCombatOutcome() {
		return spaceCombatOutcome;
	}

	public boolean isTestGameScenario() {
		return testGameScenario;
	}

	public int getAnnexationSiegeRounds() {
		return annexationSiegeRounds;
	}
}
