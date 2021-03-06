package springbook.user.domain;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.*;
import javax.sql.DataSource;

/*
해당 클래스에서는 Connection 부분의 변경이 더이상 필요하지 않다.
Connection 부분을 공통으로 묶어 멤버변수로 할당했고 해당 변수에 들어갈 Connection Maker를 아래 클래스에서 직접 생성하는게 아니라 생성자를 통해 사용자가 원하는 Maker를 넣어 사용할 수 있도록 하였기 때문이다. 이렇게 필요에 따라 변경이 필요한 부분을 인터페이스로 외부로 분리시켜 사용하는 것을 전략 패턴이라고 한다.
 */
public class UserDao {
    private DataSource dataSource;
    private Connection c;
    private User user;

    public UserDao(){}

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void add(User user) throws ClassNotFoundException, SQLException{
        this.c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException{
        this.c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            this.user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if(user == null)
            throw new EmptyResultDataAccessException(1);

        return user;
    }

    public void deleteAll() throws SQLException {
        this.c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("delete from users");

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public int getCount() throws SQLException{
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select count(*) from users");

        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();
        c.close();

        return count;
    }
}
