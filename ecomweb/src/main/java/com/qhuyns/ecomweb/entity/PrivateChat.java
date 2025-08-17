package com.qhuyns.ecomweb.entity;

//import jakarta.persistence.*;
import com.qhuyns.ecomweb.entity.key.PrivateChatKey;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("private_chat")
public class PrivateChat {
    @PrimaryKey
    PrivateChatKey key;
    String roomId;
}