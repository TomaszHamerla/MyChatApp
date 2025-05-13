package com.example.chatapp.model.chat;

import com.example.chatapp.model.message.Message;
import com.example.chatapp.model.message.MessageState;
import com.example.chatapp.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "chat")
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "sender_nickname")
    private String senderNickName;

    @Column(name = "recipient_nickname")
    private String recipientNickName;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdDate DESC")
    private List<Message> messages;

    @Transient
    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0).getContent();
        }
        return null;
    }

    @Transient
    public String getChatName(Long id) {
        if (sender.getId().equals(id)) {
            return recipient.getName();
        } else {
            return sender.getName();
        }
    }

    @Transient
    public long getUnreadMessages(Long senderId) {
        return this.messages
                .stream()
                .filter(m -> m.getReceiverId().equals(senderId))
                .filter(m -> MessageState.SENT == m.getState())
                .count();
    }

    @Transient
    public String getNickName(Long id) {
        if (sender.getId().equals(id)) {
            return recipientNickName;
        } else {
            return senderNickName;
        }
    }
}
