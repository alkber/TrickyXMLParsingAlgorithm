package com.alkber.xml.algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A Tricky xml parsing algorithm
 * 
 * @author Althaf K Backer <althafkbacker@gmail.com> 23 July 2014 7:44 AM IST
 * @version 1.2
 * 
 *          Changelog - support to write the 'id' attribute to the file - ignore
 *          filters - support for child node of interest
 */
public class XMLParse {

	private static String output = "";
	private static final String fileName = "/home/alkber/output.txt";
	private static final String srcXML = "/home/alkber/input.xml";
	private static final String nodeOfInterest = "myname";
	private static final String childNodeOfInterest = "Model";

	private final static ArrayList<String> ignoreKeywords = new ArrayList<String>();

	public static void main(String args[]) throws ParserConfigurationException,
			SAXException, IOException {

		File stocks = new File(srcXML);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(stocks);
		doc.getDocumentElement().normalize();

		System.out.println("Root of xml file "
				+ doc.getDocumentElement().getNodeName());
		System.out
				.println(doc.getDocumentElement().getChildNodes().getLength());
		Node rootNode = doc.getDocumentElement();
		/*
		 * Recursive algorithm, may crash for deeply nested xml content, this is
		 * a POC
		 */
		ignoreKeywords.add("Job");
		ignoreKeywords.add("Fruits");
		findLastNodeValue(rootNode);
		writeToFile(output);

	}

	private static boolean childNodeOfInterestFound = false;

	private static void findLastNodeValue(Node currentNode) {

		if (currentNode == null) {

			return;

		}

		if (currentNode instanceof Element) {

			if (nodeOfInterest.equals(((Element) currentNode).getTagName())) {

				if (childNodeOfInterestFound
						|| childNodeOfInterest.equals(currentNode
								.getTextContent().trim())) {

					childNodeOfInterestFound = true;
					findLastNodeValue(currentNode.getFirstChild());

				}

				if (shouldIgnore(currentNode)) {

					return;

				}

				/*
				 * Trying to find last element, check if currentNode has next
				 * sibling we do this twice as XML considers newline as a child,
				 * so first call to getNextSibling returns the newline child,
				 * and next call to getNextSibling , if returns null it means we
				 * have crawled to last child.
				 */
				if (childNodeOfInterestFound
						&& currentNode.getNextSibling().getNextSibling() == null) {

					output += ((Element) currentNode).getAttribute("id") + "\n"
							+ currentNode.getTextContent() + "\n";
					/*
					 * We have reached the last child node of
					 * ChildNodeOfInterest
					 */
					childNodeOfInterestFound = false;

				}

			}

			findLastNodeValue(currentNode.getFirstChild());
		}

		findLastNodeValue(currentNode.getNextSibling());

	}

	private static void writeToFile(String output) throws IOException {
		System.out.println(output);
		;
		File file = new File(fileName);
		if (!file.exists()) {

			file.createNewFile();

		}

		FileWriter fileWritter = new FileWriter(file.getPath(), false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(output);
		bufferWritter.close();
		System.out.println("written to " + fileName);
	}

	private static boolean shouldIgnore(Node node) {

		for (String current : ignoreKeywords) {

			if (current.equals(node.getTextContent().trim())) {

				return true;

			}
		}

		return false;
	}
}
