package fengliu.cloudmusicroom.sql;

import fengliu.cloudmusicroom.CloudMusicRoom;
import fengliu.cloudmusicroom.utils.IdUtil;
import net.minecraft.text.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SqlConnection {
    /**
     * 获取数据库地址
     *
     * @return 数据库地址
     */
    public abstract String getDBUrl();

    /**
     * 获取使用表名
     *
     * @return 表名
     */
    public abstract String getTableName();

    /**
     * 获取创建使用表 sql
     *
     * @return sql
     */
    public abstract String getCreateTableSql();

    /**
     * 以该配置执行 sql 语句
     *
     * @param sql sql 语句
     * @return 结果
     */
    public <T> T runSql(Function<Statement, T> sql) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(this.getDBUrl());
            Statement statement = connection.createStatement();
            T data = sql.apply(statement);
            statement.close();
            connection.close();
            return data;
        } catch (Exception e) {
            CloudMusicRoom.LOGGER.error(Text.translatable(IdUtil.error("sql.run.error")).getString(), e);
        }
        return null;
    }

    /**
     * 创建表
     */
    public boolean createTable() {
        return this.runSql(statement -> {
            try {
                statement.executeQuery(String.format("SELECT name FROM sqlite_master WHERE type='table' AND name='%s';", this.getTableName())).getString(0);
                return true;
            } catch (SQLException ignored) {
            }

            try {
                statement.execute(this.getCreateTableSql());
            } catch (SQLException e) {
                return true;
            }
            CloudMusicRoom.LOGGER.info(Text.translatable(IdUtil.info("sql.not.table.%s".formatted(this.getTableName()))).getString());
            return true;
        }) != null;
    }

    /**
     * 以该配置执行 sql 语句
     *
     * @param sql sql
     * @return 结果
     */
    public <T> T executeSpl(Function<Statement, T> sql) {
        if (!createTable()) {
            CloudMusicRoom.LOGGER.error(Text.translatable(IdUtil.error("sql.exist.table.%s".formatted(this.getTableName()))).getString());
            return null;
        }
        return runSql(sql);
    }
}
