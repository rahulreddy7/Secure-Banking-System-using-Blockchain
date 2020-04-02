package io.sbs.dto;

public class TransferResponseDTO {
	private boolean activate_otp_flow;

	public boolean isActivate_otp_flow() {
		return activate_otp_flow;
	}

	public void setActivate_otp_flow(boolean activate_otp_flow) {
		this.activate_otp_flow = activate_otp_flow;
	}

	public TransferResponseDTO(boolean activate_otp_flow) {
		super();
		this.activate_otp_flow = activate_otp_flow;
	}

	

}
