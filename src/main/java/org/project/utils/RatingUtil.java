package org.project.utils;

import org.springframework.stereotype.Component;

/**
 * Utility bean exposed to Thymeleaf templates as <code>@ratingUtil</code>.
 * It converts a numeric rating (0-5) to an HTML snippet containing
 * filled/empty star <span> elements that can be styled with CSS.
 */
@Component("ratingUtil")
public class RatingUtil {

    /**
     * Generate star markup for the given rating value.
     *
     * @param ratingObj rating may be Integer, Double, etc.  Null or out-of-range values are treated as 0.
     * @return HTML string with five <span> elements holding ★ (filled) or ☆ (empty) characters.
     */
    public String generateStars(Object ratingObj) {
        int rating = 0;
        if (ratingObj instanceof Number number) {
            rating = (int) Math.round(number.doubleValue());
        }

        rating = Math.max(0, Math.min(5, rating));

        StringBuilder html = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            html.append("<span>")
                .append(i < rating ? "&#9733;" : "&#9734;") // ★ or ☆
                .append("</span>");
        }
        return html.toString();
    }
}
