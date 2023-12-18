import Action from '~/util/action';

export default abstract class SubmitInterceptor {
	/**
	 * Allows to abort the HTTP request by returning a value that is not undefined.
	 */
	preHandle(action: Action, values): Promise<any> | undefined {
		// must be Promise<any> for the future web socket use case!!!!
		return undefined;
	}

	/**
	 * Allows to modify the result of the HTTP request if the HTTP request was not aborted.
	 */
	postHandle(data): any {
		return data;
	}
}
