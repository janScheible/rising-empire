export default class Viewport {
	#left: number;
	#right: number;
	#top: number;
	#bottom: number;

	constructor(left: number, right: number, top: number, bottom: number) {
		this.#left = left;
		this.#right = right;
		this.#top = top;
		this.#bottom = bottom;
	}

	contains(el: HTMLElement | { x: number; y: number }) {
		const x = el instanceof HTMLElement ? el.offsetLeft + el.clientWidth / 2 : el.x;
		const y = el instanceof HTMLElement ? el.offsetTop + el.clientHeight / 2 : el.y;

		return x >= this.#left && x <= this.#right && y >= this.#top && y <= this.#bottom;
	}

	intersects(circleX: number, circleY: number, radius: number) {
		// temporary variables to set edges for testing
		let testX = circleX;
		let testY = circleY;

		// which edge is closest?
		if (circleX < this.#left) {
			// test left edge
			testX = this.#left;
		} else if (circleX > this.#right) {
			// right edge
			testX = this.#right;
		}

		if (circleY < this.#top) {
			// top edge
			testY = this.#top;
		} else if (circleY > this.#bottom) {
			// bottom edge
			testY = this.#bottom;
		}

		// get distance from closest edges
		const distX = circleX - testX;
		const distY = circleY - testY;

		const distance = Math.sqrt(distX * distX + distY * distY);

		// if the distance is less than the radius, collision!
		return distance <= radius;
	}

	get left() {
		return this.#left;
	}

	get right() {
		return this.#right;
	}

	get top() {
		return this.#top;
	}

	get bottom() {
		return this.#bottom;
	}
}
