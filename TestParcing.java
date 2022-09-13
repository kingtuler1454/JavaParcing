
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestParcing {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";//драйвер для h2
    static final String DB_URL = "jdbc:h2:~/test";//адрес до бд

    //  Database credentials
    static final String USER = "QWERTY";//логин
    static final String PASS = "QWERTY";//пароль


    // для получения json
    public static String get(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream(); //коннектимся

        StringBuilder result = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();//закрываем поток чтения
        return result.toString();//озвращаем в формате стринг
    }

public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{

            Class.forName(JDBC_DRIVER);



            conn = DriverManager.getConnection(DB_URL,USER,PASS);




            String spisok = get("https://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&api_key=aa85ae5e1ad39b7075d70d89120be526&format=json");//получаем json
            String[] listNoNameArtists=spisok.split(":",2);//делим на две части чтобы избавиться от artists  в начале

            String []ListtNoNameArtist= listNoNameArtists[1].split(":",2);//делим на две части чтобы избавиться от artist ключа
            String SpisokBezKvadratnih=(ListtNoNameArtist[1]).substring(1,(ListtNoNameArtist[1]).length() - 1);//удаляем квадратные скобки
            String []ListElement=SpisokBezKvadratnih.split("}]},");//разделяем чтоб на один элемент была инфа про одного артиста

            String name="";
            String playcount="";
            String listerens="";

            int i=0;

            String sql = "INSERT INTO Registration " + "VALUES (name, playcount, listerens)";//чтобы не ругался блок else , так то строка не имеет особого смысла в программе
            for (String element :ListElement) {
                String []AllInterestingElement=element.split(",\"mbid\"");//делим на элементы чтобы первый элемент был только с name playcount listerens
                String InterestingElement[]=AllInterestingElement[0].split("\"");//разделяем на составляющие тобы добраться до значений
                name=InterestingElement[3];//в ячейке три сплит позволил забрать name
                playcount=InterestingElement[7];//в ячейке 7сплит позволил забрать playcount
                listerens=InterestingElement[11];//в ячейке 11 сплит позволил забрать listerens
                System.out.println(name+' '+ playcount+' '+ listerens);
                if (i==0) {
                    stmt = conn.createStatement();
                }
                else{
                    stmt.executeUpdate(sql);
                }
                sql="INSERT INTO Registration " + "VALUES (name, playcount, listerens)";
                i++;
            }



            stmt.close();
            conn.close();
        } catch(SQLException se) {

            se.printStackTrace();
        } catch(Exception e) {

            e.printStackTrace();
        } finally {
            try {
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            }
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se) {
                se.printStackTrace();
            }
        }

    }
}