package edu.pdx.cs410J.kamakshi;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;

/**
 * The main class that parses the command line and communicates with the
 * Phone Bill server using REST.
 *
 * @author Kamakshi Nagar
 */
public class Project4 {
    private static int portNumber = 0;
    private static String hostname = null;

    private static PhoneBill pb = new PhoneBill();
    private static PhoneCall pc = new PhoneCall();

    /**
     * Main program that parses the command line arguments, creates a
     * <code>PhoneCall</code>, and prints a description of the call to
     * standard out by invoking PhoneCall <code>toString</code> method.
     * Serves as a client to add and search the calls to/from server.
     *
     *
     * @throws IllegalArgumentException
     */
    public static void main(String[] args) throws IllegalArgumentException {


        boolean print = false;
        boolean host = false;
        boolean port = false;
        String customerName = null;
        String endTime;
        String startTime;

        switch (args.length) {
            case 0:
                usage("Missing command line arguments");
                break;

            case 1:
                switch (args[0]) {
                    case "-README":
                        printReadmeMessage();
                    case "-print":
                        usage("Missing Customer Name");
                    case "-host":
                        usage("Missing Host");
                    case "-port":
                        usage("Missing Port");
                    case "-search":
                        usage("Missing Customer Name");
                    default:
                        usage("Missing Caller Number");
                }
                break;
            case 2:
                for (int i = 0; i < 2; i++) {
                    if (args[i].equals("-README")) printReadmeMessage();
                }
                switch (args[0]) {
                    case "-print":
                        usage("Missing Caller Number");
                    case "-host":
                        usage("Missing Port");
                    case "-port":
                        usage("Missing Host");
                    case "-search":
                        usage("Missing StartDate");
                    default:
                        usage("Missing Callee Number");
                }
                break;
            case 3:
                for (int i = 0; i < 3; i++) {
                    if (args[i].equals("-README")) printReadmeMessage();
                }
                switch (args[0]) {
                    case "-print":
                        usage("Missing Callee Number");
                    case "-host":
                        usage("Missing Port");
                    case "-port":
                        usage("Missing Host");
                    case "-search":
                        usage("Missing StartTime");
                    default:
                        usage("Missing StartDate");
                }
                break;
            case 4:
                switch (args[0]) {
                    case "-print":
                        usage("Missing StartDate");
                    case "-host":
                        host=true;port=true; hostname=args[1];portNumber=validatePort(args[3]);
                        break;
                        //usage("Missing Customer Name");
                    case "-port":
                        host=true;port=true; hostname=args[3];portNumber=validatePort(args[1]);
                        break;
                        //usage("Missing Customer Name");
                    case "-search":
                        usage("Missing AM/PM");
                    default:
                        usage("Missing StartTime");
                }
                break;
            case 5:
                switch (args[0]) {
                    case "-print":
                        usage("Missing StartTime");
                    case "-host":
                        usage("Missing Caller Number");
                    case "-port":
                        usage("Missing Caller Number");
                    case "-search":
                        usage("Missing End Date");
                    default:
                        usage("Missing AM/PM");
                }
                break;
            case 6:
                switch (args[0]) {
                    case "-print":
                        usage("Missing AM/PM");
                    case "-host":
                        if(args[4].startsWith("-")){host=true;port=true;
                            hostname= args[1];portNumber=validatePort(args[3]); break;}
                        usage("Missing Callee Number");
                    case "-port":
                        if(args[4].startsWith("-")){host=true;port=true;
                            hostname= args[3];portNumber=validatePort(args[1]); break;}
                        usage("Missing Callee Number");
                    case "-search":
                        usage("Missing End Time");
                    default:
                        usage("Missing EndDate");
                }
                break;
            case 7:
                switch (args[0]) {
                    case "-print":
                        usage("Missing EndDate");
                    case "-host":
                        usage("Missing StartDate");
                    case "-port":
                        usage("Missing StartDate");
                    case "-search":
                        usage("Missing AM/PM");
                    default:
                        usage("Missing EndTime");
                }
                break;
            case 8:
                switch (args[0]) {
                    case "-print":
                        usage("Missing EndTime");
                    case "-search":
                        usage("Cant Search Without Host and Port");
                    default:
                        usage("Missing AM/PM");
                }
            default:
                int i = 0;
                if (args[0].startsWith("-")) {
                    if (args.length > 14) {
                        usage("Extraneous Argument");
                    }
                    switch (args[0]) {
                        case "-print":
                            i = 1;
                            print = true;
                            if (args[1].equals("-search")) {
                                usage("No calls can be added");
                            }
                            if (args[1].equals("-host")) {
                                i = 5;
                                host = true;
                                port = true;
                                hostname = args[i - 3];
                                portNumber = validatePort(args[i - 1]);
                            }
                            if (args[1].equals("-port")) {
                                i = 5;
                                host = true;
                                port = true;
                                hostname = args[i - 1];
                                portNumber = validatePort(args[i - 3]);
                            }
                            break;
                        case "-host":
                            i = 4;
                            host = true;
                            port = true;
                            hostname = args[i - 3];
                            portNumber = validatePort(args[i - 1]);
                            if (args[4].equals("-search")) {
                                i = 5;
                                hostname = args[i - 4];
                                portNumber = validatePort(args[i - 2]);
                                customerName = args[i];
                                startTime = (args[i + 1] + " " + args[i + 2] + " " + args[i + 3]);
                                endTime = (args[i + 4] + " " + args[i + 5] + " " + args[i + 6]);
                                searchOption(customerName, startTime, endTime);
                            }
                            if (args[4].equals("-print")) {
                                print = true;
                                i = 5;
                            }
                            break;
                        case "-port":
                            i = 4;
                            host = true;
                            port = true;
                            hostname = args[i - 1];
                            portNumber = validatePort(args[i - 3]);
                            if (args[4].equals("-search")) {
                                i = 5;
                                hostname = args[i - 2];
                                portNumber = validatePort(args[i - 4]);
                                customerName = args[i];
                                startTime = (args[i + 1] + " " + args[i + 2] + " " + args[i + 3]);
                                endTime = (args[i + 4] + " " + args[i + 5] + " " + args[i + 6]);
                                searchOption(customerName, startTime, endTime);
                            }
                            if (args[4].equals("-print")) {
                                print = true;
                                i = 5;
                            }
                            break;
                        case "-search":
                            if(args[1].equals("-print")){usage("No Calls can be added");}
                            if(args[1].equals("-host")){i=5; ;hostname = args[i - 3];
                                portNumber = validatePort(args[i - 1]);}
                            if(args[1].equals("-port")){i=5; ;hostname = args[i - 1];
                                portNumber = validatePort(args[i-3]);}

                            customerName = args[i];
                            startTime = (args[i + 1] + " " + args[i + 2] + " " + args[i + 3]);
                            endTime = (args[i + 4] + " " + args[i + 5] + " " + args[i + 6]);
                            searchOption(customerName, startTime, endTime);
                            break;
                        default:
                            usage("Invalid Command line Option");
                            break;
                    }
                } else if (args.length > 9) {
                    usage("Extraneous Argument");
                }

                pb = new PhoneBill(args[i]);
                customerName = pb.customerName;
                pc = new PhoneCall(args[i + 1], args[i + 2], args[i + 3] + " " + args[i + 4] + " " + args[i + 5], args[i + 6] + " " + args[i + 7] + " " + args[i + 8]);
        }

        if (print) {
            printPhoneCall();
        }
        if (host || port) {
            PhoneBillRestClient client = new PhoneBillRestClient(hostname, portNumber);
            HttpRequestHelper.Response response;
            try {
                if (customerName == null) {
                    response = client.getAllCalls();
                } else if (pc == null) {
                    response = client.getCalls(customerName);
                } else {
                    response = client.addPhoneCall(pb.customerName, pc.getCaller(), pc.getCallee(), pc.getStartTimeString(), pc.getEndTimeString());
                }
                checkResponseCode(HttpURLConnection.HTTP_OK, response);
                System.out.println(response.getContent());
            } catch (IOException e) {
                error("Some IO error " + e);
                e.printStackTrace();
            }
            System.exit(0);
        } else pb.addPhoneCall(pc);
        pc.toString();
        System.exit(0);
    }

    /**
     * This is Method to send Request to the server to find the calls
     * made in specified time.
     * @param c customer name
     * @param start start time
     * @param end end Time
     */
    private static void searchOption(String c, String start, String end) {
        PhoneBillRestClient client = new PhoneBillRestClient(hostname, portNumber);
        HttpRequestHelper.Response response;
        try {
            response = client.getCallsForDates(c, start, end);
            System.out.println(response.getContent());
        } catch (IOException e) {
            error("Some IO error " + e);
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Port number must be an Integer
     * @param arg the <code>String</code> for port number
     * @return validated port number
     */
    private static int validatePort(String arg) {
        try {
            portNumber = Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            error("Invalid port Number" + ex.getMessage());
            ex.printStackTrace();
        }
        return portNumber;
    }

    /**
     * Makes sure that the give response has the expected HTTP status code
     *
     * @param code     The expected status code
     * @param response The response from the server
     */
    private static void checkResponseCode(int code, HttpRequestHelper.Response response) {
        if (response.getCode() != code) {
            error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                    response.getCode(), response.getContent()));
        }
    }

    /**
     *Prints the error message for the program and exits.
     * @param message An error message to print
     */
    private static void error(String message) {
        PrintStream err = System.err;
        err.println("**Error Message " + message);
        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     *
     * @param message An error message to print
     */
    private static void usage(String message) {

        String usage = "usage: [options] <args> " +
                "args are (in this order)\n" +
                "  customer \n" +
                "  callerNumber : nnn-nnn-nnnn \n" +
                "  calleeNumber : nnn-nnn-nnnn \n" +
                "  startTime : MM/dd/yyyy hh:mm AM/PM\n" +
                "  endTime : MM/dd/yyyy hh:mm AM/PM\n" +
                "Options are (options may appear in this order)\n " +
                "-host hostname \n" +
                "-port port \n " +
                "-search \n" +
                "-print \n" +
                "-README \n";
        PrintStream err = System.err;
        err.println("** " + message);
        err.println(usage);
        System.exit(1);

    }

    /**
     * This Method has description message for -README command line Argument.
     * It describes the Project4 and the classes we will be using for it.
     */
    private static void printReadmeMessage() {
        System.out.print("This is a README for java Project4 : A REST-ful Phone Bill Web Service at Portland State University Summer 2015, created by Kamakshi Nagar.\n" +
                "The project is to generate a phone bill for the customers by maintaining the record of calls at given time. We enter customer name, phone number and callee's phone number for a given time, we record the call, which can be printed using the -print command.\n" +
                "A -pretty command can be used to pretty print the phone bill.\n" +
                "There is -host and -port to connect to the server and We can add and search Phone calls in the PhoneBill. \n" +
                "We have java files as Project4.java,PhoneBillRestClient.java,PhoneBillServlet.java,PrettyPrinter.java, PhoneBill.java, PhoneCall.java.\n" +
                "PhoneBill has Customer name for whom we are generating the Bill and PhoneCall has the callerNumber, calleeNumber, startTime and endTime of call.\n" +
                "Project4.java is the main file in which command line parsing is done, it has coding for README, print,textFile and pretty commands.\n");
        System.exit(0);
    }

    /**
     * This Method add call to Phone Bill and print a phone call to Standard Out.
     */
    private static void printPhoneCall() {
        pb.addPhoneCall(pc);
        System.out.println(pc.toString());
    }
}