import React, { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import { fetchMessagesByRoomId, sendMessage, getMyInfo } from "../../api/api";
import defaultAvatar from "../../assets/default-avatar-icon-of-social-media-user-vector.jpg";

interface Message {
  key: {
    messageId: string;
    roomId: string;
    sentAt: string;
  };
  sender: string;
  sendername?: string; // add senderName
  content: string;
  type: string;
}

interface ChatBoxProps {
  roomId: string;
  roomName: string; // Added roomName to props
  onClose: () => void;
}

const ChatBox: React.FC<ChatBoxProps> = ({ roomId, roomName, onClose }) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [userId, setUserId] = useState<string>(""); // State to store userId
  const stompClient = useRef<Client | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Fetch user info to get userId
    const token = localStorage.getItem("token");
    if (token) {
      getMyInfo(token).then((res) => {
        setUserId(res.data.result.id);
      });
    }

    // Fetch messages history via REST API
    fetchMessagesByRoomId(roomId)
      .then(res => setMessages(res.data.result || []));

    // Subscribe WebSocket
    const socket = new SockJS(`http://localhost:8082/ws${token ? `?token=${token}` : ''}`);
    const client = new Client({
      webSocketFactory: () => socket as any,
      onConnect: () => {
        client.subscribe(`/topic/room.${roomId}`, () => {
          fetchMessagesByRoomId(roomId)
            .then(res => setMessages(res.data.result || []));
        });
      },
    });
    client.activate();
    stompClient.current = client;
    return () => {
      client.deactivate();
    };
  }, [roomId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = () => {
    if (!input.trim() || !stompClient.current) return;
    const msg = {
      key: { roomId },
      sender: userId,
      content: input,
      type: "text"
    };
    stompClient.current.publish({
      destination: "/ws-app/messages/chat.sendMessage",
      body: JSON.stringify(msg)
    });
    setInput("");
  };

  return (
    <div
      className="fixed bottom-4 right-4 w-80 border rounded-lg shadow-lg z-50 flex flex-col bg-white"
      style={{ height: 400 }}
    >
      <div className="flex items-center justify-between p-3 border-b bg-[#cc3333] text-white rounded-t-lg">
        <span className="font-bold text-lg">{roomName || roomId}</span>
        <button
          onClick={onClose}
          className="text-white hover:text-red-400 text-xl"
          title="Close Chat"
        >
          ×
        </button>
      </div>
      <div className="flex-1 overflow-y-auto p-3 space-y-3 bg-white" style={{ maxHeight: 320 }}>
        {messages.map((m) => {
          const isUserMessage = m.sender === userId;
          return (
            <div
              key={m.key?.messageId || m.key?.sentAt || Math.random()}
              className={`flex items-center gap-2 ${isUserMessage ? 'justify-end' : 'justify-start'}`}
            >
              {!isUserMessage && (
                <img
                  src={defaultAvatar}
                  alt="avatar"
                  className="w-8 h-8 rounded-full object-cover border border-gray-300 shadow"
                />
              )}
              <div className={`flex flex-col max-w-[60%] ${isUserMessage ? 'items-end' : 'items-start'}`}>
                <span className="text-xs mb-0.5 text-gray-400">{isUserMessage ? 'Bạn' : (m.sendername || m.sender)}</span>
                <div className={`relative group px-3 py-1.5 rounded-2xl shadow-sm break-words text-base ${isUserMessage ? 'bg-[#cc3333] text-white' : 'bg-gray-100 text-black'}`} style={{minWidth: 40, maxWidth: '100%', fontSize: '15px'}}>
                  {m.content}
                  <span className="absolute left-1/2 -bottom-5 -translate-x-1/2 text-[10px] text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity bg-white px-2 py-0.5 rounded shadow border z-10 whitespace-nowrap">
                    {m.key?.sentAt ? new Date(m.key.sentAt).toLocaleTimeString() : ''}
                  </span>
                </div>
              </div>
              {isUserMessage && (
                <img
                  src={defaultAvatar}
                  alt="avatar"
                  className="w-8 h-8 rounded-full object-cover border border-gray-300 shadow"
                />
              )}
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>
      <div className="flex items-center border-t p-3 bg-white">
        <input
          className="flex-1 border rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#cc3333] bg-white text-[#222] placeholder-gray-500"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Nhập tin nhắn..."
        />
        <button
          className="ml-3 bg-[#cc3333] text-white px-4 py-2 rounded-full hover:bg-[#b82d2d] focus:ring-2 focus:ring-[#cc3333] font-bold"
          onClick={handleSend}
        >
          Gửi
        </button>
      </div>
    </div>
  );
};

export default ChatBox;
