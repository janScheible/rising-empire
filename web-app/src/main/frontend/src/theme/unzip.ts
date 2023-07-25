import * as fflate from '~/fflate-0.7.4';

export default async function unzip(fileContent: Uint8Array): Promise<{ [path: string]: Uint8Array }> {
	return new Promise((resolve, reject) => {
		fflate.unzip(fileContent, (error, data) => {
			if (error) {
				reject(error);
			} else {
				resolve(data);
			}
		});
	});
}
