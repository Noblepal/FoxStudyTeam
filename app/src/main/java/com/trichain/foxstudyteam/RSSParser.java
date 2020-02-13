package com.trichain.foxstudyteam;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trichain.foxstudyteam.adapter.NewsAdapter;
import com.trichain.foxstudyteam.models.RSSItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RSSParser {

    private static final String TAG = "RSSParser";
    // RSS XML document CHANNEL tag
    private static String TAG_CHANNEL = "channel";
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_DESRIPTION = "description";
    private static String TAG_ITEM = "item";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG_GUID = "guid";
    private static String img_content="media:content";
    String category = "";
    String xml = null;

    private Context context;

    public RSSParser(Context context) {
        this.context = context;
    }

    public List<RSSItem> getRSSFeedItems(String rss_url, NewsAdapter adapter, ArrayList<RSSItem> arrayList, String category) {
        String rss_feed_xml;
        this.category = category;
        rss_feed_xml = this.getXmlFromUrl(rss_url, adapter, arrayList);


        return null;
    }

    public void parAgain(String rss_feed_xml, NewsAdapter adapter, final ArrayList<RSSItem> arrayList) {

        List<RSSItem> itemsList = new ArrayList<>();
        Log.e(TAG, "getRSSFeedItems: found something " + rss_feed_xml);
        if (rss_feed_xml != null) {
            try {
                Document doc = this.getDomElement(rss_feed_xml);
                NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
                Element e = (Element) nodeList.item(0);

                NodeList items = e.getElementsByTagName(TAG_ITEM);
                for (int i = 0; i < items.getLength(); i++) {
                    Element e1 = (Element) items.item(i);

                    String title = this.getValue(e1, TAG_TITLE);
                    String link = this.getValue(e1, TAG_LINK);
                    String description = this.getValue(e1, TAG_DESRIPTION);
                    String pubdate = this.getValue(e1, TAG_PUB_DATE);
                    String guid = this.getValue(e1, TAG_GUID);
                    String image = this.getValue2(e1, img_content);
                    if (category=="sports"){
                        image = getImage(description);
                    }
                    switch (category){
                        case "trending":
                            image = getImage(description);
                            break;
                        case "breaking":
                            image = getImage(description);
                            break;
                        case "environment":
                            image = getImage(description);
                            break;
                        case "politics":
                            image = getImage(description);
                            break;
                        case "sports":
                            image = getImage(description);
                            break;
                        case "stock":
                            image = getImage(description);
                            break;
                        case "lifestyle":
                            image = getImage(description);
                            break;
                        case "health":
                            image = getImage(description);
                            break;
                        case "tech":
                            image = getImage(description);
                            break;
                        case "business":
                            image = getImage(description);
                            break;
                        case "entertainment":
                            image = getImage(description);
                            break;
                        case "weather":
                            image = getImage(description);
                            break;
                        case "art":
                            image = getImage(description);
                            break;
                        case "travel":
                            image = getImage(description);
                            break;
                        case "science":
                            image = getImage(description);
                            break;
                        case "food":
                            image = getImage(description);
                            break;
                        case "other":
                            image = getImage(description);
                            break;
                        default:
                            image = getImage(description);
                            break;

                    }
                    /*if(image==null){
                        Element elem=(Element) items.item(i);
                        Node child;
                        if (elem != null) {
                            if (elem.hasChildNodes()) {
                                for (child = elem.getFirstChild(); child != null; child = child
                                        .getNextSibling()) {
                                    if ("media:content".equals(child)) {
                                        Element contentElement = (Element) elem;
                                        if (contentElement.hasAttribute("url")) {
                                            image = contentElement.getAttribute("url");
                                            Log.e(TAG, "getRSSFeedItems: data:: " + image);
                                        }
                                    }

                                }
                            }
                        }

                    }*/

                    RSSItem rssItem = new RSSItem(title, link, description, pubdate, guid, category,image);
                    rssItem.setTitle(title);
                    rssItem.setLink(link);
                    rssItem.setDescription(description);
                    rssItem.setPubdate(pubdate);
                    rssItem.setGuid(guid);
                    rssItem.setImage(image);
                    // adding item to list
                    itemsList.add(rssItem);
//                    Log.e(TAG, "getRSSFeedItems: data:: " + image);
                }
            } catch (Exception e) {
                // Check log for errors
                Log.e(TAG, "parAgain: "+e );
                e.printStackTrace();
            }
            arrayList.addAll(itemsList);
            Collections.shuffle(arrayList);
            adapter.notifyDataSetChanged();
        }
    }

    public String getImage(String a){
        String data = a;
        String[] items = data.split("<img src=\"");
        String[] items2= items[1].split("\"");
        Log.e(TAG, "getImage: "+items2[0] );
        return items2[0];
    }

   /* public String getImage(String thing) {
        Log.e(TAG, "getImage: thing "+thing );
        int index = thing.lastIndexOf("jpg");
        int index2 = thing.lastIndexOf("http");
        String yourCuttedString = thing.substring(0, index);
        Log.e(TAG, "getImage: 1 "+index );
        yourCuttedString = yourCuttedString.substring(1, index2);
        Log.e(TAG, "getImage: 2 "+index2 );
       return "http"+yourCuttedString+"jpg";
    }*/
    public String getXmlFromUrl(final String url, final NewsAdapter adapter, final ArrayList<RSSItem> arrayList) {

        try {

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    xml = response;
//                    Log.e(TAG, "onResponse: found something " + xml);
                    parAgain(xml, adapter, arrayList);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onResponse: found nothing, reason: " + error.getLocalizedMessage());
                }
            });


            NetworkRequest.getInstance(context).addToRequestQueue(request);
//            Log.e(TAG, "onResponse: found something 22 " + xml);
            return xml;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Document getDomElement(String xml) {

        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("Error1: ", e.getMessage());
            return null;
        }catch (IOException e) {
            Log.e("Error3: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    Log.e(TAG, "getElementValue2: node item"+child.getPrefix());
                    Element e= (Element) elem;

                    if (e.hasAttribute("url")) {
                        String a=e.getAttribute("url");
                        Log.e(TAG, "getElementValue3: data:: " + a);
                        return a;
                    }
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
    public final String getElementValue2(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    Element e= (Element) child;
                    Log.e(TAG, "getElementValue2: node item"+child );
                    if (e.hasAttribute("url")) {
                        String a=e.getAttribute("url");
                        Log.e(TAG, "getElementValue2: data:: " + a);
                        return a;
                    }

                }
            }
        }
        return "";
    }
    public String getValue2(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue2(n.item(0));
    }
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}