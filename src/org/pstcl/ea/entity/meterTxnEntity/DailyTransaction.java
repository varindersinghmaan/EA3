package org.pstcl.ea.entity.meterTxnEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "DAILY_TRANSACTION", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "LOC_ID", "transactionDate" }),
		@UniqueConstraint(columnNames = { "METER_ID", "transactionDate" }) })
public class DailyTransaction extends DailyTransactionMappedSuper {

	public DailyTransaction() {
		super();
	}

}
