declare function smoothScroll(options: {
	scrollingElement: HTMLElement;
	xPos: number;
	yPos: number;
	duration: number;
	complete?: () => void;
}): void;
