package app.osmosi.heater.adapters.http;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HttpAdapterConfig {
	private final List<CentralHeatingConfig> centralHeating;
	private final Optional<RequestConfig> hotWater;

	private Stream<Node> getChildren(NodeList list) {
		return IntStream.range(0, list.getLength()).mapToObj(list::item);
	}

	private Predicate<Node> byNodeName(String name) {
		return n -> n.getNodeName().equals(name);
	}

	private Optional<Node> firstChild(NodeList list, String name) {
		return getChildren(list).filter(byNodeName(name)).findFirst();
	}

	private Optional<String> getPayload(Node request, String tagName) {
		Optional<Node> onPayload = firstChild(request.getChildNodes(), tagName);
		if (onPayload.isPresent()) {
			Node payload = onPayload.get();
			return Optional.of(payload.getTextContent());
		}
		return Optional.empty();
	}

	private RequestConfig parseRequest(Element req) {
		String onURL = req.getAttribute("on");
		String offURL = req.getAttribute("off");
		String method = req.getAttribute("method");
		Optional<String> onPayload = getPayload(req, "on-payload");
		Optional<String> offPayload = getPayload(req, "off-payload");
		return new RequestConfig(onURL, offURL, method, onPayload, offPayload);
	}

	private CentralHeatingConfig parseCHConfig(Node ch) {
		Element floor = (Element) ch;
		String floorName = floor.getAttribute("name");
		String deviceName = floor.getAttribute("device");
		var requestNode = firstChild(ch.getChildNodes(), "request");
		RequestConfig request = null;
		if (requestNode.isPresent()) {
			Element requestElement = (Element) requestNode.get();
			request = parseRequest(requestElement);
		}
		return new CentralHeatingConfig(floorName, deviceName, request);
	}

	public HttpAdapterConfig(File file) throws IOException, SAXException, ParserConfigurationException {
		// FileInputStream fis = new FileInputStream(new
		// File("/projects/heater/config/http-adapter.xml"));
		FileInputStream fis = new FileInputStream(file);
		DocumentBuilderFactory bFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = bFactory.newDocumentBuilder();
		Document document = builder.parse(fis);
		Node root = document.getFirstChild();
		Optional<Node> centralHeating = firstChild(root.getChildNodes(), "centralheating");
		Optional<Node> hotWater = firstChild(root.getChildNodes(), "hotwater");

		if (centralHeating.isPresent()) {
			Element ch = (Element) centralHeating.get();
			NodeList children = ch.getElementsByTagName("floor");
			List<CentralHeatingConfig> chConfigs = getChildren(children)
					.map(this::parseCHConfig)
					.collect(Collectors.toList());
			this.centralHeating = chConfigs;
		} else {
			this.centralHeating = List.of();
		}

		if (hotWater.isPresent()) {
			Optional<Node> request = firstChild(hotWater.get().getChildNodes(), "request");
			if (request.isPresent()) {
				var req = parseRequest((Element) request.get());
				this.hotWater = Optional.of(req);
			} else {
				this.hotWater = Optional.empty();
			}
		} else {
			this.hotWater = Optional.empty();
		}
	}

	public List<CentralHeatingConfig> getCentralHeating() {
		return centralHeating;
	}

	public Optional<RequestConfig> getHotWater() {
		return hotWater;
	}
}
