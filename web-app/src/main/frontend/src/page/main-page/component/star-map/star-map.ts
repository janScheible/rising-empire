import Star from '~/page/main-page/component/star-map/component/star';
import Reconciler from '~/util/reconciler';
import Fleet from '~/page/main-page/component/star-map/component/fleet';
import StarSelection from '~/page/main-page/component/star-map/component/star-selection';
import StarBackground from '~/component/star-background';
import FleetSelection from '~/page/main-page/component/star-map/component/fleet-selection';
import Itinerary from '~/page/main-page/component/star-map/component/itinerary';
import StarNotification from '~/page/main-page/component/star-map/component/star-notification';
import HypermediaUtil from '~/util/hypermedia-util';
import sleep from '~/util/sleep';
import debounce from '~/util/debounce';
import Viewport from '~/partial/viewport';
import Ranges from '~/page/main-page/component/star-map/component/ranges';
import cssUrl from '~/util/cssUrl';

export default class StarMap extends HTMLElement {
	static NAME = 're-star-map';

	static #SCROLL_SPEED_PIXEL_PER_SECOND = 1700;

	#resizeObserver: ResizeObserver;

	#wrapperEl: HTMLDivElement;

	#starNotificationEl: StarNotification;
	#starSelectionEl: StarSelection;
	#fleetSelectionEl: FleetSelection;
	#itineraryEl: Itinerary;
	#rangesEl: Ranges;
	#starBackgroundEl: StarBackground;

	#startScrollAction;
	#reloadAction;
	#endScrollAction;

	#scrolling: boolean;
	#fleetZIndex: number;
	#onScroll;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: grid;
  					place-items: center;

					overflow: hidden;

					background-color: rgba(0, 0, 0, 0.75);
				}

				#wrapper {
					display: inline-block;
					position: relative;

					transform: none;
				}

				${StarNotification.NAME} {
					z-index: 3000;
				}

				${Star.NAME} {
					z-index: 1000;
				}

				${StarSelection.NAME} {
					z-index: 1250;
				}

				${Fleet.NAME} {
					z-index: 1500;
				}

				${FleetSelection.NAME} {
					z-index: 2000;
				}

				${Itinerary.NAME} {
					z-index: 1300;
				}
				
				${Ranges.NAME} {
					z-index: 250;
				}
			</style>

			<div id="wrapper">
				<${StarNotification.NAME}></${StarNotification.NAME}>
				<${StarSelection.NAME} hidden></${StarSelection.NAME}>
				<${FleetSelection.NAME} hidden></${FleetSelection.NAME}>
				<${Itinerary.NAME} hidden></${Itinerary.NAME}>
				<${Ranges.NAME}></${Ranges.NAME}>
				<canvas id="background" is="${StarBackground.NAME}" width="0" height="0"></canvas>
			</div>`;

		this.#wrapperEl = this.shadowRoot.querySelector('#wrapper');

		this.#starNotificationEl = this.shadowRoot.querySelector(StarNotification.NAME);
		this.#starSelectionEl = this.shadowRoot.querySelector(StarSelection.NAME);
		this.#fleetSelectionEl = this.shadowRoot.querySelector(FleetSelection.NAME);
		this.#itineraryEl = this.shadowRoot.querySelector(Itinerary.NAME);
		this.#rangesEl = this.shadowRoot.querySelector(Ranges.NAME);
		this.#starBackgroundEl = this.shadowRoot.querySelector('#background');

		this.addEventListener('auxclick', (e) => {
			if (e.button === 1) {
				this.#scaleMap(!this.#isMinimized());
			}
		});

		this.addEventListener('click', (e) => {
			const clickedEls = e
				.composedPath()
				.filter((el) => el instanceof Star || el instanceof Fleet || el instanceof StarNotification);

			if (clickedEls.length === 0 && !this.#isMinimized()) {
				const boundingClientRect = this.getBoundingClientRect();

				const offsetX = e.clientX - boundingClientRect.left - this.clientWidth / 2;
				const offsetY = e.clientY - boundingClientRect.top - this.clientHeight / 2;

				if (this.#startScrollAction && this.#scrolling !== true) {
					this.#scrolling = true;

					HypermediaUtil.submitAction(this.#startScrollAction);

					this.addEventListener('scroll', this.#onScroll);
					// NOTE If for example clicked in the left top corner when already being most top and left does not trigger
					//      an scroll event at all... this makes sure that the debounceed scroll end is always started.
					this.#onScroll();
				}

				// NOTE As soon as `this.scrollBy({ left: offsetX, top: offsetY, behavior: 'smooth' })` is supported by all browsers this can be used.
				const distance = Math.sqrt(Math.pow(offsetX, 2) + Math.pow(offsetY, 2));
				smoothScroll({
					scrollingElement: this,
					xPos: this.scrollLeft + offsetX,
					yPos: this.scrollTop + offsetY,
					duration: (distance / StarMap.#SCROLL_SPEED_PIXEL_PER_SECOND) * 1000,
				});
			} else {
				const selectedFleetEl = clickedEls
					.filter((el) => el instanceof Fleet)
					.map((fleetEl: Fleet) => fleetEl)?.[0];
				if (selectedFleetEl) {
					const fleetsAtSameLocation = Array.from(
						this.shadowRoot.elementsFromPoint(e.clientX, e.clientY)
					).filter((el) => el instanceof Fleet);

					if (fleetsAtSameLocation.length > 1) {
						const fleetEls = this.#getFleetEls();

						const minFleetZIndex = Math.min(...fleetEls.map((fleetEl) => fleetEl.getZIndex()));
						this.#fleetZIndex = this.#fleetZIndex || minFleetZIndex;

						selectedFleetEl.style.zIndex = (minFleetZIndex - 1).toString();

						if (minFleetZIndex < this.#fleetZIndex - 100) {
							fleetEls.sort((a, b) => a.getZIndex() - b.getZIndex());
							for (let i = 0; i < fleetEls.length; i++) {
								fleetEls[i].style.zIndex = (this.#fleetZIndex + i).toString();
							}
						}
					}
				}
			}
		});

		this.#onScroll = debounce(async () => {
			await HypermediaUtil.submitAction(this.#reloadAction);

			this.#scrolling = false;
			this.removeEventListener('scroll', this.#onScroll);
			if (this.#endScrollAction) {
				HypermediaUtil.submitAction(this.#endScrollAction);
			}
		}, 5);
	}

	connectedCallback() {
		this.#resizeObserver = new ResizeObserver(
			debounce(() => {
				if (this.#isMinimized()) {
					this.#scaleMap(true);
				}
			}, 50)
		);
		this.#resizeObserver.observe(this);
	}

	#getFleetEls() {
		return Array.from(this.#wrapperEl.querySelectorAll(Fleet.NAME)).map((fleetEl: Fleet) => fleetEl);
	}

	#getStarEls() {
		return Array.from(this.#wrapperEl.querySelectorAll(Star.NAME)).map((starEl: Fleet) => starEl);
	}

	async render(data) {
		this.#reloadAction = HypermediaUtil.getAction(data, 'reload');
		this.#startScrollAction = HypermediaUtil.getAction(data, 'start-scroll');
		this.#endScrollAction = HypermediaUtil.getAction(data, 'end-scroll');

		Reconciler.reconcileChildren(this.#wrapperEl, this.#getStarEls(), data.stars, Star.NAME);
		Reconciler.reconcileChildren(this.#wrapperEl, this.#getFleetEls(), data.fleets, Fleet.NAME);

		this.#starSelectionEl.render(data.starSelection);
		this.#fleetSelectionEl.render(data.fleetSelection);
		this.#itineraryEl.render(data.itinerary);
		this.#rangesEl.render(data.ranges);

		Reconciler.reconcileProperty(this.#starBackgroundEl, 'width', data.starBackground.width);
		Reconciler.reconcileProperty(this.#starBackgroundEl, 'height', data.starBackground.height);

		this.#scaleMap(data.miniMap);

		if (data.scrollTo) {
			if (data.scrollTo.center || !this.getStarMapViewport().contains(data.scrollTo)) {
				this.scrollTo({
					left: data.scrollTo.x - this.clientWidth / 2,
					top: data.scrollTo.y - this.clientHeight / 2,
				});
			}
		}
		this.#starNotificationEl.render(data.starNotification);

		// we just have to wait for the CSS animations of the fleets to finish
		if (data.fleetMovements) {
			return sleep(1200);
		}
	}

	#scaleMap(miniMap) {
		let transform = 'none';
		if (miniMap) {
			const scale = Math.min(this.clientWidth / this.scrollWidth, this.clientHeight / this.scrollHeight);

			if (scale < 1) {
				const translateX = -1 * ((this.scrollWidth - this.clientWidth) / 2) + this.scrollLeft;
				const translateY = -1 * ((this.scrollHeight - this.clientHeight) / 2) + this.scrollTop;

				transform = `translate(${translateX}px, ${translateY}px) scale(min(${scale}, 1))`;
			}
		}
		Reconciler.reconcileStyle(this.#wrapperEl, 'transform', transform);
	}

	getStarMapViewport() {
		return new Viewport(
			Math.round(this.scrollLeft - Math.max(Star.WIDTH, Fleet.WIDTH) / 2),
			Math.round(this.scrollLeft + this.clientWidth + Math.max(Star.WIDTH, Fleet.WIDTH) / 2),
			Math.round(this.scrollTop - Math.max(Star.HEIGHT, Fleet.HEIGHT) / 2),
			Math.round(this.scrollTop + this.clientHeight + Math.max(Star.HEIGHT, Fleet.HEIGHT) / 2)
		);
	}

	#isMinimized() {
		return this.#wrapperEl.style.transform !== 'none';
	}

	disconnectedCallback() {
		this.#resizeObserver.disconnect();
	}
}

customElements.define(StarMap.NAME, StarMap);
