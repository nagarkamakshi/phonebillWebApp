package edu.pdx.cs410J.kamakshi;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send key/value pairs.
 */
public class PhoneBillRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "phonebill";
    private static final String SERVLET = "calls";

    private final String url;

    /**
     * Creates a client to the Phone Bil REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public PhoneBillRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     * Returns all calls from the server
     */
    public Response getAllCalls() throws IOException
    {
        return get(this.url);
    }

    /**
     * Returns all values for the given customerName
     */
    public Response getCalls( String customerName ) throws IOException
    {
        return get(this.url, "customer", customerName);
    }

    /**
     * This method sends request to server to get Calls made in specified time.
     * @param customerName Name of the customer
     * @param startTime start time for range
     * @param endTime end time for range
     * @return HttpResponse from server , PhoneBill with calls at specified time.
     * @throws IOException
     */
    public Response getCallsForDates( String customerName,String startTime,String endTime ) throws IOException
    {
        return get(this.url, "customer", customerName,"startTime", startTime,"endTime", endTime);
    }

    /**
     *Adds the Phone Call to the PhoneBill.
     * @return Store call in the server.
     * @throws IOException
     */
    public Response addPhoneCall(String customerName,String caller,String callee, String startTime,String endTime ) throws IOException
    {
        return post( this.url,"customerName", customerName,"caller",caller,"callee"
                ,callee,"startTime",startTime,"endTime",endTime);

    }
}
