package org.project.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Getter
@Setter
public class TimestampUtils {
    private Timestamp timestamp;

    //  ---------------------- CONSTRUCTORS ----------------------
    public TimestampUtils() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public TimestampUtils(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public TimestampUtils(Date date) {
        this.timestamp = new Timestamp(date.getTime());
    }

    // ------------------------- ADD & SUBTRACT TIME ----------------------
    public Timestamp plusSeconds(int seconds) {
        long milliseconds = timestamp.getTime() + (seconds * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp plusSeconds(Timestamp timestamp, int seconds) {
        long milliseconds = timestamp.getTime() + (seconds * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp minusSeconds(int seconds) {
        long milliseconds = timestamp.getTime() - (seconds * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp minusSeconds(Timestamp timestamp, int seconds) {
        long milliseconds = timestamp.getTime() - (seconds * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp plusMinutes(int minutes) {
        long milliseconds = timestamp.getTime() + (minutes * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp plusMinutes(Timestamp timestamp, int minutes) {
        long milliseconds = timestamp.getTime() + (minutes * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp minusMinutes(int minutes) {
        long milliseconds = timestamp.getTime() - (minutes * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp plusHours(int hours) {
        long milliseconds = timestamp.getTime() + (hours * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp plusHours(Timestamp timestamp, int hours) {
        long milliseconds = timestamp.getTime() + (hours * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp minusHours(int hours) {
        long milliseconds = timestamp.getTime() - (hours * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp minusHours(Timestamp timestamp, int hours) {
        long milliseconds = timestamp.getTime() - (hours * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp minusMinutes(Timestamp timestamp, int minutes) {
        long milliseconds = timestamp.getTime() - (minutes * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Timestamp plusDays(int days) {
        long milliseconds = timestamp.getTime() + (days * 24 * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Date plusDaysAsDate(int days) {
        long milliseconds = timestamp.getTime() + (days * 24 * 60 * 60 * 1000L);
        return new Date(milliseconds);
    }

    public Timestamp plusDays(Timestamp timestamp, int days) {
        long milliseconds = timestamp.getTime() + (days * 24 * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Date plusDaysAsDate(Date date, int days) {
        long milliseconds = date.getTime() + (days * 24 * 60 * 60 * 1000L);
        return new Date(milliseconds);
    }

    public Timestamp minusDays(int days) {
        long milliseconds = timestamp.getTime() - (days * 24 * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Date minusDaysAsDate(int days) {
        long milliseconds = timestamp.getTime() - (days * 24 * 60 * 60 * 1000L);
        return new Date(milliseconds);
    }

    public Timestamp minusDays(Timestamp timestamp, int days) {
        long milliseconds = timestamp.getTime() - (days * 24 * 60 * 60 * 1000L);
        return new Timestamp(milliseconds);
    }

    public Date minusDaysAsDate(Date date, int days) {
        long milliseconds = date.getTime() - (days * 24 * 60 * 60 * 1000L);
        return new Date(milliseconds);
    }

    // ------------------------- GET TIME BOUNDARIES ----------------------
    public Timestamp getStartOfDay() {
        LocalDateTime ldt = toLocalDateTime().toLocalDate().atStartOfDay();
        return Timestamp.valueOf(ldt);
    }

    public Timestamp getStartOfDay(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDateTime startOfDay = ldt.toLocalDate().atStartOfDay();
        return Timestamp.valueOf(startOfDay);
    }

    public Timestamp getEndOfDay() {
        LocalDate date = toLocalDateTime().toLocalDate();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfDay);
    }

    public Timestamp getEndOfDay(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDate date = ldt.toLocalDate();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfDay);
    }

    public Timestamp getStartOfWeek() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate monday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 1);
        LocalDateTime startOfWeek = monday.atStartOfDay();
        return Timestamp.valueOf(startOfWeek);
    }

    public Date getStartOfWeekAsDate() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate monday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 1);
        LocalDateTime startOfWeek = monday.atStartOfDay();
        return Date.valueOf(startOfWeek.toLocalDate());
    }

    public Timestamp getStartOfWeek(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDate monday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 1);
        LocalDateTime startOfWeek = monday.atStartOfDay();
        return Timestamp.valueOf(startOfWeek);
    }

    public Date getStartOfWeekAsDate(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        LocalDate monday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 1);
        LocalDateTime startOfWeek = monday.atStartOfDay();
        return Date.valueOf(startOfWeek.toLocalDate());
    }

    public Timestamp getEndOfWeek() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate sunday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 7);
        LocalDateTime endOfWeek = sunday.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfWeek);
    }

    public Date getEndOfWeekAsDate() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate sunday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 7);
        LocalDateTime endOfWeek = sunday.atTime(23, 59, 59, 999_000_000);
        return Date.valueOf(endOfWeek.toLocalDate());
    }

    public Timestamp getEndOfWeek(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDate sunday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 7);
        LocalDateTime endOfWeek = sunday.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfWeek);
    }

    public Date getEndOfWeekAsDate(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        LocalDate sunday = ldt.toLocalDate().with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 7);
        LocalDateTime endOfWeek = sunday.atTime(23, 59, 59, 999_000_000);
        return Date.valueOf(endOfWeek.toLocalDate());
    }

    public Timestamp getStartOfMonth() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDateTime startOfMonth = ldt.withDayOfMonth(1).toLocalDate().atStartOfDay();
        return Timestamp.valueOf(startOfMonth);
    }

    public Date getStartOfMonthAsDate() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDateTime startOfMonth = ldt.withDayOfMonth(1).toLocalDate().atStartOfDay();
        return Date.valueOf(startOfMonth.toLocalDate());
    }

    public Timestamp getStartOfMonth(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDateTime startOfMonth = ldt.withDayOfMonth(1).toLocalDate().atStartOfDay();
        return Timestamp.valueOf(startOfMonth);
    }

    public Date getStartOfMonthAsDate(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = ldt.withDayOfMonth(1).toLocalDate().atStartOfDay();
        return Date.valueOf(startOfMonth.toLocalDate());
    }

    public Timestamp getEndOfMonth() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate lastDay = ldt.toLocalDate().withDayOfMonth(ldt.toLocalDate().lengthOfMonth());
        LocalDateTime endOfMonth = lastDay.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfMonth);
    }

    public Date getEndOfMonthAsDate() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate lastDay = ldt.toLocalDate().withDayOfMonth(ldt.toLocalDate().lengthOfMonth());
        LocalDateTime endOfMonth = lastDay.atTime(23, 59, 59, 999_000_000);
        return Date.valueOf(endOfMonth.toLocalDate());
    }

    public Timestamp getEndOfMonth(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDate lastDay = ldt.toLocalDate().withDayOfMonth(ldt.toLocalDate().lengthOfMonth());
        LocalDateTime endOfMonth = lastDay.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfMonth);
    }

    public Date getEndOfMonthAsDate(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        LocalDate lastDay = ldt.toLocalDate().withDayOfMonth(ldt.toLocalDate().lengthOfMonth());
        LocalDateTime endOfMonth = lastDay.atTime(23, 59, 59, 999_000_000);
        return Date.valueOf(endOfMonth.toLocalDate());
    }

    public Timestamp getStartOfYear() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDateTime startOfYear = ldt.withDayOfYear(1).toLocalDate().atStartOfDay();
        return Timestamp.valueOf(startOfYear);
    }

    public Date getStartOfYearAsDate() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDateTime startOfYear = ldt.withDayOfYear(1).toLocalDate().atStartOfDay();
        return Date.valueOf(startOfYear.toLocalDate());
    }

    public Timestamp getStartOfYear(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDateTime startOfYear = ldt.withDayOfYear(1).toLocalDate().atStartOfDay();
        return Timestamp.valueOf(startOfYear);
    }

    public Date getStartOfYearAsDate(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        LocalDateTime startOfYear = ldt.withDayOfYear(1).toLocalDate().atStartOfDay();
        return Date.valueOf(startOfYear.toLocalDate());
    }

    public Timestamp getEndOfYear() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate lastDay = ldt.toLocalDate().withDayOfYear(ldt.toLocalDate().lengthOfYear());
        LocalDateTime endOfYear = lastDay.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfYear);
    }

    public Date getEndOfYearAsDate() {
        LocalDateTime ldt = toLocalDateTime();
        LocalDate lastDay = ldt.toLocalDate().withDayOfYear(ldt.toLocalDate().lengthOfYear());
        LocalDateTime endOfYear = lastDay.atTime(23, 59, 59, 999_000_000);
        return Date.valueOf(endOfYear.toLocalDate());
    }

    public Timestamp getEndOfYear(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        LocalDate lastDay = ldt.toLocalDate().withDayOfYear(ldt.toLocalDate().lengthOfYear());
        LocalDateTime endOfYear = lastDay.atTime(23, 59, 59, 999_000_000);
        return Timestamp.valueOf(endOfYear);
    }

    public Date getEndOfYearAsDate(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        LocalDate lastDay = ldt.toLocalDate().withDayOfYear(ldt.toLocalDate().lengthOfYear());
        LocalDateTime endOfYear = lastDay.atTime(23, 59, 59, 999_000_000);
        return Date.valueOf(endOfYear.toLocalDate());
    }

    // ------------------------- GET TIME COMPONENTS ----------------------
    public int getSecond() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getSecond();
    }

    public int getSecond(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getSecond();
    }

    public int getSecond(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getSecond();
    }

    public int getMinute() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getMinute();
    }

    public int getMinute(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getMinute();
    }

    public int getMinute(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getMinute();
    }

    public int getHour() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getHour();
    }

    public int getHour(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getHour();
    }

    public int getHour(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getHour();
    }

    public int getDay() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getDayOfMonth();
    }

    public int getDay(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getDayOfMonth();
    }

    public int getDay(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getDayOfMonth();
    }

    public int getDayOfWeek() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getDayOfWeek().getValue();
    }

    public int getDayOfWeek(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getDayOfWeek().getValue();
    }

    public int getDayOfWeek(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getDayOfWeek().getValue();
    }

    public int getDayOfYear() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getDayOfYear();
    }

    public int getDayOfYear(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getDayOfYear();
    }

    public int getDayOfYear(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getDayOfYear();
    }

    public int getMonth() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getMonthValue();
    }

    public int getMonth(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getMonthValue();
    }

    public int getMonth(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getMonthValue();
    }

    public int getYear() {
        LocalDateTime ldt = toLocalDateTime();
        return ldt.getYear();
    }

    public int getYear(Timestamp timestamp) {
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.getYear();
    }

    public int getYear(Date date) {
        LocalDateTime ldt = date.toLocalDate().atStartOfDay();
        return ldt.getYear();
    }

    // ------------------------- COMPARISON METHODS ----------------------
    public boolean isBefore(TimestampUtils other) {
        return this.timestamp.before(other.timestamp);
    }

    public boolean isBefore(Timestamp other) {
        return this.timestamp.before(other);
    }

    public boolean isBefore(Date other) {
        return this.timestamp.before(new Timestamp(other.getTime()));
    }

    public boolean isBefore(Timestamp timestamp1, Timestamp timestamp2) {
        return timestamp1.before(timestamp2);
    }

    public boolean isBefore(Date date1, Date date2) {
        return new Timestamp(date1.getTime()).before(new Timestamp(date2.getTime()));
    }

    public boolean isBeforeOrEqual(TimestampUtils other) {
        return !this.timestamp.after(other.timestamp);
    }

    public boolean isBeforeOrEqual(Timestamp other) {
        return !this.timestamp.before(other);
    }

    public boolean isBeforeOrEqual(Date other) {
        return !this.timestamp.after(new Timestamp(other.getTime()));
    }

    public boolean isBeforeOrEqual(Timestamp timestamp1, Timestamp timestamp2) {
        return !timestamp1.after(timestamp2);
    }

    public boolean isBeforeOrEqual(Date date1, Date date2) {
        return !new Timestamp(date1.getTime()).after(new Timestamp(date2.getTime()));
    }

    public boolean isAfter(TimestampUtils other) {
        return this.timestamp.after(other.timestamp);
    }

    public boolean isAfter(Timestamp other) {
        return this.timestamp.after(other);
    }

    public boolean isAfter(Date other) {
        return this.timestamp.after(new Timestamp(other.getTime()));
    }

    public boolean isAfter(Timestamp timestamp1, Timestamp timestamp2) {
        return timestamp1.after(timestamp2);
    }

    public boolean isAfter(Date date1, Date date2) {
        return new Timestamp(date1.getTime()).after(new Timestamp(date2.getTime()));
    }

    public boolean isAfterOrEqual(TimestampUtils other) {
        return !this.timestamp.before(other.timestamp);
    }

    public boolean isAfterOrEqual(Timestamp other) {
        return !this.timestamp.after(other);
    }

    public boolean isAfterOrEqual(Date other) {
        return !this.timestamp.before(new Timestamp(other.getTime()));
    }

    public boolean isAfterOrEqual(Timestamp timestamp1, Timestamp timestamp2) {
        return !timestamp1.before(timestamp2);
    }

    public boolean isAfterOrEqual(Date date1, Date date2) {
        return !new Timestamp(date1.getTime()).before(new Timestamp(date2.getTime()));
    }

    public boolean isSameDay(TimestampUtils other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDateTime().toLocalDate();
        return d1.isEqual(d2);
    }

    public boolean isSameDay(Timestamp other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDateTime().toLocalDate();
        return d1.isEqual(d2);
    }

    public boolean isSameDay(Date other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDate();
        return d1.isEqual(d2);
    }

    public boolean isSameDay(Timestamp timestamp1, Timestamp timestamp2) {
        LocalDate d1 = timestamp1.toLocalDateTime().toLocalDate();
        LocalDate d2 = timestamp2.toLocalDateTime().toLocalDate();
        return d1.isEqual(d2);
    }

    public boolean isSameDay(Date date1, Date date2) {
        LocalDate d1 = date1.toLocalDate();
        LocalDate d2 = date2.toLocalDate();
        return d1.isEqual(d2);
    }

    public boolean isSameMonth(TimestampUtils other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDateTime().toLocalDate();
        return d1.getYear() == d2.getYear() && d1.getMonthValue() == d2.getMonthValue();
    }

    public boolean isSameMonth(Timestamp other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDateTime().toLocalDate();
        return d1.getYear() == d2.getYear() && d1.getMonthValue() == d2.getMonthValue();
    }

    public boolean isSameMonth(Date other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDate();
        return d1.getYear() == d2.getYear() && d1.getMonthValue() == d2.getMonthValue();
    }

    public boolean isSameMonth(Timestamp timestamp1, Timestamp timestamp2) {
        LocalDate d1 = timestamp1.toLocalDateTime().toLocalDate();
        LocalDate d2 = timestamp2.toLocalDateTime().toLocalDate();
        return d1.getYear() == d2.getYear() && d1.getMonthValue() == d2.getMonthValue();
    }

    public boolean isSameMonth(Date date1, Date date2) {
        LocalDate d1 = date1.toLocalDate();
        LocalDate d2 = date2.toLocalDate();
        return d1.getYear() == d2.getYear() && d1.getMonthValue() == d2.getMonthValue();
    }

    public boolean isSameYear(TimestampUtils other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDateTime().toLocalDate();
        return d1.getYear() == d2.getYear();
    }

    public boolean isSameYear(Timestamp other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDateTime().toLocalDate();
        return d1.getYear() == d2.getYear();
    }

    public boolean isSameYear(Date other) {
        LocalDate d1 = this.toLocalDateTime().toLocalDate();
        LocalDate d2 = other.toLocalDate();
        return d1.getYear() == d2.getYear();
    }

    public boolean isSameYear(Timestamp timestamp1, Timestamp timestamp2) {
        LocalDate d1 = timestamp1.toLocalDateTime().toLocalDate();
        LocalDate d2 = timestamp2.toLocalDateTime().toLocalDate();
        return d1.getYear() == d2.getYear();
    }

    public boolean isSameYear(Date date1, Date date2) {
        LocalDate d1 = date1.toLocalDate();
        LocalDate d2 = date2.toLocalDate();
        return d1.getYear() == d2.getYear();
    }

    // ------------------------- CONVERSION METHODS ----------------------
    public Date toSqlDate() {
        return new Date(timestamp.getTime());
    }

    public Date toSqlDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }

    public Timestamp toSqlTimestamp() {
        return timestamp;
    }

    public LocalDateTime toLocalDateTime() {
        return timestamp.toLocalDateTime();
    }

    // ------------------------- FORMAT METHODS ----------------------
    public String format(String pattern) {
        LocalDateTime ldt = toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return ldt.format(formatter);
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
