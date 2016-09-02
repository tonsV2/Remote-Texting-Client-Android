package dk.fitfit.remotetexting.resource;


public class MessageResource {
	private PhoneNumberResource to;
	private PhoneNumberResource from;
	private String content;
	private long timestampProvider;
	private long timestampReceived;

	public PhoneNumberResource getTo() {
		return to;
	}

	public void setTo(PhoneNumberResource to) {
		this.to = to;
	}

	public PhoneNumberResource getFrom() {
		return from;
	}

	public void setFrom(final PhoneNumberResource from) {
		this.from = from;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public long getTimestampProvider() {
		return timestampProvider;
	}

	public void setTimestampProvider(final long timestampProvider) {
		this.timestampProvider = timestampProvider;
	}

	public long getTimestampReceived() {
		return timestampReceived;
	}

	public void setTimestampReceived(final long timestampReceived) {
		this.timestampReceived = timestampReceived;
	}
}
