package javafxmvc.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael Vargas Mesquita
 */
public class DatabaseMySQL implements Database {

    private Connection connection;

    @Override
   public Connection conectar() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/javafxmvc?useTimezone=true&serverTimezone=UTC";
            String user = "root";
            String pass = "lolo0";
            
            Class.forName(driver);
            
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Sucesso na conexão! \n");
            return this.connection; 
                        
        } catch(ClassNotFoundException e) {
            System.out.println("excecao ClassNotFound...");
            e.printStackTrace();
            return null;
        } catch(SQLException e) {
            System.out.println("SQL Exception... ");
            e.printStackTrace();
            return null;
        }     
    }

    @Override
    public void desconectar(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabasePostgreSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
