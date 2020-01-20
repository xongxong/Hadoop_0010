import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class ParseSQL {
    public static void parse() {
        String sql = "create proc StuProc\n" +
                "as \n" +
                "begin \n" +
                "select S#,Sname,Sage,Ssex from student\n" +
                "end\n" +
                "go";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLCallStatement sqlCallStatement = parser.parseCall();
        sqlCallStatement.setDbType(JdbcConstants.MYSQL);
    }

    private static final String dbTable = "DB_TABLE";
    private static final String dbColumn = "DB_COLUMN";
    private static final String rtn = "RETURN";

    public static Map handleSQL(String sql, String dbType) {
        List<String> lists = splitSQL(sql);
        Map<String, Map<String, Set<String>>> map = new HashMap();
        int index = 0;
        for (String list : lists)
            map.put(rtn + index++, parseSQL(list, dbType));
        for (Map.Entry m : map.entrySet()) {
            System.out.println(m);
        }
        return map;
    }

    public static List<String> splitSQL(String sql) {
        String[] strs = sql.toLowerCase().split(";");
        Pattern patternRtn = Pattern.compile(" return ([\\s\\S]*);");
        Pattern patternSel = Pattern.compile("select ([\\s\\S]*);");
        Matcher matcher;
        List<String> list = new ArrayList<>();
        for (String str : strs) {
            matcher = patternRtn.matcher(str + ";");
            while (matcher.find()) {
                matcher = patternSel.matcher(matcher.group(0));
                while (matcher.find()) {
                    list.add(matcher.group(0));
                    System.out.println(matcher.group(0));
                }
            }
        }
        return list;
    }

    public static Map<String, Set<String>> parseSQL(String sql, String dbType) {
        //格式化输出
        String result = SQLUtils.format(sql, dbType);
        System.out.println(result); // 缺省大写格式
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        Map<String, Set<String>> map = new HashMap<>();
        Set<String> table = null;
        Set<String> column = null;
        for (int i = 0; i < stmtList.size(); i++) {
            SQLStatement stmt = stmtList.get(i);
            PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
            stmt.accept(visitor);
            table = new HashSet<>();
            column = new HashSet<>();
            //获取表名称
            System.out.println("Tables : " + visitor.getTables().keySet());
            for (TableStat.Name name : visitor.getTables().keySet())
                table.add(name.getName());
            //获取操作方法名称,依赖于表名称
            System.out.println("Manipulation : " + visitor.getTables());
            //获取字段名称
            System.out.println("fields : " + visitor.getColumns());
            for (TableStat.Column co : visitor.getColumns()) {
                column.add(co.getName());
            }
        }
        map.put(dbTable, table);
        map.put(dbColumn, column);
        return map;
    }

    public static void main(String[] args) {
        String sql = "CREATE OR REPLACE FUNCTION \"public\".\"my_alarmyl\"(\"line_in\" varchar, \"equipment_in\" varchar, \"glassid_in\" varchar, \"type_in\" varchar)\n" +
                "  RETURNS TABLE(\"occur_date_out\" varchar, \"release_date_out\" varchar, \"glassid_out\" varchar, \"product_name_out\" varchar, \"line_name_out\" varchar, \"equipment_code_out\" varchar, \"alarm_code_out\" varchar, \"alarm_text_out\" varchar) AS $BODY$DECLARE sql_ VARCHAR;\n" +
                "DECLARE glassid_count NUMERIC;\n" +
                "declare internal_hour NUMERIC;\n" +
                "BEGIN\n" +
                "  SELECT count(b.s_type) into glassid_count from my_strjudge(GlassID_in,'glassid_alarmyl') b;\n" +
                "  glassid_count := 2;\n" +
                "  SELECT to_number(itemvalue,'fm9999999') into internal_hour FROM mst_syspara WHERE lower(systemcode) = 'dmp' and lower(itemname) = 'alarm_time';\n" +
                "\tIF(glassid_count < 1) THEN \n" +
                "\t\t\t\t\t return query\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\tselect CAST('ERROR: 输入GlassID不正确' AS VARCHAR) line_code_out,CAST('GlassID不存在，查询结果为空' AS VARCHAR) release_date_out\n" +
                "                              ,CAST('' AS VARCHAR) GlassID_out,CAST('' AS VARCHAR) productName_out,CAST('' AS VARCHAR) Line_name_out,CAST('' AS VARCHAR)  equipment_code_out\n" +
                "                              ,CAST('' AS VARCHAR) alarm_code_out,CAST('' AS VARCHAR) alarm_text_out\n" +
                "                              ;\n" +
                "\tELSEIF(lower(type_in) = 'alarmyl') THEN \n" +
                "\t\t\t\t\t return query\n" +
                "\t\t\t\t\t\t\t\t\tselect cast(to_char(b.occur_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) occur_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,cast(to_char(b.release_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) release_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.glass_id,l.product_name\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,s.line_name\n" +
                "                        ,cast((coalesce(b.EQUIPMENT_GROUP_CODE,'') || coalesce(b.EQUIPMENT_CODE,'')) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t--,cast((b.equipment_group_code|| b.equipment_code) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.alarm_code,b.alarm_text \n" +
                "\t\t\t\t\t\t\t\t\tfrom tbl_alarm b\n" +
                "\t\t\t\t\t\t\t\t\tleft JOIN mst_line s on s.line_code = b.line_code\n" +
                "                    left join tbl_glass l on l.glass_id = b.glass_id\n" +
                "\t\t\t\t\t\t\t\t\t\twhere (b.glass_id in(SELECT my_strjudge(TRIM(GlassID_in),'glassid_alarmyl')) or TRIM(GlassID_in) = '')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.line_code in(SELECT my_strjudge(line_in,'line_alarmyl')) or line_in = '0')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.equipment_group_code in(SELECT my_strjudge(line_in || '#' ||equipment_in,'equipment_alarmyl')) or equipment_in = '0la' or equipment_in = '0ls')                       \n" +
                "                          and (now() - b.occur_date) < \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tto_timestamp((select time_pattern from my_timefunction('','', 'alarmyl')),'yyyy-mm-dd hh24:mi:ss')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t- to_timestamp('1018-08-01 00:00:00','yyyy-mm-dd hh24:mi:ss')                     \n" +
                "                    ORDER BY b.occur_date,b.glass_id\n" +
                "\t\t\t\t\t\t\t\t\t\t;\n" +
                " ELSE\n" +
                "      RETURN QUERY \n" +
                "\t\t\t\t\t\t\t\t\tselect cast(to_char(b.occur_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) occur_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,cast(to_char(b.release_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) release_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.glass_id,l.product_name\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,s.line_name\n" +
                "                        ,cast((coalesce(b.EQUIPMENT_GROUP_CODE,'') || coalesce(b.EQUIPMENT_CODE,'')) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t--,cast((b.equipment_group_code|| b.equipment_code) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.alarm_code,b.alarm_text \n" +
                "\t\t\t\t\t\t\t\t\tfrom tbl_alarm b\n" +
                "\t\t\t\t\t\t\t\t\tleft JOIN mst_line s on s.line_code = b.line_code\n" +
                "                    left join tbl_glass l on l.glass_id = b.glass_id\n" +
                "\t\t\t\t\t\t\t\t\twhere (b.glass_id in(SELECT my_strjudge('','glassid_alarmyl')) or GlassID_in = '')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.line_code in(SELECT my_strjudge(line_in,'line_alarmyl')) or line_in = '0')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.equipment_group_code in(SELECT my_strjudge(line_in || '#' ||equipment_in,'equipment_alarmyl')) or equipment_in = '0la' or equipment_in = '0ls')  \n" +
                "                        and (now() - b.occur_date) < \n" +
                "                                 (to_timestamp((select  time_pattern from my_timefunction('','', 'alarmyl')),'yyyy-mm-dd hh24:mi:ss')\n" +
                "                                  - to_timestamp('1018-08-01 00:00:00','yyyy-mm-dd hh24:mi:ss'))\n" +
                "                    ORDER BY b.occur_date,b.glass_id\n" +
                "\t\t\t\t\t\t\t    ;\n" +
                " END IF;\n" +
                "END\n" +
                "$BODY$\n" +
                "  LANGUAGE plpgsql VOLATILE\n" +
                "  COST 100\n" +
                "  ROWS 1000";
        handleSQL(sql, JdbcConstants.POSTGRESQL);
        String sql1 = "select cast(to_char(b.occur_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) occur_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,cast(to_char(b.release_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) release_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.glass_id,l.product_name\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,s.line_name\n" +
                "                        ,cast((coalesce(b.EQUIPMENT_GROUP_CODE,'') || coalesce(b.EQUIPMENT_CODE,'')) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t--,cast((b.equipment_group_code|| b.equipment_code) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.alarm_code,b.alarm_text \n" +
                "\t\t\t\t\t\t\t\t\tfrom tbl_alarm b\n" +
                "\t\t\t\t\t\t\t\t\tleft JOIN mst_line s on s.line_code = b.line_code\n" +
                "                    left join tbl_glass l on l.glass_id = b.glass_id\n" +
                "\t\t\t\t\t\t\t\t\t\twhere (b.glass_id in(SELECT my_strjudge(TRIM(GlassID_in),'glassid_alarmyl')) or TRIM(GlassID_in) = '')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.line_code in(SELECT my_strjudge(line_in,'line_alarmyl')) or line_in = '0')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.equipment_group_code in(SELECT my_strjudge(line_in || '#' ||equipment_in,'equipment_alarmyl')) or equipment_in = '0la' or equipment_in = '0ls')                       \n" +
                "                          and (now() - b.occur_date) < \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tto_timestamp((select time_pattern from my_timefunction('','', 'alarmyl')),'yyyy-mm-dd hh24:mi:ss')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t- to_timestamp('1018-08-01 00:00:00','yyyy-mm-dd hh24:mi:ss')                     \n" +
                "                    ORDER BY b.occur_date,b.glass_id\n" +
                "\t\t\t\t\t\t\t\t\t\t;";

        String sql2 = "select cast(to_char(b.occur_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) occur_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,cast(to_char(b.release_date,'yyyy-MM-dd hh24:mi:ss') as VARCHAR) release_date\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.glass_id,l.product_name\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,s.line_name\n" +
                "                        ,cast((coalesce(b.EQUIPMENT_GROUP_CODE,'') || coalesce(b.EQUIPMENT_CODE,'')) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t--,cast((b.equipment_group_code|| b.equipment_code) as VARCHAR) as equipment_group_code\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t,b.alarm_code,b.alarm_text \n" +
                "\t\t\t\t\t\t\t\t\tfrom tbl_alarm b\n" +
                "\t\t\t\t\t\t\t\t\tleft JOIN mst_line s on s.line_code = b.line_code\n" +
                "                    left join tbl_glass l on l.glass_id = b.glass_id\n" +
                "\t\t\t\t\t\t\t\t\twhere (b.glass_id in(SELECT my_strjudge('','glassid_alarmyl')) or GlassID_in = '')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.line_code in(SELECT my_strjudge(line_in,'line_alarmyl')) or line_in = '0')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t    and (b.equipment_group_code in(SELECT my_strjudge(line_in || '#' ||equipment_in,'equipment_alarmyl')) or equipment_in = '0la' or equipment_in = '0ls')  \n" +
                "                        and (now() - b.occur_date) < \n" +
                "                                 (to_timestamp((select  time_pattern from my_timefunction('','', 'alarmyl')),'yyyy-mm-dd hh24:mi:ss')\n" +
                "                                  - to_timestamp('1018-08-01 00:00:00','yyyy-mm-dd hh24:mi:ss'))\n" +
                "                    ORDER BY b.occur_date,b.glass_id\n" +
                "\t\t\t\t\t\t\t    ;";
        //parse(sql);
        //parse(sql1);
    }
}
