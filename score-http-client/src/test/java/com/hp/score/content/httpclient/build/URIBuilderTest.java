package com.hp.score.content.httpclient.build;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: tusaa
 * Date: 7/16/14
 */
public class URIBuilderTest {
    private static final String URL = "http://localhost:8002";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void buildURIWithEncoding() throws URISyntaxException {
        String queryParams = "param 1=value1&param 2=value2";
        URI uri = new URIBuilder().setUrl(URL).setQueryParams(queryParams).buildURI();
        assertEquals(URL + "?param+1=value1&param+2=value2", uri.toString());
    }

    @Test
    public void buildURIWithEncoding1() throws URISyntaxException {
        String queryParams = "param 1=&param 2=";
        URI uri = new URIBuilder().setUrl(URL).setEncodeQueryParams("true").setQueryParams(queryParams).buildURI();
        assertEquals(URL + "?param+1=&param+2=", uri.toString());
    }

    @Test
    public void buildURIWithoutEncoding() throws UnsupportedEncodingException, URISyntaxException {
        String queryParams = "param1=The+string+%C3%BC%40foo-bar";
        URI uri = new URIBuilder().setUrl(URL).setEncodeQueryParams("false").setQueryParams(queryParams).buildURI();
        assertEquals(URL + "?param1=The+string+%C3%BC%40foo-bar", uri.toString());
    }

    @Test
    public void buildURIWithoutQueryParams() throws UnsupportedEncodingException, URISyntaxException {
        URI uri = new URIBuilder().setUrl(URL).setEncodeQueryParams("false").setQueryParams(null).buildURI();
        assertEquals(URL, uri.toString());
    }

    @Test
    public void buildURIWithException() throws UnsupportedEncodingException, URISyntaxException {
        String url = "http://[localhost]:8002";
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("could not parse");
        new URIBuilder().setUrl(url).setEncodeQueryParams("false").setQueryParams("").buildURI();
    }
}