// Type definitions for stomp.js
// Custom type definition, not complete.

interface Stomp {
    over(socketType: any): Client;
}

interface StompMessage {
    body: string;
}

interface HeartBeatConfig {
    incoming: number;
    outgoing: number;
}

interface Client {
    connect(headers: any, onConnect: () => any);
    subscribe(url: string, handler: (message: StompMessage) => any);
	send(destination: string, headers: any, body: string);
    heartbeat: HeartBeatConfig;
}

declare var Stomp: Stomp;