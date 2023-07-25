import Story from '~/storybook/stories/story';
import Theme from '~/theme/theme';

export default class ThemeStories {
	static async showTheme(story: Story) {
		story.showHtml(`		
			<img id="theme-image"></img>
		`);

		const response = await fetch('/storybook/moo-theme.zip');
		const fileContent = await response.body.getReader().read();

		await Theme.load(fileContent.value);

		Theme.enabled = true;
		story.querySelector('#theme-image').src = Theme.getDataUrl('planet-terran');
		await Theme.clear();
	}
}
