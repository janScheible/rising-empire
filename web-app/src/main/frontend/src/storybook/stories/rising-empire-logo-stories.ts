import RisingEmpireLogo from '~/component/rising-empire-logo';
import Story from '~/storybook/stories/story';

export default class RisingEmpireLogoStories {
	static showLogo(story: Story) {
		story.showHtml(`<${RisingEmpireLogo.NAME}></${RisingEmpireLogo.NAME}>`);
	}
}
