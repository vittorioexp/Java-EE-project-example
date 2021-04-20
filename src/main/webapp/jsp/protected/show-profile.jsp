<%@ page import="it.unipd.dei.yourwaytoitaly.resource.Tourist" %>
<%@ page import="it.unipd.dei.yourwaytoitaly.resource.Company" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unipd.dei.yourwaytoitaly.resource.Booking" %>
<%@ page import="it.unipd.dei.yourwaytoitaly.resource.Advertisement" %>
<!--
Copyright 2021 University of Padua, Italy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author: Marco Basso
Author: Vittorio Esposito
Author: Francesco Giurisato
Author: Matteo Piva
Version: 1.0
Since: 1.0
-->


<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>show profile</title>
    </head>
    <body>
    <header>
        <h1>Show profile</h1>
    </header>
    <nav>
        <a href="${pageContext.request.contextPath}/index">Home</a>

        <c:choose>
            <c:when test="${empty sessionScope.Authorization}">
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/user/do-logout">Logout</a>
                <a href="${pageContext.request.contextPath}/user/do-edit">Edit profile</a>
            </c:otherwise>
        </c:choose>

        <a href="${pageContext.request.contextPath}/html/contacts.html">Contacts</a>

    </nav>


    <c:choose>
        <c:when test="${userType}">
            </br>
                <p>Name: ${user.name}</p>
                <p>Surname: ${user.surname}</p>
                <p>Email: ${user.email}</p>
                <p>Phone number: ${user.phoneNumber}</p>
                <p>Birth date: ${user.birthDate}</p>
                <p>Address: ${user.address}</p>
                <p>City: ${user.idCity}</p>
                <p>Your score is ${score} </p>
            </br>
        <p></p>
            <table>
                <tr>
                    <td>Booking date   </td>
                    <td>Item booked    </td>
                    <td>Booking state  </td>
                </tr>
            </table>
            <%
                List<Booking> bookinglist = (List) request.getAttribute("bookingList");
                // TODO: for each element, show also the advertisement title (for clarity reasons)
            %>
            <table>
            <c:forEach items="<%=bookinglist%>" var="booking">
                    <tr>
                        <td>${booking.date}    </td>
                        <td>${booking.numBooking}    </td>
                        <td>${booking.state}    </td>
                        <td>
                            <form id="deleteBookingForm" name="deleteBookingForm" action = "<c:url value="/booking-delete"/>" method="DELETE">
                                <input type="hidden" name="idAdvertisement" value="${booking.idAdvertisement}"/>
                                <button type="submit" >Delete</button><br/>
                            </form>
                        </td>
                    </tr>
            </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            </br>
            <p>Name: ${user.name}</p>
            <p>Email: ${user.email}</p>
            <p>Phone number: ${user.phoneNumber}</p>
            <p>Address: ${user.address}</p>
            <p>City: ${user.idCity}</p>
            </br>
            <form method="GET" action="<c:url value="/adv-do-create"/>">
                <button type="submit">New advertisement</button>
            </form>
            <%
                List<Advertisement> advertisementList = (List<Advertisement>) request.getAttribute("advertisementList");
                // TODO: html method DELETE is converted into GET (and a JSON obj must be provided)
            %>
            </br>
            <table>
            <c:forEach items="${advertisementList}" var="adv">
                    <tr>
                        <td>${adv.title}   </td>
                        <td>${adv.dateStart}   </td>
                        <td>${adv.dateEnd}   </td>
                        <td>${adv.numTotItem}   </td>
                        <td>${adv.price}   </td>
                        <td>
                            <form id="gotoEditAdvertisementForm" name="gotoEditAdvertisementForm" method="GET"
                                  action="<c:url value="/adv-edit"/>">
                                <input type="hidden" name="idAdvertisement" value="${adv.idAdvertisement}">
                                <button type="submit">Edit</button><br/>
                            </form>
                        </td>
                        <td>
                            <form id="gotoShowAdvertisementForm" name="gotoShowAdvertisementForm" method="GET"
                                  action="<c:url value="/adv-show/${adv.idAdvertisement}"/>" >
                                <button type="submit">Info</button><br/>
                            </form>
                        </td>
                        <td>
                            <form id="deleteAdvertisementForm" name="deleteAdvertisementForm" method="DELETE"
                                  action="<c:url value="/adv/${adv.idAdvertisement}"/>" >
                                <button type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
            </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>

     <div>
         <c:import url="/jsp/include/show-message.jsp"/>
     </div>

    </body>
</html>