package com.scheible.risingempire.mootheme.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.thetaphi.forbiddenapis.SuppressForbidden;

/**
 * @author sj
 */
@SuppressForbidden
public class WrappedLogger {

	private final Logger delegate;

	private WrappedLogger(Logger delegate) {
		this.delegate = delegate;
	}

	public static WrappedLogger getLogger(Class<?> clazz) {
		return new WrappedLogger(Logger.getLogger(clazz.getName()));
	}

	public void info(String msg, Object... args) {
		this.delegate.log(Level.INFO, msg, args);
	}

	public void error(String msg, Object... args) {
		this.delegate.log(Level.SEVERE, msg, args);
	}

}
