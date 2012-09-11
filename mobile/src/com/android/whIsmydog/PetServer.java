package com.android.whIsmydog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.os.Bundle;

import com.facebook.android.Util;

public class PetServer {
	private static final String HOST_URL = "http://gentle-hamlet-5222.herokuapp.com/";

	public String request(String graphPath, Bundle params, String httpMethod)
			throws FileNotFoundException, MalformedURLException, IOException {
		String url = HOST_URL + graphPath;
		return Util.openUrl(url, httpMethod, params);
	}
}
