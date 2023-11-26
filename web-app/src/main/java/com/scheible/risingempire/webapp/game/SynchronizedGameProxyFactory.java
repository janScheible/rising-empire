package com.scheible.risingempire.webapp.game;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.PlayerGame;

/**
 * Synchronizes a game instance (and it's returned PlayerGame instances) to make it ready
 * for concurrent usage.
 *
 * @author sj
 */
public class SynchronizedGameProxyFactory {

	public static Game getProxy(Game game) {
		return (Game) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { Game.class }, (Object proxy, Method method, Object... args) -> {
					synchronized (game) {
						Object result = method.invoke(game, args);
						if (result instanceof PlayerGame) {
							return synchronizePlayerGame((PlayerGame) result, game);
						}
						else {
							return result;
						}
					}
				});
	}

	private static PlayerGame synchronizePlayerGame(PlayerGame playerGame, Game game) {
		return (PlayerGame) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { PlayerGame.class }, (Object proxy, Method method, Object... args) -> {
					synchronized (game) {
						return method.invoke(playerGame, args);
					}
				});
	}

}
