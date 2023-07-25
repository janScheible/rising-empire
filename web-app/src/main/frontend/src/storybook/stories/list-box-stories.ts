import ListBox from '~/component/list-box';
import Story from '~/storybook/stories/story';

export default class ListBoxStories {
	static showListBox(story: Story) {
		story.showHtml(`		
			<${ListBox.NAME} id="story-list-box" style="max-width: 450px; min-height: 200px;">
				<div>first<br><span>with linebreak...</span></div>
				<div>second entry</div>
			</${ListBox.NAME}>
		`);

		story
			.querySelector('#story-list-box')
			.addEventListener('currentindexchange', (e) => console.log(e.target.selectedIndex));
	}
}
