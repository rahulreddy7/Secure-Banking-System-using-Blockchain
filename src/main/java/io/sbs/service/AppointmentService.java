package io.sbs.service;

import io.sbs.dto.AppointmentDTO;
import io.sbs.dto.WorkflowDTO;

public interface AppointmentService {
	public WorkflowDTO createAppointments(WorkflowDTO workflowDTO);
}
