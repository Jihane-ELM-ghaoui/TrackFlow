import React, { useState, useEffect, useRef } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';
import WebSocketService from './WebSocketService';
import { formatDistanceToNow, format } from 'date-fns';
import Avatar from 'react-avatar';
import './Chat.css';

const Chat = () => {
  const [receivers, setReceivers] = useState([]);
  const [idToken, setIdToken] = useState('');
  const { user, isAuthenticated, getIdTokenClaims, getAccessTokenSilently } = useAuth0();
  const [token, setToken] = useState(null);
  const [userMetadata, setUserMetadata] = useState(null);
  const [isAddChatVisible, setIsAddChatVisible] = useState(false);
  const [email, setEmail] = useState('');
  const [receiver, setReceiver] = useState(null);
  const [receiverName, setReceiverName] = useState(null);
  const [message, setMessage] = useState('');
  const [message2, setMessage2] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const [selectedReceiver, setSelectedReceiver] = useState(null);
  const messageContainerRef = useRef(null);

  useEffect(() => {
    if (!isAuthenticated) {
      return;
    }

    const getUserMetadataAndToken = async () => {
      try {
        const idTokenClaims = await getIdTokenClaims();
        console.log('ID token claims:', idTokenClaims);

        if (idTokenClaims) {
          const metadata = idTokenClaims['https://demo.app.com/user_metadata'];
          console.log('User metadata:', metadata);
          setUserMetadata(metadata);
        } else {
          console.error('No ID token claims available');
        }

        const accessToken = await getAccessTokenSilently();
        console.log('Access Token:', accessToken);
        setToken(accessToken);
      } catch (error) {
        console.error('Error getting token or metadata:', error);
      }
    };

    getUserMetadataAndToken();
  }, [getIdTokenClaims, getAccessTokenSilently, isAuthenticated]);

  const fetchReceivers = async () => {
    try {
      const idTokenClaims = await getIdTokenClaims();
      const idToken = idTokenClaims.__raw;
      setIdToken(idToken);
  
      const response = await axios.get('http://localhost:8888/chat-service/api/messages/receivers', {
        headers: {
          Authorization: `Bearer ${idToken}`,
        },
      });
  
      
      const sortedReceivers = response.data.sort((a, b) => {
        const timestampA = new Date(a.lastMessageTime).getTime(); // Convert to timestamp
        const timestampB = new Date(b.lastMessageTime).getTime(); // Convert to timestamp
  
        return timestampB - timestampA; // Sort descending by timestamp
      });
  
      setReceivers(sortedReceivers);
    } catch (error) {
      console.error('Error fetching receivers:', error);
    }
  };
  

  useEffect(() => {
    fetchReceivers();
  }, []);

  const toggleAddChatBox = () => {
    setIsAddChatVisible(!isAddChatVisible);
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
    setReceiver(null);
    setMessage('');
    setErrorMessage('');
  };

  const handleMessageChange = (e) => {
    setMessage(e.target.value);
  };

  const handleSearchUser = async () => {
    try {
      const idTokenClaims = await getIdTokenClaims();
      const idToken = idTokenClaims.__raw;
      setIdToken(idToken);

      const response = await axios.get(`http://localhost:8888/user-service/api/protected/search`, {
        params: { email },
        headers: {
          Authorization: `Bearer ${idToken}`,
        },
      });

      if (response.data) {
        setReceiver(response.data.userId);
        setReceiverName(response.data.full_Name);
        setErrorMessage('');
      } else {
        setErrorMessage('User not found');
        setReceiver(null);
      }
    } catch (error) {
      console.error('Error searching user:', error);
      setErrorMessage('Error occurred. Please try again.');
    }
  };

  const handleCloseDropdown = () => {
    setIsAddChatVisible(false);
    setReceiver(null);
    setEmail('');
    setMessage('');
    setErrorMessage('');
  };

  const fetchMessages = async (receiver) => {
    if (!receiver) return;

    try {
      const idTokenClaims = await getIdTokenClaims();
      const idToken = idTokenClaims.__raw;
      setIdToken(idToken);

      const sanitizedReceiver = receiver.replace('|', '');

      const response = await axios.get(`http://localhost:8888/chat-service/api/messages?receiver=${sanitizedReceiver}`, {
        headers: {
          Authorization: `Bearer ${idToken}`,
        },
      });
      setMessages(response.data);
    } catch (error) {
      console.error('Error fetching messages:', error);
    }
  };

  useEffect(() => {
    if (selectedReceiver) {
      fetchMessages(selectedReceiver.id);
    }

    WebSocketService.connect(handleMessageReceived);

    return () => {
      WebSocketService.disconnect();
    };
  }, [selectedReceiver]);

  useEffect(() => {
    if (messageContainerRef.current) {
      messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
    }
  }, [messages]);

  const handleContentChange = (receiver) => {
    console.log('Selected receiver:', receiver);
    setSelectedReceiver(receiver);
    fetchMessages(receiver.id);
  };

  const handleMessageReceived = (newMessage) => {
    console.log('Received message:', newMessage);

    // Update the receiver list after receiving a message
    fetchReceivers();

    if (
      (newMessage.sender === user.sub && newMessage.receiver === selectedReceiver.id) ||
      (newMessage.sender === selectedReceiver.id && newMessage.receiver === user.sub)
    ) {
      setMessages((prevMessages) => [...prevMessages, newMessage]);
    }
  };

  const handleSendFirstMessage = async () => {
    if (message && user.sub) {
      const newMessage = {
        sender: user.sub,
        receiver: receiver,
        content: message,
        receivername: receiverName,
        sendername: userMetadata?.Full_Name,
      };

      WebSocketService.sendMessage(newMessage);
      setMessage('');

      try {
        await fetchReceivers();
        toggleAddChatBox();
      } catch (error) {
        console.error('Error fetching receivers after sending message:', error);
      }
    } else {
      console.log('Cannot send message. Ensure message and username are set.');
    }
  };

  const handleSendMessage = async (selectedReceiver) => {
    if (message2 && user.sub) {
      const newMessage = {
        sender: user.sub,
        receiver: selectedReceiver.id,
        content: message2,
        receivername: selectedReceiver.username,
        sendername: userMetadata?.Full_Name,
      };

      WebSocketService.sendMessage(newMessage);
      setMessage2('');

      try {
        await fetchMessages(selectedReceiver.id);
        await fetchReceivers();
      } catch (error) {
        console.error('Error fetching messages or receivers after sending message:', error);
      }
    } else {
      console.log('Cannot send message. Ensure message and username are set.');
    }
  };

  return (
    <div className="SBChat-container">
      <div className="SBChat-left">
        <div className="SBChat-header">
          Chat Room
          <div className="SBChat-search-container">
            <button className="SBChat-search-button" onClick={toggleAddChatBox}>
              New Chat
            </button>
          </div>
        </div>

        {isAddChatVisible && (
          <div className="SBChat-dropdown">
            <button className="SBChat-close-btn" onClick={handleCloseDropdown}>
              <i className="bx bx-x"></i>
            </button>

            <input
              type="email"
              value={email}
              onChange={handleEmailChange}
              placeholder="Enter receiver's email..."
            />

            <button className="SBChat-dropdown-button" onClick={handleSearchUser}>Start Chat</button>

            {receiver ? (
              <div>
                <input
                  type="text"
                  value={message}
                  onChange={handleMessageChange}
                  placeholder="Write your message..."
                />
                <button className="SBChat-dropdown-button" onClick={handleSendFirstMessage}>Send</button>
              </div>
            ) : (
              errorMessage ? (
                <p className="SBerror-message">{errorMessage}</p>
              ) : null
            )}
          </div>
        )}

        {receivers.map((receiver, index) => (
          <div
            key={index}
            className="SBChat-list-item"
            onClick={() => handleContentChange(receiver)}
          >
            <Avatar name={receiver.username} size="35" round={true} />
            <div className="SBChat-content">
              <p className="SBChat-name">{receiver.username}</p>
              <p className="SBChat-message">{receiver.lastMessage || "No messages yet"}</p>
            </div>

            <div className="SBChat-message-time">
            {receiver.lastMessageTime&& (
              <small>
            {format(new Date(receiver.lastMessageTime), 'hh:mm a')} {/* Format the time */}
          </small>
        )}
      </div>

          </div>
        ))}
      </div>

      <div className="SBChat-right">
        {selectedReceiver ? (
          <div>
            <h3>{selectedReceiver.username}</h3>

            <div className="SBChat-messages" ref={messageContainerRef}>
              {messages.map((msg, index) => (
                <div key={index} className="message">
                  <p className={`${msg.sender === user.sub ? 'sentSB' : 'receivedSB'}`}>{msg.content}</p>

                  {msg.timestamp && (
                    <small className={`message-timestamp ${msg.sender === user.sub ? 'sentSB' : 'receivedSB'}`}>
                      {new Date(msg.timestamp).toDateString() === new Date().toDateString()
                        ? format(new Date(msg.timestamp), 'hh:mm a') // Just time if today
                        : `${formatDistanceToNow(new Date(msg.timestamp), { addSuffix: true })}, ${format(new Date(msg.timestamp), 'hh:mm a')}`
                      }
                    </small>
                  )}
                </div>
              ))}
            </div>

            <input
              type="text"
              value={message2}
              onChange={(e) => setMessage2(e.target.value)}
              placeholder="Write a message..."
            />
            <button onClick={() => handleSendMessage(selectedReceiver)}><i className="bx bx-send"></i></button>
          </div>
        ) : (
          <div>
            <p></p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Chat;
