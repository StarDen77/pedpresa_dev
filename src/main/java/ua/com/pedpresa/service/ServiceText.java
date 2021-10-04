package ua.com.pedpresa.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.springframework.util.StringUtils;
import ua.com.pedpresa.src.News;
import ua.com.pedpresa.src.Props;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

public class ServiceText {
    private static final String PUNCT = "“!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

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
//                    String content = news.getContent().replace("  "," ").trim();
                    String contWithoutRN = content.substring(0, content.contains("<p>Читайте також") ? content.indexOf("<p>Читайте також") : content.length());
                    System.out.println(contWithoutRN);
                    boolean endOfHref;
                    List<String> strings = new ArrayList<>(List.of(contWithoutRN.split("\\n")));

                    for (String string : strings) {
                        string = string.replace("<"," <");
                        string = string.replace(">","> ");
                        string = string.replace("&nbsp;","");
                        string = string.replace("  "," ").trim();
                        System.out.println(StringUtils.countOccurrencesOf(string," "));
                        StringBuilder strA = new StringBuilder();
                        List<String> allWords = List.of(string.split(" "));
                        strA.append(allWords.size());
                        for (int i = 0; i < allWords.size(); i++) {
                            endOfHref  = true;
//                            System.out.println(allWords.get(i));
                            if(allWords.get(i).startsWith("<") && allWords.get(i).endsWith(">")) {
                                strA.append(allWords.get(i)).append(i).append(";");
                            }
                            if(allWords.get(i).startsWith("<a") && allWords.get(i+1).startsWith("href")) {
                                int q = 0;
                                while (endOfHref) {
                                    q++;
//                                    System.out.println(allWords.get(i+q)+" "+q);
                                    if (allWords.get(i+q).endsWith("/a>")) {
                                        strA.append(allWords.get(i))
                                                .append(" ")
                                                .append(allWords.get(i+1))
                                                .append(i).append("-")
                                                .append(allWords.get(i+q))
                                                .append(i+q);
                                        endOfHref = false;
                                    }
                                }
                            }
                        }
                        System.out.println(strA.toString());

                        System.out.println("|"+string+"|");
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
                    }

// считаем количество слов всего + первый тег + позиция тега + ";" и т.д.

                    System.out.println(z);
                    System.out.println(title);


                    String titleMod = "";
                    String textMod = "";
// ------------------------------------ parsing sentences -------------------------------------------
                    List<String> sentences = new ArrayList<>();
                    List<String> words = new ArrayList<>();
                    Map<String, Integer> countWords = new HashMap<>();


                    String mod_title = modificate(title, con);
                    System.out.println(mod_title);
                    System.out.println("");
                    System.out.println("");

                    StringBuilder modPost = new StringBuilder();
                    modPost.append(mod_title).append("\\n");
                    sentences = List.of(Jsoup.parse(contWithoutRN).text().split("\\. "));
                    for (String sentence : sentences) {
                        System.out.println(sentence);

                        String mod_sentence = modificate(sentence, con).replace("..", ".");

                        System.out.println(mod_sentence);
                        modPost.append(mod_sentence).append("\\n");

                        System.out.println("+++++++++++++++++++++++++++++++");
                    }

                    //                    ---------------------------- list of words -----
                    words = List.of(modPost.toString().split(" "));
                    for (String word : words) {
                        word = fSym(word).isEmpty() ? word : word.substring(1);
                        word = lastSym(word).isEmpty() ? word : word.substring(0, word.length() - 1);
//                        System.out.println(word);
                        if (countWords.containsKey(word)) countWords.replace(word, countWords.get(word) + 1);
                        else countWords.put(word, 1);
                    }

                    for (Map.Entry<String, Integer> entry : countWords.entrySet()) {
                        if (entry.getValue() > 2 && entry.getKey().length() > 2)
                            System.out.println(entry.getKey() + " == " + entry.getValue());
                    }

//                  ----------------------- end list of words


                } // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! end of EXECUTOR

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

            if (words[i].charAt(0) == '“') {

            }
//            if(words[i].contains("<")) i++;


            if (words.length >= rm && i + rm <= words.length) {
                fS = fSym(words[i]);
                fW = fS.isEmpty() ? words[i] : words[i].substring(1);
                lastS = lastSym(words[i + rm - 1]);
                lastW = lastS.isEmpty() ? words[i + rm - 1] : words[i + rm - 1].substring(0, words[i + rm - 1].length() - 1);

                String searchString5 = fW + " " + words[i + 1] + " " + words[i + 2] + " " + words[i + 3] + " " + lastW;
                ps55.setString(1, searchString5);
                ResultSet rs = ps55.executeQuery();
                while (rs.next()) {
                    mod_word = fS + rs.getString(1) + lastS;
                }
                rs.close();
            }
            fS = "";
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) {
                if (words.length >= rm && i + rm <= words.length) { // --------- 4
                    fS = fSym(words[i]);
                    fW = fS.isEmpty() ? words[i] : words[i].substring(1);
                    lastS = lastSym(words[i + rm - 1]);
                    lastW = lastS.isEmpty() ? words[i + rm - 1] : words[i + rm - 1].substring(0, words[i + rm - 1].length() - 1);
                    String searchString4 = fW + " " + words[i + 1] + " " + words[i + 2] + " " + lastW;
                    ps54.setString(1, searchString4);
                    ResultSet rs = ps54.executeQuery();
                    while (rs.next()) {
                        mod_word = fS + rs.getString(1) + lastS;
                    }
                    rs.close();
                }
            } else {
                i++;
            }
            fS = "";
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) {
                if (words.length >= rm && i + rm <= words.length) { // ---------- 3
                    fS = fSym(words[i]);
                    fW = fS.isEmpty() ? words[i] : words[i].substring(1);
                    lastS = lastSym(words[i + rm - 1]);
                    lastW = lastS.isEmpty() ? words[i + rm - 1] : words[i + rm - 1].substring(0, words[i + rm - 1].length() - 1);
                    String searchString3 = fW + " " + words[i + 1] + " " + lastW;
                    ps53.setString(1, searchString3);
                    ResultSet rs = ps53.executeQuery();
                    while (rs.next()) {
                        mod_word = fS + rs.getString(1) + lastS;
                    }
                    rs.close();
                }
            } else {
                i++;
            }
            fS = "";
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) {
                if (words.length >= rm && i + rm <= words.length) { // ---------- 2
                    fS = fSym(words[i]);
                    fW = fS.isEmpty() ? words[i] : words[i].substring(1);
                    lastS = lastSym(words[i + rm - 1]);
                    lastW = lastS.isEmpty() ? words[i + rm - 1] : words[i + rm - 1].substring(0, words[i + rm - 1].length() - 1);
                    String searchString2 = fW + " " + lastW;
                    ps52.setString(1, searchString2);
                    ResultSet rs = ps52.executeQuery();
                    while (rs.next()) {
                        mod_word = fS + rs.getString(1) + lastS;
                    }
                    rs.close();
                }
            } else {
                i++;
            }
            fS = "";
            lastS = "";
            rm--;

            if (mod_word.isEmpty()) { // ------------- 1

                lastS = lastSym(words[i + rm - 1]);
                lastW = lastS.isEmpty() ? words[i + rm - 1] : words[i + rm - 1].substring(0, words[i + rm - 1].length() - 1);
                fS = fSym(lastW);
                fW = fS.isEmpty() ? lastW : lastW.substring(1);
                String searchString1 = fW;
                ps51.setString(1, searchString1);
                ResultSet rs = ps51.executeQuery();
                while (rs.next()) {
                    mod_word = fS + rs.getString(1) + lastS;
                }
                rs.close();
            } else {
                i++;
            }
            fS = "";
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
        char c = str.charAt(str.length() - 1);
        if (PUNCT.indexOf(c) >= 0) {
            res = str.substring(str.length() - 1);
//            System.out.println(str + " "+ res);
        }
        return res;
    }

    public static String fSym(String str) {
        String res = "";
        char c = str.charAt(0);
//        System.out.println("--"+c+"--");
        if (PUNCT.indexOf(c) >= 0) {
            res = str.substring(0, 1);
//            System.out.println(str + " "+ res);
        }

        return res;
    }

    public String tagFinder(String str) {

        return "";
    }
}



