package vn.clmart.manager_service.generator;


import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrderGenerator implements IdentifierGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OrderGenerator.class);
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {
        Connection connection = session.connection();
        String prefix = "LC_ORDER";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(o.id) from lcOrder.`order` o");
            resultSet.next();
            Integer id = resultSet.getInt(1) + 99;
            return prefix + "_" + id;
        }
        catch(Exception ex){
            logger.error("ORDER_GENERATOR", ex);
            throw new RuntimeException(ex);
        }
    }
}
