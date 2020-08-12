package eu.similarity.msc.user_examples;

import eu.similarity.msc.local_context.LocalContext;

public class MyLocalContext extends LocalContext {

	@Override
	public String getUsername() {
		return "weizhengli";
	}

	@Override
	public String getLocalFileRoot() {

		return "Volumes/Data/MetricSpaceContextData";
	}

}
