package com.sougata.train_booking.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Table(name="seat_ids")
public class SeatId {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String seatId;

    private String bookingId;

    public SeatId(String bookingId, String seatId) {
        this.bookingId = bookingId;
        this.seatId = seatId;
    }

    public SeatId() {

    }
}