export default function capitalize(text: string): string {
	return text.replace(/(^\w{1})|(\s+\w{1})/g, (letter) => letter.toUpperCase());
}
