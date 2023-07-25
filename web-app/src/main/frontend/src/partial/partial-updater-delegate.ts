import PartialUpdater from '~/partial/partial-updater';
import NoopPartialUpdater from '~/partial/noop-partial-updater';

export default class PartialUpdaterDelegate {
	#partialUpdater;
	#delegate;

	constructor(partialUpdater) {
		this.#partialUpdater = partialUpdater;
		this.#delegate = partialUpdater;
	}

	beforeRender(data) {
		return this.#delegate.beforeRender(data);
	}

	interceptSubmit(action, values) {
		return this.#delegate.interceptSubmit(action, values);
	}

	toggle() {
		if (this.#delegate instanceof PartialUpdater) {
			this.#delegate = new NoopPartialUpdater();
			return false;
		} else {
			this.#delegate = this.#partialUpdater;
			return true;
		}
	}
}
