import cssUrl from '~/util/cssUrl';
import sleep from '~/util/sleep';
import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import ContainerTtile from '~/component/container-title';
import Theme from '~/theme/theme';
import FlowLayout from '~/component/flow-layout';

export default class ThemeManager extends HTMLElement {
	static NAME = 're-theme-manager';

	static #SESSION_STORAGE_SKIP_THEME_UPLOAD = 'doSkipThemeUpload';

	#loadingEl: HTMLDivElement;
	#skipOrUploadEl: HTMLDivElement;
	#skipThemeUploadEl: HTMLButtonElement;

	#themeUploadEl: HTMLInputElement;

	#containerButtonsEl: ContainerButtons;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: inline-block;
				}

				#loading {
					padding: 6px 64px;
				}

				#theme-upload {
					display: none;
				}
			</style>
			<${Container.NAME} border>
				<${ContainerTtile.NAME}>Theme manager</${ContainerTtile.NAME}>

				<${FlowLayout.NAME}>
					<div id="loading">Loading...</div>
					<div id="skip-or-upload" hidden>
						<input id="theme-upload" type="file" accept="application/zip">
						<div>No theme found! Either upload <label for="theme-upload"><button id="theme-button-button">moo-theme.zip</button></label><br>
						(saving requires a non-private tab) or skip.</div>
					</div>
				</${FlowLayout.NAME}>

				<${ContainerButtons.NAME} hidden><button id="skip-theme-upload">Skip</button></${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#loadingEl = this.shadowRoot.querySelector('#loading');

		this.shadowRoot.querySelector('#theme-button-button').addEventListener('click', (event) => {
			(event.target as HTMLElement).parentElement.click();
		});

		this.#skipOrUploadEl = this.shadowRoot.querySelector('#skip-or-upload');
		this.#skipThemeUploadEl = this.shadowRoot.querySelector('#skip-theme-upload');
		this.#skipThemeUploadEl.addEventListener('click', (event) => {
			sessionStorage.setItem(ThemeManager.#SESSION_STORAGE_SKIP_THEME_UPLOAD, 'true');

			this.#loadingEl.hidden = false;

			this.#skipOrUploadEl.hidden = true;
			this.#containerButtonsEl.hidden = true;

			this.#dispatchLoadedEvent(false);
		});

		this.#themeUploadEl = this.shadowRoot.querySelector('#theme-upload');
		this.#themeUploadEl.addEventListener('change', (event) => this.#userSelectedTheme(event));

		this.#containerButtonsEl = this.shadowRoot.querySelector(ContainerButtons.NAME);
	}

	async apply() {
		await Theme.apply();

		const doSkipThemeUpload = 'true' === sessionStorage.getItem(ThemeManager.#SESSION_STORAGE_SKIP_THEME_UPLOAD);
		if (Theme.isEmpty() && !doSkipThemeUpload) {
			this.#loadingEl.hidden = true;

			this.#skipOrUploadEl.hidden = false;
			this.#containerButtonsEl.hidden = false;
		} else {
			this.#loadingDone();
		}
	}

	forceTheme() {
		sessionStorage.removeItem(ThemeManager.#SESSION_STORAGE_SKIP_THEME_UPLOAD);
	}

	#dispatchLoadedEvent(theme: boolean) {
		this.dispatchEvent(new CustomEvent('loaded', { detail: { theme } }));
	}

	async #userSelectedTheme(event) {
		this.#loadingEl.hidden = false;

		this.#skipOrUploadEl.hidden = true;
		this.#containerButtonsEl.hidden = true;

		const file = this.#themeUploadEl.files[0];
		const fileContent = new Uint8Array(await file.arrayBuffer());

		this.load(fileContent);
	}

	async load(fileContent: Uint8Array) {
		await Theme.load(fileContent);
		await this.#loadingDone();
	}

	async #loadingDone() {
		await sleep(200);
		this.#loadingEl.innerText = 'Loading... done!';
		await sleep(100);
		this.#loadingEl.innerText = 'Loading...';
		this.#dispatchLoadedEvent(true);
	}
}

customElements.define(ThemeManager.NAME, ThemeManager);
