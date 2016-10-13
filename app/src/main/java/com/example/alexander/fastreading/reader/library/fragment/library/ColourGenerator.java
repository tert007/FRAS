package com.example.alexander.fastreading.reader.library.fragment.library;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Alexander on 11.10.2016.
 */
public class ColourGenerator {

        /*
                0xffe57373,
                0xfff06292,
                0xffba68c8,
                0xff9575cd,
                0xff7986cb,
                0xff64b5f6,
                0xff4fc3f7,
                0xff4dd0e1,
                0xff4db6ac,
                0xff81c784,
                0xffaed581,
                0xffff8a65,
                0xffd4e157,
                0xffffd54f,
                0xffffb74d, // last usesed
                0xffa1887f,
                0xff90a4ae
                */

    public static int getRandomColor(char letter) {
        char c = Character.toUpperCase(letter);

        if (c >= '0' && c <= '9')
            return 0xffe57373;

        if (c == 'A' || c == 'B' || c == 'А' || c == 'Б' || c == 'В')
            return 0xfff06292;

        if (c == 'C' || c == 'D' || c == 'Г' || c == 'Д' || c == 'Е')
            return 0xffba68c8;

        if (c == 'E' || c == 'F' || c == 'Ё' || c == 'Ж' || c == 'З')
            return 0xff9575cd;

        if (c == 'G' || c == 'H' || c == 'И' || c == 'Й' || c == 'К')
            return 0xff7986cb;

        if (c == 'I' || c == 'J' || c == 'Л' || c == 'М' || c == 'Н')
            return 0xff64b5f6;

        if (c == 'K' || c == 'L' || c == 'О' || c == 'П' || c == 'Р')
            return 0xff4fc3f7;

        if (c == 'M' || c == 'N' || c == 'С' || c == 'Т' || c == 'У')
            return 0xff4dd0e1;

        if (c == 'O' || c == 'P' || c == 'Ф' || c == 'Х' || c == 'Ц')
            return 0xff4db6ac;

        if (c == 'Q' || c == 'R' || c == 'Ч' || c == 'Ш' || c == 'Щ')
            return 0xff81c784;

        if (c == 'S' || c == 'T' || c == 'Ъ' || c == 'Ы' || c == 'Ь')
            return 0xffaed581;

        if (c == 'U' || c == 'V'  || c == 'Э' || c == 'Ю' || c == 'Я')
            return 0xffff8a65;

        if (c == 'W' || c == 'X')
            return 0xffd4e157;

        if (c == 'Y' || c == 'Z')
            return 0xffffd54f;

        return 0xffffb74d;
    }
}
