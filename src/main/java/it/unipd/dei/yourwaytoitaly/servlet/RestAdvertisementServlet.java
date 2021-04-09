package it.unipd.dei.yourwaytoitaly.servlet;

public final class RestAdvertisementServlet extends AbstractDatabaseServlet {
    // .../show-advertisement/{idAdvertisement}
    // .../show-advertisement-list/
    // .../show-user
    // .../show-bookings
    // .../login , register

    // servlet > RestAdvertisementServlet.java GET PUT
    // rest > AdvertisementRestResource.java
    /**
     * The JSON MIME media type
     */
    private static final String JSON_MEDIA_TYPE = "application/json";

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The any MIME media type
     */
    private static final String ALL_MEDIA_TYPE = "*/*";

    @Override
    protected final void service(final HttpServletRequest req, final HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType(JSON_UTF_8_MEDIA_TYPE);
        final OutputStream out = res.getOutputStream();

        try {
            // if the request method and/or the MIME media type are not allowed, return.
            // Appropriate error message sent by {@code checkMethodMediaType}
            if (!checkMethodMediaType(req, res)) {
                return;
            }

            // if the requested resource was an Employee, delegate its processing and return
            if (processEmployee(req, res)) {
                return;
            }

            // if none of the above process methods succeeds, it means an unknow resource has been requested
            final Message m = new Message("Unknown resource requested.", "E4A6",
                    String.format("Requested resource is %s.", req.getRequestURI()));
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            m.toJSON(out);
        } finally {
            // ensure to always flush and close the output stream
            out.flush();
            out.close();
        }
    }

    /**
     * Checks that the request method and MIME media type are allowed.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request method and the MIME type are allowed; {@code false} otherwise.
     *
     * @throws IOException if any error occurs in the client/server communication.
     */
    private boolean checkMethodMediaType(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException {

        final String method = req.getMethod();
        final String contentType = req.getHeader("Content-Type");
        final String accept = req.getHeader("Accept");
        final OutputStream out = res.getOutputStream();

        Message m = null;

        if(accept == null) {
            m = new Message("Output media type not specified.", "E4A1", "Accept request header missing.");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(out);
            return false;
        }

        if(!accept.contains(JSON_MEDIA_TYPE) && !accept.equals(ALL_MEDIA_TYPE)) {
            m = new Message("Unsupported output media type. Resources are represented only in application/json.",
                    "E4A2", String.format("Requested representation is %s.", accept));
            res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            m.toJSON(out);
            return false;
        }

        switch(method) {
            case "GET":
            case "DELETE":
                // nothing to do
                break;

            case "POST":
            case "PUT":
                if(contentType == null) {
                    m = new Message("Input media type not specified.", "E4A3", "Content-Type request header missing.");
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    m.toJSON(out);
                    return false;
                }

                if(!contentType.contains(JSON_MEDIA_TYPE)) {
                    m = new Message("Unsupported input media type. Resources are represented only in application/json.",
                            "E4A4", String.format("Submitted representation is %s.", contentType));
                    res.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    m.toJSON(out);
                    return false;
                }

                break;
            default:
                m = new Message("Unsupported operation.",
                        "E4A5", String.format("Requested operation %s.", method));
                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                m.toJSON(out);
                return false;
        }

        return true;
    }


    /**
     * Checks whether the request if for an {@link Employee} resource and, in case, processes it.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the request was for an {@code Employee}; {@code false} otherwise.
     *
     * @throws IOException if any error occurs in the client/server communication.
     */
    private boolean processEmployee(HttpServletRequest req, HttpServletResponse res) throws IOException {

        final String method = req.getMethod();
        final OutputStream out = res.getOutputStream();

        String path = req.getRequestURI();
        Message m = null;

        // the requested resource was not an employee
        if(path.lastIndexOf("rest/employee") <= 0) {
            return false;
        }

        try {
            // strip everyhing until after the /employee
            path = path.substring(path.lastIndexOf("employee") + 8);

            // the request URI is: /employee
            // if method GET, list employees
            // if method POST, create employee
            if (path.length() == 0 || path.equals("/")) {

                switch (method) {
                    case "GET":
                        new EmployeeRestResource(req, res, getDataSource().getConnection()).listEmployee();
                        break;
                    case "POST":
                        new EmployeeRestResource(req, res, getDataSource().getConnection()).createEmployee();
                        break;
                    default:
                        m = new Message("Unsupported operation for URI /employee.",
                                "E4A5", String.format("Requested operation %s.", method));
                        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        m.toJSON(res.getOutputStream());
                        break;
                }
            } else {
                // the request URI is: /employee/salary/{salary}
                if (path.contains("salary")) {
                    path = path.substring(path.lastIndexOf("salary") + 6);

                    if (path.length() == 0 || path.equals("/")) {
                        m = new Message("Wrong format for URI /employee/salary/{salary}: no {salary} specified.",
                                "E4A7", String.format("Requesed URI: %s.", req.getRequestURI()));
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        m.toJSON(res.getOutputStream());
                    } else {
                        switch (method) {
                            case "GET":

                                // check that the parameter is actually an integer
                                try {
                                    Integer.parseInt(path.substring(1));

                                    new EmployeeRestResource(req, res, getDataSource().getConnection()).searchEmployeeBySalary();
                                } catch (NumberFormatException e) {
                                    m = new Message(
                                            "Wrong format for URI /employee/salary/{salary}: {salary} is not an integer.",
                                            "E4A7", e.getMessage());
                                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                    m.toJSON(res.getOutputStream());
                                }

                                break;
                            default:
                                m = new Message("Unsupported operation for URI /employee/salary/{salary}.", "E4A5",
                                        String.format("Requested operation %s.", method));
                                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                m.toJSON(res.getOutputStream());
                                break;
                        }
                    }
                } else {
                    // the request URI is: /employee/{badge}
                    try {

                        // check that the parameter is actually an integer
                        Integer.parseInt(path.substring(1));

                        switch (method) {
                            case "GET":
                                new EmployeeRestResource(req, res, getDataSource().getConnection()).readEmployee();
                                break;
                            case "PUT":
                                new EmployeeRestResource(req, res, getDataSource().getConnection()).updateEmployee();
                                break;
                            case "DELETE":
                                new EmployeeRestResource(req, res, getDataSource().getConnection()).deleteEmployee();
                                break;
                            default:
                                m = new Message("Unsupported operation for URI /employee/{badge}.",
                                        "E4A5", String.format("Requested operation %s.", method));
                                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                                m.toJSON(res.getOutputStream());
                        }
                    } catch (NumberFormatException e) {
                        m = new Message("Wrong format for URI /employee/{badge}: {badge} is not an integer.",
                                "E4A7", e.getMessage());
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        m.toJSON(res.getOutputStream());
                    }
                }
            }
        } catch(Throwable t) {
            m = new Message("Unexpected error.", "E5A1", t.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }

        return true;

    }
}
