import SeededGenerator from '~/util/seeded-generator';

export default class StarBackground extends HTMLCanvasElement {
	static NAME = 're-star-background';

	static get observedAttributes() {
		return ['width', 'height', 'animated'];
	}

	#visible = false;

	#initialized = { width: -1, height: -1, animated: undefined };

	#animated = false;
	#previousTimestamp: number;
	#offsetX = 0;
	#animationDirection = -1;

	#observer: IntersectionObserver;
	#offscreenCanvas: HTMLCanvasElement;

	constructor() {
		super();
	}

	connectedCallback() {
		this.#animated = this.hasAttribute('animated');

		if (!this.#observer) {
			this.#observer = new IntersectionObserver(
				(entries, observer) => {
					entries.forEach((entry) => {
						if ((this.#visible = entry.intersectionRatio == 1)) {
							if (this.#animated) {
								requestAnimationFrame(this.#animate.bind(this));
							} else if (!this.#isInitialized()) {
								this.#draw();
							}
						}
					});
				},
				{ threshold: 1 }
			);
			this.#observer.observe(this);
		}
	}

	disconnectedCallback() {
		this.#visible = false;

		this.#observer.disconnect();
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if (oldValue === newValue) {
			return;
		} else if (name === 'width' || name === 'height') {
			if (this.isConnected && !this.#isInitialized()) {
				this.#draw();
			}
		} else if (name === 'animated') {
			this.#animated = newValue !== null;

			if (this.#animated && oldValue === null && this.#visible) {
				requestAnimationFrame(this.animate.bind(this));
			} else if (!this.#animated && oldValue !== null && newValue === null) {
				this.#initialized = { width: -1, height: -1, animated: undefined };
				this.#offsetX = 0;

				this.#draw();
			}
		}
	}

	#isInitialized() {
		return (
			this.#initialized.width === this.width &&
			this.#initialized.height === this.height &&
			this.#initialized.animated === this.#animated
		);
	}

	#animate(timestamp) {
		if (this.#previousTimestamp) {
			const deltaT = timestamp - this.#previousTimestamp;
			const previousOffsetX = this.#offsetX;

			// move with 20 pixel/sec
			this.#offsetX += deltaT * (20 / 1000);

			if (this.width > 0 && Math.trunc(this.#offsetX) - Math.trunc(previousOffsetX) > 0) {
				this.#draw();

				// wrap around offsetX as well to not let it grow indefinitely
				if (this.#offsetX > this.width + 1) {
					this.#offsetX = this.#offsetX % this.width;
				}
			}
		}

		this.#previousTimestamp = timestamp;

		if (this.#visible && this.#animated) {
			requestAnimationFrame(this.#animate.bind(this));
		}
	}

	#draw() {
		// skip rendering when size not set
		if (this.width === 0 || this.height === 0) {
			return;
		}

		if (!this.#isInitialized()) {
			this.#initialized = { width: this.width, height: this.height, animated: this.#animated };

			if (this.#animated) {
				if (!this.#offscreenCanvas) {
					this.#offscreenCanvas = document.createElement('canvas');
				}
				this.#offscreenCanvas.width = this.width;
				this.#offscreenCanvas.height = this.height;

				StarBackground.#drawStars(this.#offscreenCanvas);
			} else {
				this.#offscreenCanvas = null;
				StarBackground.#drawStars(this);
			}
		}

		if (this.#animated) {
			const ctx = this.getContext('2d', { alpha: false });

			const offsetX =
				(this.#animationDirection > 0 ? 0 : this.width) +
				this.#animationDirection * (Math.trunc(this.#offsetX) % this.width);

			ctx.drawImage(this.#offscreenCanvas, offsetX, 0);
			ctx.drawImage(this.#offscreenCanvas, offsetX - this.width, 0);
		}
	}

	static #drawStars(canvas: HTMLCanvasElement) {
		const max = canvas.width * canvas.height * 0.00035 + 10;

		const ctx = canvas.getContext('2d', { alpha: false });

		ctx.fillStyle = 'black';
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		const generator = new SeededGenerator('star-background');

		for (let j = 0; j < 4; j++) {
			const color = 60 + j * 30;
			ctx.fillStyle = `rgb(${color}, ${color}, ${color})`;

			for (let i = 0; i < max; i++) {
				const x = generator.random() * canvas.width;
				const y = generator.random() * canvas.height;

				const starScale = generator.random();

				ctx.fillRect(x, y, 0.5 + starScale * 2, 0.5 + starScale * 2);
			}
		}
	}
}

customElements.define(StarBackground.NAME, StarBackground, { extends: 'canvas' });
