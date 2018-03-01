package com.mapping.xmlcustomizer.getrules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapping.xmlcustomizer.XMLCustomizer;
import com.mapping.xmlcustomizer.functions.XCpadArray1D;
import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.StreamTransformationException;

public class XCgetRules extends XMLCustomizer {

	String delimiter = "~@#~";
	String senderAgency = "VMR_Key";
	String senderScheme = "VMR_Source";
	String receiverAgency = "VMR_Return";
	String receiverScheme = "VMR_Target";
	String context = "";

	public String[][] executeXCgetRules(StringBuilder in, String omParam, AbstractTrace trace)
			throws StreamTransformationException {

		trace.addInfo("Class XCgetRules: Acquiring rules for XML Customizer");

		String docType = "";
		String xcTable = "";
		String[] docKey = new String[] {};
		String[] initVMKeys = new String[] { "", "", "", "", "", "" };
		String[] blankVMKey = new String[] { "", "", "", "", "", "" };
		String[] xcRulesTemp = new String[] {};
		String[][] xcRules = new String[][] {};
		List<String[]> xcRulesArray = new ArrayList<String[]>();

		try {

			// Get Rules Step 1: Identify document type
			XCgetDocType getDocType = new XCgetDocType();
			docType = getDocType.executeXCgetDocType(in, trace);

			// Get Rules Step 2: Prepare document
			if (docType.equals("IDOC")) {
				XCprepIDOC prepIDOC = new XCprepIDOC();
				docKey = prepIDOC.executeXCprepIDOC(in, trace);
			} else if (docType.equals("EDIFACT")) {
				XCprepEDIFACT prepEDIFACT = new XCprepEDIFACT();
				docKey = prepEDIFACT.executeXCprepEDIFACT(in, omParam, trace);
			}

			try {
				xcTable = docKey[11];
			} catch (Exception e) {
				docKey = XCpadArray1D.executeXCpadArray1D(docKey, "", 12);
			}

			// Get Rules Step 3: Initialize document
			XCinitDocument initDocument = new XCinitDocument();
			initVMKeys = initDocument.executeXCinitDocument(docKey, trace);

			// Get Rules Step 4: Get rules from cache
			if (!Arrays.equals(blankVMKey, initVMKeys)) {

				// XCgetVM getVM = new XCgetVM();
				XCgetVMentry getVMentry = new XCgetVMentry();

				// Get Rules Step 4.1: Get A4 rules from cache

				int ruleNumber = 10100;
				int xcRulesTempLen = 1;
				boolean Lmatch = false;

				while ((xcRulesTempLen > 0) && (ruleNumber < 11000)) {

					xcRulesTemp = getVMentry.executeGetVMentry(xcTable, initVMKeys[0], ruleNumber, trace);
					if (xcRulesTemp.length > 0) {
						xcRulesArray.add(xcRulesTemp);
					}
					xcRulesTempLen = xcRulesTemp.length;
					ruleNumber = ruleNumber + 100;

					if (ruleNumber == 11000) {
						break;
					}
				}

				// Get Rules Step 4.2: Get L* rules from cache

				for (int L = 1; ((L < 5) && (!Lmatch)); L++) {
					ruleNumber = 10100;
					xcRulesTempLen = 1;
					while ((xcRulesTempLen > 0) && (ruleNumber < 11000)) {

						xcRulesTemp = getVMentry.executeGetVMentry(xcTable, initVMKeys[L], ruleNumber, trace);
						if (xcRulesTemp.length > 0) {
							xcRulesArray.add(xcRulesTemp);
							Lmatch = true;
						}
						xcRulesTempLen = xcRulesTemp.length;
						ruleNumber = ruleNumber + 100;

						if (ruleNumber == 11000) {
							break;
						}
					}
				}

				// Get Rules Step 4.2: Get Z4 rules from cache

				ruleNumber = 10100;
				xcRulesTempLen = 1;

				while ((xcRulesTempLen > 0) && (ruleNumber < 11000)) {

					xcRulesTemp = getVMentry.executeGetVMentry(xcTable, initVMKeys[5], ruleNumber, trace);

					if (xcRulesTemp.length > 0) {
						xcRulesArray.add(xcRulesTemp);
					}
					xcRulesTempLen = xcRulesTemp.length;
					ruleNumber = ruleNumber + 100;

					if (ruleNumber == 11000) {
						break;
					}
				}

			} else {

				// No rules acquired
				trace.addInfo("Class XCgetRules: Document not initialized");
				xcRules = new String[][] {};

			}

		} catch (Exception exception) {

			trace.addInfo("Class XCgetRules error: " + exception);
			trace.addInfo("Class XCgetRules error: Returning blank rules");
			return xcRules;

		}

		xcRules = xcRulesArray.toArray(xcRules);
		trace.addInfo("Class XCgetRules: " + xcRules.length + " rule(s) acquired");
		return xcRules;

	}
}
