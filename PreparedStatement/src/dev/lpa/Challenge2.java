package dev.lpa;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

record OrderDetail(int orderDetailId, String itemDescription, int quantity) {
    public OrderDetail(String itemDescription, int quantity){
        this(-1, itemDescription, quantity);
    }

    public String toJSON() {
//        return new StringJoiner(", ", OrderDetail.class.getSimpleName() + "[", "]")
//                .add("itemDescription='" + itemDescription + "'")
//                .add("quantity=" + quantity)
//                .toString();
        return String.format(
                "{\"orderDetailId\": %d, \"itemDescription\": \"%s\", \"quantity\": %d}",
                orderDetailId,
                itemDescription.replace("\"", "\\\""), // escape quotes just in case
                quantity
        );
    }
}

record Order(int orderId, String dateString, List<OrderDetail> details) {
    public Order(String dateString) {
        this(-1, dateString, new ArrayList<>());
    }

    public void addDetails(String itemDescription, int quantity) {
        OrderDetail item = new OrderDetail(itemDescription, quantity);
        details.add(item);
    }

    public String getDetailsJson() {
        StringJoiner jsonString = new StringJoiner(",",  "[", "]");
        details.forEach(detail -> jsonString.add(detail.toJSON()));
        return jsonString.toString();
    }
}

public class Challenge2 {
    public static void main(String[] args) {
        var dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
//        dataSource.setDatabaseName("");
        dataSource.setUser( System.getenv("MYSQL_USER"));
        dataSource.setPassword(System.getenv("MYSQL_PASS"));
        List<Order> orders = readData();

        try (Connection conn = dataSource.getConnection();)  {
//            String alterString = "ALTER TABLE storefront.order_details ADD COLUMN quantity INT";
//            Statement stmt = conn.createStatement();
//            stmt.execute(alterString);

//            addOrders(conn, orders);
            CallableStatement cs = conn.prepareCall(
                    "{ CALL storefront.addOrder(?, ?, ?, ?) }");
            DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                            .withResolverStyle(ResolverStyle.STRICT);

            orders.forEach(o -> {
                try {
                    LocalDateTime ldt = LocalDateTime.parse(o.dateString(), dtf);
                    Timestamp timestamp = Timestamp.valueOf(ldt);
                    cs.setTimestamp(1, timestamp);
                    cs.setString(2, o.getDetailsJson());
                    cs.registerOutParameter(3, Types.INTEGER);
                    cs.registerOutParameter(4, Types.INTEGER);
                    cs.execute();

                    System.out.printf("%d records inserted for %d (%s)%n",
                            cs.getInt(4), cs.getInt(3),
                            o.dateString());
                } catch (SQLException e) {
                    System.out.printf("Problem with %s: %s %n", o.dateString(), e.getMessage());
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Order> readData () {
        List<Order> vals = new ArrayList<>();

        try (Scanner scanner = new Scanner(Path.of("Orders.csv"))) {
            scanner.useDelimiter("[,\\n]");
            var list = scanner.tokens().map(java.lang.String::trim).toList();

            for (int i = 0; i < list.size(); i++) {
                String value = list.get(i);
                if (value.equals("order")) {
                    var date = list.get(++i);
                    vals.add(new Order(date));
                } else if (value.equals("item")) {
                    var qty = Integer.parseInt(list.get(++i));
                    var description = list.get(++i);
                    Order order= vals.get(vals.size() - 1);
                    order.addDetails(description, qty);
                }
            }
            vals.forEach(System.out::println);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return vals;
    }

    private static void addOrder(Connection conn, PreparedStatement psOrder,
                                 PreparedStatement psDetail, Order order) throws SQLException {
        try {
            conn.setAutoCommit(false);

            int orderId = -1;
            psOrder.setString(1, order.dateString());
            if (psOrder.executeUpdate() == 1) {
                var rs = psOrder.getGeneratedKeys();
                if(rs.next()){
                    orderId =  rs.getInt(1);
                    System.out.println("Order Id: " + orderId);

                    if (orderId > -1) {
                        psDetail.setInt(1, orderId);
                        for (OrderDetail detail : order.details()) {
                            psDetail.setString(2, detail.itemDescription());
                            psDetail.setInt(3, detail.quantity());
                            psDetail.addBatch();
                        }
                        int[] data = psDetail.executeBatch();
                        int rowsInserted = Arrays.stream(data).sum();
                        if (rowsInserted != order.details().size()) {
                            throw new SQLException("Inserts don't match!");
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new RuntimeException(e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static void addOrders(Connection conn, List<Order>  orders) {
        String insertOrder = "INSERT INTO storefront.orders(order_date) VALUES (?) ";
        String insertDetail = "INSERT INTO storefront.order_details " +
                "(order_id, item_description, quantity) VALUES (?, ?, ?) ";

        try (PreparedStatement psOrder = conn.prepareStatement(insertOrder,Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psDetail = conn.prepareStatement(insertDetail, Statement.RETURN_GENERATED_KEYS);
        ) {
            orders.forEach(order -> {
                try {
                    addOrder(conn, psOrder, psDetail, order);
                } catch (SQLException e) {
                    System.err.printf("%d (%s) %s%n", e.getErrorCode(), e.getSQLState(), e.getMessage());
                    System.err.println("Problem: " + psOrder);
                    System.err.println("Order: " + order);
//                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
