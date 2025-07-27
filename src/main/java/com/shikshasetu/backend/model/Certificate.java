package com.shikshasetu.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Course course;

    private LocalDate issuedDate;

    private String certificateCode; // like a UUID or unique hash

    public Certificate() {}

    public Certificate(User user, Course course, String certificateCode, LocalDate issuedDate) {
        this.user = user;
        this.course = course;
        this.certificateCode = certificateCode;
        this.issuedDate = issuedDate;
    }

    public LocalDate getIssuedDate() {
    return issuedDate;
    }

    public String getCertificateCode() {
    return certificateCode;
    }
}
