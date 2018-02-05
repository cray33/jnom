package my.home.jnom.predata.service;

import my.home.jnom.predata.entity.NodeEntity;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.NodeHandler;
import my.home.jnom.predata.handler.WayHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 31.01.18
 * Time: 20:17
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RequestService {
    private static final Logger LOG = LoggerFactory.getLogger(RequestService.class);

    private static final int MAX_REQUEST_ENTITIES_COUNT = 50;

    public List<NodeEntity> loadNodesFromRemote(SAXParser parser, List<Long> ids) throws SAXException {
        List<NodeEntity> result = new ArrayList<>();
        if (ids.isEmpty()) {
            return result;
        }

        // get nodes by portions
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < ids.size(); i+= MAX_REQUEST_ENTITIES_COUNT) {
            List<Long> idsPortion = getPortion(i, ids);
            String url = "https://www.openstreetmap.org/api/0.6/nodes?nodes=" + StringUtils.join(idsPortion, ",");
            NodeHandler handler = new NodeHandler(idsPortion);
            boolean success = false;
            while ( ! success) {
                try {
                    makeRequest(url, parser, handler);
                    result.addAll(handler.getLoadedNodes());
                    success = true;
                } catch (IOException ex) {
                    LOG.warn("loadNodesFromRemote", ex);
                }
            }
        }


        return result;
    }

    public List<WayEntity> loadWaysFromRemote(SAXParser parser, List<Long> ids) throws SAXException {
        List<WayEntity> result = new ArrayList<>();
        if (ids.isEmpty()) {
            return result;
        }

        // get ways by portions
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < ids.size(); i+= MAX_REQUEST_ENTITIES_COUNT) {
            List<Long> idsPortion = getPortion(i, ids);
            String url = "https://www.openstreetmap.org/api/0.6/ways?ways=" + StringUtils.join(idsPortion, ",");
            WayHandler handler = new WayHandler(idsPortion);
            boolean success = false;
            while ( ! success) {
                try {
                    makeRequest(url, parser, handler);
                    result.addAll(handler.getLoadedWays());
                    success = true;
                } catch (IOException ex) {
                    LOG.warn("loadWaysFromRemote", ex);
                }
            }
        }


        return result;
    }

    private List<Long> getPortion(int i, List<Long> ids) {
        int idsSize = ids.size();
        if (idsSize <= i + MAX_REQUEST_ENTITIES_COUNT) {
            return ids.subList(i, idsSize);
        }
        return ids.subList(i, i + MAX_REQUEST_ENTITIES_COUNT);
    }

    public void makeRequest(String url, SAXParser parser, DefaultHandler handler) throws IOException, SAXException {
          LOG.info("request to {}", url);

          HttpURLConnection huc = (HttpURLConnection) new URL(url).openConnection();
          HttpURLConnection.setFollowRedirects(false);
          huc.setConnectTimeout(15 * 1000);
          huc.setReadTimeout(15 * 1000);
          huc.setRequestMethod("GET");
          //huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
          huc.connect();
          InputStream input = huc.getInputStream();

          parser.parse(new InputSource(input), handler);
      }
}
