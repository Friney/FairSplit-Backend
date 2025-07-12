package com.friney.fairsplit.core.entity.summary;

import com.friney.fairsplit.api.dto.user.UserDto;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayerInfo {
    private UserDto user;
    private BigDecimal total;
}