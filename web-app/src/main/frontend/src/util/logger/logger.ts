import LogLevel from '~/util/logger/log-level';

export default class Logger {
	static #NOOP = function () {};

	#name;
	#level;

	constructor(name: string, level: LogLevel) {
		this.#name = name;
		this.setLevel(level);
	}

	setLevel(level: LogLevel) {
		if (!(level === 'ERROR' || level === 'WARN' || level === 'INFO' || level === 'DEBUG' || level === 'NONE')) {
			throw new Error(`The level ${level} is unknown!`);
		}

		this.#level = level;

		this.error = this.isErrorEnabled() ? console.error.bind(window.console) : Logger.#NOOP;
		this.warn = this.isWarnEnabled() ? console.warn.bind(window.console) : Logger.#NOOP;
		this.info = this.isInfoEnabled() ? console.info.bind(window.console) : Logger.#NOOP;
		this.debug = this.isDebugEnabled() ? console.debug.bind(window.console) : Logger.#NOOP;
	}

	error(...data: any) {
		// bound to console.xxx(...) in setLevel(...) anyway (to preserve line numbers)
	}

	isErrorEnabled() {
		return (
			this.#level !== 'NONE' &&
			(this.#level === 'ERROR' || this.#level === 'WARN' || this.#level === 'INFO' || this.#level === 'DEBUG')
		);
	}

	warn(...data: any) {
		// bound to console.xxx(...) in setLevel(...) anyway (to preserve line numbers)
	}

	isWarnEnabled() {
		return this.#level !== 'NONE' && (this.#level === 'WARN' || this.#level === 'INFO' || this.#level === 'DEBUG');
	}

	info(...data: any) {
		// bound to console.xxx(...) in setLevel(...) anyway (to preserve line numbers)
	}

	isInfoEnabled() {
		return this.#level !== 'NONE' && (this.#level === 'INFO' || this.#level === 'DEBUG');
	}

	debug(...data: any) {
		// bound to console.xxx(...) in setLevel(...) anyway (to preserve line numbers)
	}

	isDebugEnabled() {
		return this.#level !== 'NONE' && this.#level === 'DEBUG';
	}

	getLevel() {
		return this.#level;
	}

	getName() {
		return this.#name;
	}
}
