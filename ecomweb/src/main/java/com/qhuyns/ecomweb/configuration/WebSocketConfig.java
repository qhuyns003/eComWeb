package com.qhuyns.ecomweb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
// websocket giup client thay du lieu realtime ngay khong can reload nhu http
// ws thuần chỉ là giao thức kết nối bắt tay giữa client và server
// còn stomp là giao thức chạy trên socket mới làm việc trao đổi dữ liệu, quy ước giữa client và service cụ thể
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    /topic/: gửi cho nhiều người
// queue/: gửi cho từng người
// app: prefix cho client gửi lên server
// user: prefix cho channel riêng từng user
// ws: endpoint để client connect



//    Khi user đăng nhập, FE lấy token và mở kết nối WebSocket tới /ws?token=... bằng SockJS.
//    Khi kết nối thành công, FE subscribe vào /user/queue/notifications để nhận thông báo cá nhân.
//    Khi có message mới, FE tự động update UI, không cần reload.

//    Khi FE connect tới /ws, Spring gọi AuthHandshakeInterceptor:
//    Lấy token từ query param, giải mã lấy username.
//    Gán username và Principal vào attributes.
//    CustomPrincipalHandshakeHandler lấy Principal từ attributes, gán vào WebSocket session.
//    Khi backend muốn gửi thông báo, gọi messagingTemplate.convertAndSendToUser(username, "/queue/notifications", ...).
//    Spring sẽ tìm session WebSocket có Principal trùng username và gửi message realtime tới FE.
//    FE nhận message qua subscription và hiển thị ngay lập tức.
//    Bước tạo kết nối đã định tuyến sẵn 1 đường hầm dành cho username đó, khi có reqquest đến user cụ thể thì chỉ cần tìm đúng đường hầm đã kết nối là đc
//    BE có thể chứa nhiều ws sesion 1 lúc
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // phat thong bao tu server
        config.enableSimpleBroker("/topic/", "/queue/");
        // thong bao ai cho server
        config.setApplicationDestinationPrefixes("/ws-app");
        // phat thong bao rieng
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // dang ki endpoint ws de ket noi
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
            // giai ma token va gan username, principle luu vao 1 map la bien tam de truyen cho buoc sau
        .addInterceptors(new AuthHandshakeInterceptor())
            // custom de lay ra Priciple da tao o buoc truoc de tao ws session
            // ws session nam tren ca FE (yeu cau truoc) va BE, thong qua buoc bat tay
        .setHandshakeHandler(new CustomPrincipalHandshakeHandler())
        .withSockJS();
    }


}
