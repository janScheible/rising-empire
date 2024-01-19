import StarMap from '~/page/main-page/component/star-map/star-map';
import Story from '~/storybook/stories/story';

export default class StarMapStories {
	static showStarMapNotification(story: Story) {
		story.setRenderData(`{
			"starBackground": {
				"width": 320,
				"height": 240
			},
			"ranges": {
				"starMapWidth": 320,
				"starMapHeight": 240,
				"fleetRanges": [],
				"extendedFleetRanges": [],
				"colonyScannerRanges": [],
				"fleetScannerRanges": []
			},
			"stars": [],
			"fleets": [],
			"starNotifications": [{
				"starId": "s34x35",
				"x": 20,
				"y": 20,
				"text": "This is a notification for s60x60. Please do what ever it tells you..."
			}]
		}`);

		story.showHtml(`<${StarMap.NAME}></${StarMap.NAME}>`);
	}
}
