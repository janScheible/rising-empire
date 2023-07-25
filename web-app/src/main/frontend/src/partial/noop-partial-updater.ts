export default class NoopPartialUpdater {
	constructor(viewportFn?, renderFn?) {}

	beforeRender(data) {
		return data;
	}

	interceptSubmit(action, values) {
		return true;
	}
}
