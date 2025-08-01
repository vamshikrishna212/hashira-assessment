package com.example.vamshi123;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) throws IOException {

        try {
            InputStream inputStream = ShamirSecretSharing.class.getClassLoader().getResourceAsStream("input2.json");

            if (inputStream == null) {
                System.err.println("Error: input.json not found in resources folder.");
                return;
            }

            // Read and parse the JSON file from the InputStream
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Get k from the keys object
            int k = rootNode.get("keys").get("k").asInt();

            // Decode the shares (x, y)
            Map<BigInteger, BigInteger> points = new HashMap<>();
            List<BigInteger> xCoords = new ArrayList<>();

            rootNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key);
                    int base = Integer.parseInt(entry.getValue().get("base").asText());
                    String valueStr = entry.getValue().get("value").asText();
                    BigInteger y = new BigInteger(valueStr, base);
                    points.put(BigInteger.valueOf(x), y);
                    xCoords.add(BigInteger.valueOf(x));
                }
            });

            // Use the first k points for Lagrange interpolation
            List<Map.Entry<BigInteger, BigInteger>> shares = new ArrayList<>(points.entrySet());
            BigInteger secret = BigInteger.ZERO;

            for (int j = 0; j < k; j++) {
                Map.Entry<BigInteger, BigInteger> currentShare = shares.get(j);
                BigInteger xj = currentShare.getKey();
                BigInteger yj = currentShare.getValue();

                BigInteger numerator = BigInteger.ONE;
                BigInteger denominator = BigInteger.ONE;

                for (int i = 0; i < k; i++) {
                    if (i != j) {
                        BigInteger xi = shares.get(i).getKey();
                        numerator = numerator.multiply(xi.negate());
                        denominator = denominator.multiply(xj.subtract(xi));
                    }
                }

                // Calculate the term for the current share
                BigInteger term = yj.multiply(numerator).divide(denominator);
                secret = secret.add(term);
            }

            System.out.println("The secret is: " + secret);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

// /**
// * Hello world!
// *
// */
// public class App
// {
// public static void main( String[] args )
// {
// System.out.println( "Hello World!" );
// }
// }
