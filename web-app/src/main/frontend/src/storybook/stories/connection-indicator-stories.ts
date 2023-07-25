import ConnectionIndicator from '~/component/connection-indicator';
import Story from '~/storybook/stories/story';

export default class ConnectionIndicatorStories {
	static showConnectionIndicator(story: Story) {
		story.showHtml(
			`Connection status: <${ConnectionIndicator.NAME}></${ConnectionIndicator.NAME}> (<button>Toggle</button>)`
		);

		const connectionIndicatorEl = story.querySelector(ConnectionIndicator.NAME) as ConnectionIndicator;
		story.querySelector('button').addEventListener('click', (event) => {
			connectionIndicatorEl.connected = !connectionIndicatorEl.connected;
		});
	}
}
