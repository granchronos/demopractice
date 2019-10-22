package com.example.demo;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppMainDemoProject extends Thread {

    public void run() {
        try {
            //Get links from file
            List<String> zeldas = getLinks();
            //Get HTML from website
            obtainDataHtmlContent(zeldas);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    private List<String> getLinks() throws IOException {
        return Files.lines(Paths.get("/opt/links.txt")).collect(Collectors.toList());
    }

    private String obtainDataHtmlContent(List<String> links) {
        return links.stream().map(link -> {
            try {
                int count = 0;
                URL zelda = new URL(link);
                URLConnection zeldaConn = zelda.openConnection();
                zeldaConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                zeldaConn.connect();
                System.out.println(link);
                BufferedReader br = new BufferedReader(new InputStreamReader(zeldaConn.getInputStream()));
                List<String> linkContent = new ArrayList<>();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    linkContent.add(inputLine);
                }
                br.close();
                String data = "";
                List<String> toparse = new ArrayList<>();
                if (!linkContent.isEmpty()) {
                    data = String.join("", linkContent);
                    for (int i = 0; i < data.length(); i += 10) {
                        int from = data.indexOf("twitter", i);
                        i += 10;
                        int to = data.indexOf("twitter", i);
                        String line = "";
                        if (to > 0) {
                            //line = data.substring(data.lastIndexOf("twitter"), data.lastIndexOf("twitter") + 7);
                            line = data.substring(from, to);
                        } /*else {
                            line = data.substring(from, to);
                        }*/
                        if (!line.isEmpty()) {
                            toparse.add(line);
                            count++;
                        }
                    }
                    List<String> result = new ArrayList<>();
                    result.add("There is a concurrence of: " + count);
                    result.add("The lines are: ");
                    result.addAll(toparse);
                    toFile(zelda.getHost(), result);
                } else {
                    toFile(zelda.getHost(), Collections.singletonList("The page is not available"));
                }
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
                try {
                    toFile(link, Collections.singletonList("The page is not available"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }).collect(Collectors.joining());
    }

    private void toFile(String link, List<String> content) throws IOException {
        Path file = Paths.get("/opt/links-" + link.replace(".", "-") + ".txt");
        Files.write(file, content, StandardCharsets.UTF_8);
    }
}
