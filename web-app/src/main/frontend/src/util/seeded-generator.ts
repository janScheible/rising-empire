/**
 * Seeded pseudo-random number generator based on https://stackoverflow.com/a/47593316.
 */
export default class SeededGenerator {
	#sfc32Instance;

	constructor(seedString) {
		const seed = SeededGenerator.#xmur3(seedString);
		this.#sfc32Instance = SeededGenerator.#sfc32(seed(), seed(), seed(), seed());
	}

	random() {
		return this.#sfc32Instance();
	}

	static #xmur3(str) {
		for (var i = 0, h = 1779033703 ^ str.length; i < str.length; i++)
			(h = Math.imul(h ^ str.charCodeAt(i), 3432918353)), (h = (h << 13) | (h >>> 19));
		return function () {
			h = Math.imul(h ^ (h >>> 16), 2246822507);
			h = Math.imul(h ^ (h >>> 13), 3266489909);
			return (h ^= h >>> 16) >>> 0;
		};
	}

	static #sfc32(a, b, c, d) {
		return function () {
			a >>>= 0;
			b >>>= 0;
			c >>>= 0;
			d >>>= 0;
			var t = (a + b) | 0;
			a = b ^ (b >>> 9);
			b = (c + (c << 3)) | 0;
			c = (c << 21) | (c >>> 11);
			d = (d + 1) | 0;
			t = (t + d) | 0;
			c = (c + t) | 0;
			return (t >>> 0) / 4294967296;
		};
	}
}
