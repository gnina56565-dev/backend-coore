package ru.mentee.power.crm.exception;

import lombok.Getter;
import ru.mentee.power.crm.model.LeadStatus;
import java.util.UUID;

@Getter
public class IllegalLeadStateException extends RuntimeException {

	private final UUID leadId;
	private final LeadStatus currentStatus;

	public IllegalLeadStateException(UUID leadId, LeadStatus currentStatus) {
		super(String.format("Lead %s cannot be converted. Current status: %s", leadId, currentStatus));
		this.leadId = leadId;
		this.currentStatus = currentStatus;
	}
}
