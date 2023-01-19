package com.komponente.reservation_service.user_sync_comm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankDto {
    private String name;
    private int minDays;
    private int maxDays;
    private int discount;
}
