interface ReconciliationOptions {
	renderCallbackFn?: (el: HTMLElement | SVGElement, data: any) => void;
	idAttributName?: string;
	idValueFn?: (data: any) => string;
	insertionMode?: 'APPEND' | 'PREPEND';
	afterCreateCallbackFn?: (el: HTMLElement | SVGElement, data: any) => void;
	beforeDeleteCallbackFn?: (el: HTMLElement | SVGElement) => void;
}

export default ReconciliationOptions;
