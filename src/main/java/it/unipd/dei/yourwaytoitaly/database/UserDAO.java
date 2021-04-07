package it.unipd.dei.yourwaytoitaly.database;


import it.unipd.dei.yourwaytoitaly.resource.Advertisement;
import it.unipd.dei.yourwaytoitaly.resource.Company;
import it.unipd.dei.yourwaytoitaly.resource.Tourist;
import it.unipd.dei.yourwaytoitaly.resource.User;
import it.unipd.dei.yourwaytoitaly.utils.DataSourceProvider;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * UserDAO.java
 * Class for:
 * - inserting a User (company/tourist)
 * - editing a User (company/tourist)
 * - searching and returning a User (company/tourist) by EMAIL_T and PASSWORD
 * - returning the score accumulated by a User (Tourist)
 * inside the database
 *
 * @author Vittorio Esposito
 * @author Marco Basso
 * @author Matteo Piva
 * @version 1.0
 * @since 1.0
 */

public class UserDAO extends AbstractDAO{

    /**
     * Creates a new user (company/tourist).
     *
     * @return the just created user
     *
     * @throws SQLException
     *             if any error occurs while creating users.
     */
    public static User createUser(User user) throws SQLException, NamingException {
        final String STATEMENT_TOURIST =
                "INSERT INTO YWTI.Tourist (email_t, surname, name, birth_date, phone_number, address, password, ID_city)" +
                        "SELECT ?, ?, ?, ?, ?, ?, ?, ID_CITY" +
                        "FROM YWTI.City WHERE City.name = ? RETURNING *;";


        final String STATEMENT_COMPANY =
                "INSERT INTO YWTI.Company (email_c, name_c, phone_number, address, password, ID_city)" +
                        "SELECT ?, ?, ?, ?, ?, ID_CITY" +
                        "FROM YWTI.City WHERE City.name = ? RETURNING *;";

        Connection con = DataSourceProvider.getDataSource().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        User u = null;

        try {
            if (user instanceof Tourist) {
                pstmt = con.prepareStatement(STATEMENT_TOURIST);
                pstmt.setString(1, ((Tourist) user).getEmail());
                pstmt.setString(2, ((Tourist) user).getSurname());
                pstmt.setString(3, ((Tourist) user).getName());
                pstmt.setDate(4, ((Tourist) user).getBirthDate());
                pstmt.setString(5, ((Tourist) user).getPhoneNumber());
                pstmt.setString(6, ((Tourist) user).getAddress());
                pstmt.setString(7, ((Tourist) user).getPassword());

                rs = pstmt.executeQuery();

                while (rs.next()) {
                    u = new Tourist(
                            rs.getString("email_t"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone_number"),
                            rs.getInt("ID_city"),
                            rs.getString("surname"),
                            rs.getDate("birth_date"));
                }
            } else if (user instanceof Company) {
                pstmt = con.prepareStatement(STATEMENT_COMPANY);
                pstmt.setString(1, ((Company) user).getEmail());
                pstmt.setString(2, ((Company) user).getName());
                pstmt.setString(3, ((Company) user).getPhoneNumber());
                pstmt.setString(4, ((Company) user).getAddress());
                pstmt.setString(5, ((Company) user).getPassword());
                pstmt.setInt(6, ((Company) user).getIdCity());
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    u = new Company(
                            rs.getString("email_c"),
                            rs.getString("password"),
                            rs.getString("address"),
                            rs.getString("phone_number"),
                            rs.getInt("ID_city"),
                            rs.getString("name_c"));
                }

            }
        } finally {
            //close all the possible resources
            cleaningOperations(pstmt, rs, con);
        }
        return u;
    }

    /**
     * Edits a user (company/tourist).
     *
     * @return the just created user
     *
     * @throws SQLException
     *             if any error occurs while creating users.
     */
    public static void editUser(User user) throws SQLException, NamingException {
        final String STATEMENT_TOURIST_EDIT =
                "UPDATE TWTI.TOURIST SET password = MD5(?) , phone_number = ? WHERE email_t = ?;";


        /**
         * The SQL statement to be executed
         */
        final String STATEMENT_COMPANY_EDIT =
                "UPDATE TWTI.COMPANY SET password = MD5(?) , phone_number = ? WHERE email_c = ?;";
        Connection con = DataSourceProvider.getDataSource().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (user instanceof Tourist) {
                pstmt = con.prepareStatement(STATEMENT_TOURIST_EDIT);
                pstmt.setString(1, ((Tourist) user).getPassword());
                pstmt.setString(2, ((Tourist) user).getPhoneNumber());
                pstmt.setString(3, ((Tourist) user).getEmail());
                rs = pstmt.executeQuery();

            } else if (user instanceof Company) {
                pstmt = con.prepareStatement(STATEMENT_COMPANY_EDIT);
                pstmt.setString(1, ((Company) user).getPassword());
                pstmt.setString(2, ((Company) user).getPhoneNumber());
                pstmt.setString(3, ((Company) user).getEmail());
                rs = pstmt.executeQuery();

            }
        } finally {
            //close all the possible resources
            cleaningOperations(pstmt, rs, con);
        }
        return;
    }


    /**
     * Searches user by email and password
     *
     * @return a user objects matching the parameter.
     *
     * @throws SQLException
     *             if any error occurs while searching.
     */
    public static User searchUserLogin(final String reqEmail, final String reqPassword) throws SQLException, NamingException {
        final String STATEMENT =
                "SELECT email_t, surname, name, birth_date, phone_number, address, password, ID_city " +
                        "FROM YWTI.TOURIST " +
                        "WHERE email_t = ? AND password = MD5(?);";

        Connection con = DataSourceProvider.getDataSource().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // the results of the search
        User user = null;

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setString(1, reqEmail);
            pstmt.setString(2, reqPassword);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                user = new Tourist(
                        rs.getString("email_t"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getInt("ID_city"),
                        rs.getString("surname"),
                        rs.getDate("birth_date"));
            }
        } finally {
            //close all the possible resources
            cleaningOperations(pstmt, rs, con);
        }

        return user;
    }

    /**
     * Searches the user score.
     *
     * @return a TypeAdvertisement objects matching with the Id of the type advertisement.
     * @throws SQLException if any error occurs while searching for a type advertisement.
     */
    public static int searchUserScore(String reqIdTourist) throws SQLException, NamingException {

        final String STATEMENT = "SELECT *\n" +
                "FROM YWTI.BOOKING JOIN YWTI.ADVERTISEMENT ON ADVERTISEMENT.ID_ADVERTISEMENT = BOOKING.ID_ADVERTISEMENT\n" +
                "WHERE BOOKING.email_t = ? AND BOOKING.state = 'SUCCESSFUL';";
        Connection con = DataSourceProvider.getDataSource().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Advertisement> bookings = null;
        int totalScore = 0;

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setString(1, reqIdTourist);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(new Advertisement(
                        rs.getInt("ID_advertisement"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("score"),
                        rs.getInt("price"),
                        rs.getInt("num_tot_item"),
                        rs.getDate("date_start"),
                        rs.getDate("date_end"),
                        rs.getTime("time_start"),
                        rs.getTime("time_end"),
                        rs.getString("email_c"),
                        rs.getInt("ID_type")));
            }

            for (Advertisement adv : bookings) {
                totalScore = +adv.getScore();
            }
        } finally {
            //close all the possible resources
            cleaningOperations(pstmt, rs, con);
        }
        return totalScore;
    }


}