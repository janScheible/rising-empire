import Position from '~/util/position';

export default class FleetLocationUtil {
	static withOffsetCentered(
		x: number,
		y: number,
		size: { width: number; height: number },
		orbiting: boolean,
		justLeaving: boolean
	): Position {
		const { x: xWithOffset, y: yWithOffset } = FleetLocationUtil.withOffset(x, y, orbiting, justLeaving);
		return {
			x: xWithOffset - size.width / 2,
			y: yWithOffset - size.height / 2,
		};
	}

	static withOffset(x: number, y: number, orbiting: boolean, justLeaving: boolean): Position {
		return {
			x: x + (orbiting ? 36 : justLeaving ? -14 : 0),
			y: y + (orbiting ? -18 : justLeaving ? -18 : 0),
		};
	}
}
