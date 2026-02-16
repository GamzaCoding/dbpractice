package com.choi.dbpractice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Profile("import")
@Component
public class CsvToH2Importer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    @Value("${app.import.dir}")
    private String importDir;

    public CsvToH2Importer(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Path dir = Paths.get(importDir);
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("디렉토리가 없습니다. : " + dir);
        }

        String sql = """
                INSERT INTO product
                (product_name, survey_date, product_price, store_name, maker, is_sale, is_one_plus_one)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        int batchSize = 2000;
        Charset csvCharset = Charset.forName("MS949");

        try (DirectoryStream<Path> pathStream = Files.newDirectoryStream(dir, "*.{csv,CSV}")) {
            for (Path file : pathStream) {
                importOneFile(file, csvCharset, sql, batchSize);
            }
        }
    }

    private void importOneFile(Path file, Charset csvCharset, String sql, int batchSize) throws IOException {
        Resource csv = resourceLoader.getResource("file:" + file.toAbsolutePath());
        List<Object[]> batch = new ArrayList<>(batchSize);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csv.getInputStream(), csvCharset));
             CSVParser parser = CSVFormat.DEFAULT.builder()
                   .setHeader()
                   .setSkipHeaderRecord(true)
                   .setIgnoreSurroundingSpaces(true)
                   .build()
                   .parse(br)) {

            for (CSVRecord record : parser) {
                String productName = getOrNull(record, "상품명");
                LocalDate surveyDate = parseDate(getOrNull(record, "조사일"));
                BigDecimal productPrice = parsePrice(getOrNull(record, "판매가격"));
                String storeName = getOrNull(record, "판매업소");
                String maker = getOrNull(record, "제조사");
                Boolean isSale = parseYN(getOrNull(record, "세일여부"));
                Boolean isOnePlusOne = parseYN(getOrNull(record, "원플러스원"));

                if (productName == null || surveyDate == null) {
                    continue;
                }

                batch.add(new Object[]{productName, surveyDate, productPrice, storeName, maker, isSale, isOnePlusOne});

                if (batch.size() == batchSize) {
                    jdbcTemplate.batchUpdate(sql, batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, batch);
                batch.clear();
            }
        }
    }

    private String getOrNull(CSVRecord r, String header) {
        try {
            String v = r.get(header);
            if (v == null) return null;
            v = v.trim();
            return v.isEmpty() ? null : v;
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(String v) {
        if (v == null) {
            return null;
        }

        try {
            return LocalDate.parse(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parsePrice(String v) {
        if (v == null) {
            return null;
        }
        String cleaned = v.replaceAll("[^0-9.]", "");
        if (cleaned.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean parseYN(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        String s = v.trim().toUpperCase();
        if (s.equals("Y") || s.equals("YES") || s.equals("TRUE") || s.equals("1") || s.equals("O")) {
            return true;
        }
        if (s.equals("N") || s.equals("NO")  || s.equals("FALSE")|| s.equals("0") || s.equals("X")) {
            return false;
        }
        return null;
    }
}
