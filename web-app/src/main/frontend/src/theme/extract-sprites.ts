export default async function extractSprites(
	sheetArray: Uint8Array,
	sheetWidth: number,
	sheetHeight: number,
	spriteWidth: number,
	spriteHeight: number,
	margin: number,
	targetSpriteWidth = spriteWidth,
	targetSpriteHeight = spriteHeight
): Promise<Blob[]> {
	const sheetBlob = new Blob([sheetArray], { type: 'image/png' });
	const sheetImage = await createImageBitmap(sheetBlob);

	const sourceCanvas = new OffscreenCanvas(sheetImage.width, sheetImage.height);
	const sourceContext = sourceCanvas.getContext('2d');
	sourceContext.drawImage(sheetImage, 0, 0);

	const targetCanvas = new OffscreenCanvas(targetSpriteWidth, targetSpriteHeight);
	const targetContext = targetCanvas.getContext('2d');

	return Promise.all(
		Array.from(spriteIterator(sheetWidth, sheetHeight, spriteWidth, spriteHeight, margin))
			.map((spritePos) => sourceContext.getImageData(spritePos.x, spritePos.y, spriteWidth, spriteHeight))
			.map((sprite) => putCentered(targetContext, sprite).convertToBlob())
	);
}

function putCentered(targetContext: OffscreenCanvasRenderingContext2D, sprite: ImageData): OffscreenCanvas {
	const targetCanvas = targetContext.canvas;

	targetContext.clearRect(0, 0, targetCanvas.width, targetCanvas.height);

	const dx = (targetCanvas.width - sprite.width) / 2;
	const dy = (targetCanvas.height - sprite.height) / 2;
	targetContext.putImageData(sprite, dx, dy);

	return targetCanvas;
}

function* spriteIterator(
	sheetWidth: number,
	sheetHeight: number,
	spriteWidth: number,
	spriteHeight: number,
	margin: number
): Generator<{ x: number; y: number }> {
	const xMax = (sheetWidth - margin) / (spriteWidth + margin);
	const yMax = (sheetHeight - margin) / (spriteHeight + margin);

	for (let y = 0; y < yMax; y++) {
		for (let x = 0; x < xMax; x++) {
			yield { x: margin + (margin + spriteWidth) * x, y: margin + (margin + spriteHeight) * y };
		}
	}
}
