import Action from '~/util/action';

export default abstract class SubmitInterceptor {
	/**
	 * Allows to abort the HTTP request by returning a promise instead of undefined.
	 */
	preHandle(action: Action, values): Promise<any> | undefined {
		return undefined;
	}

	/**
	 * Allows to modify the result of the HTTP request if the HTTP request was not 
	 * aborted by this interceptor (all previous interceptors are post-handled).
	 */
	postHandle(data): any {
		return data;
	}
}
