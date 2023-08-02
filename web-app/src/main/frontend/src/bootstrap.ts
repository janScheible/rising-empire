import Frontend from '~/frontend';
import HypermediaUtil from '~/util/hypermedia-util';
import PartialUpdater from '~/partial/partial-updater';
import FetchUtil from '~/util/fetch-util';
import PartialUpdaterDelegate from '~/partial/partial-updater-delegate';
import Reconciler from '~/util/reconciler';
import ErrorUtil from '~/util/error-util';
import LoggerFactory from '~/util/logger/logger-factory';
import Logger from '~/util/logger/logger';
import Theme from '~/theme/theme';
import Sockette from '~/sockette-2.0.6';

ErrorUtil.registerGlobalErrorListener(document.body.dataset.errorsUri);

const logger: Logger = LoggerFactory.get(`${import.meta.url}`);

LoggerFactory.configure('WARN', {
	[logger.getName()]: 'INFO',
});

document.addEventListener('keydown', async (event) => {
	if (event.ctrlKey && event.altKey && event.key === 'r') {
		logger.info(Reconciler.toggleDebugLog() ? 'activated reconciliation log' : 'deactivated reconciliation log');
	} else if (event.ctrlKey && event.altKey && event.key === 'p') {
		logger.info(partialUpdater.toggle() ? 'activated partial updates' : 'deactivated partial updates');
		init();
	} else if (event.ctrlKey && event.altKey && event.key === 'h') {
		logger.info(
			HypermediaUtil.toggleDebugLog() ? 'activated hypermedia action log' : 'deactivated hypermedia action log'
		);
	} else if (event.ctrlKey && event.altKey && event.key === 't') {
		logger.info(Theme.toggle() ? 'activated theme' : 'deactivated theme');
	} else if (event.ctrlKey && event.altKey && event.key === 'c') {
		await Theme.clear();
		frontendEl.forceTheme();
		logger.info('cleared theme');
	}
});

const frontendEl = new Frontend();
document.body.appendChild(frontendEl);

const partialUpdater = new PartialUpdaterDelegate(
	new PartialUpdater(
		() => frontendEl.getStarMapViewport(),
		(data) => render(data)
	)
);

async function render(data) {
	await frontendEl.render(partialUpdater.beforeRender(data));
	frontendEl.loadIndicator(false);

	if (data.fleetMovements) {
		frontendEl.beginNewTurn();
	}
}

HypermediaUtil.addSubmitInterceptor((action, values) => partialUpdater.interceptSubmit(action, values));
HypermediaUtil.addSubmitInterceptor((action, values) => (frontendEl.loadIndicator(true), true));
HypermediaUtil.setActionResponseCallbackFn((data) => render(data));

const sessionId = sessionStorage.getItem('sessionId') ?? crypto.randomUUID();
sessionStorage.setItem('sessionId', sessionId);

const notificationWebSocketUri = new URL(document.body.dataset.notificationUri, window.location.href);
notificationWebSocketUri.searchParams.append('sessionId', sessionId);
notificationWebSocketUri.protocol = notificationWebSocketUri.protocol.replace(/^https/, 'wss').replace(/^http/, 'ws');

const notificationWebSocket = new Sockette(notificationWebSocketUri.toString(), {
	onmessage: async (event) => {
		const data = JSON.parse(event.data);

		if (data.type === 'turn-finished') {
			frontendEl.fleetMovements();
		} else if (data.type === 'turn-finish-status') {
			frontendEl.updateTurnStatus(data.playerStatus);
		} else if (data.type === 'player-available') {
			init();
		} else if (
			data.type === 'player-already-taken' ||
			data.type === 'player-kicked' ||
			data.type === 'game-stopped'
		) {
			notificationWebSocket.close();
			frontendEl.showPlayerError(
				data.type === 'player-already-taken'
					? 'already-taken'
					: data.type === 'player-kicked'
					? 'kicked'
					: 'game-stopped'
			);
		}
	},
	onopen: (event) => frontendEl.showConnected(true),
	onerror: (event) => frontendEl.showConnected(false),
	onclose: (event) => frontendEl.showConnected(false),
});

async function init() {
	const data = await FetchUtil.jsonFetch(document.body.dataset.frontendInitUri);
	render(data);
}
