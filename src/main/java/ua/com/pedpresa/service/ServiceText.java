package ua.com.pedpresa.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import ua.com.pedpresa.src.News;
import ua.com.pedpresa.src.Props;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceText {
    private static final String PUNCT = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    public static void textExecute() throws FileNotFoundException {
        News news = null;
        List<News> newsList = new ArrayList<>();

        try {
            Class.forName(Props.DB_DRIVER);
            Connection con = DriverManager.getConnection(Props.DB_URL, Props.DB_USER, Props.DB_PASS);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(Props.SQL4);
            int z = 0;

            while (rs.next()) {

                news = new News(rs.getInt(1),
                        rs.getString(2),
                        rs.getDate(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getInt(9));

//                ---------------------------- execute non executed news (isEx == 0) ------------------------------------


                if (news.getIsEx() == 0) {
                    newsList.add(news);
                    z++;


                    String title = news.getTitle();
                    String content = Jsoup.clean(news.getContent(), Safelist.relaxed());
////                String content = Jsoup.parse(news.getContent()).text();
                    String clearContent = content.substring(0, content.contains("<p>Читайте також") ? content.indexOf("<p>Читайте також") : content.length());
                    System.out.println(z);
                    System.out.println(title);


//                    System.out.println(clearContent);
                    String titleMod = "";
                    String textMod = "";
// ------------------------------------ parsing sentences -------------------------------------------
                    List<String> sentences = new ArrayList<>();
                    List<String> words = new ArrayList<>();

                    String mod_title = modificate(title,con);
                    System.out.println(mod_title);
                    System.out.println("");
                    System.out.println("");


                    sentences = List.of(Jsoup.parse(clearContent).text().split("\\. "));
                    for (String sentence : sentences) {
                        System.out.println(sentence);

                        String mod_sentence = modificate(sentence, con).replace("..",".");

                        System.out.println(mod_sentence);

                        System.out.println("+++++++++++++++++++++++++++++++");
                    }

                }

                System.out.println("----------------------------------------------------");

// ----------------------------- end of parsing sentences ------------------------------------
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String modificate(String sent, Connection con) throws SQLException {
        PreparedStatement ps51 = con.prepareStatement(Props.SQL51);
        PreparedStatement ps52 = con.prepareStatement(Props.SQL52);
        PreparedStatement ps53 = con.prepareStatement(Props.SQL53);
        PreparedStatement ps54 = con.prepareStatement(Props.SQL54);
        PreparedStatement ps55 = con.prepareStatement(Props.SQL55);
        String mod_word;
        StringBuilder resultString = new StringBuilder();
        String[] words = sent.split(" ");
        int rm = 0;
        boolean found;
        String lastW = "";
        String lastS = "";
        String fW = "";
        String fS = "";


//        for (int i = 0; i < words.length; i++) {
//            System.out.println(words[i]);
//        }


        for (int i = 0; i < words.length; i++) {
            mod_word = "";
            rm = 5;
            lastS = "";
            fS = "";


            if (words.length >= rm && i + rm <= words.length) {

                if (lastSym(words[i+4]).isEmpty()){
                  lastW = words[i+4];
                }  else {
                    lastW = words[i+4].substring(0,words[i+4].length()-1);
                    lastS = lastSym(words[i+4]);
                }
                String searchString5 = words[i] + " " + words[i + 1] + " " + words[i + 2] + " " + words[i + 3] + " " + lastW;
                ps55.setString(1, searchString5);
                ResultSet rs = ps55.executeQuery();
                while (rs.next()) {
                    mod_word = rs.getString(1)+lastS;
                }
                rs.close();
            }
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) {
                if (words.length >= rm && i + rm <= words.length) { // --------- 4
                    if (lastSym(words[i+3]).isEmpty()){
                        lastW = words[i+3];
                    }  else {
                        lastW = words[i+3].substring(0,words[i+3].length()-1);
                        lastS = lastSym(words[i+3]);
                    }
                    String searchString4 = words[i] + " " + words[i + 1] + " " + words[i + 2] + " " + lastW;
                    ps54.setString(1, searchString4);
                    ResultSet rs = ps54.executeQuery();
                    while (rs.next()) {
                        mod_word = rs.getString(1)+lastS;
                    }
                    rs.close();
                }
            } else {
                i ++;
            }
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) {
                if (words.length >= rm && i + rm <= words.length) { // ---------- 3
                    if (lastSym(words[i+2]).isEmpty()){
                        lastW = words[i+2];
                    }  else {
                        lastW = words[i+2].substring(0,words[i+2].length()-1);
                        lastS = lastSym(words[i+2]);
                    }
                    String searchString3 = words[i] + " " + words[i + 1] + " " + lastW;
                    ps53.setString(1, searchString3);
                    ResultSet rs = ps53.executeQuery();
                    while (rs.next()) {
                        mod_word = rs.getString(1)+lastS;
                    }
                    rs.close();
                }
            } else {
                i ++;
            }
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) {
                if (words.length >= rm && i + rm <= words.length) { // ---------- 2
                    if (lastSym(words[i+1]).isEmpty()){
                        lastW = words[i+1];
                    }  else {
                        lastW = words[i+1].substring(0,words[i+1].length()-1);
                        lastS = lastSym(words[i+1]);
                    }
                    String searchString2 = words[i] + " " +lastW;
                    ps52.setString(1, searchString2);
                    ResultSet rs = ps52.executeQuery();
                    while (rs.next()) {
                        mod_word = rs.getString(1)+lastS;
                    }
                    rs.close();
                }
            } else {
                i ++;
            }
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) { // ------------- 1
                if (lastSym(words[i]).isEmpty()){
                    lastW = words[i];
                }  else {
                    lastW = words[i].substring(0,words[i].length()-1);
                    lastS = lastSym(words[i]);
                }
                String searchString1 = lastW;
                ps51.setString(1, searchString1);
                ResultSet rs = ps51.executeQuery();
                while (rs.next()) {
                    mod_word = rs.getString(1)+lastS;
                }
                rs.close();
            } else {
                i ++;
            }
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) resultString.append(words[i]).append(" ");
            else resultString.append(mod_word).append(" ");

        }
        String result = resultString.toString().trim() + ". ";

        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }

    public static String lastSym(String str) {
        String res = "";
        char c = str.charAt(str.length()-1);
        if (PUNCT.indexOf(c) >= 0) {
            res = str.substring(str.length() - 1);
//            System.out.println(str + " "+ res);
        }
        return res;
    }

    public static String fSym(String str) {
        String res = "";
        char c = str.charAt(0);
        if (PUNCT.indexOf(c) >= 0) {
            res = str.substring(1);
            System.out.println(str + " "+ res);
        }
        return res;
    }

}



