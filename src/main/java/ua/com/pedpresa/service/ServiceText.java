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
            StringBuilder modPost = new StringBuilder();
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
//                    System.out.println(contWithoutRN);
                    boolean endOfHref;
                    String mod_title = modificate(title, con);
                    System.out.println(mod_title);
                    System.out.println("");

//                    List<String> sentences = new ArrayList<>();
                    List<String> words;
                    Map<String, Integer> countWords = new HashMap<>();

                    modPost.append(mod_title).append(System.lineSeparator());
//                    sentences = List.of(Jsoup.parse(contWithoutRN).text().split("\\. "));


                    List<String> strings = new ArrayList<>(List.of(contWithoutRN.split("\\n")));

                    for (String string : strings) {
                        string = string.replace("<", " <");
                        string = string.replace(">", "> ");
                        string = string.replace("&nbsp;", " ");
                        string = string.replace("  ", " ").trim();
//                        System.out.println(StringUtils.countOccurrencesOf(string," "));
                        StringBuilder strA = new StringBuilder();
                        List<String> allWords = List.of(string.split(" "));
//                        strA.append(allWords.size() - 1).append(";");
                        String tString = Jsoup.parse(string).text().trim();
                        String mod_string = tString.isEmpty() ? "" :
                        modificate(tString.replace(" ,", ","), con).replace("..", ".");
//                        String mod_string = modificate(string, con).replace("..", ".");

                        int u = tString.length() - mod_string.length();             // variable = different lengths between original string and modificated string
                        for (int i = 0; i < allWords.size(); i++) {
                            endOfHref = true;
                            if (allWords.get(i).startsWith("<") && allWords.get(i).endsWith(">")) {
                                strA.append(allWords.get(i)).append(i==0? i : i - u).append(";");
                                u++;
                            }
                            if (allWords.get(i).startsWith("<a") && allWords.get(i + 1).startsWith("href")) {
                                strA.append(allWords.get(i)).append(" ").append(allWords.get(i + 1)).append(i - u).append(";");
                                u = u + 2;
                            }
                        }

//                        ------------------ return tags to string ------------
                        System.out.println("|" + string + "|");
                        List<String> strTag = List.of(strA.toString().split(";"));
                        mod_string = mod_string + (" ".repeat(strTag.size()));
                        System.out.println("|" + mod_string + "|");

                        String tagS = "";
                        String tag = "";
                        int tagPos1 = 0;
                        int tagPos2 = 0;
                        System.out.println(strA);
                        StringBuilder sb = new StringBuilder();


                        List<String> modStringList = List.of(mod_string.split(" "));

                        for (int i = 0; i < strTag.size(); i++) {
                            tag = strTag.get(i);
                            tagPos1 = Integer.parseInt(tag.substring(tag.indexOf(">") + 1));
                            if ((i + 1) >= strTag.size()) {
                                tagPos2 = Integer.parseInt(strTag.get(i).substring(strTag.get(i).indexOf(">") + 1));
                            } else {
                                tagPos2 = Integer.parseInt(strTag.get(i + 1).substring(strTag.get(i + 1).indexOf(">") + 1));
                            }
                            if (tagPos2 > modStringList.size()) tagPos2 = modStringList.size();
                            tagS = tag.substring(0, tag.indexOf(">") + 1);


                            if (tagPos1 <= 0) tagPos1 = 0;

                            sb.append(tagS);
                            for (int j = tagPos1; j < tagPos2; j++) {
                                sb.append(modStringList.get(j)).append(" ");
                            }

                        }

                        System.out.println("+|" + sb + "|+");
                        modPost =  modPost.append(sb).append(System.lineSeparator());
//                        System.out.println(strA);

                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
                        System.out.println();
                    }

// считаем количество слов всего + первый тег + позиция тега + ";" и т.д.

                    //                    ---------------------------- list of words -----
                    modPost.append(System.lineSeparator()).append("Tags:");

                    words = List.of(modPost.toString().split(" "));
                    for (String word : words) {
                        word = fSym(word).isEmpty() ? word : word.substring(1);
                        word = lastSym(word).isEmpty() ? word : word.substring(0, word.length() - 1);
                        if (countWords.containsKey(word)) countWords.replace(word, countWords.get(word) + 1);
                        else countWords.put(word, 1);
                    }

                    for (Map.Entry<String, Integer> entry : countWords.entrySet()) {
                        if (entry.getValue() > 2 && entry.getKey().length() > 2) {
//                            System.out.println(entry.getKey() + " == " + entry.getValue());       !!!!!!!!!!! добавить сверку с базой по ключевым словам !!!!!!!!!!!!
                            modPost.append(" #").append(entry.getKey());
                        }
                    }

//                  ----------------------- end list of words


                } // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! end of EXECUTOR

                System.out.println("----------------------------------------------------");
                System.out.println();


// ----------------------------- end of parsing sentences ------------------------------------
            }
            System.out.println(modPost);

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

        for (int i = 0; i < words.length; i++) {
            mod_word = "";
            rm = 5;
            lastS = "";
            fS = "";

//            if (words[i].charAt(0) == '“') {
//
//            }
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
        String result = "";
        if (resultString.toString().trim().endsWith(":") ||
                resultString.toString().trim().endsWith(";") ||
                resultString.toString().trim().endsWith("!") ||
                resultString.toString().trim().endsWith("?")) {
            result = resultString.toString().trim() + " ";
        } else {
            result = resultString.toString().trim() + ". ";
        }
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }

    public static String lastSym(String str) {
        String res = "";
        if (str.isEmpty()) return res;
        char c = str.charAt(str.length() - 1);
        if (PUNCT.indexOf(c) >= 0) {
            res = str.substring(str.length() - 1);
//            System.out.println(str + " "+ res);
        }
        return res;
    }

    public static String fSym(String str) {
        String res = "";
        if (str.isEmpty()) return res;
        char c = str.charAt(0);
        if (PUNCT.indexOf(c) >= 0) {
            res = str.substring(0, 1);
        }

        return res;
    }

    public String tagFinder(String str) {

        return "";
    }
}



