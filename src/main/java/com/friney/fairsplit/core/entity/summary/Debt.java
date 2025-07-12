package com.friney.fairsplit.core.entity.summary;

import com.friney.fairsplit.api.dto.user.UserDto;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Debt {
    private UserDto from;
    private UserDto to;
    private BigDecimal amount;
}
