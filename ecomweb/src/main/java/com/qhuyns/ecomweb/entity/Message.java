package com.qhuyns.ecomweb.entity;

//import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Message.java
// table nay cua cassandra kp cua jkt
@Table("message")
public class Message {
    @PrimaryKey
    private MessagePrimaryKey key;
    private String sender;
    private String content;
    private String type;
}