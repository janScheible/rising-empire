import StarBackground from '~/component/star-background';
import Story from '~/storybook/stories/story';

export default class StarBackgroundStories {
	static showStarBackgroundAnimated(story: Story) {
		story.showHtml(`
			<canvas is=${StarBackground.NAME} id="animated-star-background" width="64" height="32" animated></canvas>
			<button id="animated-star-background-hide-show-button">Hide/Show</button>
		`);

		story.querySelector('#animated-star-background-hide-show-button').addEventListener('click', (e) => {
			const el = story.querySelector('#animated-star-background');
			el.hidden = !el.hidden;
		});
	}

	static showStarBackground(story: Story) {
		story.showHtml(`<canvas is=${StarBackground.NAME} width="64" height="32"></canvas>`);
	}
}
