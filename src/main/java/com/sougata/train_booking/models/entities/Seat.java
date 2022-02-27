package com.sougata.train_booking.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Builder
@Entity
@AllArgsConstructor
@Table(name="seats")
public class Seat {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String seatId;

    private String seatNo;
    private String coachType;
    private String coachNo;

    public Seat() {

    }

}

