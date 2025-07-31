package org.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/logs")
public class LogViewController {

    private static final String LOG_FILE_PATH = "logs/request.log";
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String viewLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String searchEmail,
            Model model) {
        List<String> rawLines;
        try {
            Path path = Paths.get(LOG_FILE_PATH);
            rawLines = Files.exists(path)
                    ? Files.readAllLines(path, StandardCharsets.UTF_8)
                    : List.of();
        } catch (IOException e) {
            model.addAttribute("logs", List.of(List.of("❌ Không thể đọc log: " + e.getMessage())));
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("searchEmail", searchEmail);
            return "frontend/logs";
        }

        Collections.reverse(rawLines);
        List<List<String>> logBlocks = splitLogByUserBlocks(rawLines);

        // Lọc log theo email nếu có searchEmail
        if (searchEmail != null && !searchEmail.isEmpty()) {
            logBlocks = logBlocks.stream()
                    .filter(group -> {
                        for (String line : group) {
                            if (line.contains("Người dùng:")) {
                                int startIndex = line.indexOf("Người dùng:") + "Người dùng:".length();
                                int endIndex = line.indexOf(" (vai trò:");
                                if (startIndex >= 0 && endIndex > startIndex) {
                                    String emailInLog = line.substring(startIndex, endIndex).trim();
                                    return emailInLog.equalsIgnoreCase(searchEmail);
                                }
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }


        int totalLogs = logBlocks.size();
        int totalPages = (int) Math.ceil((double) totalLogs / PAGE_SIZE);
        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalLogs);


        List<List<String>> pagedLogs = (totalLogs > 0) ? logBlocks.subList(startIndex, endIndex) : new ArrayList<>();

        model.addAttribute("logs", pagedLogs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchEmail", searchEmail);

        return "frontend/logs";
    }

    private List<List<String>> splitLogByUserBlocks(List<String> allLines) {
        List<List<String>> groupedLogs = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();

        for (String line : allLines) {
            if (line.contains("📦 Phản hồi trả về")) {
                if (!currentGroup.isEmpty()) {
                    groupedLogs.add(currentGroup);
                    currentGroup = new ArrayList<>();
                }
            }
            currentGroup.add(line);
        }

        if (!currentGroup.isEmpty()) {
            groupedLogs.add(currentGroup);
        }

        return groupedLogs;
    }
}