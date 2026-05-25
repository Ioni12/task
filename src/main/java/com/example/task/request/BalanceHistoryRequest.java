package com.example.task.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BalanceHistoryRequest {

    @NotNull(message = "you should provide a account id")
    private Long Id;

    public BalanceHistoryRequest(Long Id) {
        this.Id = Id;
    }

}
