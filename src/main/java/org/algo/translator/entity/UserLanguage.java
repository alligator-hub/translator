package org.algo.translator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.algo.translator.enums.LanguageType;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Follower follower;

    @Enumerated(EnumType.STRING)
    private LanguageType fromLang;

    @Enumerated(EnumType.STRING)
    private LanguageType toLang;

    private Integer lastBoardId;

    public UserLanguage(Follower follower) {
        this.follower = follower;
    }
}
