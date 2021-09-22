package org.kimios.utils.session;

public class SessionUtils {

    public static String generateSessionUid()
    {
        String[] hash = {
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "I",
                "J",
                "K",
                "L",
                "M",
                "N",
                "O",
                "P",
                "Q",
                "R",
                "S",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z",
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9"
        };
        String sessionUID = "";
        for (int i = 0; i < 20; i++) {
            sessionUID += hash[(int) (Math.random() * (hash.length))];
        }
        return sessionUID;
    }
}
