package ua.com.pedpresa.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ua.com.pedpresa.src.ConsoleColors;
import ua.com.pedpresa.src.News;
import ua.com.pedpresa.src.Props;
import ua.com.pedpresa.src.Sites;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.time.LocalDateTime.now;

public class ServiceSite {

    public static void siteReadService() throws IOException {

        //        -----------------------------------  read table of sites ---------------
        Sites sites = null;
        List<Sites> siteList = new ArrayList<>();
        try {
            Class.forName(Props.DB_DRIVER);
            Connection con = DriverManager.getConnection(Props.DB_URL, Props.DB_USER, Props.DB_PASS);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet rs = stmt.executeQuery(Props.SQL1);
            while (rs.next())

                sites = new Sites(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getInt(8));

            siteList.add(sites);

            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("----------------------------- SiteList");
        for (Sites sites1 : siteList) {
            System.out.println(sites1.toString());
        }
        System.out.println(" --------------------------------- end of SiteList");
//          ---------------------------- read news from each sites -------------------

        List<News> allNews = new ArrayList<>();

        for (int s = 0; s < siteList.size(); s++) {

            Document mainPage = Jsoup.connect(siteList.get(s).getUrl())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .referrer("https://google.com/")
                    .get();

            String title = mainPage.title().substring(mainPage.title().indexOf("|") + 2);
            Elements elements = mainPage.getElementsByClass(siteList.get(s).getClassFilter());
//        System.out.println(title);

            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).toString().contains(siteList.get(s).getTrimFilter())) {
                    String str = elements.get(i).toString();
                    int postId = Integer.parseInt(str.substring(str.indexOf(siteList.get(s).getTrimFilter()) + 16, str.indexOf(siteList.get(s).getTrimFilter()) + 21));

//                System.out.println("------------------------------------" + postId);

                    Document page = Jsoup.connect(String.format("https://www.schoollife.org.ua/?p=%s", postId))
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                            .referrer("")
                            .get();
                    Elements el = page.getElementsByClass(siteList.get(s).getPostFilter());

                    News post = new News();
                    post.setTitle(page.title().substring(0, page.title().length() - 15));
                    post.setCreatedDate(new Date());
                    post.setContent(el.get(0).toString().substring(42, el.get(0).toString().indexOf("<div style=\"clear:both;\"></div>")));
                    post.setCanonical(postId);
                    post.setKeyWords(el.get(0).attr("tagcloud"));
                    post.setSiteName(title);
                    post.setUrl(String.format(siteList.get(s).getCanonical(), postId));

                    allNews.add(post);
//                System.out.println(page.title().substring(0,page.title().length()-15) + "=============" + el.size());
//                System.out.println(el.get(0));

                }
                Collections.sort(allNews);

//                -------------------------- adding to news table -----------------
            }
            System.out.println("Inserting records into the table...");
            try (Connection con = DriverManager.getConnection(Props.DB_URL, Props.DB_USER, Props.DB_PASS);
                 PreparedStatement ps2 = con.prepareStatement(Props.SQL2);
                 PreparedStatement ps3 = con.prepareStatement(Props.SQL3)) {
                int z = 0;

                for (News aNew : allNews) {
                    assert sites != null;
                    if (aNew.getCanonical() > sites.getIdLastPost()) {
                        ps2.setString(1, aNew.getTitle());
                        ps2.setTimestamp(2, Timestamp.valueOf(now()));
                        ps2.setString(3, aNew.getContent());
                        ps2.setInt(4, aNew.getCanonical());
                        ps2.setString(5, aNew.getKeyWords());
                        ps2.setString(6, aNew.getSiteName());
                        ps2.setString(7, aNew.getUrl());
                        ps2.setInt(8, 0);

                        ps2.executeUpdate();
                        z++;
                        System.out.println(ConsoleColors.GREEN + "Add post +" + aNew.getCanonical() + "+ " + aNew.getTitle() + ConsoleColors.RESET);
                    } else System.out.println("Skip post -" + aNew.getCanonical() + "- " + aNew.getTitle());
                }
                if (z > 0) {
                    System.out.println(ConsoleColors.GREEN + String.format("Added %d news!", z) + ConsoleColors.RESET);
                    System.out.println(ConsoleColors.RED + "Last canonical = " + allNews.get(allNews.size() - 1).getCanonical() + ConsoleColors.RESET);
                    ps3.setInt(1, allNews.get(allNews.size() - 1).getCanonical());
                    ps3.setInt(2, sites.getId());

                    ps3.executeUpdate();
                }
            } catch (SQLException e) {
                System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
