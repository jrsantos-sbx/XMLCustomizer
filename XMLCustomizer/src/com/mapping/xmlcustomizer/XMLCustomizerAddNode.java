package com.mapping.xmlcustomizer;

import java.io.InputStream;
import java.io.OutputStream;

import com.sap.aii.mapping.api.StreamTransformationException;

public class XMLCustomizerAddNode {

	public void executeAddNode(String arg0, String arg1, String arg2, String arg3, InputStream in, OutputStream out)
			throws StreamTransformationException {

		// arg0 is the node to be created
		// arg1 is not used
		// arg2 is not used
		// arg3 is not used

		arg0 = "/MT_test/row/apellido";

	}

}
