import ThemeManager from '~/component/theme-manager';
import Story from '~/storybook/stories/story';
import Theme from '~/theme/theme';

export default class ThemeManagerStories {
	static showThemeManager(story: Story) {
		story.showHtml(`		
			<${ThemeManager.NAME} id="theme-manager"></${ThemeManager.NAME}>
			<div>
				<div>
					<button id="theme-manager-apply-button">Apply theme</button>
					<button id="theme-manager-force-button">Force theme</button>
					<br>
					<button id="theme-manager-clear-button">Clear theme</button>
				</div>

				<img id="theme-manager-image"></img>
				<span id="theme-manager-no-image"></span>
			</div>
		`);

		const themeManagerEl = story.querySelector('#theme-manager') as ThemeManager;
		const imageEl = story.querySelector('#theme-manager-image');
		const noImageEl = story.querySelector('#theme-manager-no-image');

		themeManagerEl.addEventListener('loaded', (e) => {
			const image = Theme.getDataUrl('star-red');
			if (image) {
				imageEl.src = image;
			} else {
				noImageEl.innerText = 'No themed red star available.';
			}
		});

		const applyButtonEl = story.querySelector('#theme-manager-apply-button');
		const forceButtonEl = story.querySelector('#theme-manager-force-button');

		const clearButtonEl = story.querySelector('#theme-manager-clear-button');

		applyButtonEl.addEventListener('click', (e) => {
			imageEl.src = '';
			noImageEl.innerText = '';

			themeManagerEl.apply();
		});

		forceButtonEl.addEventListener('click', (e) => {
			themeManagerEl.forceTheme();
		});

		clearButtonEl.addEventListener('click', (e) => {
			Theme.clear();
		});
	}
}
