import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import ContainerTtile from '~/component/container-title';
import FlowLayout from '~/component/flow-layout';
import Inspector from '~/page/main-page/component/inspector/inspector';
import Story from '~/storybook/stories/story';
import cssUrl from '~/util/cssUrl';
import ErrorUtil from '~/util/error-util';
import HypermediaUtil from '~/util/hypermedia-util';

import AllocationsStories from '~/storybook/stories/allocations-stories';
import AnnexationStories from '~/storybook/stories/annexation-stories';
import BuildQueueStories from '~/storybook/stories/build-queue-stories';
import ColonizationStories from '~/storybook/stories/colonization-stories';
import ColonyStories from '~/storybook/stories/colony-stories';
import ConnectionIndicatorStories from '~/storybook/stories/connection-indicator-stories';
import ExplorationStories from '~/storybook/stories/exploration-stories';
import FleetDeploymentStories from '~/storybook/stories/fleet-deployment-stories';
import FleetViewStories from '~/storybook/stories/fleet-view-stories';
import GameLauncherStories from '~/storybook/stories/game-launcher-stories';
import HabitabilityStories from '~/storybook/stories/habitability-stories';
import ListBoxStories from '~/storybook/stories/list-box-stories';
import NewGamePageStories from '~/storybook/stories/new-game-page-stories';
import OutcomeStories from '~/storybook/stories/outcome-stories';
import RisingEmpireLogoStories from '~/storybook/stories/rising-empire-logo-stories';
import RunningGameStories from '~/storybook/stories/running-game-stories';
import SelectTechPageStories from '~/storybook/stories/select-tech-page-stories';
import ShipSpecStories from '~/storybook/stories/ship-spec-stories';
import ShipsStories from '~/storybook/stories/ships-stories';
import SliderGroupStories from '~/storybook/stories/slider-group-stories';
import SpaceCombatPageStories from '~/storybook/stories/space-combat-page-stories';
import SpaceCombatStories from '~/storybook/stories/space-combat-stories';
import StarBackgroundStories from '~/storybook/stories/star-background-stories';
import StarMapStories from '~/storybook/stories/star-map-stories';
import StarStories from '~/storybook/stories/star-stories';
import SystemDetailsStories from '~/storybook/stories/system-details-stories';
import SystemNameStories from '~/storybook/stories/system-name-stories';
import TechPageStories from '~/storybook/stories/tech-page-stories';
import ThemeManagerStories from '~/storybook/stories/theme-manager-stories';
import ThemeStories from '~/storybook/stories/theme-stories';
import TurnFinishedDialogStories from '~/storybook/stories/turn-finished-dialog-stories';
import UnexploredStories from '~/storybook/stories/unexplored-stories';
import SubmitInterceptor from '~/util/submit-interceptor';
import Action from '~/util/action';
import TransferColonistsStories from '~/storybook/stories/transfer-colonists-stories';
import RelocateStories from '~/storybook/stories/relocate-ships-stories';

class Storybook extends HTMLElement {
	static NAME = 're-storybook';

	static #STORY_SHOW_METHOD_MAPPING = {
		'connection-indicator': (story) => ConnectionIndicatorStories.showConnectionIndicator(story),
		'list-box': (story) => ListBoxStories.showListBox(story),
		ships: (story) => ShipsStories.showShips(story),
		'star-background': (story) => StarBackgroundStories.showStarBackground(story),
		'star-background-animated': (story) => StarBackgroundStories.showStarBackgroundAnimated(story),
		'slider-group': (story) => SliderGroupStories.showSliderGroup(story),
		theme: (story) => ThemeStories.showTheme(story),
		'theme-manager': (story) => ThemeManagerStories.showThemeManager(story),
		'rising-empire-logo': (story) => RisingEmpireLogoStories.showLogo(story),
		'game-launcher': (story) => GameLauncherStories.showGameLauncher(story),
		'running-game': (story) => RunningGameStories.showRunningGame(story),
		'select-tech-page': (story) => SelectTechPageStories.showSelectTechPage(story),
		'space-combat-page': (story) => SpaceCombatPageStories.showSpaceCombatPage(story),
		'space-combat-page-animated': (story) => SpaceCombatPageStories.showSpaceCombatPageAnimated(story),
		'ship-specs': (story) => ShipSpecStories.showShipSpesc(story),
		outcome: (story) => OutcomeStories.showOutcomes(story),
		'new-game-page': (story) => NewGamePageStories.showNewGamePage(story),
		'tech-page': (story) => TechPageStories.showTechPage(story),
		habitability: (story) => HabitabilityStories.showHabitability(story),
		'system-name': (story) => SystemNameStories.showSystemName(story),
		colonization: (story) => ColonizationStories.showColonization(story),
		annexation: (story) => AnnexationStories.showAnnexation(story),
		exploration: (story) => ExplorationStories.showExploration(story),
		'fleet-deployment-deployable-fleet': (story) =>
			FleetDeploymentStories.showFleetDeploymentDeployableFleet(story),
		'fleet-view': (story) => FleetViewStories.showFleetView(story),
		'space-combat-in-explored-system': (story) => SpaceCombatStories.showSpaceCombatInExploredSystem(story),
		'space-combat-in-unexplored-system': (story) => SpaceCombatStories.showSpaceCombatPageInUnexploredSystem(story),
		'system-details-own-colony': (story) => SystemDetailsStories.showSystemDetailsOwnColony(story),
		allocations: (story) => AllocationsStories.showAllocations(story),
		'build-queue': (story) => BuildQueueStories.showBuildQueue(story),
		colony: (story) => ColonyStories.showColony(story),
		'transfer-colonists': (story) => TransferColonistsStories.showTransferColonists(story),
		'relocate-ships': (story) => RelocateStories.showRelocateShips(story),
		'system-details-foreign-colony': (story) => SystemDetailsStories.showSystemDetailsForeignColony(story),
		'system-details-foreign-colony-outdated': (story) =>
			SystemDetailsStories.showSystemDetailsForeignColonyOutdated(story),
		'system-details-no-colony': (story) => SystemDetailsStories.showSystemDetailsNoColony(story),
		unexplored: (story) => UnexploredStories.showUnexplored(story),
		'star-map-notification': (story) => StarMapStories.showStarMapNotification(story),
		star: (story) => StarStories.showStars(story),
		'turn-finished-dialog-waiting-for-others': (story) =>
			TurnFinishedDialogStories.showTurnFinishedDialogWaitingForOthers(story),
	};

	#stageEl: HTMLDivElement;
	#renderDataEl: HTMLTextAreaElement;
	#renderDataOriginalEl: HTMLTextAreaElement;
	#applyRenderDataEl: HTMLButtonElement;
	#resetRenderDataEl: HTMLButtonElement;

	#storiesEl: HTMLSelectElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: inline-block;

					width: 100vw;
					height: 100vh;
				}

				#storybook-page {
					height: 100%;
				}

				.inspector-child-story {
					display: inline-block;
					width: ${Inspector.WIDTH}px;
				}

				#stage {
					border: 2px solid deeppink;
					overflow: auto;
				}

				#json {
					flex: 0.8;
				}

				#render-data {
					width: 100%;
					height: 100%;
				}

				#reset-render-data:enabled {
					background-color: red;
				}

				#stories {
					width: 296px;
				}

				#stories > option.lv-1 {
					padding-left: 12px;
				}

				#stories > option.lv-2 {
					padding-left: 24px;
				}

				#stories > option.lv-3 {
					padding-left: 36px;
				}

				#stories > option.lv-4 {
					padding-left: 48px;
				}
			</style>

			<${Container.NAME} id="storybook-page" gap="M">
				<${ContainerTtile.NAME}>Rising Empire Storybook</${ContainerTtile.NAME}>

				<${FlowLayout.NAME} data-fill-vertically gap="M">
					<select id="stories" size="500">
						<option disabled>component</option>
							<option value="connection-indicator" class="lv-1">connection-indicator</option>
							<option value="list-box" class="lv-1">list-box</option>
							<option value="ships" class="lv-1">ships</option>
							<option value="star-background" class="lv-1">star-background</option>
							<option value="star-background-animated" data-animated-story class="lv-1">star-background-animated</option>
							<option value="slider-group" class="lv-1">slider-group</option>
							<option value="theme" class="lv-1">theme</option>							
							<option value="theme-manager" data-animated-story class="lv-1">theme-manager</option>
							<option value="rising-empire-logo" class="lv-1">rising-empire-logo</option>
						<option disabled>game browser</option>
							<option value="game-launcher" class="lv-1">game-launcher</option>
							<option value="running-game" class="lv-1">running-game</option>
						<option disabled>page</option>
							<option value="new-game-page" class="lv-1">new-game-page</option>
							<option value="select-tech-page" class="lv-1">select-tech-page</option>
							<option value="space-combat-page" class="lv-1">space-combat-page</option>
								<option value="ship-specs" class="lv-2">ship-specs</option>
								<option value="outcome" class="lv-2">outcome</option>
							<option value="space-combat-page-animated" data-animated-story class="lv-1">space-combat-page-animated</option>
							<option value="tech-page" class="lv-1">tech-page</option>
							<option disabled class="lv-1">main-page</option>
								<option disabled class="lv-2">inspector</option>
									<option value="habitability" class="lv-3">habitability</option>
									<option value="system-name" class="lv-3">system-name</option>
									<option value="colonization" class="lv-3">colonization</option>
									<option value="annexation" class="lv-3">annexation</option>
									<option value="exploration" class="lv-3">exploration</option>
									<option value="fleet-deployment-deployable-fleet" class="lv-3">fleet-deployment for deployable fleet</option>
									<option value="fleet-view" class="lv-3">fleet-view</option>
									<option value="space-combat-in-explored-system" class="lv-3">space-combat in explored system</option>
									<option value="space-combat-in-unexplored-system" class="lv-3">space-combat in unexplored system</option>
									<option value="system-details-own-colony" class="lv-3">system-details for own colony</option>
										<option value="allocations" class="lv-4">allocations</option>
										<option value="build-queue" class="lv-4">build-queue</option>
										<option value="colony" class="lv-4">colony</option>
										<option value="transfer-colonists" class="lv-4">transfer-colonists</option>
										<option value="relocate-ships" class="lv-4">relocate-ships</option>
									<option value="system-details-foreign-colony" class="lv-3">system-details with foreign colony</option>
										<option value="system-details-foreign-colony-outdated" class="lv-4">outdated</option>
									<option value="system-details-no-colony" class="lv-3">system-details with no colony</option>
									<option value="unexplored" class="lv-3">unexplored</option>
								<option disabled class="lv-2">star-map</option>
									<option value="star-map-notification" class="lv-3">star-map notification</option>
									<option value="star" class="lv-3">star</option>
								<option value="turn-finished-dialog-waiting-for-others" class="lv-2">turn-finished-dialog waiting for others</option>
					</select>

					<div id="stage" data-flow-size="1fr"></div>
				</${FlowLayout.NAME}>

				<${FlowLayout.NAME}" id="json">
					<textarea id="render-data"></textarea>
					<textarea id="render-data-original" hidden></textarea>				
				</${FlowLayout.NAME}">

				<${ContainerButtons.NAME}>
					<button id="apply-render-data">Apply</button>
					<button id="reset-render-data">Reset</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#stageEl = this.shadowRoot.querySelector('#stage');
		this.#renderDataEl = this.shadowRoot.querySelector('#render-data');
		this.#renderDataOriginalEl = this.shadowRoot.querySelector('#render-data-original');
		this.#applyRenderDataEl = this.shadowRoot.querySelector('#apply-render-data');
		this.#resetRenderDataEl = this.shadowRoot.querySelector('#reset-render-data');

		this.#storiesEl = this.shadowRoot.querySelector('#stories');
		this.shadowRoot.querySelector('#stories').addEventListener('dblclick', (e) => {
			const optionEl = e.target as HTMLOptionElement;

			if (!optionEl.disabled) {
				const storyId = optionEl.value;
				this.showStory(storyId);
			}
		});

		this.#applyRenderDataEl.addEventListener('click', () => this.#renderElement(this.#getRenderableStageEls()));

		this.#resetRenderDataEl.addEventListener('click', (e) => {
			const storyId = this.#storiesEl.value;
			sessionStorage.removeItem(storyId);
			this.#renderDataEl.value = this.#renderDataOriginalEl.value;

			this.#renderElement(this.#getRenderableStageEls());
		});
	}

	#renderElement(renderableStageEls) {
		const storyId = this.#storiesEl.value;
		sessionStorage.setItem(storyId, this.#renderDataEl.value);

		if (renderableStageEls.length > 0) {
			const data = JSON.parse(this.#renderDataEl.value);
			renderableStageEls.forEach((renderableEl: HTMLElement) =>
				renderableEl['render'](
					renderableEl.dataset.jsonIndex ? data[parseInt(renderableEl.dataset.jsonIndex)] : data
				)
			);

			this.#resetRenderDataEl.disabled =
				this.#renderDataEl.value === '' || this.#renderDataEl.value === this.#renderDataOriginalEl.value;
		} else {
			this.#resetRenderDataEl.disabled = true;
		}

		if (!document.querySelector('#story-render-done')) {
			document.body.insertAdjacentHTML('beforeend', `<div id="story-render-done" style="display: none;"></div>`);
		}
	}

	async showStory(storyId) {
		const showMethod = Storybook.#STORY_SHOW_METHOD_MAPPING[storyId];
		if (!showMethod) {
			if (storyId !== '') {
				console.warn(`No show method found for story with id '${storyId}'!`);
			}
		} else {
			this.#stageEl.innerText = '';
			this.#renderDataEl.value = '';

			window.location.hash = storyId;
			this.#storiesEl.value = storyId;

			await showMethod(new Story(storyId, this));

			const renderableStageEls = this.#getRenderableStageEls();
			const hasRenderableElelemts = renderableStageEls.length > 0;
			this.#renderDataEl.disabled = !hasRenderableElelemts;
			this.#applyRenderDataEl.disabled = !hasRenderableElelemts;

			this.#renderElement(renderableStageEls);
		}
	}

	/**
	 * If renderable the element itself, otherwise the renderable children (if there are any).
	 * */
	#getRenderableStageEls() {
		const inStageEl = this.#stageEl.firstElementChild;
		if (typeof inStageEl['render'] === 'function') {
			return [inStageEl];
		} else {
			return [...inStageEl.children].filter((el) => typeof el['render'] === 'function');
		}
	}

	dispatchHypermediaSubmitEvent(action, values) {
		const hypermediaSubmit = { action, values };
		console.log(JSON.stringify(hypermediaSubmit));

		const inStageEl = this.#stageEl.firstElementChild;
		inStageEl.dispatchEvent(new CustomEvent('hypermedia-submit', { detail: hypermediaSubmit }));
	}

	setRenderData(storyId, json) {
		this.#renderDataOriginalEl.value = JSON.stringify(JSON.parse(json), null, 2);
		const sessionStorageJson = sessionStorage.getItem(storyId);
		if (sessionStorageJson !== null && sessionStorageJson.trim().length !== 0) {
			this.#renderDataEl.value = sessionStorageJson;
		} else {
			this.#renderDataEl.value = this.#renderDataOriginalEl.value;
		}
	}

	getRenderData() {
		return this.#renderDataEl.value;
	}

	showHtml(html) {
		this.#stageEl.innerHTML = html;
	}
}

customElements.define(Storybook.NAME, Storybook);

ErrorUtil.registerGlobalErrorListener();

const storybookEl = new Storybook();
document.body.appendChild(storybookEl);

HypermediaUtil.addSubmitInterceptor(
	new (class extends SubmitInterceptor {
		override preHandle(action: Action, values: any): Promise<any> | undefined {
			console.log({ action, values });
			// intercept all requests to the backend
			return Promise.resolve({});
		}
	})()
);
HypermediaUtil.setActionResponseCallbackFn((data) => null);

storybookEl.showStory(window.location.hash.replace('#', ''));
