/**
 * @param file Similar to the JavaScript bare import specifier file must be a CSS bare identifier that starts with '~/'.
 * @param callerModuleUrl `import.meta.url` of the calling module.
 */
export default function cssUrl(file: string, callerModuleUrl: string) {
	if (!file.startsWith('~/')) {
		throw new Error(`CSS URLs must start with '~/' but '${file}' does not!`);
	}

	const rev = new URL(callerModuleUrl).searchParams.get('rev');
	const thisModuleUrl = import.meta.url;
	// go up the 'util' directory to end up in the root of the frontend
	const rootRelativeFile = '..' + file.substring(1);
	return "url('" + new URL(rootRelativeFile, thisModuleUrl).href + '?rev=' + encodeURIComponent(rev) + "')";
}
