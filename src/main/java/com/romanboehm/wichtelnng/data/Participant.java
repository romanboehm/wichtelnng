package com.romanboehm.wichtelnng.data;

import com.romanboehm.wichtelnng.usecases.registerparticipant.RegisterParticipant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "event")
@Entity(name = "Participant")
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(nullable = false, length = 100)
    private String name;

    @NaturalId
    @Column(nullable = false, length = 255)
    private String email;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public static Participant from(RegisterParticipant registerParticipant) {
        return new Participant()
                .setName(registerParticipant.getParticipantName())
                .setEmail(registerParticipant.getParticipantEmail());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Participant that = (Participant) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
