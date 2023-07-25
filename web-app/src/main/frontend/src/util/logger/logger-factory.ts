import Logger from '~/util/logger/logger';
import LogLevel from '~/util/logger/log-level';

// go up the 'logger' and 'util' directories to end up in the root of the frontend
const frontendRootPathName = new URL('../../', import.meta.url).pathname;

export default class LoggerFactory {
	static #loggers: { [key: string]: WeakRef<Logger> } = {};
	static #logLevels = {};

	static #globalLoglevel: LogLevel = 'WARN';

	static get(moduleUrlOrloggerName: string) {
		let loggerName = moduleUrlOrloggerName;

		if (moduleUrlOrloggerName.includes(':')) {
			const pathname = new URL(moduleUrlOrloggerName).pathname;
			const cleanedPathname = pathname.substring(frontendRootPathName.length, pathname.lastIndexOf('.'));
			loggerName = cleanedPathname.split('/').join('.');
		}

		const existingLogger = LoggerFactory.#loggers[loggerName]?.deref();

		if (existingLogger) {
			return existingLogger;
		} else {
			const logger = new Logger(loggerName, LoggerFactory.#getLogLevel(loggerName));
			LoggerFactory.#loggers[loggerName] = new WeakRef(logger);
			return logger;
		}
	}

	static configure(globalLoglevel: LogLevel, levels) {
		LoggerFactory.#globalLoglevel = globalLoglevel;

		this.#logLevels = levels ?? {};

		for (const loggerName of Object.keys(LoggerFactory.#loggers)) {
			const logger = LoggerFactory.#loggers[loggerName]?.deref();

			if (logger) {
				logger?.setLevel(LoggerFactory.#getLogLevel(logger.getName()));
			} else {
				// in case of 'broken' WeakRef the logger is removed
				delete LoggerFactory.#loggers[loggerName];
			}
		}
	}

	static #getLogLevel(loggerName) {
		return LoggerFactory.#logLevels[loggerName] ?? LoggerFactory.#globalLoglevel;
	}

	static getLoggerNames() {
		return Object.entries(LoggerFactory.#loggers)
			.filter(([name, weakRef]) => weakRef.deref())
			.map(([name, weakRef]) => name);
	}
}
