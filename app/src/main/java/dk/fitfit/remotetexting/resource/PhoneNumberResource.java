package dk.fitfit.remotetexting.resource;

public class PhoneNumberResource {
	private String number;

	public PhoneNumberResource(String sender) {
		this.number = sender;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(final String number) {
		this.number = number;
	}
}
