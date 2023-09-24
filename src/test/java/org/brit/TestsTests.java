package org.brit;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

public class TestsTests {
    static class User {
        private String firstName;
        private String lastName;
        private int age;

        public User(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "User{" +
                   "firstName='" + firstName + '\'' +
                   ", lastName='" + lastName + '\'' +
                   ", age=" + age +
                   '}';
        }
    }


    public static Map<Integer, User> getDataFromFile(File dataFile) {
        Map<Integer, User> userData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String firstName = parts[1];
                String lastName = parts[2];
                int age = Integer.parseInt(parts[3]);
                userData.put(id, new User(firstName, lastName, age));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userData;
    }

    public static User getDataById(Map<Integer, User> mapData, Integer id) {
        return mapData.get(id);
    }

    public static int getNumberOfOccurrences(Map<Integer, User> mapData, String lastName) {
        int count = 0;
        for (User user : mapData.values()) {
            if (user.getLastName().equals(lastName)) {
                count++;
            }
        }
        return count;
    }

    public static List<User> getUsersAgeMoreThen(Map<Integer, User> mapData, int age) {
        List<User> result = new ArrayList<>();
        for (User user : mapData.values()) {
            if (user.getAge() > age) {
                result.add(user);
            }
        }
        return result;
    }

    public static Map<String, List<Integer>> findEqualUsers(Map<Integer, User> users) {
        Map<String, List<Integer>> equalUsers = new HashMap<>();
        Map<String, List<Integer>> tempMap = new HashMap<>();

        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            String fullName = entry.getValue().getFirstName() + " " + entry.getValue().getLastName();
            tempMap.computeIfAbsent(fullName, k -> new ArrayList<>()).add(entry.getKey());
        }

        for (Map.Entry<String, List<Integer>> entry : tempMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                equalUsers.put(entry.getKey(), entry.getValue());
            }
        }

        return equalUsers;
    }

    public static void main(String[] args) {
//        assertThatList(List.of("WELCOME TO THE LANE HEALTH ONLINE PORTAL",
//                "A simple and convenient solution to manage healthcare costs",
//                "Sign up",
//                "Don't have an account? Sign up",
//                "Delta Dental employee? Register Here",
//                "Register Here",
//                "Sign in",
//                "Forgot Password?",
//                "Reset Password",
//                " ",
//                "Contact Us"))
//                .usingElementComparator((str1, str2) -> str1.toLowerCase().contains(str2.toLowerCase()) ? 1 : -1)
//                .containsAll(List.of("Welcome", "Sign in", "Forgot password?", "Sign up"));
        List<String> strings = List.of("WELCOME TO THE LANE HEALTH ONLINE PORTAL",
                "A simple and convenient solution to manage healthcare costs",
                "Sign up",
                "Don't have an account? Sign up",
                "Delta Dental employee? Register Here",
                "Register Here",
                "Sign in",
                "Forgot Password?",
                "Reset Password",
                " ",
                "Contact Us");

//        assertThat(strings)
//                .anyMatch(p -> p.toLowerCase().contains(str.toLowerCase())))

        List.of("Welcome", "Sign in", "Forgot password?", "Sign up")
                .forEach(str -> assertThat(strings.stream().anyMatch(p -> p.toLowerCase().contains(str.toLowerCase())))
                        .isTrue());


    }
}

