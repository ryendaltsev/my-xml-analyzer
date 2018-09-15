package com.agileengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MyXMLAnalyzer {

    private static final String CHARSET_NAME = "utf-8";
    private static Logger LOGGER = LoggerFactory.getLogger(MyXMLAnalyzer.class);

    private File sourceFile;
    private File targetFile;
    private String sourceElementId = "make-everything-ok-button";
    private Optional<Element> searchResult = Optional.empty();

    public static void main(String[] args) {
        MyXMLAnalyzer analyzer = new MyXMLAnalyzer();
        analyzer.setup(args);
        analyzer.run();
    }

    private void run() {
        if (this.sourceFile.exists() && this.targetFile.exists()) {
           findSourceElementById(this.sourceElementId).ifPresent(this::findSimilarElement);
            if (this.searchResult.isPresent()) {
                LOGGER.info(String.format("\nSource file:%s,\nTarget file:%s",this.sourceFile, this.targetFile));
                LOGGER.info(String.format("Similar element:%s", this.searchResult.get()));
            }
            else {
                LOGGER.info("Similar element was not found");
            }
        }
        else {
            LOGGER.warn(String.format("Source(%s) or target(%s) was not defined correctly.", sourceFile, targetFile));
        }
    }

    void setup(String[] args) {
        this.sourceFile = new File(args.length > 0 ? args[0] : "");
        this.targetFile = new File(args.length > 1 ? args[1] : "");
    }

    private Optional<Element> findSourceElementById(String targetElementId) {
        try {
            Document doc = Jsoup.parse(this.sourceFile, CHARSET_NAME,sourceFile.getAbsolutePath());
            return Optional.of(doc.getElementById(targetElementId));
        } catch (IOException e) {
            LOGGER.error(String.format("Error reading file:%s, error:%s", sourceFile.getAbsolutePath(), e.getMessage()));
            return Optional.empty();
        }
    }

    private void findSimilarElement(Element element) {
        try {
            Document doc = Jsoup.parse(this.targetFile, CHARSET_NAME, targetFile.getAbsolutePath());
            String elementTitle = element.attr("title");
            Elements result = doc.select(String.format("[title=%s]", elementTitle));
            if (result.size() == 1) {
                this.searchResult = Optional.of(result.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
