package com.qhuyns.ecomweb.entity;

//import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("room")
public class Room {
    @PrimaryKey
    @Column("room_id")
    private String roomId;
    private String name;
    @Column("created_at")
    private LocalDateTime createdAt;
}