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
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static String img_content="media";
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
                    String image=getImage2(description);
                    String image2=this.getValue2(e1, TAG_GUID);
//                    String image=this.getValue2(e1, img_content);
/*
                    switch (category){
                        case "trending":
                            image = getImage(e1,description);
                            break;
                        case "breaking":
                            image = getImage(e1,description);
                            break;
                        case "environment":
                            image = getImage(e1,description);
                            break;
                        case "politics":
                            image = getImage(e1,description);
                            break;
                        case "sports":
                            image = getImage(e1,description);
                            break;
                        case "stock":
                            image = getImage(e1,description);
                            break;
                        case "lifestyle":
                            image = getImage(e1,description);
                            break;
                        case "health":
                            image = getImage(e1,description);
                            break;
                        case "tech":
                            image = getImage(e1,description);
                            break;
                        case "business":
                            image = getImage(e1,description);
                            break;
                        case "entertainment":
                            image = getImage(e1,description);
                            break;
                        case "weather":
                            image = getImage(e1,description);
                            break;
                        case "art":
                            image = getImage(e1,description);
                            break;
                        case "travel":
                            image = getImage(e1,description);
                            break;
                        case "science":
                            image = getImage(e1,description);
                            break;
                        case "food":
                            image = getImage(e1,description);
                            break;
                        case "other":
                            image = getImage(e1,description);
                            break;
                        default:
                            image = getImage(e1,description);
                            break;

                    }*/
                    String[] manychannels=rss_feed_xml.split("<"+TAG_CHANNEL+">");
                    if (2<=manychannels.length){
                        String[] manyitems=manychannels[1].split("<"+TAG_ITEM+">");
                        if (i<=(manyitems.length-1)){
                            String[] manyimages=manyitems[i+1].split(".jpg");
//                            Log.e(TAG, "manyimages: " +manyimages[0]);
                            if (2<=manyimages.length){
                                String org=manyimages[0];
                                String[] bits = org.split("\"");
                                String lastOne = bits[bits.length-1];

                                Log.e(TAG, "Final image 1: "+lastOne+".jpg");
                                Log.e(TAG, "Final image 2: "+title );
                                image=lastOne+".jpg";
                            }else{
                                Log.e(TAG, "Final: Nothing" );
                            }
                        }else{
                            Log.e(TAG, "getImage: ");
                        }
                    }else{
                        Log.e(TAG, "getImage: ");
                    }


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

    public String getImage(Element e1, String a){
        String image= this.getValue2(e1, img_content);




        String data = a;
        String[] items = data.split("<img src=\"");
        String[] items2= items[1].split("\"");
        Log.e(TAG, "getImage: "+items2[0] );
        return items2[0];
    }

    public String getImage2(String a){
        String data = a;
        String[] items2;
        String b="";
        String[] items = data.split("<img src=\"");
        if (2<=items.length){
            items2= items[1].split("\"");
            b=items2[0];
        }else{
            Log.e(TAG, "getImage: "+a);
        }
        return b;
    }
    public void decode(String res){
        String title = "";
        String link = "";
        String description = "";
        String pubdate = "";
        String guid = "";
        String image = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            InputStream is = new ByteArrayInputStream(res.getBytes());
            xpp.setInput(is,null);
            // xpp.setInput(getInputStream(url), "UTF-8");

            boolean insideItem = false;

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem) {
                            Log.e("Title is", xpp.nextText());
                            title=xpp.nextText();
                        }
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem) {
                            Log.e("Link is", xpp.nextText());
                            link=xpp.nextText();
                        }
                    } else if (xpp.getName().equalsIgnoreCase("comments")) {
                        if (insideItem) {
                            Log.e("Comment is.", xpp.nextText());
                        }
                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        if (insideItem) {
                            Log.e("Publish Date is.", xpp.nextText());
                        }
                    } else if (xpp.getName().equalsIgnoreCase(TAG_DESRIPTION)) {
                        if (insideItem) {
                            Log.e("Media TAG_DESRIPTION", xpp.nextText());
                            description=xpp.nextText();
                        }
                    } else if (xpp.getName().equalsIgnoreCase("media:content")) {
                        if (insideItem){
                            Log.e("Media Content url is.", xpp.getAttributeValue(null, "url"));
                            image=xpp.getAttributeValue(null, "url");
                            if (image.contentEquals("")||image==null){
                                getImage2(description);
                            }
                        }
                    } else if (xpp.getName().equalsIgnoreCase("media:title")) {
                        if (insideItem) {
                            Log.e("Media Content title.", xpp.nextText());
                        }
                    }
                    /*RSSItem rssItem = new RSSItem(title, link, description, pubdate, guid, category,image);
                    rssItem.setTitle(title);
                    rssItem.setLink(link);
                    rssItem.setDescription(description);
                    rssItem.setPubdate(pubdate);
                    rssItem.setGuid(guid);
                    rssItem.setImage(image);
                    // adding item to list
                    itemsList.add(rssItem);*/
//                    Log.e(TAG, "getRSSFeedItems: data:: " + image);
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                }

                eventType = xpp.next(); /// move to next element
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
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
//                    decode(xml);

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
                StringBuilder sb = new StringBuilder();

                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
//                    DOMImplementationLS lsImpl = (DOMImplementationLS)elem.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
//
//                    LSSerializer lsSerializer = lsImpl.createLSSerializer();
//                    sb.append(lsSerializer.writeToString(child));
//                    Log.e(TAG, "getElementValue2: node item"+child.getPrefix());
//                    Element e= (Element) elem;
//
//                    Log.e(TAG, "getElementValue2: node item"+e.getAttributeNS(null,"url") );
//
//                    if (e.hasAttribute("url")) {
//                        String a=e.getAttribute("url");
//                        Log.e(TAG, "getElementValue3: data:: " + a);
//                        return a;
//                    }
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                    if (child.getNodeType() == Node.ATTRIBUTE_NODE) {
                        String key = child.getAttributes().getNamedItem("url").getNodeValue();
                        Log.e(TAG, "getElementValue: "+key );
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
                StringBuilder sb = new StringBuilder();

                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
//                    DOMImplementationLS lsImpl = (DOMImplementationLS)elem.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
//
//                    LSSerializer lsSerializer = lsImpl.createLSSerializer();
//                    sb.append(lsSerializer.writeToString(child));
//                    Log.e(TAG, "getElementValue2: node item"+child.getPrefix());
//                    Element e= (Element) elem;
//
//                    Log.e(TAG, "getElementValue2: node item"+e.getAttributeNS(null,"url") );
//
//                    if (e.hasAttribute("url")) {
//                        String a=e.getAttribute("url");
//                        Log.e(TAG, "getElementValue3: data:: " + a);
//                        return a;
//                    }
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                    if (child.getNodeType() == Node.ATTRIBUTE_NODE) {
                        String key = child.getAttributes().getNamedItem("url").getNodeValue();
                        Log.e(TAG, "getElementValue: "+key );
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
    public String getValue2(Element elem, String str) {
//        NodeList elem = item2.getChildNodes();
        if (elem != null) {
            Node child;
            if (elem.hasChildNodes()) {
                StringBuilder sb = new StringBuilder();

                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
//                    DOMImplementationLS lsImpl = (DOMImplementationLS)elem.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
//
//                    LSSerializer lsSerializer = lsImpl.createLSSerializer();
//                    sb.append(lsSerializer.writeToString(child));
//                    Log.e(TAG, "getElementValue2: node item"+child.getPrefix());
//                    Element e= (Element) elem;
//
//                    Log.e(TAG, "getElementValue2: node item"+e.getAttributeNS(null,"url") );
//
//                    if (e.hasAttribute("url")) {
//                        String a=e.getAttribute("url");
//                        Log.e(TAG, "getElementValue3: data:: " + a);
//                        return a;
//                    }
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                    if (child.getNodeType() == Node.ATTRIBUTE_NODE) {
                        String key = child.getAttributes().getNamedItem("url").getNodeValue();
                        Log.e(TAG, "getElementValuex: "+key );
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}