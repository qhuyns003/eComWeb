package com.ecomweb.identity_service.configuration;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// su dung keycloak - ho tro oauth2 va sso
// realm : workspace bao gom cac he thong la cac client muon su dung chung 1 server xac thuc
// client: cac server tuong ung
// user : cac user hop le de dang nhap vao
// realm role : cac role trong các service client

// key cloak oauth 2:
// - dam nhan tat ca thao tac xac thuc , xac minh token -> khong can customJwtDecoder, Converter, parseToken, Verify token
// - cac thao tac do duco xu ly tren Keycloak UI -> tuy nhien 1 so cai phai config
// - token duco tao tren keycloak -> exp, iat hay sub sẽ được cấu hình tại Keycloak UI
// keycloak sso:
// - bat buoc phai su dung frontend - client (giao dien eycloak UI) de dang nhap eu muon dam bao
// - viec config cho client thi k can trach nhiem phia BE - chi can co du ten host + realm + ten client
// key cloak backend:
// chi day cac truong muon xac thuc sang nhu username,pass
// phia ben csdl BE can luu keycloak user id de truy van khi can
// co luu secretkey - la khoa de co tham quyen truy cap db cua keycloak, cac quyen han cung duco gioi han boi admin tao Keycloak

// flow: Fe -> login -> Ui keycloak -> kiem tra va tra ve code -> FE nhan code va trao doi code lay token -> luu token
// cac buoc tren thuc hien giau FE va Keycloak, co thu vien agent ho tro tren FE

//flow xac thuc token : BE goi den Keycloak de lay public key(chi dung de giai ma, k dung de ma hoa) -> parse duoc -> oauth2 giup xac thuc cac truong exp
// flow xax thuc nguoi dung : keycloak tra ve code -> FE goi url doi code lay token (do keycloak ban dau tra code tren url -> k bao mat -> khi doi code token duco tra trong body)
// ke gian k the doi code lay token duoc, vi domain cua no k dc config de doi token
@Configuration
public class KeycloakConfig {
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloakAdmin() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
