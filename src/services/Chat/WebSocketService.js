import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
    constructor() {
        this.client = null;
    }


    connect = (onMessageReceived) => {
        this.client = new Client({
            brokerURL: "ws://localhost:8001/chat",
            connectHeaders: {},
            debug: function (str) {
                console.log(str);
            },
            onConnect: () => {
                console.log('WebSocket Connected');
    
                // Subscribe to `/topic/messages` for regular messages
                this.client.subscribe('/topic/messages', (message) => {
                    onMessageReceived(JSON.parse(message.body));
                });
                
            },
            onStompError: function (frame) {
                console.error('STOMP error', frame);
            },
            webSocketFactory: () => {
                return new SockJS('http://localhost:8001/chat');
            }
        });
    
        this.client.activate();
    };



    sendMessage = (message) => {
        if (this.client && this.client.connected) {
            console.log("Sending message:", message);
            this.client.publish({
                destination: '/app/sendMessage', // Prefix for server-side controller
                body: JSON.stringify(message),
            });
        } else {
            console.error("WebSocket not connected.");
        }
    };

    disconnect = () => {
        if (this.client) {
            this.client.deactivate();
        }
    };


    
}

export default new WebSocketService();

