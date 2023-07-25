export default function debounce(func, wait) {
	let timeout = null;

	return (...args) => {
		const next = () => func(...args);
		clearTimeout(timeout);
		timeout = setTimeout(next, wait);
	};
}
