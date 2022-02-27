package com.sougata.train_booking.models.aggregate;

import com.sougata.train_booking.models.entities.Booking;
import com.sougata.train_booking.models.entities.SeatId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSeatList {
    private Booking booking;
    private List<SeatId> seatIdList;
}
