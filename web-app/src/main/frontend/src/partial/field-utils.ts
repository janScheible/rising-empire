import Viewport from '~/partial/viewport';

export default class FieldUtils {
	static #JSON_PATH_VIEWPORT_PATTERN =
		/\$\.starMap\.(?<type>\w+)\[\?\(@\.x>(?<left>\d+)&&@\.x<(?<right>\d+)&&@\.y>(?<top>\d+)&&@\.y<(?<bottom>\d+)\)\]/;
	static #ALL_WHITESPACE_PATTERN = /\s/g;

	static shouldBeRendered(fields, prefix) {
		return fields.filter((field) => field.startsWith(prefix)).length !== 0;
	}

	static getStarMapViewport(fields, type) {
		if (!fields) {
			return;
		} else {
			const [viewport] = fields
				.map((f) => f.replace(FieldUtils.#ALL_WHITESPACE_PATTERN, ''))
				.map((f) => FieldUtils.#JSON_PATH_VIEWPORT_PATTERN.exec(f))
				.filter((f) => f !== null && f.groups.type === type)
				.map((bb) => new Viewport(bb.groups.left, bb.groups.right, bb.groups.top, bb.groups.bottom));
			return viewport;
		}
	}

	static toStarMapFieldsViewport(viewport, type) {
		return (
			`$.starMap.${type}[?(@.x > ${Math.max(0, viewport.left)} && @.x < ${viewport.right} ` +
			`&& @.y > ${Math.max(0, viewport.top)} && @.y < ${viewport.bottom})]`
		);
	}
}
