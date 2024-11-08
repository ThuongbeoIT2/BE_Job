package com.example.oauth2.SapoStore.modelStatistical;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionStatistical {
    private long transactionSuccess;
    private long transactionError;
}
