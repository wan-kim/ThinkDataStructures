package com.allendowney.thinkdast;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class WikiPhilosophy {

	final static List<String> visited = new ArrayList<>();
	final static WikiFetcher wf = new WikiFetcher();

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * <p>
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * <p>
	 * 1. Clicking on the first non-parenthesized, non-italicized link
	 * 2. Ignoring external links, links to the current page, or red links
	 * 3. Stopping when reaching "Philosophy", a page with no links or a page
	 * that does not exist, or when a loop occurs
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		String destination = "https://en.wikipedia.org/wiki/Philosophy";
		String destination = "https://en.wikipedia.org/wiki/Element_(mathematics)";
		String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";

		testConjecture(destination, source, 10);
	}

	/**
	 * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
	 *
	 * @param destination
	 * @param source
	 * @throws IOException
	 */
	public static void testConjecture(String destination, String source, int limit) throws IOException {
		// TODO: FILL THIS IN!
		String link = source;
		for (int i = 0; i < limit; i++) {
			System.out.println("link : " + link);
			if (visited.contains(link)) {
				System.out.println("fail..");
				break;
			} else {
				visited.add(link);
			}

			Elements paras = wf.fetchWikipedia(link);

			boolean linkFound = false;
			for (Element el : paras) {
				if (linkFound) {
					break;
				}

				Deque<String> parenthesisStack = new ArrayDeque<>();
				Iterable<Node> iter = new WikiNodeIterable(el);

				for (Node n : iter) {
					if (n instanceof TextNode) {
						TextNode t = (TextNode) n;

						StringTokenizer st = new StringTokenizer(t.text(), "()", true);
						while (st.hasMoreTokens()) {
							String token = st.nextToken();
							if (token.equals("(")) {
								parenthesisStack.push("(");
							}
							if (token.equals(")")) {
								if (!parenthesisStack.isEmpty()) {
									parenthesisStack.pop();
								}
							}
						}
					}

					if (n instanceof Element) {
						Element e = (Element) n;
						if (!parenthesisStack.isEmpty()) {
							continue;
						}

						String href = e.absUrl("href");
						if (href == null || "".equals(href)) {
							continue;
						}

						boolean hasItalic = false;
						for(Element p = e; p != null; p = p.parent()) {
							if (e.tagName().equalsIgnoreCase("i") ||
									e.tagName().equalsIgnoreCase("em")) {
								hasItalic = true;
								break;
							}
						}

						if(hasItalic) {
							continue;
						}

						link = href;
						linkFound = true;
						break;
					}
				}
			}

			if (linkFound && destination.equals(link)) {
				System.out.print("destination : " + destination);
				System.out.println(" Found!");
				break;
			}
		}
	}
}