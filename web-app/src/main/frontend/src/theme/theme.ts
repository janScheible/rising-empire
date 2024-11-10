import { get, set, del } from '~/idb-keyval-6.2.1';
import Logger from '~/util/logger/logger';
import LoggerFactory from '~/util/logger/logger-factory';
import unzip from '~/theme/unzip';
import extractSprites from '~/theme/extract-sprites';

export default class Theme {
	static #QUALIFIERS = [];

	static #SHIPS_SPRITE_SHEET = {
		sheetWidth: 597,
		sheetHeight: 303,
		spriteWidth: 96,
		spriteHeight: 72,
		margin: 3,
		targetSpriteWidth: 102,
		targetSpriteHeight: 72,
	};

	static #SPRITE_SHEETS = {
		stars: {
			sprites: [
				{ qualifier: 'star-yellow', x: 0, y: 0 },
				{ qualifier: 'star-red', x: 1, y: 0 },
				{ qualifier: 'star-green', x: 2, y: 0 },
				{ qualifier: 'star-blue', x: 3, y: 0 },
				{ qualifier: 'star-white', x: 4, y: 0 },
				{ qualifier: 'star-purple', x: 5, y: 0 },
			],
			sheetWidth: 255,
			sheetHeight: 39,
			spriteWidth: 39,
			spriteHeight: 33,
			margin: 3,
			targetSpriteWidth: 33,
			targetSpriteHeight: 33,
		},
		'stars-small': {
			sprites: [
				{ qualifier: 'star-yellow-small', x: 0, y: 0 },
				{ qualifier: 'star-red-small', x: 1, y: 0 },
				{ qualifier: 'star-green-small', x: 2, y: 0 },
				{ qualifier: 'star-blue-small', x: 3, y: 0 },
				{ qualifier: 'star-white-small', x: 4, y: 0 },
				{ qualifier: 'star-purple-small', x: 5, y: 0 },
			],
			sheetWidth: 147,
			sheetHeight: 27,
			spriteWidth: 21,
			spriteHeight: 21,
			margin: 3,
		},
		fleets: {
			sprites: [
				{ qualifier: 'fleet-blue', x: 0, y: 0 },
				{ qualifier: 'fleet-green', x: 1, y: 0 },
				{ qualifier: 'fleet-purple', x: 2, y: 0 },
				{ qualifier: 'fleet-red', x: 3, y: 0 },
				{ qualifier: 'fleet-white', x: 4, y: 0 },
				{ qualifier: 'fleet-yellow', x: 5, y: 0 },
			],
			sheetWidth: 147,
			sheetHeight: 15,
			spriteWidth: 21,
			spriteHeight: 9,
			margin: 3,
		},
		planets: {
			sprites: [
				{ qualifier: 'planet-radiated', x: 5, y: 4 },
				{ qualifier: 'planet-toxic', x: 5, y: 0 },
				{ qualifier: 'planet-inferno', x: 5, y: 1 },
				{ qualifier: 'planet-dead', x: 6, y: 0 },
				{ qualifier: 'planet-tundra', x: 3, y: 4 },
				{ qualifier: 'planet-barren', x: 4, y: 3 },
				{ qualifier: 'planet-minimal', x: 3, y: 3 },
				{ qualifier: 'planet-desert', x: 0, y: 4 },
				{ qualifier: 'planet-steppe', x: 0, y: 2 },
				{ qualifier: 'planet-arid', x: 6, y: 2 },
				{ qualifier: 'planet-ocean', x: 5, y: 3 },
				{ qualifier: 'planet-jungle', x: 0, y: 3 },
				{ qualifier: 'planet-terran', x: 0, y: 0 },
			],
			sheetWidth: 696,
			sheetHeight: 378,
			spriteWidth: 96,
			spriteHeight: 72,
			margin: 3,
			targetSpriteWidth: 93,
			targetSpriteHeight: 69,
		},
		'ships-blue': {
			sprites: [
				{ qualifier: 'ship-blue-small', x: 0, y: 0 },
				{ qualifier: 'ship-blue-medium', x: 1, y: 1 },
				{ qualifier: 'ship-blue-large', x: 2, y: 2 },
				{ qualifier: 'ship-blue-huge', x: 3, y: 3 },
			],
			...Theme.#SHIPS_SPRITE_SHEET,
		},
		'ships-white': {
			sprites: [
				{ qualifier: 'ship-white-small', x: 0, y: 0 },
				{ qualifier: 'ship-white-medium', x: 1, y: 1 },
				{ qualifier: 'ship-white-large', x: 2, y: 2 },
				{ qualifier: 'ship-white-huge', x: 3, y: 3 },
			],
			...Theme.#SHIPS_SPRITE_SHEET,
		},
		'ships-yellow': {
			sprites: [
				{ qualifier: 'ship-yellow-small', x: 0, y: 0 },
				{ qualifier: 'ship-yellow-medium', x: 1, y: 1 },
				{ qualifier: 'ship-yellow-large', x: 2, y: 2 },
				{ qualifier: 'ship-yellow-huge', x: 3, y: 3 },
			],
			...Theme.#SHIPS_SPRITE_SHEET,
		},
	};

	static #logger: Logger = LoggerFactory.get(`${import.meta.url}`);

	static #elements: { [id: string]: string } = {};

	static #enabled: boolean = false;

	static async apply() {
		const qualifiers = Theme.#allQualifiers();

		const imageBlobResults: PromiseSettledResult<Blob>[] = await Promise.allSettled(
			qualifiers.map((qualifier) => get(qualifier))
		);

		for (let i = 0; i < qualifiers.length; i++) {
			const qualifier = qualifiers[i];
			const result = imageBlobResults[i];

			if (result.status === 'rejected') {
				Theme.#logger.warn(`Preloading of ${qualifier} failed: ${result.reason}`);
			} else if (!result.value) {
				Theme.#logger.warn(`Preloading of ${qualifier} had no result.`);
			} else {
				Theme.#elements[qualifier] = URL.createObjectURL(result.value);
			}
		}

		Theme.#enabled = true;
	}

	static async load(fileContent: Uint8Array) {
		const unzipped = await unzip(fileContent);

		const idbSets = [];
		for (const fileEntry of Object.entries(unzipped)) {
			const fileName = fileEntry[0];
			const qualifier = fileName.substring(0, fileName.lastIndexOf('.'));

			if (fileName.toLowerCase().endsWith('.txt')) {
				const text = new TextDecoder().decode(fileEntry[1] as Uint8Array);
				console.log(text); // TODO store the values somewhere
			} else if (Theme.#QUALIFIERS.indexOf(qualifier) >= 0) {
				const blob = new Blob([fileEntry[1] as Uint8Array], { type: 'image/png' });
				Theme.#elements[qualifier] = URL.createObjectURL(blob);

				idbSets.push(set(qualifier, blob));
			} else if (Object.keys(Theme.#SPRITE_SHEETS).indexOf(qualifier) >= 0) {
				const spriteSheet = Theme.#SPRITE_SHEETS[qualifier];
				const spriteBlobs = await extractSprites(
					fileEntry[1] as Uint8Array,
					spriteSheet.sheetWidth,
					spriteSheet.sheetHeight,
					spriteSheet.spriteWidth,
					spriteSheet.spriteHeight,
					spriteSheet.margin,
					spriteSheet.targetSpriteWidth,
					spriteSheet.targetSpriteHeight
				);

				for (const sprite of spriteSheet.sprites) {
					const spriteBlob = spriteBlobs[Theme.#getSpriteIndex(spriteSheet, sprite.x, sprite.y)];
					Theme.#elements[sprite.qualifier] = URL.createObjectURL(spriteBlob);

					idbSets.push(set(sprite.qualifier, spriteBlob));
				}
			}
		}

		await Promise.allSettled(idbSets);
	}

	static #getSpriteIndex(spriteSheet, x: number, y: number): number {
		const width = (spriteSheet.sheetWidth - spriteSheet.margin) / (spriteSheet.spriteWidth + spriteSheet.margin);
		return x + y * width;
	}

	static isEmpty(): boolean {
		return Object.keys(Theme.#elements).length === 0;
	}

	static getDataUrl(qualifier: string): string {
		return Theme.#enabled ? Theme.#elements[qualifier] : undefined;
	}

	static set enabled(enabled: boolean) {
		Theme.#enabled = enabled;
	}

	static get enabled() {
		return Theme.#enabled;
	}

	static toggle(): boolean {
		return (Theme.#enabled = !Theme.#enabled);
	}

	static async clear() {
		Theme.#enabled = false;
		Theme.#elements = {};
		await Promise.allSettled(Theme.#allQualifiers().map((qualifier) => del(qualifier)));
	}

	static #allQualifiers() {
		const spriteSheetQualifiers = Object.values(Theme.#SPRITE_SHEETS)
			.flatMap((spriteSheet) => spriteSheet.sprites)
			.map((sprite) => sprite.qualifier);
		return [...Theme.#QUALIFIERS, ...spriteSheetQualifiers];
	}
}
