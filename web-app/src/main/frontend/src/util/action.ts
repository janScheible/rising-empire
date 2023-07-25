interface Action {
	//
	// from server-side
	//

	name: string;
	href: string;
	method: 'GET' | 'POST';
	fields: { name: string; value: any }[];
	contentType?: string;

	//
	// from client-side
	//
	synthetic?: boolean;
}

export default Action;
