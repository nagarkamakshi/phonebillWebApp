package edu.pdx.cs410J.kamakshi;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>PhoneBill</code>.  It extends <code>HttpServlet</code> and has
 * doGet() and doPost() method.
 *
 * @author Kamakshi Nagar
 * @see PrettyPrinter,PhoneBill,PhoneCall
 */
public class PhoneBillServlet extends HttpServlet
{
    private PhoneBill bill = new PhoneBill();
    private Map<String,PhoneBill> phoneBillMap = new HashMap<>();

    public void init(ServletConfig servletConfig) throws ServletException {
        bill = new PhoneBill();
    }

    /**
     * Handles an HTTP GET request from a client by writing the calls of a Customer's PhoneBill
     * specified in the "customerName" HTTP parameter to the HTTP response.  If the "CustomerName",
     * "StartTime" And "endTime" is specified then all calls that occurred between given time
     * will be Pretty Printed to the HTTP response.
     *
     * @throws ServletException, IOException
     */

    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );
        String url = request.getRequestURI();
        String last = url.substring(url.lastIndexOf('/')+1, url.length());
        String name= request.getParameter("customer");
        String start= request.getParameter("startTime");
        String end = request.getParameter("endTime");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        dateFormat.setLenient(false);
        Date newDate= null; Date newDate1 = null;
        PrintWriter pw = new PrintWriter(response.getWriter());

        // Get the PhoneCalls for the customer.
        if(name!= null && start==null && end==null){
            if(name.equals(bill.customerName)){
            AbstractPhoneBill pb = this.phoneBillMap.get(name);
            new PrettyPrinter(pw).dump(pb);
            }
            else response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bill not found for the Customer");
        }
         // Search the Calls Made between given startTime and endTime.
        else if(start!=null && end !=null){
            String startTime = validateTime(start, response);
            String endTime = validateTime(end,response);

            try {
                newDate = dateFormat.parse(startTime);
                newDate1 = dateFormat.parse(endTime);
            } catch (ParseException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Date Time");
                System.exit(1);
                e.printStackTrace();}
            final Date finalNewDate = newDate;
            final Date finalNewDate1 = newDate1;
            Collection<AbstractPhoneCall> collect = bill.getPhoneCalls().
                    stream().
                    filter(bill -> bill.getStartTime().getTime() >= finalNewDate.getTime() && bill.getStartTime().getTime() <= finalNewDate1.getTime()).
                    collect(Collectors.toList());
            if(collect.size()>0) {
                PhoneBill bil = new PhoneBill();
                if(!bill.customerName.equals(name)){response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bill not found for the Customer Name");}
                bil.customerName= name;
                collect.forEach(bil::addPhoneCall);
                new PrettyPrinter(new PrintWriter(response.getWriter(), true)).dump(bil);
            } else
               response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Could not find any matching calls");
        } else if(last.equals("calls")){
            Collection<AbstractPhoneCall> call = bill.getPhoneCalls();
            writePhoneCalls(response,call.toArray(new PhoneCall[call.size()]));
            response.setStatus( HttpServletResponse.SC_OK );
        }
        else response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Malformed Name "+ last);
    }

    /**
     * Handles an HTTP POST request by storing the calls in the PhoneBill specified by the
     * request parameters.  It Pretty Prints the Calls to the HTTP response.
     *
     * @throws IOException, ServletException
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );
        PrintWriter p= response.getWriter();
        String nm = request.getParameter("customerName");
        //check the different customer name
       if(bill.customerName!=null && !bill.customerName.equals(nm) ){
           p.println("Name does not Match..Its "+ bill.customerName+"'s Bill..");
           response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Malformed Name ");
        }
       else
          createPhoneCall(request,response);
        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * This is the helper Method for the Post request.
     * Adds calls to the Phone Calls and Map Phone Bill to the Client.
     *
     * @param request PhoneCalls arguments
     * @param response PhoneBill
     * @throws IOException
     */
    private void createPhoneCall(HttpServletRequest request, HttpServletResponse response) throws IOException{

        PhoneCall phoneCall= new PhoneCall();
        //Get the Parameter Values from request
        String customer = getParameter("customerName", request);
        bill.setCustomerName(getParameter("customerName",request));
        phoneCall.setCaller(getParameter("caller", request));
        phoneCall.setCallee(getParameter("callee", request));
        phoneCall.setStartTimeString(getParameter("startTime",request));
        phoneCall.setEndTimeString(getParameter("endTime", request));

       try{
        phoneCall.toString();}
       catch(Exception e){
           response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Some Error in Parameter"+ e);
       }

        bill.addPhoneCall(phoneCall); // Add calls to the Bill.
        this.phoneBillMap.put(customer,bill); // Map Customer to PhoneBill
        writePhoneCalls(response, phoneCall); //Pretty Print the Phone Bill

    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
        throws IOException
    {
        PrintWriter pw = response.getWriter();
        pw.println( Messages.missingRequiredParameter(parameterName));
        pw.flush();
        
        response.setStatus( HttpServletResponse.SC_PRECONDITION_FAILED );
    }

    /**
     * Pretty Prints the PhoneCalls of the PhoneBill to the HTTP response.
     *
     * @throws IOException
     */
    private void writePhoneCalls( HttpServletResponse response,PhoneCall... phoneCalls) throws IOException
    {
        PrintWriter pw = new PrintWriter(response.getWriter() ,true);
        for(PhoneCall phoneCall: phoneCalls){
            bill.addPhoneCall(phoneCall);
        }
        new PrettyPrinter(pw).dump(bill);
    }


    /**
     * Validates the Date Time <code>String</code> of the request Parameter.
     * @param time <code>String</code>  Parameter fot startTime and endTime
     * @param response HTTP Response
     * @return validated <code>String</code>
     * @throws IOException
     */
    private String validateTime(String time,HttpServletResponse response) throws IOException{

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        dateFormat.setLenient(false);
        Date newDate= null;
        try {
            newDate = dateFormat.parse(time);

        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid Date Time \n");
            e.printStackTrace();}

        int f= DateFormat.SHORT;
        DateFormat df = DateFormat.getDateTimeInstance(f, f, Locale.ENGLISH);
        time = df.format(newDate);

        return time;
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (value == null || "".equals(value)) {
        return null;

      } else {
        return value;
      }
    }

}
