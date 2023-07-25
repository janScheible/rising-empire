import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import Ships from '~/component/ships';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class ShipSpecs extends HTMLElement {
	static NAME = 're-ship-specs';

	#shipsEl: Ships;
	#nameEl: HTMLDivElement;
	#shieldEl: HTMLDivElement;
	#beamDefenceEl: HTMLDivElement;
	#attackLevelEl: HTMLDivElement;
	#damageOrWarpLabelEl: HTMLDivElement;
	#damageOrWarpEl: HTMLDivElement;
	#missleDefenceEl: HTMLDivElement;
	#hitsEl: HTMLDivElement;
	#speedEl: HTMLDivElement;
	#equipmentListEl: FlowLayout;
	#equipmentEl: FlowLayout;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#ship-type-container {
					background-color: black !important;
				}
			</style>
			<${GridLayout.NAME} cols="min-content 1fr 1fr 1fr" border>
				<${FlowLayout.NAME} id="ship-type-container" data-row-span="4" data-no-padding direction="column" axis-align="center">
					<${Ships.NAME} id="ships"></${Ships.NAME}>
				</${FlowLayout.NAME}>
				
				<${FlowLayout.NAME} id="name" data-col-span="2" cross-axis-align="center"></${FlowLayout.NAME}>

				<${FlowLayout.NAME} gap="XL" cross-axis-align="center">
					<div data-flow-size="1fr">Shield</div>
					<div id="shield"></div>
				</${FlowLayout.NAME}>
				<${FlowLayout.NAME} gap="XL" cross-axis-align="center">
					<div data-flow-size="1fr">Beam Def</div>
					<div id="beam-defence"></div>
				</${FlowLayout.NAME}>
				<${FlowLayout.NAME} gap="XL" cross-axis-align="center">
					<div data-flow-size="1fr">Attack Lvl</div>
					<div id="attack-level"></div>
				</${FlowLayout.NAME}>
				<${FlowLayout.NAME} gap="XL" cross-axis-align="center">
					<div id="damage-or-warp-label" data-flow-size="1fr">-</div>
					<div id="damage-or-warp"></div>
				</${FlowLayout.NAME}>
				<${FlowLayout.NAME} gap="XL" cross-axis-align="center">
					<div data-flow-size="1fr">Misl Def</div>
					<div id="missle-defence"></div>
				</${FlowLayout.NAME}>
				<${FlowLayout.NAME} gap="XL" cross-axis-align="center">
					<div data-flow-size="1fr">Hits</div>
					<div id="hits"></div>
				</${FlowLayout.NAME}>
				<${FlowLayout.NAME} gap="XL" cross-axis-align="center">
					<div data-flow-size="1fr">Speed</div>
					<div id="speed"></div>
				</${FlowLayout.NAME}>

				<${FlowLayout.NAME} id="equipment-list" data-col-span="3" axis-align="start">
					<${FlowLayout.NAME} id="equipment" direction="column" gap="S">
					</${FlowLayout.NAME}>
				</${FlowLayout.NAME}>
			</${GridLayout.NAME}>`;

		this.#shipsEl = this.shadowRoot.querySelector('#ships');
		this.#nameEl = this.shadowRoot.querySelector('#name');
		this.#shieldEl = this.shadowRoot.querySelector('#shield');
		this.#beamDefenceEl = this.shadowRoot.querySelector('#beam-defence');
		this.#attackLevelEl = this.shadowRoot.querySelector('#attack-level');
		this.#damageOrWarpLabelEl = this.shadowRoot.querySelector('#damage-or-warp-label');
		this.#damageOrWarpEl = this.shadowRoot.querySelector('#damage-or-warp');
		this.#missleDefenceEl = this.shadowRoot.querySelector('#missle-defence');
		this.#hitsEl = this.shadowRoot.querySelector('#hits');
		this.#speedEl = this.shadowRoot.querySelector('#speed');
		this.#equipmentListEl = this.shadowRoot.querySelector('#equipment-list');
		this.#equipmentEl = this.shadowRoot.querySelector('#equipment');
	}

	async render(data) {
		const shipsAnimation = this.#shipsEl.render(data.ships);

		Reconciler.reconcileProperty(this.#nameEl, 'innerText', data.name);

		ShipSpecs.#reconcileInnerTextIfDefined(this.#shieldEl, data.shield);
		ShipSpecs.#reconcileInnerTextIfDefined(this.#beamDefenceEl, data.beamDefence);
		ShipSpecs.#reconcileInnerTextIfDefined(this.#attackLevelEl, data.attackLevel);
		Reconciler.reconcileProperty(
			this.#damageOrWarpLabelEl,
			'innerText',
			data.warp !== null && data.warp !== undefined ? 'Warp' : 'Dam'
		);
		ShipSpecs.#reconcileInnerTextIfDefined(this.#damageOrWarpEl, data.warp ?? data.damage);
		ShipSpecs.#reconcileInnerTextIfDefined(this.#missleDefenceEl, data.missleDefence);
		ShipSpecs.#reconcileInnerTextIfDefined(this.#hitsEl, data.hits);
		ShipSpecs.#reconcileInnerTextIfDefined(this.#speedEl, data.speed);

		if (data.equipment && data.equipment.length > 0) {
			Reconciler.reconcileAttribute(this.#equipmentListEl, 'axis-align', 'start');
			Reconciler.reconcileChildren(this.#equipmentEl, this.#equipmentEl.children, data.equipment, 'div', {
				idAttributName: 'data-equipment',
				renderCallbackFn: (el, data) => Reconciler.reconcileProperty(el, 'innerText', ' - ' + data),
				idValueFn: (data) => data,
			});
		} else {
			Reconciler.reconcileAttribute(this.#equipmentListEl, 'axis-align', 'center');
			Reconciler.reconcileProperty(this.#equipmentEl, 'innerText', data.equipment ? 'No equipment' : '?');
		}

		return shipsAnimation;
	}

	static #reconcileInnerTextIfDefined(el, value) {
		Reconciler.reconcileProperty(
			el,
			'innerText',
			value !== undefined ? value : '?',
			(previous) => previous !== value
		);
	}
}

customElements.define(ShipSpecs.NAME, ShipSpecs);
