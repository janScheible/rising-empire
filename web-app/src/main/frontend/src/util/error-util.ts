export default class ErrorUtil {
	static registerGlobalErrorListener(errorsUri?) {
		window.addEventListener('error', (e) => {
			ErrorUtil.#reportError(e.error, errorsUri);
		});

		window.addEventListener('unhandledrejection', (e) => {
			ErrorUtil.#reportError(e.reason, errorsUri);
		});
	}

	static #reportError(error, errorsUri?) {
		const message = error.message;
		const fileName = error.fileName;
		const lineNumber = error.lineNumber;
		const columnNumber = error.columnNumber;
		const stack = error.stack;

		if (errorsUri) {
			fetch(errorsUri, {
				method: 'POST',
				headers: {
					Accept: 'application/json',
					'Content-Type': 'application/json',
				},
				body: JSON.stringify({
					message,
					fileName,
					lineNumber,
					columnNumber,
					stack,
				}),
			}).catch((error) => {
				// silently ignore network error when reporting frontend errors to the backend
			});
		}

		alert(`A frontend error occured, please reload the page.`);
	}
}
