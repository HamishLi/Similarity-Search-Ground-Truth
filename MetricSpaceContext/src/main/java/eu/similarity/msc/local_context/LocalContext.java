package eu.similarity.msc.local_context;

public abstract class LocalContext {
	public String getUsername() {
		return "default_username";
	}

	public abstract String getLocalFileRoot();
}
